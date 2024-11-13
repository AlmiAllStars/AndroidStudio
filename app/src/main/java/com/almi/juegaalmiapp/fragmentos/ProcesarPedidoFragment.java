package com.almi.juegaalmiapp.fragmentos;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.almi.juegaalmiapp.ApiClient;
import com.almi.juegaalmiapp.ApiService;
import com.almi.juegaalmiapp.CarritoService;
import com.almi.juegaalmiapp.CartItem;
import com.almi.juegaalmiapp.R;
import com.almi.juegaalmiapp.modelo.CarritoItem;
import com.almi.juegaalmiapp.viewmodel.SharedViewModel;
import com.almi.juegaalmiapp.ClienteService;
import com.almi.juegaalmiapp.CarritoService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProcesarPedidoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProcesarPedidoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ClienteService clienteServicio;

    private CarritoService carritoService;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProcesarPedidoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProcesarPedidoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProcesarPedidoFragment newInstance(String param1, String param2) {
        ProcesarPedidoFragment fragment = new ProcesarPedidoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_procesar_pedido, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        Objects.requireNonNull(requireActivity()).findViewById(R.id.toolbar).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.bottom_navigation).setVisibility(View.GONE);

        // Configurar el botón "Cancelar" para cerrar el fragmento actual
        TextView cancelButton = toolbar.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack(); // Vuelve al fragmento anterior
        });

        Spinner direccionSpinner = view.findViewById(R.id.spinnerDireccion);
        Spinner metodoPagoSpinner = view.findViewById(R.id.spinnerMetodoPago);
        EditText nuevaDireccionInput = view.findViewById(R.id.etNuevaDireccion);
        clienteServicio = new ClienteService(getContext());
        carritoService = new CarritoService(getContext());

        setupSpinner(direccionSpinner, Arrays.asList("Urreta Kalea", "Nueva Dirección", "Recoger en tienda"));
        setupSpinner(metodoPagoSpinner, Arrays.asList("Tarjeta de Crédito", "PayPal", "Transferencia Bancaria"));

        // Mostrar el input "Nueva Dirección" si se selecciona esa opción
        direccionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) { // Nueva Dirección
                    nuevaDireccionInput.setVisibility(View.VISIBLE);
                } else {
                    nuevaDireccionInput.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Botones
        view.findViewById(R.id.buyNowButtonTop).setOnClickListener(v -> procesarCarrito());
        view.findViewById(R.id.buyNowButtonBottom).setOnClickListener(v -> procesarCarrito());

        EditText etNombre = view.findViewById(R.id.etNombre);
        EditText etTelefono = view.findViewById(R.id.etTelefono);
        EditText etCorreo = view.findViewById(R.id.etCorreo);

        // Obtén el SharedViewModel
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Observa los datos
        sharedViewModel.getName().observe(getViewLifecycleOwner(), nombre -> {
            String apellido = sharedViewModel.getSurname().getValue(); // Obtén el apellido actual
            if (nombre != null) {
                etNombre.setText(nombre + (apellido != null ? " " + apellido : ""));
            }
        });

        sharedViewModel.getSurname().observe(getViewLifecycleOwner(), apellido -> {
            String nombre = sharedViewModel.getName().getValue(); // Obtén el nombre actual
            if (apellido != null) {
                etNombre.setText((nombre != null ? nombre : "") + " " + apellido);
            }
        });

        sharedViewModel.getPhone().observe(getViewLifecycleOwner(), telefono -> {
            if (telefono != null) {
                etTelefono.setText(telefono);
            }
        });

        sharedViewModel.getEmail().observe(getViewLifecycleOwner(), correo -> {
            if (correo != null) {
                etCorreo.setText(correo);
            }
        });

        return view;
    }

    private void setupSpinner(Spinner spinner, List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                items
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Volver a mostrar Toolbar y BottomNavigationView
        Objects.requireNonNull(requireActivity()).findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        requireActivity().findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);
    }

    public void procesarCarrito() {
        List<CarritoItem> cartItems = carritoService.getCartItems();
        List<Map<String, Object>> operations = new ArrayList<>();

        for (CarritoItem item : cartItems) {
            Map<String, Object> operation = new HashMap<>();
            operation.put("id_product", item.getId());
            operation.put("type", item.getOperationType());

            if ("order".equals(item.getOperationType())) {
                operation.put("quantity", item.getCantidad());
            } else if ("rent".equals(item.getOperationType())) {
                operation.put("rental_time", 5);
                operation.put("price", 15.0);
            } else {
                operation.put("price", item.getPrice());
            }
            operations.add(operation);
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("worker_id", 1);
        requestBody.put("operations", operations);

        String token = "Bearer " + clienteServicio.getToken();
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Log.d("ProcesarCarrito", "Enviando: " + requestBody.toString());

        apiService.procesarCarrito(token, requestBody).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("ProcesarCarrito", "Operaciones procesadas correctamente");
                    carritoService.limpiarCarrito();

                    // Mostrar el toast
                    Toast.makeText(getContext(), "Pedido tramitado", Toast.LENGTH_SHORT).show();

                    // Cerrar el fragmento actual
                    requireActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Log.e("ProcesarCarrito", "Error al procesar: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ProcesarCarrito", "Fallo en la red: " + t.getMessage());
            }
        });
    }


}