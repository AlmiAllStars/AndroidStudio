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
import com.almi.juegaalmiapp.adaptadores.RecentDevicesAdapter;
import com.almi.juegaalmiapp.modelo.Device;
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

public class DevicesFragment extends Fragment {
    private RecyclerView recyclerView;
    private ViewPager2 recentDevicesCarousel;
    private RecentDevicesAdapter recentDevicesAdapter;
    private CategoryAdapter categoryAdapter;
    private Map<String, List<Device>> categoryMap;
    private List<Device> recentDevices;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private androidx.appcompat.widget.SearchView searchView;
    private List<Device> originalDevices;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devices, container, false);

        searchView = view.findViewById(R.id.search_view_devices);
        searchView.setQueryHint("Buscar en Dispositivos");

        recentDevicesCarousel = view.findViewById(R.id.recent_devices_carousel);
        recyclerView = view.findViewById(R.id.recycler_view_devices);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recentDevices = new ArrayList<>();
        recentDevicesAdapter = new RecentDevicesAdapter(recentDevices, getContext());
        recentDevicesCarousel.setAdapter(recentDevicesAdapter);

        categoryMap = new LinkedHashMap<>();
        categoryAdapter = new CategoryAdapter(categoryMap, getContext());
        recyclerView.setAdapter(categoryAdapter);

        fetchDevices();

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
                    restaurarDevices(); // Restaurar juegos originales si el texto está vacío
                } else {
                    filtrarDevices(newText); // Filtrar juegos basados en la búsqueda
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

    private void restaurarDevices() {
        recentDevices.clear();
        recentDevices.addAll(originalDevices); // Restaurar la lista original de devices
        recentDevicesAdapter.notifyDataSetChanged();

        // Mostrar la lista de devices
        recyclerView.setVisibility(View.VISIBLE);
    }


    private void filtrarDevices(String query) {
        List<Device> filteredDevices = new ArrayList<>();
        String lowerCaseQuery = query.toLowerCase();

        for (Device device : recentDevices) {
            if (device.getName().toLowerCase().contains(lowerCaseQuery) ||
                    device.getDescription().toLowerCase().contains(lowerCaseQuery)) {
                filteredDevices.add(device);
            }
        }

        recentDevices.clear();
        recentDevices.addAll(filteredDevices);
        recentDevicesAdapter.notifyDataSetChanged(); // Asegúrate de que devicesAdapter esté configurado

        // Ocultar el RecyclerView de consolas si está visible
        recyclerView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE); // Mostrar solo devices
    }


    private void fetchDevices() {
        String url = " https://juegalmiapp.duckdns.org/juegalmi/ws/devices";
        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        List<Device> devices = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject deviceObject = response.getJSONObject(i);

                            String category = deviceObject.getString("type");
                            Device device = new Device(
                                    deviceObject.getInt("id"),
                                    deviceObject.getString("name"),
                                    deviceObject.getString("description"),
                                    deviceObject.optString("picture", ""),
                                    category
                            );

                            devices.add(device);

                            categoryMap.computeIfAbsent(category, k -> new ArrayList<>()).add(device);
                        }

                        recentDevices.clear();
                        recentDevices.addAll(devices.subList(0, Math.min(devices.size(), 5)));
                        recentDevicesAdapter.notifyDataSetChanged();
                        originalDevices = new ArrayList<>(devices);

                        categoryAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e("DevicesFragment", "JSON parsing error: ", e);
                        Toast.makeText(getContext(), "Error al parsear los datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("DevicesFragment", "Error al cargar los datos: ", error);
                    Toast.makeText(getContext(), "Error al cargar los datos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(jsonArrayRequest);
    }
}
