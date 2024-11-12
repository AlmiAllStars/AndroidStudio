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
import java.util.Map;

public class DevicesFragment extends Fragment {
    private RecyclerView recyclerView;
    private ViewPager2 recentDevicesCarousel;
    private RecentDevicesAdapter recentDevicesAdapter;
    private CategoryAdapter categoryAdapter;
    private Map<String, List<Device>> categoryMap;
    private List<Device> recentDevices;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devices, container, false);

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

        return view;
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
