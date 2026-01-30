package io.github.vvb2060.ims;

import android.app.IActivityManager;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.ServiceManager;
import android.system.Os;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import rikka.shizuku.ShizukuBinderWrapper;

public class PrivilegedProcess extends Instrumentation {

    private static final String PREFS_NAME = "ims_config";
    private static final String CUSTOM_OVERRIDES_KEY = "custom_overrides";

    @Override
    public void onCreate(Bundle arguments) {
        Log.i("PrivilegedProcess", "onCreate called");

        // 等待 Shizuku binder 准备好
        int maxRetries = 50; // 最多等待 5 秒
        for (int i = 0; i < maxRetries; i++) {
            if (rikka.shizuku.Shizuku.pingBinder()) {
                Log.i("PrivilegedProcess", "Shizuku binder is ready");
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }

        boolean success = false;
        try {
            overrideConfig();
            Log.i("PrivilegedProcess", "overrideConfig completed successfully");
            success = true;
        } catch (Exception e) {
            Log.e("PrivilegedProcess", "Failed to override config", e);
        }

        Intent intent = new Intent(getTargetContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("config_applied", success);
        getTargetContext().startActivity(intent);

        finish(0, new Bundle());
    }

    private void overrideConfig() throws Exception {
        Log.i("PrivilegedProcess", "overrideConfig started");
        var binder = ServiceManager.getService(Context.ACTIVITY_SERVICE);
        var am = IActivityManager.Stub.asInterface(new ShizukuBinderWrapper(binder));
        Log.i("PrivilegedProcess", "Starting shell permission delegation");
        am.startDelegateShellPermissionIdentity(Os.getuid(), null);
        try {
            var cm = getContext().getSystemService(CarrierConfigManager.class);
            var sm = getContext().getSystemService(SubscriptionManager.class);
            var values = new PersistableBundle();

            // Apply Presets
            for (ConfigDefinition def : PresetConfigs.getPresets()) {
                applyConfigItem(values, def.key, def.defaultValue);
            }

            // Apply Overrides
            applyCustomOverrides(getContext(), values);

            // Handle WiFi Country Code
            String wifiCountryCode = values.getString("virtual.wifi_country_code");
            if (wifiCountryCode != null && wifiCountryCode.matches("^[A-Z]{2}$")) {
                try {
                    Log.i("PrivilegedProcess", "Setting WiFi country code to: " + wifiCountryCode);
                    Process p = Runtime.getRuntime().exec("su");
                    java.io.OutputStream os = p.getOutputStream();
                    os.write(("cmd wifi force-country-code enabled " + wifiCountryCode + "\n").getBytes());
                    os.write("exit\n".getBytes());
                    os.flush();
                    p.waitFor();
                } catch (Exception e) {
                    Log.e("PrivilegedProcess", "Failed to set WiFi country code", e);
                }
            }

            // 读取用户选择的 SubId
            SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            int selectedSubId = prefs.getInt("selected_subid", 1);
            Log.i("PrivilegedProcess", "Selected SubId: " + selectedSubId);

            int[] subIds;
            if (selectedSubId == -1) {
                // 应用到所有 SIM 卡
                subIds = (int[]) sm.getClass().getMethod("getActiveSubscriptionIdList").invoke(sm);
                Log.i("PrivilegedProcess", "Applying to all SIM cards: " + java.util.Arrays.toString(subIds));
            } else {
                // 只应用到选中的 SIM 卡
                subIds = new int[]{selectedSubId};
                Log.i("PrivilegedProcess", "Applying to SIM: " + selectedSubId);
            }

            for (var subId : subIds) {
                Log.i("PrivilegedProcess", "Processing SubId: " + subId);
                var bundle = cm.getConfigForSubId(subId, "vvb2060_config_version");
                int currentVersion = bundle.getInt("vvb2060_config_version", 0);
                Log.i("PrivilegedProcess", "Current version: " + currentVersion + ", BuildConfig: " + BuildConfig.VERSION_CODE);

                if (currentVersion != BuildConfig.VERSION_CODE) {
                    values.putInt("vvb2060_config_version", BuildConfig.VERSION_CODE);
                    // 使用反射调用 overrideConfig
                    try {
                        cm.getClass().getMethod("overrideConfig", int.class, PersistableBundle.class)
                            .invoke(cm, subId, values);
                        Log.i("PrivilegedProcess", "Applied config (2-param) to SubId: " + subId);
                    } catch (NoSuchMethodException e) {
                        // 如果不存在两参数方法，尝试三参数方法
                        cm.getClass().getMethod("overrideConfig", int.class, PersistableBundle.class, boolean.class)
                            .invoke(cm, subId, values, false);
                        Log.i("PrivilegedProcess", "Applied config (3-param) to SubId: " + subId);
                    }
                } else {
                    Log.i("PrivilegedProcess", "Config already up-to-date for SubId: " + subId);
                }
            }
        } finally {
            am.stopDelegateShellPermissionIdentity();
            Log.i("PrivilegedProcess", "Stopped shell permission delegation");
        }
    }

    // Removed duplicate logic.

    private static void applyCustomOverrides(Context context, PersistableBundle bundle) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(CUSTOM_OVERRIDES_KEY, "{}");
        try {
            JSONObject overrides = new JSONObject(json);
            java.util.Iterator<String> keys = overrides.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = overrides.get(key);
                applyConfigItem(bundle, key, value);
            }
        } catch (Exception e) {
            Log.e("PrivilegedProcess", "Failed to apply custom overrides", e);
        }
    }

    private static void applyConfigItem(PersistableBundle bundle, String key, Object value) {
        if (value instanceof Boolean) {
            bundle.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            bundle.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            bundle.putLong(key, (Long) value);
        } else if (value instanceof Double) {
            bundle.putDouble(key, (Double) value);
        } else if (value instanceof String) {
            bundle.putString(key, (String) value);
        } else if (value instanceof int[]) {
            bundle.putIntArray(key, (int[]) value);
        } else if (value instanceof String[]) {
            bundle.putStringArray(key, (String[]) value);
        } else if (value instanceof JSONArray) {
            JSONArray array = (JSONArray) value;
            try {
                if (array.length() > 0) {
                    Object first = array.get(0);
                    if (first instanceof Integer) {
                        int[] intArray = new int[array.length()];
                        for (int i = 0; i < array.length(); i++) {
                            intArray[i] = array.getInt(i);
                        }
                        bundle.putIntArray(key, intArray);
                    } else if (first instanceof String) {
                        String[] stringArray = new String[array.length()];
                        for (int i = 0; i < array.length(); i++) {
                            stringArray[i] = array.getString(i);
                        }
                        bundle.putStringArray(key, stringArray);
                    }
                } else {
                    bundle.putIntArray(key, new int[0]);
                }
            } catch (Exception e) {
                // Ignore
            }
        }
    }
}
