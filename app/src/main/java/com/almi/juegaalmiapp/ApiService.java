package com.almi.juegaalmiapp;

import com.almi.juegaalmiapp.modelo.Client;
import com.almi.juegaalmiapp.modelo.Pedido;
import com.almi.juegaalmiapp.modelo.Product;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    // Método para registrar un cliente
    @POST("register")
    Call<ClientResponse> registerClient(@Body Client client);

    @POST("secure/clientbyEmail")
    Call<Client> getClientById(
            @Header("Authorization") String token,
            @Body EmailRequest emailRequest
    );

    @PUT("secure/editClient")
    Call<Void> updateClient(
            @Header("Authorization") String token,
            @Body Map<String, Object> updateData
    );

    @GET("secure/sales")
    Call<List<Pedido>> obtenerPedidos(@Header("Authorization") String token);

    @POST("secure/operations")
    Call<Void> procesarCarrito(@Header("Authorization") String token, @Body Map<String, Object> requestBody);


    // Metodo para hacer login, enviando email y password
    @POST("https://juegalmiapp.duckdns.org/api/login")
    Call<ClientResponse> loginClient(@Body LoginRequest loginRequest);

    @GET("product/{id}")
    Call<Product> getProductById(@Path("id") String productId);

}
