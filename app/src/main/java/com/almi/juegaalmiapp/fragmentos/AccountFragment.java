package com.almi.juegaalmiapp.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.almi.juegaalmiapp.R;
import com.almi.juegaalmiapp.viewmodel.SharedViewModel;

public class AccountFragment extends Fragment {

    private TextView userNameTextView;
    private TextView userSurnameTextView;
    private TextView userPhoneTextView;
    private TextView userAddressTextView;
    private TextView userPostalCodeTextView;
    private TextView userEmailTextView;
    private TextView userPassTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        userNameTextView = view.findViewById(R.id.user_name);
        userSurnameTextView = view.findViewById(R.id.user_surname);
        userPhoneTextView = view.findViewById(R.id.user_tlf);
        userAddressTextView = view.findViewById(R.id.user_adress);
        userPostalCodeTextView = view.findViewById(R.id.user_cp);
        userEmailTextView = view.findViewById(R.id.user_email);
        userPassTextView = view.findViewById(R.id.user_pass);

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getName().observe(getViewLifecycleOwner(), newName -> userNameTextView.setText(newName));
        sharedViewModel.getSurname().observe(getViewLifecycleOwner(), newSurname -> userSurnameTextView.setText(newSurname));
        sharedViewModel.getPhone().observe(getViewLifecycleOwner(), newPhone -> userPhoneTextView.setText(newPhone));
        sharedViewModel.getAddress().observe(getViewLifecycleOwner(), newAddress -> userAddressTextView.setText(newAddress));
        sharedViewModel.getPostalCode().observe(getViewLifecycleOwner(), newPostalCode -> userPostalCodeTextView.setText(newPostalCode));
        sharedViewModel.getEmail().observe(getViewLifecycleOwner(), newEmail -> userEmailTextView.setText(newEmail));

        userPassTextView.setText("******");

        ImageButton closeButton = view.findViewById(R.id.close_account_button);
        closeButton.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack();
        });

        view.findViewById(R.id.button_name).setOnClickListener(v -> openEditDialog("name"));
        view.findViewById(R.id.button_surname).setOnClickListener(v -> openEditDialog("surname"));
        view.findViewById(R.id.button_phone).setOnClickListener(v -> openEditDialog("phone"));
        view.findViewById(R.id.button_address).setOnClickListener(v -> openEditDialog("address"));
        view.findViewById(R.id.button_postal_code).setOnClickListener(v -> openEditDialog("postal_code"));
        view.findViewById(R.id.button_password).setOnClickListener(v -> openEditDialog("password"));

        return view;
    }

    private void openEditDialog(String fieldType) {
        String title;
        int layoutResId;

        switch (fieldType) {
            case "name":
                title = "Editar Nombre";
                layoutResId = R.layout.dialog_edit_name;
                break;
            case "surname":
                title = "Editar Apellido";
                layoutResId = R.layout.dialog_edit_surname;
                break;
            case "phone":
                title = "Editar Teléfono";
                layoutResId = R.layout.dialog_edit_phone;
                break;
            case "address":
                title = "Editar Dirección";
                layoutResId = R.layout.dialog_edit_address;
                break;
            case "postal_code":
                title = "Editar Código Postal";
                layoutResId = R.layout.dialog_edit_postal_code;
                break;
            case "password":
                title = "Editar Contraseña";
                layoutResId = R.layout.dialog_edit_password;
                break;
            default:
                return;
        }

        EditFieldDialogFragment dialogFragment = EditFieldDialogFragment.newInstance(title, layoutResId);
        dialogFragment.show(getParentFragmentManager(), "editDialog");
    }
}
