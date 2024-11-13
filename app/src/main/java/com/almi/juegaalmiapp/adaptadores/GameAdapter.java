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

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {
    private List<Game> gameList;
    private Context context;

    public GameAdapter(List<Game> gameList, Context context) {
        this.gameList = gameList;
        this.context = context;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video_game, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game game = gameList.get(position);
        holder.nameTextView.setText(game.getName());
        holder.priceTextView.setText("€" + game.getPrice());

        Glide.with(context)
                .load("https://juegalmiapp.duckdns.org" + game.getPicture())
                .placeholder(R.drawable.bioshock)
                .into(holder.gameImageView);

        holder.itemView.setOnClickListener(v -> {
            String productId = String.valueOf(game.getId());
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
            ProductDetailFragment fragment = ProductDetailFragment.newInstance(productId);

            // Reemplazar el fragmento actual con el ProductDetailFragment
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public static class GameViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, priceTextView;
        ImageView gameImageView;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.game_name);
            priceTextView = itemView.findViewById(R.id.game_price);
            gameImageView = itemView.findViewById(R.id.game_image);
        }
    }
}
