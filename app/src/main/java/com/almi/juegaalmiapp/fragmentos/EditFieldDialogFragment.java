package com.almi.juegaalmiapp.fragmentos;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
                Toast.makeText(getContext(), "Nuevo valor guardado: " + newValue, Toast.LENGTH_SHORT).show();

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

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Guarda el nuevo valor
                switch (fieldTitle) {
                    case "Editar Nombre":
                        editor.putString("name", newValue);
                        break;
                    case "Editar Apellido":
                        editor.putString("surname", newValue);
                        break;
                    case "Editar Teléfono":
                        editor.putString("phone", newValue);
                        break;
                    case "Editar Dirección":
                        editor.putString("address", newValue);
                        break;
                    case "Editar Código Postal":
                        editor.putString("postalCode", newValue);
                        break;
                    case "Editar Contraseña":
                        editor.putString("password", newValue);
                        break;
                }
                editor.apply();

                dismiss();
            } else {
                Toast.makeText(getContext(), "El campo no puede estar vacío", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
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
