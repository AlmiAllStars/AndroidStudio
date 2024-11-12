package com.almi.juegaalmiapp.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.almi.juegaalmiapp.ApiClient;
import com.almi.juegaalmiapp.ApiService;
import com.almi.juegaalmiapp.ClienteService;
import com.almi.juegaalmiapp.R;
import com.almi.juegaalmiapp.adaptadores.PedidoAdapter;
import com.almi.juegaalmiapp.modelo.Pedido;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ListaPedidosFragment extends Fragment {

    private RecyclerView recyclerView;
    private PedidoAdapter adapter;
    private ProgressBar progressBar;
    private List<Pedido> pedidos = new ArrayList<>();
    private ClienteService clienteServicio;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista_pedidos, container, false);

        recyclerView = view.findViewById(R.id.recyclerPedidos);
        progressBar = view.findViewById(R.id.progressBar);

        // Configura el Toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Mis Pedidos"); // Título
        toolbar.setTitleTextColor(getResources().getColor(R.color.black)); // Color del texto
        toolbar.setNavigationIcon(R.drawable.salir); // Icono de flecha de retroceso
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed()); // Acción de retroceso
        Objects.requireNonNull(requireActivity()).findViewById(R.id.toolbar).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.bottom_navigation).setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PedidoAdapter(pedidos, pedido -> abrirDetallePedido(pedido));
        recyclerView.setAdapter(adapter);

        // Inicializa ClienteServicio
        clienteServicio = new ClienteService(getContext());


        // Llamar a la función para obtener los pedidos
        cargarPedidos();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Volver a mostrar Toolbar y BottomNavigationView
        Objects.requireNonNull(requireActivity()).findViewById(R.id.toolbar).setVisibility(View.VISIBLE);
        requireActivity().findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);
    }

    private void cargarPedidos() {
        progressBar.setVisibility(View.VISIBLE);

        String token = "Bearer " + clienteServicio.getToken();

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.obtenerPedidos(token).enqueue(new Callback<List<Pedido>>() {
            @Override
            public void onResponse(Call<List<Pedido>> call, Response<List<Pedido>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    pedidos.clear();
                    pedidos.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    mostrarError("Error al obtener pedidos: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Pedido>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                mostrarError("Fallo en la red: " + t.getMessage());
            }
        });
    }

    private void mostrarError(String mensaje) {
        Toast.makeText(getContext(), mensaje, Toast.LENGTH_LONG).show();
    }

    private void abrirDetallePedido(Pedido pedido) {
        DetallePedidoFragment detalleFragment = DetallePedidoFragment.newInstance(pedido);
        requireActivity().getSupportFragmentManager()  // Accede al FragmentManager de la actividad principal
                .beginTransaction()
                .add(R.id.fragment_container, detalleFragment) // Añade el nuevo fragmento encima del actual
                .addToBackStack(null) // Permite regresar al fragmento anterior con el botón "X" o back
                .commit();
    }
}
