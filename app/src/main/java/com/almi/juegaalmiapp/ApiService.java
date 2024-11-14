package com.almi.juegaalmiapp;

import com.almi.juegaalmiapp.modelo.ActiveReparation;
import com.almi.juegaalmiapp.modelo.Client;
import com.almi.juegaalmiapp.modelo.ClientResponse2;
import com.almi.juegaalmiapp.modelo.Pedido;
import com.almi.juegaalmiapp.modelo.Product;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {


    @POST("register")
    Call<ClientResponse2> registerClient(@Body Map<String, Object> body);




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

    @GET("secure/client/activeRepairs")
    Call<List<ActiveReparation>> obtenerReparacionesActivas(@Header("Authorization") String token);

    // Metodo para hacer login, enviando email y password
    @POST("https://retodalmi.duckdns.org/api/login")
    Call<ClientResponse> loginClient(@Body LoginRequest loginRequest);

    @GET("product/{id}")
    Call<Product> getProductById(@Path("id") String productId);

    @POST("secure/repair")
    Call<Void> crearReparacion(@Header("Authorization") String token, @Body Map<String, String> reparacionData);

    @Multipart
    @POST("secure/uploadClientPicture")
    Call<Void> uploadClientPicture(
            @Header("Authorization") String token, // Token de autorización
            @Part MultipartBody.Part picture // Archivo de la imagen
    );

}
