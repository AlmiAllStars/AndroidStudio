package com.almi.juegaalmiapp.fragmentos;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.almi.juegaalmiapp.MainActivity;
import android.Manifest;
import com.almi.juegaalmiapp.R;
import com.almi.juegaalmiapp.adaptadores.CategoryAdapter;
import com.almi.juegaalmiapp.adaptadores.RecentGamesAdapter;
import com.almi.juegaalmiapp.modelo.Game;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class GamesFragment extends Fragment {

    private RecyclerView recyclerView;  // Declarar el RecyclerView aquí
    private ViewPager2 recentGamesCarousel;
    private RecentGamesAdapter recentGamesAdapter;
    private CategoryAdapter categoryAdapter;
    private Map<String, List<Game>> categoryMap;
    private List<Game> recentGames;
    private List<Game> originalRecentGames;  // I
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private androidx.appcompat.widget.SearchView searchView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_games, container, false);
        searchView = view.findViewById(R.id.search_view_videojuegos);
        searchView.setQueryHint("Buscar en videojuegos");

        recentGamesCarousel = view.findViewById(R.id.recent_games_carousel);
        recentGames = new ArrayList<>();
        originalRecentGames = new ArrayList<>(); // I
        recentGamesAdapter = new RecentGamesAdapter(recentGames, getContext());
        recentGamesCarousel.setAdapter(recentGamesAdapter);

        // Inicializar el RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_games);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        categoryMap = new LinkedHashMap<>();
        categoryAdapter = new CategoryAdapter(categoryMap, getContext());
        recyclerView.setAdapter(categoryAdapter);

        fetchGames();


        ImageView micIcon = view.findViewById(R.id.mic_icon);

        micIcon.setOnClickListener(v -> iniciarReconocimientoVoz());


        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // No hacer nada al enviar
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    restaurarRecentGames(); // Restaurar juegos originales si el texto está vacío
                } else {
                    filtrarRecentGames(newText); // Filtrar juegos basados en la búsqueda
                }
                return true;
            }
        });

        return view;
    }

    private void checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }

    private void iniciarReconocimientoVoz() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        try {
            startActivityForResult(intent, REQUEST_RECORD_AUDIO_PERMISSION);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "Reconocimiento de voz no soportado en este dispositivo.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<String> resultados = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (resultados != null && !resultados.isEmpty()) {
                String textoReconocido = resultados.get(0);
                searchView.setQuery(textoReconocido, true); // Poner el texto en el SearchView
            }
        }
    }



    private void filtrarRecentGames(String query) {
        List<Game> filteredGames = new ArrayList<>();
        String lowerCaseQuery = query.toLowerCase();

        for (Map.Entry<String, List<Game>> entry : categoryMap.entrySet()) {
            for (Game game : entry.getValue()) {
                if (game.getName().toLowerCase().contains(lowerCaseQuery) ||
                        game.getDescription().toLowerCase().contains(lowerCaseQuery)) {
                    filteredGames.add(game);
                }
            }
        }

        recentGames.clear();
        recentGames.addAll(filteredGames);
        recentGamesAdapter.notifyDataSetChanged();

        // Ocultar el RecyclerView de géneros
        recyclerView.setVisibility(View.GONE);
    }


    private void restaurarRecentGames() {
        recentGames.clear();
        recentGames.addAll(originalRecentGames); // Restaurar los juegos originales
        recentGamesAdapter.notifyDataSetChanged();

        // Mostrar el RecyclerView de géneros
        recyclerView.setVisibility(View.VISIBLE);
    }


    private void fetchGames() {
        String url = "https://juegalmiapp.duckdns.org/juegalmi/ws/videogames";
        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        List<Game> games = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject gameObject = response.getJSONObject(i);
                            JSONArray genresArray = gameObject.has("genres") ? gameObject.getJSONArray("genres") : new JSONArray();
                            String[] genres = new String[genresArray.length()];
                            for (int j = 0; j < genresArray.length(); j++) {
                                genres[j] = genresArray.getString(j);
                            }
                            Game game = new Game(
                                    gameObject.optInt("id", 0),
                                    gameObject.optString("name", "Nombre no disponible"),
                                    gameObject.optString("description", "Descripción no disponible"),
                                    gameObject.optDouble("price", 0.0),
                                    genres,
                                    gameObject.optString("release_date", "Fecha no disponible"),
                                    gameObject.optInt("pegi", 0),
                                    gameObject.optString("picture", "imagen_default"),
                                    gameObject.optString("platform", "Plataforma no disponible"),
                                    gameObject.optInt("quantity", 0)
                            );

                            games.add(game);

                            for (String genre : genres) {
                                categoryMap.computeIfAbsent(genre, k -> new ArrayList<>()).add(game);
                            }
                        }

                        recentGames.clear();
                        recentGames.addAll(games.subList(0, Math.min(games.size(), 5)));
                        originalRecentGames.addAll(recentGames); // I
                        recentGamesAdapter.notifyDataSetChanged();
                        categoryAdapter.notifyDataSetChanged();


                    } catch (JSONException e) {
                        Log.e("GamesFragment", "JSON parsing error: ", e);
                        Toast.makeText(getContext(), "Error al parsear los datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("GamesFragment", "Error al cargar los datos: ", error);
                    Toast.makeText(getContext(), "Error al cargar los datos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        queue.add(jsonArrayRequest);
    }
}
