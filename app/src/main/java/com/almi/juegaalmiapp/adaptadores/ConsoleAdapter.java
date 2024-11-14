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
import com.almi.juegaalmiapp.modelo.Console;
import com.bumptech.glide.Glide;
import java.util.List;

public class ConsoleAdapter extends RecyclerView.Adapter<ConsoleAdapter.ConsoleViewHolder> {
    private List<Console> consoleList;
    private Context context;

    public ConsoleAdapter(List<Console> consoleList, Context context) {
        this.consoleList = consoleList;
        this.context = context;
    }

    @NonNull
    @Override
    public ConsoleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_console, parent, false);
        return new ConsoleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsoleViewHolder holder, int position) {
        Console console = consoleList.get(position);
        holder.nameTextView.setText(console.getName());
        holder.descriptionTextView.setText(console.getDescription());

        Glide.with(context)
                .load("https://retodalmi.duckdns.org" + console.getPicture())
                .placeholder(R.drawable.consola06)  // Imagen de placeholder predeterminada
                .into(holder.consoleImageView);

        holder.itemView.setOnClickListener(v -> {
            String productId = String.valueOf(console.getId()); // Obtener el ID del producto
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
        return consoleList.size();
    }

    public static class ConsoleViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, descriptionTextView;
        ImageView consoleImageView;

        public ConsoleViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.console_name);
            descriptionTextView = itemView.findViewById(R.id.console_description);
            consoleImageView = itemView.findViewById(R.id.console_image);
        }
    }
}
