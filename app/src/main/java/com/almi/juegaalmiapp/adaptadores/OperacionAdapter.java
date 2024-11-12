package com.almi.juegaalmiapp.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.almi.juegaalmiapp.R;
import com.almi.juegaalmiapp.fragmentos.ProductDetailFragment;
import com.almi.juegaalmiapp.modelo.Operation;
import com.bumptech.glide.Glide;

import java.util.List;

public class OperacionAdapter extends RecyclerView.Adapter<OperacionAdapter.OperacionViewHolder> {
    private List<Operation> operations;

    public OperacionAdapter(List<Operation> operations) {
        this.operations = operations;
    }

    @NonNull
    @Override
    public OperacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_operacion, parent, false);
        return new OperacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OperacionViewHolder holder, int position) {
        holder.bind(operations.get(position));
    }

    @Override
    public int getItemCount() {
        return operations.size();
    }

    static class OperacionViewHolder extends RecyclerView.ViewHolder {
        private TextView nombreProducto, cantidad, precio;
        private ImageView imagenProducto;

        public OperacionViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreProducto = itemView.findViewById(R.id.nombreProducto);
            cantidad = itemView.findViewById(R.id.cantidadProducto);
            precio = itemView.findViewById(R.id.precioProducto);
            imagenProducto = itemView.findViewById(R.id.imagenProducto);


        }

        public void bind(Operation operation) {
            nombreProducto.setText(operation.getProduct().getName());
            cantidad.setText("Cantidad: " + 1);  // Cambiar si tienes un campo real
            precio.setText("Precio: $" + operation.getCharge());

            itemView.setOnClickListener(v -> {
                String productId = String.valueOf(operation.getProduct().getId()); // Obtener el ID del producto
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                ProductDetailFragment fragment = ProductDetailFragment.newInstance(productId);

                // Reemplazar el fragmento actual con el ProductDetailFragment
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)  // Asegúrate de tener un contenedor de fragmentos en tu layout
                        .addToBackStack(null) // Agregar a la pila para permitir navegación hacia atrás
                        .commit();
            });

            Glide.with(itemView.getContext())
                    .load(operation.getProduct().getPicture())
                    .placeholder(R.drawable.bioshock)
                    .error(R.drawable.bioshock)
                    .into(imagenProducto);
        }
    }
}
