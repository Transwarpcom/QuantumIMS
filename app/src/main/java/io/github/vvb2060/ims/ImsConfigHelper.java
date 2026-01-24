package io.github.vvb2060.ims;

import android.app.IActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.os.ServiceManager;
import android.system.Os;
import android.telephony.CarrierConfigManager;
import android.util.Log;

import rikka.shizuku.ShizukuBinderWrapper;

public class ImsConfigHelper {

    private static final String TAG = "ImsConfigHelper";
    private static final String PREFS_NAME = "ims_config";

    public static void applyConfig(Context context, int subId) throws Exception {
        Log.i(TAG, "Starting to apply IMS configuration for subId: " + subId);

        // 获取 Shell 权限委托
        var binder = ServiceManager.getService(Context.ACTIVITY_SERVICE);
        var am = IActivityManager.Stub.asInterface(new ShizukuBinderWrapper(binder));
        am.startDelegateShellPermissionIdentity(Os.getuid(), null);

        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            boolean enableVoLTE = prefs.getBoolean("volte", true);
            boolean enableVoWiFi = prefs.getBoolean("vowifi", true);
            boolean enableVT = prefs.getBoolean("vt", true);
            boolean enableVoNR = prefs.getBoolean("vonr", true);
            boolean enableCrossSIM = prefs.getBoolean("cross_sim", true);
            boolean enableUT = prefs.getBoolean("ut", true);
            boolean enable5GNR = prefs.getBoolean("5g_nr", true);
            boolean enableSignalOpt = prefs.getBoolean("signal_opt", true);
            boolean enableGpsOpt = prefs.getBoolean("gps_opt", true);
            boolean enableIconOpt = prefs.getBoolean("icon_opt", true);
            boolean enableExtraOpt = prefs.getBoolean("extra_opt", true);

            var cm = context.getSystemService(CarrierConfigManager.class);
            var values = buildConfigBundle(enableVoLTE, enableVoWiFi, enableVT, enableVoNR,
                                           enableCrossSIM, enableUT, enable5GNR,
                                           enableSignalOpt, enableGpsOpt, enableIconOpt, enableExtraOpt);

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

    private static PersistableBundle buildConfigBundle(boolean enableVoLTE, boolean enableVoWiFi,
                                                        boolean enableVT, boolean enableVoNR,
                                                        boolean enableCrossSIM, boolean enableUT,
                                                        boolean enable5GNR,
                                                        boolean enableSignalOpt, boolean enableGpsOpt,
                                                        boolean enableIconOpt, boolean enableExtraOpt) {
        var bundle = new PersistableBundle();

        // VoLTE 配置
        if (enableVoLTE) {
            bundle.putBoolean(CarrierConfigManager.KEY_CARRIER_VOLTE_AVAILABLE_BOOL, true);
            bundle.putBoolean(CarrierConfigManager.KEY_EDITABLE_ENHANCED_4G_LTE_BOOL, true);
            bundle.putBoolean(CarrierConfigManager.KEY_HIDE_ENHANCED_4G_LTE_BOOL, false);
            bundle.putBoolean(CarrierConfigManager.KEY_HIDE_LTE_PLUS_DATA_ICON_BOOL, false);
        }

        // VT (视频通话) 配置
        if (enableVT) {
            bundle.putBoolean(CarrierConfigManager.KEY_CARRIER_VT_AVAILABLE_BOOL, true);
        }

        // UT 补充服务配置
        if (enableUT) {
            bundle.putBoolean(CarrierConfigManager.KEY_CARRIER_SUPPORTS_SS_OVER_UT_BOOL, true);
        }

        // 跨 SIM 通话配置
        if (enableCrossSIM) {
            bundle.putBoolean(CarrierConfigManager.KEY_CARRIER_CROSS_SIM_IMS_AVAILABLE_BOOL, true);
            bundle.putBoolean(CarrierConfigManager.KEY_ENABLE_CROSS_SIM_CALLING_ON_OPPORTUNISTIC_DATA_BOOL, true);
        }

        // VoWiFi 配置
        if (enableVoWiFi) {
            bundle.putBoolean(CarrierConfigManager.KEY_CARRIER_WFC_IMS_AVAILABLE_BOOL, true);
            bundle.putBoolean(CarrierConfigManager.KEY_CARRIER_WFC_SUPPORTS_WIFI_ONLY_BOOL, true);
            bundle.putBoolean(CarrierConfigManager.KEY_EDITABLE_WFC_MODE_BOOL, true);
            bundle.putBoolean(CarrierConfigManager.KEY_EDITABLE_WFC_ROAMING_MODE_BOOL, true);
            // KEY_SHOW_WIFI_CALLING_ICON_IN_STATUS_BAR_BOOL
            bundle.putBoolean("show_wifi_calling_icon_in_status_bar_bool", true);
            // KEY_WFC_SPN_FORMAT_IDX_INT
            bundle.putInt("wfc_spn_format_idx_int", 6);
        }

        // VoNR (5G 语音) 配置
        if (enableVoNR) {
            bundle.putBoolean(CarrierConfigManager.KEY_VONR_ENABLED_BOOL, true);
            bundle.putBoolean(CarrierConfigManager.KEY_VONR_SETTING_VISIBILITY_BOOL, true);
        }

        // 5G NR 配置
        if (enable5GNR) {
            bundle.putIntArray(CarrierConfigManager.KEY_CARRIER_NR_AVAILABILITIES_INT_ARRAY,
                    new int[]{CarrierConfigManager.CARRIER_NR_AVAILABILITY_NSA,
                            CarrierConfigManager.CARRIER_NR_AVAILABILITY_SA});
        }

        // 信号优化 (QNS + Signal Thresholds)
        if (enableSignalOpt) {
            bundle.putIntArray(CarrierConfigManager.KEY_5G_NR_SSRSRP_THRESHOLDS_INT_ARRAY,
                    // Boundaries: [-140 dBm, -44 dBm]
                    new int[]{
                            -128, /* SIGNAL_STRENGTH_POOR */
                            -118, /* SIGNAL_STRENGTH_MODERATE */
                            -108, /* SIGNAL_STRENGTH_GOOD */
                            -98,  /* SIGNAL_STRENGTH_GREAT */
                    });
            bundle.putInt("qns.minimum_handover_guarding_timer_ms_int", 1000);
            bundle.putIntArray("qns.voice_ngran_ssrsrp_int_array", new int[]{-120, -124});
            bundle.putIntArray("qns.ho_restrict_time_with_low_rtp_quality_int_array", new int[]{3000, 3000});
        }

        // GPS/定位优化
        if (enableGpsOpt) {
            bundle.putString("gps.normal_psds_server", "gllto.glpals.com");
            bundle.putString("gps.longterm_psds_server_1", "gllto.glpals.com");
        }

        // UI/图标增强
        if (enableIconOpt) {
            bundle.putString("5g_icon_configuration_string", "connected_mmwave:5G_PLUS");
            // China Mobile: n41, n79; Unicom/Telecom: n78
            bundle.putIntArray("additional_nr_advanced_bands_int_array", new int[]{41, 78, 79});
        }

        // 其他增强
        if (enableExtraOpt) {
            bundle.putInt("imssms.sms_max_retry_over_ims_count_int", 3);
            bundle.putBoolean("apn_expand_bool", true);
            // 5G SA Unmetered
            bundle.putBoolean("unmetered_nr_sa_bool", true);
        }

        return bundle;
    }
}
