package io.github.vvb2060.ims;

import android.telephony.CarrierConfigManager;
import java.util.ArrayList;
import java.util.List;

public class PresetConfigs {
    public static List<ConfigDefinition> getPresets() {
        List<ConfigDefinition> list = new ArrayList<>();

        // VoLTE
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_CARRIER_VOLTE_AVAILABLE_BOOL,
            "VoLTE Available", "Enable VoLTE", R.drawable.ic_volte, true));
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_EDITABLE_ENHANCED_4G_LTE_BOOL,
            "Editable Enhanced 4G LTE", "Allow editing Enhanced 4G LTE toggle", R.drawable.ic_volte, true));
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_HIDE_ENHANCED_4G_LTE_BOOL,
            "Hide Enhanced 4G LTE", "Hide Enhanced 4G LTE toggle", R.drawable.ic_volte, false));
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_HIDE_LTE_PLUS_DATA_ICON_BOOL,
            "Hide LTE+ Icon", "Hide LTE+ data icon", R.drawable.ic_volte, false));

        // VoWiFi
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_CARRIER_WFC_IMS_AVAILABLE_BOOL,
            "VoWiFi Available", "Enable VoWiFi", R.drawable.ic_vowifi, true));
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_CARRIER_WFC_SUPPORTS_WIFI_ONLY_BOOL,
            "VoWiFi Supports WiFi Only", "Support WiFi only mode", R.drawable.ic_vowifi, true));
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_EDITABLE_WFC_MODE_BOOL,
            "Editable WFC Mode", "Allow editing WFC mode", R.drawable.ic_vowifi, true));
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_EDITABLE_WFC_ROAMING_MODE_BOOL,
            "Editable WFC Roaming Mode", "Allow editing WFC roaming mode", R.drawable.ic_vowifi, true));
        list.add(new ConfigDefinition("show_wifi_calling_icon_in_status_bar_bool",
            "Show VoWiFi Icon", "Show icon in status bar", R.drawable.ic_vowifi, true));
        list.add(new ConfigDefinition("wfc_spn_format_idx_int",
            "WFC SPN Format Index", "SPN display format index", R.drawable.ic_vowifi, 6));

        // VT
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_CARRIER_VT_AVAILABLE_BOOL,
            "Video Calling Available", "Enable Video Calling", R.drawable.ic_vt, true));

        // VoNR
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_VONR_ENABLED_BOOL,
            "VoNR Enabled", "Enable Voice over NR", R.drawable.ic_5g, true));
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_VONR_SETTING_VISIBILITY_BOOL,
            "VoNR Setting Visibility", "Show VoNR setting", R.drawable.ic_5g, true));

        // 5G
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_CARRIER_NR_AVAILABILITIES_INT_ARRAY,
            "NR Availabilities", "NSA/SA Availability [1, 2]", R.drawable.ic_5g, new int[]{1, 2}));
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_5G_NR_SSRSRP_THRESHOLDS_INT_ARRAY,
            "5G NR Signal Thresholds", "Signal bar thresholds", R.drawable.ic_signal, new int[]{-128, -118, -108, -98}));
        list.add(new ConfigDefinition("additional_nr_advanced_bands_int_array",
            "Advanced NR Bands", "5G+ Bands [41, 78, 79]", R.drawable.ic_5g, new int[]{41, 78, 79}));
        list.add(new ConfigDefinition("unmetered_nr_sa_bool",
            "Unmetered NR SA", "Treat 5G SA as unmetered", R.drawable.ic_5g, true));
        list.add(new ConfigDefinition("5g_icon_configuration_string",
            "5G Icon Configuration", "Icon config string", R.drawable.ic_info, "connected_mmwave:5G_PLUS"));

        // QNS/Signal
        list.add(new ConfigDefinition("qns.minimum_handover_guarding_timer_ms_int",
            "QNS Handover Guard Timer", "Minimum handover timer (ms)", R.drawable.ic_signal, 1000));
        list.add(new ConfigDefinition("qns.voice_ngran_ssrsrp_int_array",
            "QNS Voice NR RSRP", "Voice NR RSRP Thresholds", R.drawable.ic_signal, new int[]{-120, -124}));
        list.add(new ConfigDefinition("qns.ho_restrict_time_with_low_rtp_quality_int_array",
            "QNS HO Restrict Time", "Handover restrict time", R.drawable.ic_signal, new int[]{3000, 3000}));

        // GPS
        list.add(new ConfigDefinition("gps.normal_psds_server",
            "GPS Normal PSDS Server", "Broadcom PSDS Server URL", R.drawable.ic_gps, "gllto.glpals.com"));
        list.add(new ConfigDefinition("gps.longterm_psds_server_1",
            "GPS Longterm PSDS Server", "Broadcom Longterm Server URL", R.drawable.ic_gps, "gllto.glpals.com"));

        // Extra
        list.add(new ConfigDefinition("imssms.sms_max_retry_over_ims_count_int",
            "SMS Max Retry", "Max SMS retry count over IMS", R.drawable.ic_settings, 3));
        list.add(new ConfigDefinition("apn_expand_bool",
            "APN Expand", "Unlock APN editing", R.drawable.ic_settings, true));
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_CARRIER_CROSS_SIM_IMS_AVAILABLE_BOOL,
            "Cross SIM IMS", "Enable Cross SIM calling", R.drawable.ic_sim, true));
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_ENABLE_CROSS_SIM_CALLING_ON_OPPORTUNISTIC_DATA_BOOL,
            "Cross SIM Opportunistic", "Enable on opportunistic data", R.drawable.ic_sim, true));
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_CARRIER_SUPPORTS_SS_OVER_UT_BOOL,
            "SS Over UT", "Enable Supplementary Services over UT", R.drawable.ic_settings, true));

        return list;
    }
}
