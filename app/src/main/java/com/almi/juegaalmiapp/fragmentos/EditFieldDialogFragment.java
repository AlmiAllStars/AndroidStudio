package com.almi.juegaalmiapp.fragmentos;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.almi.juegaalmiapp.ClienteService;
import com.almi.juegaalmiapp.R;
import com.almi.juegaalmiapp.viewmodel.SharedViewModel;

public class EditFieldDialogFragment extends DialogFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_LAYOUT_RES_ID = "layout_res_id";
    private String fieldTitle;
    private int layoutResId;
    private SharedViewModel sharedViewModel;

    public static EditFieldDialogFragment newInstance(String title, int layoutResId) {
        EditFieldDialogFragment fragment = new EditFieldDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            fieldTitle = getArguments().getString(ARG_TITLE);
            layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
        }

        View view = inflater.inflate(layoutResId, container, false);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        TextView titleTextView = view.findViewById(R.id.title_text_view);
        titleTextView.setText(fieldTitle);

        EditText editTextField = view.findViewById(R.id.edit_text_field);
        Button saveButton = view.findViewById(R.id.save_button);
        ImageButton closeButton = view.findViewById(R.id.close_button);

        editTextField.setText("");

        closeButton.setOnClickListener(v -> dismiss());

        saveButton.setOnClickListener(v -> {
            String newValue = editTextField.getText().toString().trim();
            if (!newValue.isEmpty()) {
                if (validarCampo(fieldTitle, newValue)) {
                    actualizarDatoUsuario(newValue); // Llamada al servidor
                    actualizarSharedViewModel(newValue); // Actualizar ViewModel localmente
                    actualizarSharedPreferences(newValue); // Persistir en local
                    dismiss();
                } else {
                    Toast.makeText(getContext(), getErrorMessage(fieldTitle), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "El campo no puede estar vacío", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    private String getErrorMessage(String campo) {
        switch (campo) {
            case "Editar Nombre":
            case "Editar Apellido":
                return "Debe contener solo letras y al menos 2 caracteres.";
            case "Editar Teléfono":
                return "Debe contener entre 7 y 15 dígitos.";
            case "Editar Dirección":
                return "Debe contener al menos 5 caracteres.";
            case "Editar Código Postal":
                return "Debe contener entre 4 y 5 dígitos.";
            case "Editar Contraseña":
                return "Debe tener entre 8 y 20 caracteres, incluyendo al menos una letra y un número.";
            default:
                return "Valor no válido.";
        }
    }


    private void actualizarDatoUsuario(String newValue) {
        ClienteService clienteService = new ClienteService(requireContext());  // Crear instancia pasando el contexto
        String campo = getCampoPorTitulo(fieldTitle);

        if (campo != null) {
            clienteService.actualizarDatoUsuario(campo, newValue);
            Toast.makeText(getContext(), "Actualizado en el servidor.", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("EditFieldDialog", "Campo no encontrado para el título: " + fieldTitle);
        }
    }


    private void actualizarSharedViewModel(String newValue) {
        switch (fieldTitle) {
            case "Editar Nombre":
                sharedViewModel.setName(newValue);
                break;
            case "Editar Apellido":
                sharedViewModel.setSurname(newValue);
                break;
            case "Editar Teléfono":
                sharedViewModel.setPhone(newValue);
                break;
            case "Editar Dirección":
                sharedViewModel.setAddress(newValue);
                break;
            case "Editar Código Postal":
                sharedViewModel.setPostalCode(newValue);
                break;
            case "Editar Contraseña":
                sharedViewModel.setPassword(newValue);
                break;
        }
    }

    private boolean validarCampo(String campo, String valor) {
        String regex;

        switch (campo) {
            case "Editar Nombre":
            case "Editar Apellido":
                regex = "^[a-zA-Z\\s]{2,}$";
                break;
            case "Editar Teléfono":
                regex = "^\\d{7,15}$";
                break;
            case "Editar Dirección":
                regex = "^[a-zA-Z0-9\\s,.-]{5,}$";
                break;
            case "Editar Código Postal":
                regex = "^\\d{4,5}$";
                break;
            case "Editar Contraseña":
                regex = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,20}$";
                break;
            default:
                return true; // Si el campo no requiere validación
        }

        return valor.matches(regex);
    }


    private void actualizarSharedPreferences(String newValue) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String campo = getCampoPorTitulo(fieldTitle);

        if (campo != null) {
            editor.putString(campo, newValue);
            editor.apply();
            Toast.makeText(getContext(), "Guardado localmente.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getCampoPorTitulo(String title) {
        switch (title) {
            case "Editar Nombre":
                return "name";
            case "Editar Apellido":
                return "surname";
            case "Editar Teléfono":
                return "phone";
            case "Editar Dirección":
                return "address";
            case "Editar Código Postal":
                return "postalCode";
            case "Editar Contraseña":
                return "password";
            default:
                return null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawableResource(android.R.color.white);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }
}
