package com.almi.juegaalmiapp;

import com.almi.juegaalmiapp.modelo.Client;
import com.google.gson.annotations.SerializedName;

public class ClientResponse {
    private String message;
    private Client client;

    @SerializedName("token")
    private String token;

    // Getters y Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
