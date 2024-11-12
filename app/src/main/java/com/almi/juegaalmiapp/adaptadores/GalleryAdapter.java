package com.almi.juegaalmiapp.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.almi.juegaalmiapp.R;
import com.almi.juegaalmiapp.fragmentos.IVDialogFragment;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {

    private List<Integer> imageList;
    private List<String> titleList;

    public GalleryAdapter(List<Integer> imageList, List<String> titleList) {
        this.imageList = imageList;
        this.titleList = titleList;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gallery, parent, false);
        return new GalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        holder.imageView.setImageResource(imageList.get(position));
        holder.titleView.setText(titleList.get(position));

        holder.imageView.setOnClickListener(v -> {

            IVDialogFragment dialog = new IVDialogFragment(position, imageList);
            dialog.show(((AppCompatActivity) holder.itemView.getContext()).getSupportFragmentManager(), "imageViewer");
        });

    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleView;

        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view_gallery);
            titleView = itemView.findViewById(R.id.image_title);
        }
    }
}