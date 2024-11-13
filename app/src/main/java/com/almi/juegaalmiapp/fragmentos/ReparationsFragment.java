package com.almi.juegaalmiapp.fragmentos;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.almi.juegaalmiapp.R;
import com.almi.juegaalmiapp.adaptadores.ActiveRepairsAdapter;
import com.almi.juegaalmiapp.adaptadores.ReparationsAdapter;
import com.almi.juegaalmiapp.modelo.ActiveReparation;
import com.almi.juegaalmiapp.modelo.ReparationItem;
import com.almi.juegaalmiapp.ApiClient;
import com.almi.juegaalmiapp.ApiService;
import com.almi.juegaalmiapp.ClienteService;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReparationsFragment extends Fragment implements ReparationsAdapter.OnItemSelectListener {

    private RecyclerView recyclerView;
    private ReparationsAdapter adapter;
    private List<ReparationItem> consoleRepairs;
    private List<ReparationItem> deviceRepairs;
    private View layoutSelectorModelo;
    private Spinner spinnerModelSelection;
    private ViewGroup containerDetallesReparacion;
    private RecyclerView recyclerViewActiveRepairs;
    private ActiveRepairsAdapter activeRepairsAdapter;
    private ClienteService clienteServicio;
    private Button btnSolicitarReparacion;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reparations, container, false);
        clienteServicio = new ClienteService(getContext());
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        recyclerView = view.findViewById(R.id.recycler_view_reparations);
        layoutSelectorModelo = view.findViewById(R.id.layout_selector_modelo);
        spinnerModelSelection = view.findViewById(R.id.spinner_model_selection);
        containerDetallesReparacion = view.findViewById(R.id.container_detalles_reparacion);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerViewActiveRepairs = view.findViewById(R.id.recycler_view_active_repairs);
        recyclerViewActiveRepairs.setLayoutManager(new LinearLayoutManager(getContext()));

        fetchActiveRepairs();

        tabLayout.addTab(tabLayout.newTab().setText("CONSOLAS"));
        tabLayout.addTab(tabLayout.newTab().setText("DISPOSITIVOS"));

        setupData();

        adapter = new ReparationsAdapter(consoleRepairs, getContext(), this);
        recyclerView.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    adapter.updateData(consoleRepairs);
                } else {
                    adapter.updateData(deviceRepairs);
                }
                layoutSelectorModelo.setVisibility(View.GONE);
                containerDetallesReparacion.setVisibility(View.GONE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        return view;
    }

    private void setupData() {
        consoleRepairs = new ArrayList<>();
        deviceRepairs = new ArrayList<>();

        consoleRepairs.add(new ReparationItem("PlayStation", R.drawable.consola06));
        consoleRepairs.add(new ReparationItem("Xbox", R.drawable.consola06));
        consoleRepairs.add(new ReparationItem("Nintendo", R.drawable.consola06));
        consoleRepairs.add(new ReparationItem("Periféricos", R.drawable.consola06));

        deviceRepairs.add(new ReparationItem("Móviles iPhone", R.drawable.device04));
        deviceRepairs.add(new ReparationItem("Móviles Android", R.drawable.device04));
        deviceRepairs.add(new ReparationItem("Tablets", R.drawable.device04));
        deviceRepairs.add(new ReparationItem("Portátiles", R.drawable.device04));
    }

    private void fetchActiveRepairs() {
        String token = "Bearer " + clienteServicio.getToken();

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.obtenerReparacionesActivas(token).enqueue(new Callback<List<ActiveReparation>>() {
            @Override
            public void onResponse(@NonNull Call<List<ActiveReparation>> call, @NonNull Response<List<ActiveReparation>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ActiveReparation> activeRepairs = response.body();
                    activeRepairsAdapter = new ActiveRepairsAdapter(activeRepairs, getContext());
                    recyclerViewActiveRepairs.setAdapter(activeRepairsAdapter);
                } else {
                    Log.e("ReparationsFragment", "Error en la respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ActiveReparation>> call, @NonNull Throwable t) {
                Log.e("ReparationsFragment", "Fallo en la llamada: " + t.getMessage());
            }
        });
    }
    private void enviarReparacion(String detalleProblema) {
        // Crear el cuerpo de la solicitud
        Map<String, String> reparacionData = new HashMap<>();
        reparacionData.put("description", detalleProblema);

        // Obtener el token desde ClienteService
        String token = "Bearer " + clienteServicio.getToken();

        // Llamada Retrofit para crear la reparación
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.crearReparacion(token, reparacionData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("ReparationsFragment", "Reparación creada exitosamente.");
                    // Recargar la lista de reparaciones activas
                    fetchActiveRepairs();
                } else {
                    Log.e("ReparationsFragment", "Error al crear reparación: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.e("ReparationsFragment", "Fallo en la red: " + t.getMessage());
            }
        });
    }


    @Override
    public void onItemSelected(ReparationItem item) {
        layoutSelectorModelo.setVisibility(View.VISIBLE);
        containerDetallesReparacion.setVisibility(View.GONE);

        List<String> modelos = new ArrayList<>();
        modelos.add("Seleccione un modelo");

        if (item.getTitle().contains("PlayStation")) {
            modelos.add("PlayStation 5");
            modelos.add("PlayStation 4");
        } else if (item.getTitle().contains("Xbox")) {
            modelos.add("Xbox Series X");
            modelos.add("Xbox One");
        } else if (item.getTitle().contains("Nintendo")) {
            modelos.add("Nintendo Switch");
        } else if (item.getTitle().contains("Periféricos")) {
            modelos.add("Mando");
            modelos.add("Auriculares");
        } else if (item.getTitle().contains("Móviles iPhone")) {
            modelos.add("iPhone 12");
            modelos.add("iPhone 11");
        } else if (item.getTitle().contains("Móviles Android")) {
            modelos.add("Samsung Galaxy S21");
            modelos.add("Samsung Galaxy S20");
        } else if (item.getTitle().contains("Tablets")) {
            modelos.add("iPad");
            modelos.add("Tablet Samsung");
        } else if (item.getTitle().contains("Portátiles")) {
            modelos.add("MacBook Pro");
            modelos.add("Portátil HP");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, modelos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerModelSelection.setAdapter(adapter);

        spinnerModelSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    containerDetallesReparacion.setVisibility(View.GONE);
                } else {
                    View detallesReparacionView = getLayoutInflater().inflate(R.layout.card_detalles_reparacion, containerDetallesReparacion, false);
                    containerDetallesReparacion.removeAllViews();
                    containerDetallesReparacion.addView(detallesReparacionView);
                    containerDetallesReparacion.setVisibility(View.VISIBLE);
                    Button btnSolicitarReparacion = detallesReparacionView.findViewById(R.id.btn_enviar_reparacion);

                    Spinner spinnerGravedad = detallesReparacionView.findViewById(R.id.spinner_gravedad);
                    List<String> opcionesGravedad = new ArrayList<>();
                    opcionesGravedad.add("Seleccione una opción");
                    opcionesGravedad.add("Baja");
                    opcionesGravedad.add("Media");
                    opcionesGravedad.add("Alta");

                    ArrayAdapter<String> gravedadAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, opcionesGravedad);
                    gravedadAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerGravedad.setAdapter(gravedadAdapter);

                    EditText detalleProblemaInput = detallesReparacionView.findViewById(R.id.et_descripcion_problema);
                    btnSolicitarReparacion.setOnClickListener(v -> {
                        String detalleProblema = detalleProblemaInput.getText().toString();
                        if (!detalleProblema.isEmpty()) {
                            enviarReparacion(detalleProblema);
                        } else {
                            Toast.makeText(getContext(), "Por favor, describe el problema.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    spinnerGravedad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


    }
}
