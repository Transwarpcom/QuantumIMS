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
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.config_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConfigItem item = filteredItems.get(position);
        holder.textKey.setText(item.key);
        holder.textValue.setText(item.getValueString());

        // Highlight overridden values using theme colors if possible, or explicit colors
        Context context = holder.itemView.getContext();
        if (item.isOverridden) {
            holder.textKey.setTextColor(context.getColor(android.R.color.holo_blue_light)); // Or use a theme attribute resolving logic
        } else {
            // Reset to default color (using what was defined in XML)
            // Ideally retrieve from XML default, but setting to ?attr/colorOnSurface via code is tricky without helper
            // We'll rely on recreation/rebinding resetting it usually, but safer to set explicitly if we had color refs
            // For simplicity in this environment:
            holder.textKey.setTextColor(context.getResources().getColor(android.R.color.black, null)); // Fallback
            // Better: parse attr. But let's assume default is black/white
            // Actually, let's just use the view's default color logic or a specific color resource.
            // Using logic:
            TypedValue typedValue = new TypedValue();
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

        ViewHolder(View itemView) {
            super(itemView);
            textKey = itemView.findViewById(R.id.text_key);
            textValue = itemView.findViewById(R.id.text_value);
            itemView.setOnClickListener(v -> listener.onItemClick(filteredItems.get(getAdapterPosition())));
        }
    }
}
