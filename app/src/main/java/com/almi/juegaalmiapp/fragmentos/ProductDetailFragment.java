package com.almi.juegaalmiapp.fragmentos;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.almi.juegaalmiapp.ApiClient;
import com.almi.juegaalmiapp.ApiService;
import com.almi.juegaalmiapp.CarritoService;
import com.almi.juegaalmiapp.ClienteService;
import com.almi.juegaalmiapp.R;
import com.almi.juegaalmiapp.modelo.CarritoItem;
import com.almi.juegaalmiapp.modelo.Product;
import com.bumptech.glide.Glide;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailFragment extends Fragment {
    private Product currentProduct;
    private static final String ARG_PRODUCT_ID = "productId";
    private String productId;
    private boolean animate = false;
    private ClienteService clienteService;
    private Button buyButton;
    private Button tryButton;

    public static ProductDetailFragment newInstance(String productId) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_ID, productId);
        fragment.setArguments(args);
        return fragment;
    }

    public void setAnimate(boolean animate) {
        this.animate = animate;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productId = getArguments().getString(ARG_PRODUCT_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);
        if (animate) {
            Animation scaleAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.scale_animation);
            view.startAnimation(scaleAnimation); // Aplica la animación solo si se especificó
        }

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ImageView productImage = view.findViewById(R.id.product_image);
        TextView toolbarTitle = view.findViewById(R.id.toolbar_title);
        TextView productDescription = view.findViewById(R.id.product_description);
        TextView buyPriceText = view.findViewById(R.id.buy_price);
        TextView tryPriceText = view.findViewById(R.id.try_price);
        Button wishlistButton = view.findViewById(R.id.wishlist_button);
        wishlistButton.setVisibility(View.GONE); // Ocultar el botón de lista de deseos
        buyButton = view.findViewById(R.id.buy_button);
        tryButton = view.findViewById(R.id.try_button);
        clienteService = new ClienteService(requireContext()); // Crear instancia con el contexto

        // Actualiza el estado del botón según el estado de inicio de sesión
        updateButtonState();


        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                // Manejo del botón de "Atrás" en la barra de herramientas
                toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
            }
        }

        loadProductDetails(productId, product -> {
            currentProduct = product;
            String productName = product.getName();
            toolbarTitle.setText(productName);
            productDescription.setText(product.getDescription());
            buyPriceText.setText(String.format(Locale.getDefault(), "%.2f€", product.getPrice()));
            tryPriceText.setText("5€/semana");

            if (productName != null && productName.length() > 12) {
                toolbarTitle.setEllipsize(android.text.TextUtils.TruncateAt.MARQUEE);
                toolbarTitle.setMarqueeRepeatLimit(-1);
                toolbarTitle.setSingleLine(true);
                toolbarTitle.setSelected(true);
            }

            String imageUrl = "https://retodalmi.duckdns.org" + product.getPicture();
            Glide.with(requireContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.bioshock)
                    .error(R.drawable.bioshock)
                    .into(productImage);

            populateDynamicContent(product);
        });

        buyButton.setOnClickListener(v -> {
            if (currentProduct != null) {
                // Crear un CarritoItem basado en el producto actual
                CarritoItem carritoItem = new CarritoItem();
                carritoItem.setId(currentProduct.getId());
                carritoItem.setName(currentProduct.getName());
                carritoItem.setDescription(currentProduct.getDescription());
                carritoItem.setPrice(currentProduct.getPrice());
                carritoItem.setPicture(currentProduct.getPicture());
                carritoItem.setCantidad(1);  // Cantidad inicial
                carritoItem.setTipo(currentProduct.getProductType());
                carritoItem.setOperationType("order");

                // Agregar al carrito
                CarritoService carritoService = new CarritoService(requireContext());
                carritoService.agregarAlCarrito(carritoItem);

                Toast.makeText(getContext(), "Producto añadido al carrito", Toast.LENGTH_SHORT).show();

                // Cerrar el fragmento después de añadir al carrito
                requireActivity().getSupportFragmentManager().popBackStackImmediate();
            } else {
                Toast.makeText(getContext(), "Cargando producto. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
            }
        });

        tryButton.setOnClickListener(v -> {
            if (currentProduct != null) {
                // Crear un CarritoItem basado en el producto actual
                CarritoItem carritoItem = new CarritoItem();
                carritoItem.setId(currentProduct.getId());
                carritoItem.setName(currentProduct.getName());
                carritoItem.setDescription(currentProduct.getDescription());
                carritoItem.setPrice(10.00);  // Precio fijo para rent
                carritoItem.setPicture(currentProduct.getPicture());
                carritoItem.setCantidad(1);  // Cantidad inicial
                carritoItem.setTipo(currentProduct.getProductType());
                carritoItem.setOperationType("rent");  // Tipo de operación "rent"

                // Agregar al carrito
                CarritoService carritoService = new CarritoService(requireContext());
                carritoService.agregarAlCarrito(carritoItem);

                Toast.makeText(getContext(), "Producto añadido al carrito como alquiler", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Cargando producto. Inténtalo de nuevo.", Toast.LENGTH_SHORT).show();
            }
        });


        wishlistButton.setOnClickListener(v -> {
            // Lógica para añadir a la lista de deseos
        });

        return view;
    }



    private void loadProductDetails(String productId, final ProductCallback callback) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getProductById(productId).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onProductLoaded(response.body());
                } else {
                    Log.e("ProductDetailFragment", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.e("ProductDetailFragment", "Failure: " + t.getMessage());
            }
        });
    }

    private void updateButtonState() {
        boolean isLoggedIn = isUserLoggedIn();
        buyButton.setEnabled(isLoggedIn); // Habilitar o deshabilitar el botón
        tryButton.setEnabled(isLoggedIn); // Habilitar o deshabilitar el botón
        buyButton.setAlpha(isLoggedIn ? 1.0f : 0.5f); // Cambiar opacidad para un feedback visual
        tryButton.setAlpha(isLoggedIn ? 1.0f : 0.5f);
    }

    private boolean isUserLoggedIn() {
        // Verifica si hay un cliente o un token válido almacenado
        return clienteService.sharedPreferences.getBoolean("isLoggedIn", false);
    }

    private void populateDynamicContent(Product product) {
        LinearLayout dynamicContainer = getView().findViewById(R.id.dynamic_content_container);
        dynamicContainer.removeAllViews();

        switch (product.getProductType()) {
            case "console":
                addDetail(dynamicContainer, "Modelo:", product.getModel());
                addDetail(dynamicContainer, "Marca:", product.getBrand());
                addDetail(dynamicContainer, "Generación:", product.getGeneration() != null ? product.getGeneration().toString() : "N/A");
                addDetail(dynamicContainer, "Disco:", product.getDisk() + "GB");
                break;

            case "videogame":
                addDetail(dynamicContainer, "Fecha de Lanzamiento:", product.getReleaseDate());
                addDetail(dynamicContainer, "PEGI:", product.getPegi() != null ? product.getPegi().toString() : "N/A");
                addDetail(dynamicContainer, "Géneros:", product.getGenres() != null ? String.join(", ", product.getGenres()) : "N/A");
                break;

            case "device":
                addDetail(dynamicContainer, "Tipo:", product.getType());
                addDetail(dynamicContainer, "Modelo:", product.getModel());
                addDetail(dynamicContainer, "Marca:", product.getBrand());
                addDetail(dynamicContainer, "Procesador:", product.getProcessor());
                addDetail(dynamicContainer, "Memoria:", product.getMemory() + "GB");
                addDetail(dynamicContainer, "Pantalla:", product.getScreen());
                addDetail(dynamicContainer, "Cámara:", product.getCamera());
                addDetail(dynamicContainer, "Batería:", product.getBattery() + "mAh");
                addDetail(dynamicContainer, "Disco:", product.getDisk() + "GB");
                break;

            default:
                addDetail(dynamicContainer, "Información no disponible", "");
                break;
        }
    }

    private void addDetail(LinearLayout container, String label, String value) {
        if (value == null || value.isEmpty()) return;

        TextView labelView = new TextView(getContext());
        labelView.setText(label);
        labelView.setTypeface(null, Typeface.BOLD);

        TextView valueView = new TextView(getContext());
        valueView.setText(value);

        container.addView(labelView);
        container.addView(valueView);
    }

    interface ProductCallback {
        void onProductLoaded(Product product);
    }
}
