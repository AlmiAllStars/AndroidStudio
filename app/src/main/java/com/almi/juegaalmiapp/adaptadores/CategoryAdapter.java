package com.almi.juegaalmiapp.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.almi.juegaalmiapp.R;
import com.almi.juegaalmiapp.modelo.Console;
import com.almi.juegaalmiapp.modelo.Device;
import com.almi.juegaalmiapp.modelo.Game;
import java.util.List;
import java.util.Map;

public class CategoryAdapter<T> extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private Map<String, List<T>> categoryMap;
    private Context context;

    public CategoryAdapter(Map<String, List<T>> categoryMap, Context context) {
        this.categoryMap = categoryMap;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = (String) categoryMap.keySet().toArray()[position];
        List<T> items = categoryMap.get(category);

        holder.categoryTitle.setText(category);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return items != null && items.size() > 3;
            }
        };
        holder.recyclerViewItems.setLayoutManager(layoutManager);
        holder.recyclerViewItems.setHasFixedSize(true);
        holder.recyclerViewItems.setNestedScrollingEnabled(false);

        if (items != null && !items.isEmpty()) {
            if (items.get(0) instanceof Game) {
                GameAdapter gameAdapter = new GameAdapter((List<Game>) items, context);
                holder.recyclerViewItems.setAdapter(gameAdapter);
            } else if (items.get(0) instanceof Console) {
                ConsoleAdapter consoleAdapter = new ConsoleAdapter((List<Console>) items, context);
                holder.recyclerViewItems.setAdapter(consoleAdapter);
            } else if (items.get(0) instanceof Device) {
                DeviceAdapter deviceAdapter = new DeviceAdapter((List<Device>) items, context);
                holder.recyclerViewItems.setAdapter(deviceAdapter);
            }
        }
    }

    @Override
    public int getItemCount() {
        return categoryMap.size();
    }

    public void setCategoryMap(Map<String, List<T>> newCategoryMap) {
        this.categoryMap = newCategoryMap;
        notifyDataSetChanged();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTitle;
        RecyclerView recyclerViewItems;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.category_title);
            recyclerViewItems = itemView.findViewById(R.id.recycler_view_category);
        }
    }
}
