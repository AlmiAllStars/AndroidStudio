package com.almi.juegaalmiapp.fragmentos;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import com.almi.juegaalmiapp.viewmodel.SharedViewModel;
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
        phoneEditText = view.findViewById(R.id.edit_text_phone);  // Campo de teléfono

        Button registerButton = view.findViewById(R.id.button_register);
        registerButton.setOnClickListener(v -> handleRegister());

        return view;
    }

    private void handleRegister() {
        String name = nameEditText.getText().toString().trim();
        String surname = surnameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String phoneStr = phoneEditText.getText().toString().trim();  // Obtener teléfono como String

        // Verificar que los campos no estén vacíos
        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || phoneStr.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar que las contraseñas coincidan
        if (!password.equals(confirmPassword)) {
            Toast.makeText(getContext(), "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convertir teléfono a int
  // Convertir de String a int

        // Crear el objeto Client
        Client newClient = new Client(name, surname, email, phoneStr);

        // Crear la solicitud Retrofit
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ClientResponse> call = apiService.registerClient(newClient);

        // Enviar la solicitud
        call.enqueue(new Callback<ClientResponse>() {
            @Override
            public void onResponse(Call<ClientResponse> call, Response<ClientResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Client registeredClient = response.body().getClient();

                    // Guardar datos del cliente en SharedPreferences
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("email", registeredClient.getEmail());
                    editor.putString("name", registeredClient.getName());
                    editor.putString("surname", registeredClient.getSurname());
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();

                    // Actualizar el SharedViewModel
                    sharedViewModel.setName(registeredClient.getName());
                    sharedViewModel.setSurname(registeredClient.getSurname());
                    sharedViewModel.setEmail(registeredClient.getEmail());

                    // Mostrar mensaje de éxito
                    Toast.makeText(getContext(), "Cuenta creada con éxito.", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }

            @Override
            public void onFailure(Call<ClientResponse> call, Throwable t) {
                // Mostrar mensaje de error en caso de fallo en la conexión
                Toast.makeText(getContext(), "Error de conexión. Intenta más tarde.", Toast.LENGTH_SHORT).show();
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
