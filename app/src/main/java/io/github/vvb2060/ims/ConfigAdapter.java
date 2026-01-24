package io.github.vvb2060.ims;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.util.TypedValue;
import android.widget.ImageView;
import com.google.android.material.materialswitch.MaterialSwitch;

public class ConfigAdapter extends RecyclerView.Adapter<ConfigAdapter.ViewHolder> {

    private List<ConfigItem> items;
    private List<ConfigItem> filteredItems;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ConfigItem item);
    }

    public ConfigAdapter(List<ConfigItem> items, OnItemClickListener listener) {
        this.items = items;
        this.filteredItems = new ArrayList<>(items);
        this.listener = listener;
    }

    public void filter(String query) {
        filteredItems.clear();
        if (query.isEmpty()) {
            filteredItems.addAll(items);
        } else {
            for (ConfigItem item : items) {
                if (item.key.toLowerCase().contains(query.toLowerCase())) {
                    filteredItems.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Decide layout?
        // Let's use config_list_item.xml but modify it to support icon and switch if needed.
        // Or create a new layout `config_list_item_rich.xml`.
        // Since I'm reusing ConfigAdapter for both Activities, let's check item type or just make the layout flexible.
        // I will assume config_list_item can handle it or I update it.
        // Actually, for "Presets", we want icon + title + desc + switch.
        // For "Advanced", we want Key + Value.
        // Let's check item type.
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.config_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConfigItem item = filteredItems.get(position);
        Context context = holder.itemView.getContext();

        if (item instanceof MainActivity.PresetConfigItem) {
            MainActivity.PresetConfigItem preset = (MainActivity.PresetConfigItem) item;
            holder.textKey.setText(preset.definition.titleRes);
            holder.textValue.setText(preset.definition.descRes);
            if (holder.icon != null) {
                holder.icon.setVisibility(View.VISIBLE);
                holder.icon.setImageResource(preset.definition.iconRes);
            }

            if (preset.defaultValue instanceof Boolean) {
                if (holder.toggle != null) {
                    holder.toggle.setVisibility(View.VISIBLE);
                    boolean isChecked = preset.isOverridden ? (Boolean) preset.overrideValue : (Boolean) preset.defaultValue;
                    holder.toggle.setOnCheckedChangeListener(null);
                    holder.toggle.setChecked(isChecked);
                    holder.toggle.setOnCheckedChangeListener((v, checked) -> {
                        // We need to trigger listener
                        listener.onItemClick(item); // This will toggle in MainActivity logic
                    });
                    // Disable click on item view if switch handles it? No, click anywhere to toggle is fine.
                    // But switch needs to be clickable.
                }
            } else {
                if (holder.toggle != null) holder.toggle.setVisibility(View.GONE);
                // Show current value for non-boolean?
                holder.textValue.setText(context.getString(preset.definition.descRes) + "\nValue: " + item.getValueString());
            }
        } else {
            // Standard/Advanced mode
            holder.textKey.setText(item.key);
            holder.textValue.setText(item.getValueString());
            if (holder.icon != null) holder.icon.setVisibility(View.GONE);
            if (holder.toggle != null) holder.toggle.setVisibility(View.GONE);
        }

        // Highlight overridden
        TypedValue typedValue = new TypedValue();
        if (item.isOverridden) {
            context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true);
            holder.textKey.setTextColor(typedValue.data);
        } else {
            context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true);
            holder.textKey.setTextColor(typedValue.data);
        }
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textKey;
        TextView textValue;
        ImageView icon;
        MaterialSwitch toggle;

        ViewHolder(View itemView) {
            super(itemView);
            textKey = itemView.findViewById(R.id.text_key);
            textValue = itemView.findViewById(R.id.text_value);
            icon = itemView.findViewById(R.id.item_icon); // Needs to be added to layout
            toggle = itemView.findViewById(R.id.item_switch); // Needs to be added to layout
            itemView.setOnClickListener(v -> {
                if (toggle != null && toggle.getVisibility() == View.VISIBLE) {
                    toggle.toggle();
                } else {
                    listener.onItemClick(filteredItems.get(getAdapterPosition()));
                }
            });
        }
    }
}
