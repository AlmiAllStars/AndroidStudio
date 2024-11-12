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

    public class RecentDevicesAdapter extends RecyclerView.Adapter<RecentDevicesAdapter.DeviceViewHolder> {

        private List<Device> recentDevices;
        private Context context;

        public RecentDevicesAdapter(List<Device> recentDevices, Context context) {
            this.recentDevices = recentDevices;
            this.context = context;
        }

        @NonNull
        @Override
        public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_recent_device, parent, false);
            return new DeviceViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
            Device device = recentDevices.get(position);
            holder.deviceTitle.setText(device.getName());
            holder.deviceDescription.setText(device.getDescription());

            Glide.with(context)
                    .load("https://juegalmiapp.duckdns.org" + device.getPicture())
                    .placeholder(R.drawable.device04)
                    .into(holder.deviceImage);

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
            return recentDevices.size();
        }

        static class DeviceViewHolder extends RecyclerView.ViewHolder {
            ImageView deviceImage;
            TextView deviceTitle, deviceDescription;

            DeviceViewHolder(View itemView) {
                super(itemView);
                deviceImage = itemView.findViewById(R.id.recent_device_image);
                deviceTitle = itemView.findViewById(R.id.recent_device_title);
                deviceDescription = itemView.findViewById(R.id.recent_device_description);
            }
        }
    }
