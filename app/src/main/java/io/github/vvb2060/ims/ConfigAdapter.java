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

public class ConfigAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_HEADER = 1;

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
                if (item instanceof MainActivity.PresetConfigItem && ((MainActivity.PresetConfigItem)item).definition.isHeader) {
                    continue; // Skip headers in search? Or maybe show if children match? For now skip.
                }
                if (item.key.toLowerCase().contains(query.toLowerCase())) {
                    filteredItems.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        ConfigItem item = filteredItems.get(position);
        if (item instanceof MainActivity.PresetConfigItem) {
            if (((MainActivity.PresetConfigItem) item).definition.isHeader) {
                return VIEW_TYPE_HEADER;
            }
        }
        return VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.config_list_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.config_list_item, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ConfigItem item = filteredItems.get(position);
        Context context = holder.itemView.getContext();

        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            MainActivity.PresetConfigItem preset = (MainActivity.PresetConfigItem) item;
            headerHolder.title.setText(preset.definition.titleRes);
            return;
        }

        ItemViewHolder itemHolder = (ItemViewHolder) holder;

        if (item instanceof MainActivity.PresetConfigItem) {
            MainActivity.PresetConfigItem preset = (MainActivity.PresetConfigItem) item;
            itemHolder.textKey.setText(preset.definition.titleRes);
            itemHolder.textValue.setText(preset.definition.descRes);
            if (itemHolder.icon != null) {
                itemHolder.icon.setVisibility(View.VISIBLE);
                itemHolder.icon.setImageResource(preset.definition.iconRes);
            }

            if (preset.defaultValue instanceof Boolean) {
                if (itemHolder.toggle != null) {
                    itemHolder.toggle.setVisibility(View.VISIBLE);
                    boolean isChecked = preset.isOverridden ? (Boolean) preset.overrideValue : (Boolean) preset.defaultValue;
                    itemHolder.toggle.setOnCheckedChangeListener(null);
                    itemHolder.toggle.setChecked(isChecked);
                    itemHolder.toggle.setOnCheckedChangeListener((v, checked) -> {
                        listener.onItemClick(item);
                    });
                    itemHolder.itemView.setOnClickListener(v -> itemHolder.toggle.toggle());
                }
            } else {
                if (itemHolder.toggle != null) itemHolder.toggle.setVisibility(View.GONE);
                itemHolder.textValue.setText(context.getString(preset.definition.descRes) + "\nValue: " + item.getValueString());
                itemHolder.itemView.setOnClickListener(v -> listener.onItemClick(item));
            }
        } else {
            itemHolder.itemView.setOnClickListener(v -> listener.onItemClick(item));
            // Standard/Advanced mode
            itemHolder.textKey.setText(item.key);
            itemHolder.textValue.setText(item.getValueString());
            if (itemHolder.icon != null) itemHolder.icon.setVisibility(View.GONE);
            if (itemHolder.toggle != null) itemHolder.toggle.setVisibility(View.GONE);
        }

        TypedValue typedValue = new TypedValue();
        if (item.isOverridden) {
            context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true);
            itemHolder.textKey.setTextColor(typedValue.data);
        } else {
            context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typedValue, true);
            itemHolder.textKey.setTextColor(typedValue.data);
        }
    }

    @Override
    public int getItemCount() {
        return filteredItems.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textKey;
        TextView textValue;
        ImageView icon;
        MaterialSwitch toggle;

        ItemViewHolder(View itemView) {
            super(itemView);
            textKey = itemView.findViewById(R.id.text_key);
            textValue = itemView.findViewById(R.id.text_value);
            icon = itemView.findViewById(R.id.item_icon);
            toggle = itemView.findViewById(R.id.item_switch);
            // Click listener set in onBindViewHolder
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        HeaderViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.header_title);
        }
    }
}
