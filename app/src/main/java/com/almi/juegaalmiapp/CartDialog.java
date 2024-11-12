package com.almi.juegaalmiapp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.almi.juegaalmiapp.adaptadores.CartAdapter;
import com.almi.juegaalmiapp.fragmentos.ProcesarPedidoFragment;
import com.almi.juegaalmiapp.modelo.CarritoItem;

public class CartDialog extends Dialog {

    private CarritoService carritoService;  // Referencia a CarritoService
    private CartAdapter adapter;

    public CartDialog(@NonNull Context context) {
        super(context);
        this.carritoService = new CarritoService(context);  // Inicializar CarritoService
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_cart);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_cart);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Context context = getContext();
        if (context instanceof FragmentActivity) {
            FragmentActivity activity = (FragmentActivity) context;
            adapter = new CartAdapter(carritoService, this::updateTotal,  activity, this);  // Pasar CarritoService al adaptador
            recyclerView.setAdapter(adapter);
        } else if (context instanceof ContextThemeWrapper) {
            Context baseContext = ((ContextThemeWrapper) context).getBaseContext();
            if (baseContext instanceof FragmentActivity) {
                FragmentActivity activity = (FragmentActivity) baseContext;
                adapter = new CartAdapter(carritoService, this::updateTotal,  activity, this);
                recyclerView.setAdapter(adapter);
            } else {
                Log.e("CartDialog", "El contexto base no es una instancia de FragmentActivity.");
            }
        } else {
            Log.e("CartDialog", "El contexto no es válido.");
        }

        findViewById(R.id.button_checkout).setOnClickListener(v -> {
            Context context2 = getContext();

            // Verificar si el contexto es un ContextThemeWrapper y obtener su base
            if (context2 instanceof ContextThemeWrapper) {
                context2 = ((ContextThemeWrapper) context2).getBaseContext();
            }

            if (context2 instanceof FragmentActivity) {
                FragmentActivity activity = (FragmentActivity) context2;
                ProcesarPedidoFragment fragment = new ProcesarPedidoFragment();

                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();

                dismiss(); // Cerrar el diálogo
            } else {
                Log.e("CartDialog", "El contexto no es una instancia de FragmentActivity.");
            }
        });

        findViewById(R.id.button_delete_all).setOnClickListener(v -> {
            carritoService.limpiarCarrito();  // Limpiar el carrito usando el servicio
            adapter.notifyDataSetChanged();
            updateTotal();
        });

        updateTotal();
    }

    private void updateTotal() {
        double total = 0.0;
        for (CarritoItem item : carritoService.getCartItems()) {
            total += item.getPrice() * item.getCantidad();
        }
        TextView cartTotal = findViewById(R.id.cart_total);
        cartTotal.setText("Total aproximado: €" + String.format("%.2f", total));
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getWindow() != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = (int) (getContext().getResources().getDisplayMetrics().heightPixels * 0.9); // 90% de la altura
            getWindow().setLayout(width, height);
        }
    }
}
