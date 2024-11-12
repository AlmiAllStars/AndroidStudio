package com.almi.juegaalmiapp.fragmentos;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.almi.juegaalmiapp.ApiClient;
import com.almi.juegaalmiapp.ApiService;
import com.almi.juegaalmiapp.CarritoService;
import com.almi.juegaalmiapp.ClientResponse;
import com.almi.juegaalmiapp.EmailRequest;
import com.almi.juegaalmiapp.LoginRequest;
import com.almi.juegaalmiapp.ClienteService;
import com.almi.juegaalmiapp.R;
import com.almi.juegaalmiapp.modelo.CarritoItem;
import com.almi.juegaalmiapp.modelo.Client;
import com.almi.juegaalmiapp.viewmodel.SharedViewModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginDialogFragment extends DialogFragment {

    private EditText emailEditText;
    private EditText passwordEditText;
    private LoginListener loginListener;
    private SharedViewModel sharedViewModel;
    private static final String TAG = "LoginDialogFragment";

    public interface LoginListener {
        void onLoginSuccess();
    }

    public void setLoginListener(LoginListener listener) {
        this.loginListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Iniciar Sesión");
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            if (window != null) {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;
                window.setLayout(width, (int) (height * 0.68));
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_login, container, false);
        ImageView logoImageView = view.findViewById(R.id.logo_image_view);
        emailEditText = view.findViewById(R.id.edit_text_email);
        passwordEditText = view.findViewById(R.id.edit_text_password);

        Button loginButton = view.findViewById(R.id.button_login);
        Button createAccountButton = view.findViewById(R.id.button_create_account);
        ImageButton closeButton = view.findViewById(R.id.close_account_button);

        logoImageView.setImageResource(R.drawable.logojuegalmi);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        closeButton.setOnClickListener(v -> dismiss());

        loginButton.setOnClickListener(v -> handleLogin());
        createAccountButton.setOnClickListener(v -> {
            RegisterDialogFragment registerDialog = new RegisterDialogFragment();
            registerDialog.show(getParentFragmentManager(), "registerDialog");
        });

        return view;
    }

    private void handleLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear el objeto LoginRequest con email y password
        LoginRequest loginRequest = new LoginRequest(email, password);

        // Obtener el servicio de la API
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ClientResponse> call = apiService.loginClient(loginRequest);

        // Enviar la solicitud
        call.enqueue(new Callback<ClientResponse>() {
            @Override
            public void onResponse(Call<ClientResponse> call, Response<ClientResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Obtener el token de la respuesta
                    String token = response.body().getToken();
                    if (token != null && !token.isEmpty()) {
                        // Guardar el token en SharedPreferences
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("token", token);
                        editor.putBoolean("isLoggedIn", true);
                        editor.apply();

// Obtener el token de SharedPreferences
                        SharedPreferences sharedPreferences2 = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                        String token2 = "Bearer " + sharedPreferences2.getString("token", "");

// Crear la solicitud con el email
                        EmailRequest emailRequest = new EmailRequest(email);
                        apiService.getClientById(token2, emailRequest).enqueue(new Callback<Client>() {
                            @Override
                            public void onResponse(Call<Client> call, Response<Client> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    Client client = response.body();
                                    client.setEmail(email);
                                    ClienteService clienteService = new ClienteService(getContext());
                                    clienteService.saveClient(client);  // Guardar cliente en SharedPreferences
                                    editor.putBoolean("isLoggedIn", true);
                                    editor.apply();
                                    // Parsear el JSON del carrito
                                    String chartJson = client.getChart();
                                    if (chartJson != null && !chartJson.isEmpty()) {
                                        Type listType = new TypeToken<List<CarritoItem>>() {}.getType();
                                        List<CarritoItem> carritoItems = new Gson().fromJson(chartJson, listType);

                                        // Guardar el carrito en el servicio
                                        CarritoService carritoService = new CarritoService(getContext());
                                        carritoService.cargarCarritoDesdeLista(carritoItems);

                                        Log.d(TAG, "Carrito cargado: " + carritoItems);
                                    } else {
                                        Log.d(TAG, "El carrito está vacío.");
                                    }

                                    // Guardar cliente completo en SharedPreferences
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("clientData", new Gson().toJson(client));
                                    editor.apply();

                                    if (loginListener != null) {
                                        loginListener.onLoginSuccess();
                                    }
                                    dismiss();
                                } else {
                                    Log.e(TAG, "Error al obtener datos del cliente.");
                                    Toast.makeText(getContext(), "Error al cargar datos del cliente.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Client> call, Throwable t) {
                                Log.e(TAG, "Error al obtener datos del cliente: ", t);
                                Toast.makeText(getContext(), "Error al obtener datos del cliente.", Toast.LENGTH_SHORT).show();
                            }
                        });



                    } else {
                        Toast.makeText(getContext(), "No se recibió el token. Intenta nuevamente.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e(TAG, "Error en la autenticación: " + errorBody);
                        Toast.makeText(getContext(), "Error en la autenticación: " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error al procesar el cuerpo de error", e);
                        Toast.makeText(getContext(), "Error en la autenticación. Intenta nuevamente.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ClientResponse> call, Throwable t) {
                Log.e(TAG, "Error en la solicitud de inicio de sesión: ", t);
                Toast.makeText(getContext(), "Error de conexión. Intenta más tarde.", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
