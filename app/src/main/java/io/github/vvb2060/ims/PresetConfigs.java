package io.github.vvb2060.ims;

import android.telephony.CarrierConfigManager;
import java.util.ArrayList;
import java.util.List;

public class PresetConfigs {
    public static List<ConfigDefinition> getPresets() {
        List<ConfigDefinition> list = new ArrayList<>();

        // VoLTE
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_CARRIER_VOLTE_AVAILABLE_BOOL,
            R.string.volte_avail_title, R.string.volte_avail_desc, R.drawable.ic_volte, true));
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_EDITABLE_ENHANCED_4G_LTE_BOOL,
            R.string.edit_enhanced_4g_title, R.string.edit_enhanced_4g_desc, R.drawable.ic_volte, true));
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_HIDE_ENHANCED_4G_LTE_BOOL,
            R.string.hide_enhanced_4g_title, R.string.hide_enhanced_4g_desc, R.drawable.ic_volte, false));
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_HIDE_LTE_PLUS_DATA_ICON_BOOL,
            R.string.hide_lte_plus_title, R.string.hide_lte_plus_desc, R.drawable.ic_volte, false));

        // VoWiFi
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_CARRIER_WFC_IMS_AVAILABLE_BOOL,
            R.string.vowifi_avail_title, R.string.vowifi_avail_desc, R.drawable.ic_vowifi, true));
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_CARRIER_WFC_SUPPORTS_WIFI_ONLY_BOOL,
            R.string.vowifi_wifi_only_title, R.string.vowifi_wifi_only_desc, R.drawable.ic_vowifi, true));
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_EDITABLE_WFC_MODE_BOOL,
            R.string.edit_wfc_mode_title, R.string.edit_wfc_mode_desc, R.drawable.ic_vowifi, true));
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_EDITABLE_WFC_ROAMING_MODE_BOOL,
            R.string.edit_wfc_roam_title, R.string.edit_wfc_roam_desc, R.drawable.ic_vowifi, true));
        list.add(new ConfigDefinition("show_wifi_calling_icon_in_status_bar_bool",
            R.string.show_vowifi_icon_title, R.string.show_vowifi_icon_desc, R.drawable.ic_vowifi, true));
        list.add(new ConfigDefinition("wfc_spn_format_idx_int",
            R.string.wfc_spn_idx_title, R.string.wfc_spn_idx_desc, R.drawable.ic_vowifi, 6));

        // VT
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_CARRIER_VT_AVAILABLE_BOOL,
            R.string.vt_avail_title, R.string.vt_avail_desc, R.drawable.ic_vt, true));

        // VoNR
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_VONR_ENABLED_BOOL,
            R.string.vonr_enabled_title, R.string.vonr_enabled_desc, R.drawable.ic_5g, true));
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_VONR_SETTING_VISIBILITY_BOOL,
            R.string.vonr_visibility_title, R.string.vonr_visibility_desc, R.drawable.ic_5g, true));

        // 5G
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_CARRIER_NR_AVAILABILITIES_INT_ARRAY,
            R.string.nr_avail_title, R.string.nr_avail_desc, R.drawable.ic_5g, new int[]{1, 2}));
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_5G_NR_SSRSRP_THRESHOLDS_INT_ARRAY,
            R.string.nr_thresholds_title, R.string.nr_thresholds_desc, R.drawable.ic_signal, new int[]{-128, -118, -108, -98}));
        list.add(new ConfigDefinition("additional_nr_advanced_bands_int_array",
            R.string.nr_adv_bands_title, R.string.nr_adv_bands_desc, R.drawable.ic_5g, new int[]{41, 78, 79}));
        list.add(new ConfigDefinition("unmetered_nr_sa_bool",
            R.string.unmetered_nr_title, R.string.unmetered_nr_desc, R.drawable.ic_5g, true));
        list.add(new ConfigDefinition("5g_icon_configuration_string",
            R.string.nr_icon_config_title, R.string.nr_icon_config_desc, R.drawable.ic_info, "connected_mmwave:5G_PLUS"));

        // QNS/Signal
        list.add(new ConfigDefinition("qns.minimum_handover_guarding_timer_ms_int",
            R.string.qns_timer_title, R.string.qns_timer_desc, R.drawable.ic_signal, 1000));
        list.add(new ConfigDefinition("qns.voice_ngran_ssrsrp_int_array",
            R.string.qns_rsrp_title, R.string.qns_rsrp_desc, R.drawable.ic_signal, new int[]{-120, -124}));
        list.add(new ConfigDefinition("qns.ho_restrict_time_with_low_rtp_quality_int_array",
            R.string.qns_ho_restrict_title, R.string.qns_ho_restrict_desc, R.drawable.ic_signal, new int[]{3000, 3000}));

        // GPS
        list.add(new ConfigDefinition("gps.normal_psds_server",
            R.string.gps_normal_title, R.string.gps_normal_desc, R.drawable.ic_gps, "gllto.glpals.com"));
        list.add(new ConfigDefinition("gps.longterm_psds_server_1",
            R.string.gps_long_title, R.string.gps_long_desc, R.drawable.ic_gps, "gllto.glpals.com"));

        // Extra
        list.add(new ConfigDefinition("imssms.sms_max_retry_over_ims_count_int",
            R.string.sms_retry_title, R.string.sms_retry_desc, R.drawable.ic_settings, 3));
        list.add(new ConfigDefinition("apn_expand_bool",
            R.string.apn_expand_title, R.string.apn_expand_desc, R.drawable.ic_settings, true));
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_CARRIER_CROSS_SIM_IMS_AVAILABLE_BOOL,
            R.string.cross_sim_title, R.string.cross_sim_desc, R.drawable.ic_sim, true));
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_ENABLE_CROSS_SIM_CALLING_ON_OPPORTUNISTIC_DATA_BOOL,
            R.string.cross_sim_opp_title, R.string.cross_sim_opp_desc, R.drawable.ic_sim, true));
        list.add(new ConfigDefinition(CarrierConfigManager.KEY_CARRIER_SUPPORTS_SS_OVER_UT_BOOL,
            R.string.ss_over_ut_title, R.string.ss_over_ut_desc, R.drawable.ic_settings, true));

        return list;
    }
}
