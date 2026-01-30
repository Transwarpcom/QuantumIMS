package io.github.vvb2060.ims;

import android.app.IActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.os.ServiceManager;
import android.system.Os;
import android.telephony.CarrierConfigManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import rikka.shizuku.ShizukuBinderWrapper;

public class ImsConfigHelper {

    private static final String TAG = "ImsConfigHelper";
    private static final String PREFS_NAME = "ims_config";
    private static final String CUSTOM_OVERRIDES_KEY = "custom_overrides";

    public static void applyConfig(Context context, int subId) throws Exception {
        Log.i(TAG, "Starting to apply IMS configuration for subId: " + subId);

        // 获取 Shell 权限委托
        var binder = ServiceManager.getService(Context.ACTIVITY_SERVICE);
        var am = IActivityManager.Stub.asInterface(new ShizukuBinderWrapper(binder));
        am.startDelegateShellPermissionIdentity(Os.getuid(), null);

        try {
            var cm = context.getSystemService(CarrierConfigManager.class);
            var values = new PersistableBundle();

            // Apply Presets
            for (ConfigDefinition def : PresetConfigs.getPresets()) {
                if (def.isHeader) continue;
                applyConfigItem(values, def.key, def.defaultValue);
            }

            // Apply Overrides (Includes virtual keys)
            applyCustomOverrides(context, values);

            // Handle Virtual Keys Logic
            if (values.getBoolean("virtual.remove_apn_readonly", false)) {
                values.putStringArray("read_only_apn_types_string_array", null);
                values.putStringArray("read_only_apn_fields_string_array", null);
                // Clean up virtual key so it's not sent to CarrierConfig (though extra keys usually ignored)
                values.putBoolean("virtual.remove_apn_readonly", false); // Just in case
            }
            if (values.getBoolean("virtual.remove_qns_ho_restrict", false)) {
                values.putIntArray("qns.ho_restrict_time_with_low_rtp_quality_int_array", null);
                 values.putBoolean("virtual.remove_qns_ho_restrict", false);
            }

            // Handle WiFi Country Code
            String wifiCountryCode = values.getString("virtual.wifi_country_code");
            if (wifiCountryCode != null && wifiCountryCode.matches("^[A-Z]{2}$")) {
                try {
                    Log.i(TAG, "Setting WiFi country code to: " + wifiCountryCode);
                    Process p = Runtime.getRuntime().exec("su");
                    java.io.OutputStream os = p.getOutputStream();
                    os.write(("cmd wifi force-country-code enabled " + wifiCountryCode + "\n").getBytes());
                    os.write("exit\n".getBytes());
                    os.flush();
                    p.waitFor();
                } catch (Exception e) {
                    Log.e(TAG, "Failed to set WiFi country code", e);
                }
            }

            var bundle = cm.getConfigForSubId(subId, "vvb2060_config_version");
            if (bundle.getInt("vvb2060_config_version", 0) != BuildConfig.VERSION_CODE) {
                values.putInt("vvb2060_config_version", BuildConfig.VERSION_CODE);
                // 使用反射调用 overrideConfig
                try {
                    cm.getClass().getMethod("overrideConfig", int.class, PersistableBundle.class)
                        .invoke(cm, subId, values);
                    Log.i(TAG, "Applied config to subscription: " + subId);
                } catch (NoSuchMethodException e) {
                    // 如果不存在两参数方法，尝试三参数方法
                    cm.getClass().getMethod("overrideConfig", int.class, PersistableBundle.class, boolean.class)
                        .invoke(cm, subId, values, false);
                    Log.i(TAG, "Applied config (non-persistent) to subscription: " + subId);
                }
            } else {
                Log.i(TAG, "Config already up-to-date for subscription: " + subId);
            }

            Log.i(TAG, "IMS configuration applied successfully");
        } finally {
            // 停止权限委托
            am.stopDelegateShellPermissionIdentity();
        }
    }

    // Removed buildConfigBundle as we use custom overrides exclusively.

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
            Log.e(TAG, "Failed to apply custom overrides", e);
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
                // Ignore parsing errors
            }
        }
    }
}
