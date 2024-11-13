package com.almi.juegaalmiapp.fragmentos;

import android.Manifest;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.almi.juegaalmiapp.R;
import com.almi.juegaalmiapp.adaptadores.CategoryAdapter;
import com.almi.juegaalmiapp.adaptadores.RecentConsolesAdapter;
import com.almi.juegaalmiapp.modelo.Console;
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

public class ConsolesFragment extends Fragment {
    private RecyclerView recyclerView;
    private ViewPager2 recentConsolesCarousel;
    private RecentConsolesAdapter recentConsolesAdapter;
    private CategoryAdapter categoryAdapter;
    private Map<String, List<Console>> categoryMap;
    private List<Console> recentConsoles;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private androidx.appcompat.widget.SearchView searchView;
    private List<Console> originalConsoles;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consoles, container, false);

        searchView = view.findViewById(R.id.search_view_consolas);
        searchView.setQueryHint("Buscar en consolas");

        recentConsolesCarousel = view.findViewById(R.id.recent_consoles_carousel);
        recyclerView = view.findViewById(R.id.recycler_view_consoles);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recentConsoles = new ArrayList<>();
        recentConsolesAdapter = new RecentConsolesAdapter(recentConsoles, getContext());
        recentConsolesCarousel.setAdapter(recentConsolesAdapter);

        categoryMap = new LinkedHashMap<>();
        categoryAdapter = new CategoryAdapter(categoryMap, getContext());
        recyclerView.setAdapter(categoryAdapter);

        fetchConsoles();

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
                    restaurarConsoles(); // Restaurar juegos originales si el texto está vacío
                } else {
                    filtrarConsoles(newText); // Filtrar juegos basados en la búsqueda
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

    private void filtrarConsoles(String query) {
        List<Console> filteredConsoles = new ArrayList<>();
        String lowerCaseQuery = query.toLowerCase();

        for (Console console : recentConsoles) {
            if (console.getName().toLowerCase().contains(lowerCaseQuery) ||
                    console.getDescription().toLowerCase().contains(lowerCaseQuery)) {
                filteredConsoles.add(console);
            }
        }

        recentConsoles.clear();
        recentConsoles.addAll(filteredConsoles);
        recentConsolesAdapter.notifyDataSetChanged(); // Asegúrate de que consolesAdapter esté configurado

        // Ocultar el RecyclerView de devices si está visible
        recyclerView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE); // Mostrar solo consolas
    }

    private void restaurarConsoles() {
        recentConsoles.clear();
        recentConsoles.addAll(originalConsoles); // Restaurar la lista original de consolas
        recentConsolesAdapter.notifyDataSetChanged();

        // Mostrar la lista de consolas
        recyclerView.setVisibility(View.VISIBLE);
    }


    private void fetchConsoles() {
        String url = "https://juegalmiapp.duckdns.org/juegalmi/ws/consoles";
        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        List<Console> consoles = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject consoleObject = response.getJSONObject(i);

                            String brand = consoleObject.getString("brand");
                            Console console = new Console(
                                    consoleObject.getInt("id"),
                                    consoleObject.getString("name"),
                                    consoleObject.getString("description"),
                                    consoleObject.getString("picture"),
                                    brand
                            );

                            consoles.add(console);

                            categoryMap.computeIfAbsent(brand, k -> new ArrayList<>()).add(console);
                        }

                        recentConsoles.clear();
                        recentConsoles.addAll(consoles.subList(0, Math.min(consoles.size(), 5)));
                        recentConsolesAdapter.notifyDataSetChanged();
                        originalConsoles = new ArrayList<>(consoles);

                        categoryAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e("ConsolesFragment", "JSON parsing error: ", e);
                        Toast.makeText(getContext(), "Error al parsear los datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("ConsolesFragment", "Error al cargar los datos: ", error);
                    Toast.makeText(getContext(), "Error al cargar los datos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(jsonArrayRequest);
    }
}
