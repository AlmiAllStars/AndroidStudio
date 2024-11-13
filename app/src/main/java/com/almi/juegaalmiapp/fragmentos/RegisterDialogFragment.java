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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import com.almi.juegaalmiapp.ApiClient;
import com.almi.juegaalmiapp.ApiService;
import com.almi.juegaalmiapp.ClientResponse;
import com.almi.juegaalmiapp.R;
import com.almi.juegaalmiapp.modelo.Client;
import com.almi.juegaalmiapp.modelo.ClientResponse2;
import com.almi.juegaalmiapp.viewmodel.SharedViewModel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterDialogFragment extends DialogFragment {

    private EditText nameEditText;
    private EditText surnameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText phoneEditText; // Campo para el teléfono
    private SharedViewModel sharedViewModel;
    private Button registerButton;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Crear Cuenta");
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_register, container, false);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        nameEditText = view.findViewById(R.id.edit_text_name);
        surnameEditText = view.findViewById(R.id.edit_text_surname);
        emailEditText = view.findViewById(R.id.edit_text_email);
        passwordEditText = view.findViewById(R.id.edit_text_password);
        confirmPasswordEditText = view.findViewById(R.id.edit_text_confirm_password);

        registerButton = view.findViewById(R.id.button_register);
        registerButton.setOnClickListener(v -> handleRegister());

        return view;
    }

    private void handleRegister() {
        String name = nameEditText.getText().toString().trim();
        String surname = surnameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Verificar que los campos no estén vacíos
        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar que las contraseñas coincidan
        if (!password.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
            return;
        }
        // Crear el objeto Client
        Client newClient = new Client(name, surname, email);

        // Crear la solicitud Retrofit
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", name);
        requestBody.put("surname", surname);
        requestBody.put("email", email);
        requestBody.put("password", password);

        Call<ClientResponse2> call = apiService.registerClient(requestBody);

        // Enviar la solicitud
        call.enqueue(new Callback<ClientResponse2>() {
            @Override
            public void onResponse(Call<ClientResponse2> call, Response<ClientResponse2> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Manejar respuesta exitosa
                    ClientResponse2 clientResponse = response.body();
                    if (clientResponse.isSuccess()) {
                        Log.d("RegisterClient", "Registro exitoso");
                        Toast.makeText(getContext(), "Registro y login exitosos", Toast.LENGTH_SHORT).show();
                    } else {
                        // Manejar errores de lógica de negocio desde el servidor
                        String error = clientResponse.getError() != null ? clientResponse.getError() : "Error desconocido";
                        Toast.makeText(getContext(), "Usuario Registrado: " + error, Toast.LENGTH_SHORT).show();
                        // Cerrar el diálogo
                        dismiss();
                    }
                } else {
                    // Manejar errores del servidor (códigos 4xx o 5xx)
                    String errorMessage = "Error en el registro";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string(); // Leer mensaje del error si está disponible
                        }
                    } catch (IOException e) {
                        Log.e("RegisterClient", "Error al leer el cuerpo de la respuesta de error", e);
                    }
                    Toast.makeText(getContext(), "Fallo en el registro: " + errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ClientResponse2> call, Throwable t) {
                // Manejar errores de red o excepciones
                Log.e("RegisterClient", "Error de red o de conexión", t);
                String error = t instanceof IOException
                        ? "Error de red, verifica tu conexión"
                        : "Error desconocido, contacta al soporte";
                Toast.makeText(getContext(), "Error en el registro: " + error, Toast.LENGTH_LONG).show();
            }
        });


    }
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            if (window != null) {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;
                window.setLayout((int) (width * 0.90), ViewGroup.LayoutParams.WRAP_CONTENT);
                int height = displayMetrics.heightPixels;
                window.setLayout((int) (width * 0.90), (int) (height * 0.75));
            }
        }
    }
}
