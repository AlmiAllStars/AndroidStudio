package com.almi.juegaalmiapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.almi.juegaalmiapp.modelo.Client;
import com.google.gson.Gson;

public class ClienteService {

    private static final String CLIENT_PREFS = "UserPrefs";
    private static final String CLIENT_KEY = "clientData";

    private final SharedPreferences sharedPreferences;

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

}
