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
import com.almi.juegaalmiapp.modelo.Game;
import com.bumptech.glide.Glide;
import java.util.List;

public class RecentGamesAdapter extends RecyclerView.Adapter<RecentGamesAdapter.RecentGameViewHolder> {
    private List<Game> recentGames;
    private Context context;

    public RecentGamesAdapter(List<Game> recentGames, Context context) {
        this.recentGames = recentGames;
        this.context = context;
    }

    @NonNull
    @Override
    public RecentGameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_game, parent, false);
        return new RecentGameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentGameViewHolder holder, int position) {
        Game game = recentGames.get(position);

        // Configurar el texto del título y descripción del juego reciente
        holder.titleTextView.setText(game.getName());
        holder.descriptionTextView.setText(game.getDescription());

        // Cargar la imagen del juego reciente usando Glide
        Glide.with(context)
                .load("https://juegalmiapp.duckdns.org" + game.getPicture())
                .placeholder(R.drawable.bioshock) // Imagen de respaldo
                .into(holder.gameImageView);

        // Configurar el click listener para abrir ProductDetailActivity
        holder.itemView.setOnClickListener(v -> {
            String productId = String.valueOf(game.getId()); // Obtener el ID del producto
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
        return recentGames.size();
    }

    public static class RecentGameViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView;
        ImageView gameImageView;

        public RecentGameViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.recent_game_title);
            descriptionTextView = itemView.findViewById(R.id.recent_game_description);
            gameImageView = itemView.findViewById(R.id.recent_game_image);
        }
    }
}
