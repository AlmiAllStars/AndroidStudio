package com.almi.juegaalmiapp.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.almi.juegaalmiapp.R;
import com.almi.juegaalmiapp.fragmentos.ProductDetailFragment;
import com.almi.juegaalmiapp.modelo.Device;
import com.bumptech.glide.Glide;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {
    private List<Device> deviceList;
    private Context context;

    public DeviceAdapter(List<Device> deviceList, Context context) {
        this.deviceList = deviceList;
        this.context = context;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_device, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        Device device = deviceList.get(position);
        holder.nameTextView.setText(device.getName());
        holder.descriptionTextView.setText(device.getDescription());

        Glide.with(context)
                .load("https://juegalmiapp.duckdns.org" + device.getPicture())
                .placeholder(R.drawable.device04) // Imagen por defecto
                .into(holder.deviceImageView);

        holder.itemView.setOnClickListener(v -> {
            String productId = String.valueOf(device.getId()); // Obtener el ID del producto
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            ProductDetailFragment fragment = ProductDetailFragment.newInstance(productId);

            // Reemplazar el fragmento actual con el ProductDetailFragment
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)  // Asegúrate de tener un contenedor de fragmentos en tu layout
                    .addToBackStack(null) // Agregar a la pila para permitir navegación hacia atrás
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, descriptionTextView;
        ImageView deviceImageView;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.device_name);
            descriptionTextView = itemView.findViewById(R.id.device_description);
            deviceImageView = itemView.findViewById(R.id.device_image);
        }
    }
}
