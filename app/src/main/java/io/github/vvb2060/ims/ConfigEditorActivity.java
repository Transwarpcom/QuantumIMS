package io.github.vvb2060.ims;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.content.SharedPreferences;
import android.content.Context;
import org.json.JSONObject;

public class ConfigEditorActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ConfigAdapter adapter;
    private List<ConfigItem> allItems = new ArrayList<>();
    private int subId;
    private static final String PREFS_NAME = "ims_config";
    private static final String CUSTOM_OVERRIDES_KEY = "custom_overrides";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_editor);

        subId = getIntent().getIntExtra("subId", -1);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadConfig();
    }

    private void loadConfig() {
        CarrierConfigManager cm = getSystemService(CarrierConfigManager.class);
        PersistableBundle bundle = null;
        try {
            if (subId != -1) {
                bundle = cm.getConfigForSubId(subId);
            } else {
                bundle = cm.getConfig();
            }
        } catch (SecurityException e) {
            // Permission denied
            android.widget.Toast.makeText(this, "Access denied: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
            // Fallback: try to just create an empty list so it doesn't crash?
            // Or maybe just return?
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bundle == null) {
            // Handle null bundle (e.g. invalid subId or service not ready)
            return;
        }

        allItems.clear();
        for (String key : bundle.keySet()) {
            Object value = bundle.get(key);
            if (key != null) {
                allItems.add(new ConfigItem(key, value));
            }
        }
        Collections.sort(allItems, (a, b) -> {
            if (a.key == null) return -1;
            if (b.key == null) return 1;
            return a.key.compareToIgnoreCase(b.key);
        });

        // Load overrides
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(CUSTOM_OVERRIDES_KEY, "{}");
        try {
            JSONObject overrides = new JSONObject(json);
            for (ConfigItem item : allItems) {
                if (overrides.has(item.key)) {
                    item.overrideValue = overrides.get(item.key);
                    item.isOverridden = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter = new ConfigAdapter(allItems, this::onItemClick);
        recyclerView.setAdapter(adapter);
    }

    private void onItemClick(ConfigItem item) {
        // Show edit dialog based on type
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
            .setTitle(item.key);

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
        // Parse newValue based on type and save to prefs
        try {
            Object val = item.parse(newValue);
            item.overrideValue = val;
            item.isOverridden = true;
            adapter.notifyDataSetChanged();
            persistOverrides();
        } catch (Exception e) {
            // Show error
        }
    }

    private void removeOverride(ConfigItem item) {
        item.isOverridden = false;
        item.overrideValue = null;
        adapter.notifyDataSetChanged();
        persistOverrides();
    }

    private void persistOverrides() {
        try {
            JSONObject json = new JSONObject();
            for (ConfigItem item : allItems) {
                if (item.isOverridden) {
                    json.put(item.key, item.overrideValue);
                }
            }
            getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(CUSTOM_OVERRIDES_KEY, json.toString())
                .apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.config_editor_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText);
                return true;
            }
        });
        return true;
    }
}
