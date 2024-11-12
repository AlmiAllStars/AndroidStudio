package com.almi.juegaalmiapp.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.almi.juegaalmiapp.R;
import com.almi.juegaalmiapp.modelo.ReparationItem;
import com.bumptech.glide.Glide;
import java.util.List;

public class ReparationsAdapter extends RecyclerView.Adapter<ReparationsAdapter.ReparationViewHolder> {

    public interface OnItemSelectListener {
        void onItemSelected(ReparationItem item);
    }

    private List<ReparationItem> reparationList;
    private Context context;
    private OnItemSelectListener listener;

    public ReparationsAdapter(List<ReparationItem> reparationList, Context context, OnItemSelectListener listener) {
        this.reparationList = reparationList;
        this.context = context;
        this.listener = listener;
    }

    public void updateData(List<ReparationItem> newData) {
        reparationList = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReparationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reparation, parent, false);
        return new ReparationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReparationViewHolder holder, int position) {
        ReparationItem item = reparationList.get(position);
        holder.titleTextView.setText(item.getTitle());
        holder.descriptionTextView.setText("Reparación disponible");

        Glide.with(context).load(item.getImageResId()).placeholder(R.drawable.bioshock).into(holder.imageView);

        holder.selectButton.setOnClickListener(v -> listener.onItemSelected(item));
    }

    @Override
    public int getItemCount() {
        return reparationList.size();
    }

    public static class ReparationViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView descriptionTextView;
        Button selectButton;

        public ReparationViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.reparation_image);
            titleTextView = itemView.findViewById(R.id.reparation_title);
            descriptionTextView = itemView.findViewById(R.id.reparation_description);
            selectButton = itemView.findViewById(R.id.select_button);
        }
    }
}
