package com.almi.juegaalmiapp.fragmentos;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.almi.juegaalmiapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StoresFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng userLocation;
    private Store nearestStore;
    private TextView tvNearestStoreName;
    private TextView tvNearestStoreDistance;
    private Polyline routePolyline;

    private final LatLng sanIgnacioLocation = new LatLng(43.268667, -2.946021);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_stores, container, false);
        mapView = vista.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        tvNearestStoreName = vista.findViewById(R.id.tvNearestStoreName);
        tvNearestStoreDistance = vista.findViewById(R.id.tvNearestStoreDistance);

        Button btnFindNearestStore = vista.findViewById(R.id.btnFindNearestStore);
        Button btnOpenMapsCascoViejo = vista.findViewById(R.id.btnOpenMapsCascoViejo);
        Button btnOpenMapsGranVia = vista.findViewById(R.id.btnOpenMapsGranVia);
        Button btnOpenMapsDeusto = vista.findViewById(R.id.btnOpenMapsDeusto);

        btnOpenMapsCascoViejo.setOnClickListener(v -> openGoogleMaps(43.256960, -2.923441, "Tienda Casco Viejo"));
        btnOpenMapsGranVia.setOnClickListener(v -> openGoogleMaps(43.263012, -2.935112, "Tienda Gran Vía"));
        btnOpenMapsDeusto.setOnClickListener(v -> openGoogleMaps(43.271610, -2.946340, "Tienda Deusto"));

        try {
            MapsInitializer.initialize(requireActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        mapView.getMapAsync(this);

        btnFindNearestStore.setOnClickListener(v -> {
            if (userLocation != null && nearestStore != null) {
                drawRouteToNearestStore();
            } else {
                Toast.makeText(requireContext(), "Esperando ubicación", Toast.LENGTH_SHORT).show();
            }
        });

        return vista;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        userLocation = sanIgnacioLocation;
        googleMap.addMarker(new MarkerOptions().position(userLocation).title("Ubicación inicial (San Ignacio)"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f));

        findNearestStoreAndUpdateUI();

        Store[] stores = {
                new Store("Tienda Casco Viejo", "Calle del Perro, 1, 48005 Bilbao, Bizkaia", "944123456", "cascoviejo@juegalmi.com", 43.256960, -2.923441),
                new Store("Tienda Gran Vía", "Gran Vía de Don Diego López de Haro, 25, 48009 Bilbao, Bizkaia", "944654321", "granvia@juegalmi.com", 43.263012, -2.935112),
                new Store("Tienda Deusto", "Calle Lehendakari Aguirre, 29, 48014 Bilbao, Bizkaia", "944987654", "deusto@juegalmi.com", 43.271610, -2.946340)
        };

        for (Store store : stores) {
            LatLng position = new LatLng(store.lat, store.lng);
            googleMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(store.nombre)
                    .snippet("Teléfono: " + store.telefono + "\nEmail: " + store.email));
        }
    }

    private void findNearestStoreAndUpdateUI() {
        Store[] stores = {
                new Store("Tienda Casco Viejo", "Calle del Perro, 1, 48005 Bilbao, Bizkaia", "944123456", "cascoviejo@juegalmi.com", 43.256960, -2.923441),
                new Store("Tienda Gran Vía", "Gran Vía de Don Diego López de Haro, 25, 48009 Bilbao, Bizkaia", "944654321", "granvia@juegalmi.com", 43.263012, -2.935112),
                new Store("Tienda Deusto", "Calle Lehendakari Aguirre, 29, 48014 Bilbao, Bizkaia", "944987654", "deusto@juegalmi.com", 43.271610, -2.946340)
        };

        double minDistance = Double.MAX_VALUE;
        for (Store store : stores) {
            LatLng storeLocation = new LatLng(store.lat, store.lng);
            double distance = SphericalUtil.computeDistanceBetween(userLocation, storeLocation);

            if (distance < minDistance) {
                minDistance = distance;
                nearestStore = store;
            }
        }

        if (nearestStore != null) {
            double distanceInKm = minDistance / 1000;
            tvNearestStoreName.setText(nearestStore.nombre);
            tvNearestStoreDistance.setText("Distancia: " + String.format("%.1f", distanceInKm) + " km en coche");
        }
    }

    private void drawRouteToNearestStore() {
        if (userLocation != null && nearestStore != null) {
            String apiKey = getString(R.string.google_maps_key);
            String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + userLocation.latitude + "," + userLocation.longitude
                    + "&destination=" + nearestStore.lat + "," + nearestStore.lng + "&key=" + apiKey;
            new DirectionsTask().execute(url);
        }
    }

    private class DirectionsTask extends AsyncTask<String, Void, List<LatLng>> {
        @Override
        protected List<LatLng> doInBackground(String... params) {
            List<LatLng> path = new ArrayList<>();
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder jsonResponse = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonResponse.append(line);
                }
                reader.close();

                JSONObject jsonObject = new JSONObject(jsonResponse.toString());
                JSONArray routesArray = jsonObject.getJSONArray("routes");
                if (routesArray.length() > 0) {
                    JSONObject route = routesArray.getJSONObject(0);
                    JSONArray legs = route.getJSONArray("legs");
                    JSONArray steps = legs.getJSONObject(0).getJSONArray("steps");

                    for (int i = 0; i < steps.length(); i++) {
                        String polyline = steps.getJSONObject(i).getJSONObject("polyline").getString("points");
                        path.addAll(decodePolyline(polyline));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return path;
        }

        @Override
        protected void onPostExecute(List<LatLng> path) {
            if (routePolyline != null) routePolyline.remove();

            PolylineOptions polylineOptions = new PolylineOptions().addAll(path).color(0xFF0000FF).width(10);
            routePolyline = googleMap.addPolyline(polylineOptions);
            Toast.makeText(requireContext(), "Ruta trazada a: " + nearestStore.nombre, Toast.LENGTH_SHORT).show();
        }
    }

    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> polyline = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            polyline.add(new LatLng(lat / 1E5, lng / 1E5));
        }
        return polyline;
    }

    private void openGoogleMaps(double lat, double lng, String label) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + lat + "," + lng + "(" + Uri.encode(label) + ")");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(requireContext(), "Google Maps no está instalado", Toast.LENGTH_SHORT).show();
        }
    }

    class Store {
        String nombre;
        String direccion;
        String telefono;
        String email;
        double lat;
        double lng;

        public Store(String nombre, String direccion, String telefono, String email, double lat, double lng) {
            this.nombre = nombre;
            this.direccion = direccion;
            this.telefono = telefono;
            this.email = email;
            this.lat = lat;
            this.lng = lng;
        }
    }
}
