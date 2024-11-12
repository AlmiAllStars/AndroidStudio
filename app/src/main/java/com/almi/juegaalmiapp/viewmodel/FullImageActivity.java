package com.almi.juegaalmiapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class FullImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        // Obtener la imagen de la intención
        ImageView fullImageView = findViewById(R.id.full_image_view);
        String imageUrl = getIntent().getStringExtra("IMAGE_URL");

        // Cargar la imagen usando Glide
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.bioshock) // Imagen de respaldo
                .error(R.drawable.bioshock)       // Imagen en caso de error
                .into(fullImageView);
    }
}
