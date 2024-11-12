package com.almi.juegaalmiapp.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.almi.juegaalmiapp.R;
import com.almi.juegaalmiapp.adaptadores.OperacionAdapter;
import com.almi.juegaalmiapp.modelo.Operation;
import com.almi.juegaalmiapp.modelo.Pedido;

import java.util.ArrayList;
import java.util.List;

public class DetallePedidoFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView totalTextView;
    private OperacionAdapter adapter;
    private List<Operation> operations = new ArrayList<>();
    private double total;

    public static DetallePedidoFragment newInstance(Pedido pedido) {
        DetallePedidoFragment fragment = new DetallePedidoFragment();
        Bundle args = new Bundle();
        args.putSerializable("pedido", pedido);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalle_pedido, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Detalle del Pedido");
        toolbar.setTitleTextColor(getResources().getColor(R.color.black));
        toolbar.setNavigationIcon(R.drawable.salir); // Puedes usar un texto "X" si no tienes este ícono
        toolbar.setNavigationOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        recyclerView = view.findViewById(R.id.recyclerOperaciones);
        totalTextView = view.findViewById(R.id.totalPedido);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            Pedido pedido = (Pedido) getArguments().getSerializable("pedido");
            operations = pedido.getOperations();
            total = calcularTotal(operations);
            totalTextView.setText("Total: $" + total);
        }

        adapter = new OperacionAdapter(operations);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private double calcularTotal(List<Operation> operations) {
        double total = 0;
        for (Operation op : operations) {
            total += op.getCharge();
        }
        return total;
    }
}
