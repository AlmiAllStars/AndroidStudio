package com.almi.juegaalmiapp.adaptadores;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.almi.juegaalmiapp.CarritoService;
import com.almi.juegaalmiapp.CartDialog;
import com.almi.juegaalmiapp.R;
import com.almi.juegaalmiapp.fragmentos.ProductDetailFragment;
import com.almi.juegaalmiapp.modelo.CarritoItem;
import com.bumptech.glide.Glide;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private CarritoService carritoService;
    private Runnable updateTotalCallback;

    private FragmentActivity activity;
    private CartDialog dialog; // Referencia al diálogo

    public CartAdapter(CarritoService carritoService, Runnable updateTotalCallback, FragmentActivity activity, CartDialog dialog) {
        this.carritoService = carritoService;  // Usar el servicio para manejar los ítems del carrito
        this.updateTotalCallback = updateTotalCallback;
        this.activity = activity;
        this.dialog = dialog;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        List<CarritoItem> cartItems = carritoService.getCartItems(); // Obtener la lista actualizada del carrito
        CarritoItem item = cartItems.get(position);

        holder.name.setText(item.getName());

        String priceText = "€" + String.format("%.2f", item.getPrice()) + " /ud.";

        SpannableString spannableString = new SpannableString(priceText);
        spannableString.setSpan(new ForegroundColorSpan(0xFFFF0000), 0, priceText.indexOf("/ud."), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Color rojo para el precio
        spannableString.setSpan(new ForegroundColorSpan(0xFF000000), priceText.indexOf("/ud."), priceText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Color negro para "/ud."

        holder.price.setText(spannableString);
        holder.quantity.setText(item.getCantidad() + " ud.");

        // Cargar imagen usando Glide o similar si es necesario
        Glide.with(holder.image.getContext()).load("https://juegalmiapp.duckdns.org" + item.getPicture()).into(holder.image);

        holder.image.setOnClickListener(v -> {
            String productId = String.valueOf(item.getId()); // Obtener el ID del producto
            ProductDetailFragment fragment = ProductDetailFragment.newInstance(productId);

            // Reemplazar el fragmento actual con el ProductDetailFragment
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();

            // Cerrar el diálogo
            dialog.dismiss();
        });


        holder.incrementButton.setOnClickListener(v -> {
            // Incrementar la cantidad del ítem
            int nuevaCantidad = item.getCantidad() + 1;
            carritoService.actualizarCantidad(item.getId(), nuevaCantidad);  // Llama a actualizarCantidad
            notifyItemChanged(holder.getAdapterPosition());
            updateTotalCallback.run();  // Actualizar el total
        });

        holder.decrementButton.setOnClickListener(v -> {
            if (item.getCantidad() > 1) {
                // Decrementar la cantidad del ítem
                int nuevaCantidad = item.getCantidad() - 1;
                carritoService.actualizarCantidad(item.getId(), nuevaCantidad);  // Llama a actualizarCantidad
                notifyItemChanged(holder.getAdapterPosition());
            } else {
                // Eliminar el ítem si la cantidad llega a 0
                carritoService.eliminarDelCarrito(item);  // Llama a eliminarDelCarrito
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    notifyItemRemoved(currentPosition);
                }
            }
            updateTotalCallback.run();  // Actualizar el total
        });
    }

    @Override
    public int getItemCount() {
        return carritoService.getCartItems().size();  // Siempre obtener tamaño actualizado
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, quantity;
        ImageView image;
        Button incrementButton, decrementButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cart_item_name);
            price = itemView.findViewById(R.id.cart_item_price);
            quantity = itemView.findViewById(R.id.cart_item_quantity);
            image = itemView.findViewById(R.id.cart_item_image);
            incrementButton = itemView.findViewById(R.id.button_increment);
            decrementButton = itemView.findViewById(R.id.button_decrement);
        }
    }
}


