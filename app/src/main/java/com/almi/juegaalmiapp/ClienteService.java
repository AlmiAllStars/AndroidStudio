package com.almi.juegaalmiapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.almi.juegaalmiapp.modelo.Client;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClienteService {

    private static final String CLIENT_PREFS = "UserPrefs";
    private static final String CLIENT_KEY = "clientData";

    public final SharedPreferences sharedPreferences;
    private final ApiService apiService = ApiClient.getClient().create(ApiService.class);

    public ClienteService(Context context) {
        this.sharedPreferences = context.getSharedPreferences(CLIENT_PREFS, Context.MODE_PRIVATE);
    }

    // Guardar cliente en SharedPreferences
    public void saveClient(Client client) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CLIENT_KEY, new Gson().toJson(client));
        editor.apply();
    }

    // Obtener cliente desde SharedPreferences
    public Client getClient() {
        String clientJson = sharedPreferences.getString(CLIENT_KEY, null);
        if (clientJson != null) {
            return new Gson().fromJson(clientJson, Client.class);
        }
        return null; // Si no hay datos
    }

    public String getToken() {
        return sharedPreferences.getString("token", "");  // Devuelve el token o una cadena vacía si no existe
    }

    public void actualizarDatoUsuario(String campo, Object valor) {
        // Preparar el cuerpo de la solicitud
        Map<String, Object> updateData = new HashMap<>();
        updateData.put(campo, valor);

        String token = "Bearer " + getToken(); // Obtener el token desde ClienteService

        // Llamar a la API para actualizar el dato
        apiService.updateClient(token, updateData).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("ClienteService", "Dato de usuario actualizado con éxito.");
                } else {
                    Log.e("ClienteService", "Error al actualizar el dato: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ClienteService", "Error de red al actualizar el dato.", t);
            }
        });
    }


}
