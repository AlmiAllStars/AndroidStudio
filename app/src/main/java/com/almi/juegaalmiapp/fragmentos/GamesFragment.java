package com.almi.juegaalmiapp.fragmentos;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
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
import java.util.Map;

public class GamesFragment extends Fragment {
    private RecyclerView recyclerView;
    private ViewPager2 recentGamesCarousel;
    private RecentGamesAdapter recentGamesAdapter;
    private CategoryAdapter categoryAdapter;
    private Map<String, List<Game>> categoryMap;
    private List<Game> recentGames;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_games, container, false);

        androidx.appcompat.widget.SearchView searchView = view.findViewById(R.id.search_view_videojuegos);
        searchView.setQueryHint("Buscar en videojuegos");

        recentGamesCarousel = view.findViewById(R.id.recent_games_carousel);
        recyclerView = view.findViewById(R.id.recycler_view_games);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recentGames = new ArrayList<>();
        recentGamesAdapter = new RecentGamesAdapter(recentGames, getContext());
        recentGamesCarousel.setAdapter(recentGamesAdapter);

        categoryMap = new LinkedHashMap<>();
        categoryAdapter = new CategoryAdapter(categoryMap, getContext());
        recyclerView.setAdapter(categoryAdapter);

        fetchGames();

        return view;
    }

    private void fetchGames() {
        String url = "https://juegalmiapp.duckdns.org/juegalmi/ws/videogames";
        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    Log.d("GamesFragment", "JSON Response: " + response.toString()); // Verifica el JSON
                    try {
                        List<Game> games = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject gameObject = response.getJSONObject(i);

                            // Obtener el arreglo de géneros con manejo de errores
                            JSONArray genresArray = gameObject.has("genres") ? gameObject.getJSONArray("genres") : new JSONArray();
                            String[] genres = new String[genresArray.length()];
                            for (int j = 0; j < genresArray.length(); j++) {
                                genres[j] = genresArray.getString(j);
                            }

                            // Crear el objeto Game con manejo de valores faltantes
                            Game game = new Game(
                                    gameObject.has("id") ? gameObject.getInt("id") : 0,
                                    gameObject.has("name") ? gameObject.getString("name") : "Nombre no disponible",
                                    gameObject.has("description") ? gameObject.getString("description") : "Descripción no disponible",
                                    gameObject.has("price") ? gameObject.getDouble("price") : 0.0,
                                    genres,
                                    gameObject.has("release_date") ? gameObject.getString("release_date") : "Fecha no disponible",
                                    gameObject.has("pegi") ? gameObject.getInt("pegi") : 0,
                                    gameObject.has("picture") ? gameObject.getString("picture") : "imagen_default",
                                    gameObject.has("platform") ? gameObject.getString("platform") : "Plataforma no disponible",
                                    gameObject.has("quantity") ? gameObject.getInt("quantity") : 0
                            );

                            games.add(game);

                            for (String genre : genres) {
                                categoryMap.computeIfAbsent(genre, k -> new ArrayList<>()).add(game);
                            }
                        }

                        recentGames.clear();
                        recentGames.addAll(games.subList(0, Math.min(games.size(), 5)));
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
