package com.almi.juegaalmiapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.almi.juegaalmiapp.modelo.CarritoItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarritoService {

    private static final String CART_PREFS = "CartPrefs";
    private static final String CART_KEY = "cartItems";

    private List<CarritoItem> cartItems;
    private final SharedPreferences sharedPreferences;
    private final ApiService apiService;
    private final ClienteService clienteService;

    public CarritoService(Context context) {
        sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
        cartItems = cargarCarritoDesdeJson();

        clienteService = new ClienteService(context);  // Instanciar ClienteService para obtener el token
        String token = clienteService.getToken();
        this.apiService = ApiClient.getClient().create(ApiService.class);
    }

    // Cargar carrito desde SharedPreferences
    private List<CarritoItem> cargarCarritoDesdeJson() {
        String cartJson = sharedPreferences.getString(CART_KEY, null);
        if (cartJson != null) {
            Type listType = new TypeToken<List<CarritoItem>>() {}.getType();
            return new Gson().fromJson(cartJson, listType);
        }
        return new ArrayList<>();
    }

    // Guardar carrito en SharedPreferences
    private void guardarCarritoEnJson() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String cartJson = new Gson().toJson(cartItems);
        editor.putString(CART_KEY, cartJson);
        editor.apply();
    }

    // Obtener todos los ítems del carrito
    public List<CarritoItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    // Agregar un ítem al carrito
    public void agregarAlCarrito(CarritoItem item) {
        cartItems.add(item);
        guardarCarritoEnJson(); // Guardar en SharedPreferences

        // Preparar datos para enviar al servidor
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("chart", new Gson().toJson(cartItems)); // Stringificar el carrito

        String token = "Bearer " + clienteService.getToken();  // Obtener el token
        apiService.updateClient(token, updateData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("CarritoService", "Carrito actualizado en el servidor.");
                } else {
                    Log.e("CarritoService", "Error al actualizar el carrito: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("CarritoService", "Error de red al actualizar el carrito.", t);
            }
        });
    }

    private void actualizarCarritoEnServidor() {
        String token = "Bearer " + clienteService.getToken();  // Obtener el token del cliente
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("chart", new Gson().toJson(cartItems));  // Stringificar el carrito

        apiService.updateClient(token, updateData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("CarritoService", "Carrito actualizado correctamente en el servidor.");
                } else {
                    Log.e("CarritoService", "Error al actualizar el carrito en el servidor: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("CarritoService", "Error de red al actualizar el carrito.", t);
            }
        });
    }


    public void eliminarDelCarrito(CarritoItem item) {
        cartItems.remove(item);
        guardarCarritoEnJson();  // Guardar en SharedPreferences

        // Enviar el carrito actualizado al servidor
        actualizarCarritoEnServidor();
    }


    public void limpiarCarrito() {
        cartItems.clear();
        guardarCarritoEnJson();  // Guardar en SharedPreferences

        // Enviar el carrito vacío al servidor
        actualizarCarritoEnServidor();
    }


    // Actualizar cantidad de un ítem específico y sincronizar con el servidor
    public void actualizarCantidad(int productId, int nuevaCantidad) {
        for (CarritoItem item : cartItems) {
            if (item.getId() == productId) {
                item.setCantidad(nuevaCantidad);
                break;
            }
        }
        guardarCarritoEnJson();  // Guardar cambios localmente

        // Enviar el carrito actualizado al servidor
        actualizarCarritoEnServidor();
    }

    public void cargarCarritoDesdeLista(List<CarritoItem> items) {
        cartItems.clear();
        cartItems.addAll(items);
        guardarCarritoEnJson(); // Persistir el carrito en SharedPreferences
    }

}
