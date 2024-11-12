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
import java.util.Map;

public class ConsolesFragment extends Fragment {
    private RecyclerView recyclerView;
    private ViewPager2 recentConsolesCarousel;
    private RecentConsolesAdapter recentConsolesAdapter;
    private CategoryAdapter categoryAdapter;
    private Map<String, List<Console>> categoryMap;
    private List<Console> recentConsoles;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consoles, container, false);

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

        return view;
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
