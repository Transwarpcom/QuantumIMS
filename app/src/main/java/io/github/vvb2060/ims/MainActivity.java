package io.github.vvb2060.ims;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import android.widget.EditText;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import org.json.JSONObject;

import rikka.shizuku.Shizuku;

public class MainActivity extends Activity {

    private static final String PREFS_NAME = "ims_config";
    private static final String CUSTOM_OVERRIDES_KEY = "custom_overrides";
    private static final String TAG = "IMS_MainActivity";

    private TextView tvAndroidVersion;
    private TextView tvShizukuStatus;
    private TextView tvPersistentWarning;
    private TextView tvSimInfo;
    private Button btnSelectSim;
    private Button btnSwitchLanguage;
    private Button btnApply;
    private RecyclerView configRecyclerView;
    private ConfigAdapter adapter;

    private SharedPreferences prefs;
    private int selectedSubId = 1; // 默认SIM 1, -1表示全部应用

    private final Shizuku.OnBinderReceivedListener binderListener = this::updateShizukuStatus;
    private final Shizuku.OnBinderDeadListener binderDeadListener = this::updateShizukuStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 应用保存的语言设置
        String language = LocaleHelper.getLanguage(this);
        LocaleHelper.updateResources(this, language);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        initViews();
        loadConfigItems();
        updateSimInfo();
        updateAndroidVersionInfo();
        updateShizukuStatus();

        Shizuku.addBinderReceivedListener(binderListener);
        Shizuku.addBinderDeadListener(binderDeadListener);

        if (getIntent().getBooleanExtra("config_applied", false)) {
            showNetworkSettingsDialog();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkActiveSims();
    }

    private void checkActiveSims() {
        if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // Can't check without permission, assume at least one might be there or user will find out
            return;
        }
        android.telephony.SubscriptionManager sm = getSystemService(android.telephony.SubscriptionManager.class);
        int count = sm.getActiveSubscriptionInfoCount();
        if (count == 0) {
            new AlertDialog.Builder(this)
                .setTitle(R.string.sim_card)
                .setMessage(R.string.no_sim_active_warning)
                .setPositiveButton(android.R.string.ok, null)
                .show();
            // Could disable buttons here
            btnApply.setEnabled(false);
            btnSelectSim.setEnabled(false);
            findViewById(R.id.btn_advanced_editor).setEnabled(false);
        } else {
            // Re-enable if previously disabled, but only if Shizuku is ready (handled by updateShizukuStatus)
            updateShizukuStatus();
            btnSelectSim.setEnabled(true);
            findViewById(R.id.btn_advanced_editor).setEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Shizuku.removeBinderReceivedListener(binderListener);
        Shizuku.removeBinderDeadListener(binderDeadListener);
    }

    private void initViews() {
        tvAndroidVersion = findViewById(R.id.tv_android_version);
        tvShizukuStatus = findViewById(R.id.tv_shizuku_status);
        tvPersistentWarning = findViewById(R.id.tv_persistent_warning);
        tvSimInfo = findViewById(R.id.tv_sim_info);
        btnSelectSim = findViewById(R.id.btn_select_sim);
        btnSwitchLanguage = findViewById(R.id.btn_switch_language);
        configRecyclerView = findViewById(R.id.configRecyclerView);
        configRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnApply = findViewById(R.id.btn_apply);
        btnApply.setOnClickListener(v -> applyConfiguration());

        findViewById(R.id.btn_advanced_editor).setOnClickListener(v -> showAdvancedEditorSimDialog());

        btnSelectSim.setOnClickListener(v -> showSimSelectionDialog());

        btnSwitchLanguage.setOnClickListener(v -> {
            LocaleHelper.toggleLanguage(this);
            recreate(); // 重新创建 Activity 以应用新语言
        });
    }

    private void loadConfigItems() {
        List<ConfigDefinition> definitions = PresetConfigs.getPresets();
        List<ConfigItem> items = new ArrayList<>();

        String json = prefs.getString(CUSTOM_OVERRIDES_KEY, "{}");
        JSONObject overrides = new JSONObject();
        try {
            overrides = new JSONObject(json);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (ConfigDefinition def : definitions) {
            ConfigItem item = new ConfigItem(def.key, def.defaultValue);
            // item.title = def.title; // ConfigItem needs title/desc/icon fields
            // Since ConfigItem is shared, I need to update it or create a subclass/wrapper.
            // For simplicity, let's update ConfigAdapter to handle ConfigDefinition lookup or extend ConfigItem.
            // Wait, ConfigItem is simple. Let's make ConfigAdapter robust or create a combined model.
            // Easier: Modify ConfigAdapter to accept ConfigDefinition list or enrich ConfigItem.
            // Let's assume I'll update ConfigAdapter to bind these.
            // Actually, I'll update ConfigItem to hold metadata if passed.

            // Check override
            if (overrides.has(def.key)) {
                try {
                    item.overrideValue = overrides.get(def.key);
                    item.isOverridden = true;
                } catch (Exception e) {}
            }
            items.add(item);
        }

        // We need an adapter that can display the rich UI (Icon, Title, Desc, Switch/Edit).
        // The existing ConfigAdapter is simple key-value.
        // I will create a new PresetAdapter in the next step or update ConfigAdapter.
        // For now, I'll instantiate ConfigAdapter and assume I will update it.
        // Wait, I am in 'Refactor MainActivity UI' step. I should have updated ConfigAdapter first or simultaneously.
        // I'll update ConfigAdapter in the next planned step. Here I just set it up.

        // I need to pass the definitions to the adapter so it can show titles/icons.
        // I'll assume ConfigAdapter will have a `setDefinitions(Map<String, ConfigDefinition>)` or similar.
        // Or I can subclass ConfigItem to PresetConfigItem extends ConfigItem.
        // Let's do that in memory here.
        List<ConfigItem> richItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            ConfigItem simple = items.get(i);
            ConfigDefinition def = definitions.get(i);
            PresetConfigItem rich = new PresetConfigItem(simple.key, simple.defaultValue, def);
            rich.overrideValue = simple.overrideValue;
            rich.isOverridden = simple.isOverridden;
            richItems.add(rich);
        }

        adapter = new ConfigAdapter(richItems, this::onPresetItemClick);
        configRecyclerView.setAdapter(adapter);
    }

    public static class PresetConfigItem extends ConfigItem {
        public ConfigDefinition definition;
        public PresetConfigItem(String key, Object defaultValue, ConfigDefinition definition) {
            super(key, defaultValue);
            this.definition = definition;
        }
    }

    private void onPresetItemClick(ConfigItem item) {
        if (item instanceof PresetConfigItem) {
            PresetConfigItem pItem = (PresetConfigItem) item;
            if ("virtual.wifi_country_code".equals(pItem.key)) {
                showCountrySelector(pItem);
                return;
            }

            if (pItem.defaultValue instanceof Boolean) {
                // Toggle
                boolean current = pItem.isOverridden ? (Boolean) pItem.overrideValue : (Boolean) pItem.defaultValue;
                saveOverride(item, String.valueOf(!current));
            } else {
                // Show edit dialog
                showEditDialog(item);
            }
        } else {
            showEditDialog(item);
        }
    }

    private void showCountrySelector(ConfigItem item) {
        String[] codes = java.util.Locale.getISOCountries();
        java.util.Arrays.sort(codes);

        String[] displayItems = new String[codes.length];
        for (int i = 0; i < codes.length; i++) {
            java.util.Locale l = new java.util.Locale("", codes[i]);
            displayItems[i] = codes[i] + " (" + l.getDisplayCountry() + ")";
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        if (item instanceof PresetConfigItem) {
            builder.setTitle(((PresetConfigItem)item).definition.titleRes);
        } else {
            builder.setTitle(item.key);
        }

        builder.setItems(displayItems, (dialog, which) -> {
            saveOverride(item, codes[which]);
        });
        builder.setNeutralButton("Reset", (dialog, which) -> {
            removeOverride(item);
        });
        builder.show();
    }

    private void showEditDialog(ConfigItem item) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
            .setTitle(item.key);

        if (item instanceof PresetConfigItem) {
            builder.setTitle(((PresetConfigItem)item).definition.titleRes);
        }

        final EditText input = new EditText(this);
        input.setText(item.getValueString());
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            saveOverride(item, input.getText().toString());
        });
        builder.setNegativeButton("Cancel", null);
        builder.setNeutralButton("Reset", (dialog, which) -> {
            removeOverride(item);
        });
        builder.show();
    }

    private void saveOverride(ConfigItem item, String newValue) {
        try {
            Object val = item.parse(newValue);
            item.overrideValue = val;
            item.isOverridden = true; // For boolean toggles, if we toggle away from default, it's an override.
            // Actually, if we toggle back to default, should we remove override?
            // "Allow users to configure each item independently".
            // If they toggle, we save the new state.

            adapter.notifyDataSetChanged();

            // Save to prefs
            String json = prefs.getString(CUSTOM_OVERRIDES_KEY, "{}");
            JSONObject overrides = new JSONObject(json);
            overrides.put(item.key, item.overrideValue);
            prefs.edit().putString(CUSTOM_OVERRIDES_KEY, overrides.toString()).apply();
        } catch (Exception e) {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeOverride(ConfigItem item) {
        item.isOverridden = false;
        item.overrideValue = null;
        adapter.notifyDataSetChanged();

        try {
            String json = prefs.getString(CUSTOM_OVERRIDES_KEY, "{}");
            JSONObject overrides = new JSONObject(json);
            overrides.remove(item.key);
            prefs.edit().putString(CUSTOM_OVERRIDES_KEY, overrides.toString()).apply();
        } catch (Exception e) {}
    }

    private void showSimSelectionDialog() {
        String[] items = {
            getString(R.string.sim_1),
            getString(R.string.sim_2),
            getString(R.string.apply_to_all_sims)
        };

        int selectedIndex = 0;
        if (selectedSubId == 1) {
            selectedIndex = 0;
        } else if (selectedSubId == 2) {
            selectedIndex = 1;
        } else if (selectedSubId == -1) {
            selectedIndex = 2;
        }

        new AlertDialog.Builder(this)
            .setTitle(R.string.select_sim)
            .setSingleChoiceItems(items, selectedIndex, (dialog, which) -> {
                if (which == 0) {
                    selectedSubId = 1;
                } else if (which == 1) {
                    selectedSubId = 2;
                } else {
                    selectedSubId = -1;
                }
                updateSimInfo();
                dialog.dismiss();
            })
            .setNegativeButton(android.R.string.cancel, null)
            .show();
    }

    private void updateSimInfo() {
        if (selectedSubId == 1) {
            tvSimInfo.setText(R.string.sim_1);
            btnApply.setText(R.string.apply_to_sim_1);
        } else if (selectedSubId == 2) {
            tvSimInfo.setText(R.string.sim_2);
            btnApply.setText(R.string.apply_to_sim_2);
        } else {
            tvSimInfo.setText(R.string.apply_to_all_sims);
            btnApply.setText(R.string.apply_to_all);
        }
    }

    // loadPreferences and savePreferences removed as we use direct overrides now.

    private void updateAndroidVersionInfo() {
        String version = String.format(getString(R.string.android_version),
                "Android " + Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ")");
        tvAndroidVersion.setText(version);

        // Check if it's QPR2 Beta 3 or higher (API 36+)
        if (Build.VERSION.SDK_INT >= 36) {
            tvPersistentWarning.setVisibility(View.VISIBLE);
        } else {
            tvPersistentWarning.setVisibility(View.GONE);
        }
    }

    private void updateShizukuStatus() {
        runOnUiThread(() -> {
            String statusText;
            int statusColor;

            if (!Shizuku.pingBinder()) {
                statusText = String.format(getString(R.string.shizuku_status),
                        getString(R.string.shizuku_not_running));
                statusColor = 0xFFFF0000;
                btnApply.setEnabled(false);
            } else if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
                statusText = String.format(getString(R.string.shizuku_status),
                        getString(R.string.shizuku_no_permission));
                statusColor = 0xFFFF9800;
                btnApply.setEnabled(false);
                requestShizukuPermission();
            } else {
                statusText = String.format(getString(R.string.shizuku_status),
                        getString(R.string.shizuku_ready));
                statusColor = 0xFF4CAF50;
                btnApply.setEnabled(true);
            }

            tvShizukuStatus.setText(statusText);
            tvShizukuStatus.setTextColor(statusColor);
        });
    }

    private void showAdvancedEditorSimDialog() {
        if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_PHONE_STATE}, 1001);
            return;
        }

        android.telephony.SubscriptionManager sm = getSystemService(android.telephony.SubscriptionManager.class);
        java.util.List<android.telephony.SubscriptionInfo> subs = sm.getActiveSubscriptionInfoList();
        if (subs == null || subs.isEmpty()) {
            Toast.makeText(this, R.string.no_sim_found, Toast.LENGTH_SHORT).show();
            return;
        }

        String[] items = new String[subs.size()];
        int[] subIds = new int[subs.size()];
        for (int i = 0; i < subs.size(); i++) {
            android.telephony.SubscriptionInfo info = subs.get(i);
            // Use DisplayName which usually contains Carrier Name. Fallback to carrier name if needed.
            CharSequence carrierName = info.getDisplayName();
            if (carrierName == null || carrierName.length() == 0) {
                carrierName = info.getCarrierName();
            }
            items[i] = String.format("%s (Slot %d)", carrierName, info.getSimSlotIndex() + 1);
            subIds[i] = info.getSubscriptionId();
        }

        new AlertDialog.Builder(this)
            .setTitle(R.string.select_sim)
            .setItems(items, (dialog, which) -> launchConfigEditor(subIds[which]))
            .show();
    }

    private void launchConfigEditor(int subId) {
        Intent intent = new Intent(this, ConfigEditorActivity.class);
        intent.putExtra("subId", subId);
        startActivity(intent);
    }

    private void requestShizukuPermission() {
        if (Shizuku.isPreV11()) {
            Toast.makeText(this, R.string.update_shizuku, Toast.LENGTH_LONG).show();
            return;
        }
        Shizuku.requestPermission(0);
    }

    private void applyConfiguration() {
        // savePreferences(); // Not needed as we save on item click

        if (!Shizuku.pingBinder()) {
            Toast.makeText(this, R.string.shizuku_not_running_msg, Toast.LENGTH_LONG).show();
            return;
        }

        if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, R.string.shizuku_no_permission_msg, Toast.LENGTH_LONG).show();
            requestShizukuPermission();
            return;
        }

        // 保存选中的 SubId 供 PrivilegedProcess 使用
        prefs.edit().putInt("selected_subid", selectedSubId).apply();

        // 使用原版的 Instrumentation 方式
        ShizukuProvider.startInstrument(this);
    }

    private void showNetworkSettingsDialog() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.config_applied)
            .setMessage(R.string.config_success_message)
            .setPositiveButton(R.string.go_to_network_settings, (dialog, which) -> {
                // 跳转到网络设置页面
                Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Unable to open network settings", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton(R.string.later, null)
            .show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        String language = LocaleHelper.getLanguage(newBase);
        LocaleHelper.updateResources(newBase, language);
        super.attachBaseContext(newBase);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showAdvancedEditorSimDialog();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
