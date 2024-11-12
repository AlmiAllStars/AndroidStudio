package com.almi.juegaalmiapp.fragmentos;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.almi.juegaalmiapp.R;

import java.util.List;

public class IVDialogFragment extends DialogFragment {

    private int currentImageIndex;
    private List<Integer> imageResIds;

    public IVDialogFragment(int currentImageIndex, List<Integer> imageResIds) {
        this.currentImageIndex = currentImageIndex;
        this.imageResIds = imageResIds;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_image_viewer, container, false);
        ImageView imageView = view.findViewById(R.id.full_image_view);
        Button buttonPrevious = view.findViewById(R.id.button_previous);
        Button buttonNext = view.findViewById(R.id.button_next);

        imageView.setImageResource(imageResIds.get(currentImageIndex));

        buttonPrevious.setOnClickListener(v -> {
            currentImageIndex = (currentImageIndex - 1 + imageResIds.size()) % imageResIds.size();
            imageView.setImageResource(imageResIds.get(currentImageIndex));
        });

        buttonNext.setOnClickListener(v -> {
            currentImageIndex = (currentImageIndex + 1) % imageResIds.size();
            imageView.setImageResource(imageResIds.get(currentImageIndex));
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {

            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);  // 90% del ancho
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.4); // 80% de la altura
            getDialog().getWindow().setLayout(width, height);


            getDialog().getWindow().setDimAmount(0.9f);
        }
    }
}
