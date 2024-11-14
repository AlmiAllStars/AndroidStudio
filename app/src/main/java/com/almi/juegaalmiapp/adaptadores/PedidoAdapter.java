package com.almi.juegaalmiapp.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.almi.juegaalmiapp.R;
import com.almi.juegaalmiapp.modelo.Operation;
import com.almi.juegaalmiapp.modelo.Pedido;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {
    private List<Pedido> pedidos;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Pedido pedido);
    }

    public PedidoAdapter(List<Pedido> pedidos, OnItemClickListener listener) {
        this.pedidos = pedidos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pedido, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        holder.bind(pedidos.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }

    static class PedidoViewHolder extends RecyclerView.ViewHolder {
        private TextView titulo, fecha, total;
        private ImageView imagenProducto;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tituloPedido);
            fecha = itemView.findViewById(R.id.fechaPedido);
            total = itemView.findViewById(R.id.totalPedido);
            imagenProducto = itemView.findViewById(R.id.imagenProducto);
        }

        public void bind(final Pedido pedido, final OnItemClickListener listener) {
            // Muestra el título del pedido
            titulo.setText("Pedido #" + pedido.getSale().getId());

            // Formatea la fecha en estilo "Nov 8, 2024"
            String fechaFormateada = formatearFecha(pedido.getSale().getTimestamp());
            fecha.setText("Fecha: " + fechaFormateada);

            // Calcula y muestra el total
            total.setText("Total: $" + calcularTotal(pedido.getOperations()));

            // Carga la imagen del primer producto
            if (pedido.getOperations() != null && !pedido.getOperations().isEmpty()) {
                String urlImagen = "";
                if (pedido.getOperations().get(0).getProduct() != null) {
                    urlImagen = pedido.getOperations().get(0).getProduct().getPicture();
                }
                Glide.with(itemView.getContext())
                        .load("https://retodalmi.duckdns.org/" + urlImagen) // Asegúrate de usar Glide para cargar imágenes
                        .placeholder(R.drawable.bioshock) // Imagen mientras carga
                        .error(R.drawable.bioshock) // Imagen en caso de error
                        .into(imagenProducto); // Tu ImageView
            } else {
                imagenProducto.setImageResource(R.drawable.bioshock); // Imagen por defecto si no hay productos
            }

            itemView.setOnClickListener(v -> listener.onItemClick(pedido));
        }

        private String formatearFecha(String timestamp) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
                Date date = inputFormat.parse(timestamp);
                return outputFormat.format(date);
            } catch (Exception e) {
                return timestamp; // Si falla el parseo, devolver el original
            }
        }



        private double calcularTotal(List<Operation> operations) {
            double total = 0;
            for (Operation op : operations) {
                total += op.getCharge(); // Usa el getter de Operation para obtener el cargo
            }
            return total;
        }


    }
}
