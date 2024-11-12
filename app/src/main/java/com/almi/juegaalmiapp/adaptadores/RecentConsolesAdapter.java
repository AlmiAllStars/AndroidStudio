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

public class RecentConsolesAdapter extends RecyclerView.Adapter<RecentConsolesAdapter.ConsoleViewHolder> {

    private List<Console> recentConsoles;
    private Context context;

    public RecentConsolesAdapter(List<Console> recentConsoles, Context context) {
        this.recentConsoles = recentConsoles;
        this.context = context;
    }

    @NonNull
    @Override
    public ConsoleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_console, parent, false);
        return new ConsoleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsoleViewHolder holder, int position) {
        Console console = recentConsoles.get(position);
        holder.consoleTitle.setText(console.getName());
        holder.consoleDescription.setText(console.getDescription());

        Glide.with(context)
                .load("https://juegalmiapp.duckdns.org" + console.getPicture())
                .placeholder(R.drawable.consola06) // imagen de placeholder
                .into(holder.consoleImage);

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
        return recentConsoles.size();
    }

    static class ConsoleViewHolder extends RecyclerView.ViewHolder {
        ImageView consoleImage;
        TextView consoleTitle, consoleDescription;

        ConsoleViewHolder(View itemView) {
            super(itemView);
            consoleImage = itemView.findViewById(R.id.recent_console_image);
            consoleTitle = itemView.findViewById(R.id.recent_console_title);
            consoleDescription = itemView.findViewById(R.id.recent_console_description);
        }
    }
}
