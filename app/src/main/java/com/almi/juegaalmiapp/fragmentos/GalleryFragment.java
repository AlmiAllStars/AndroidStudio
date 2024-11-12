package com.almi.juegaalmiapp.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.almi.juegaalmiapp.R;
import com.almi.juegaalmiapp.adaptadores.GalleryAdapter;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {
    private RecyclerView recyclerView;
    private GalleryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_gallery);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new GalleryAdapter(getImageList(), getTitleList());
        recyclerView.setAdapter(adapter);

        return view;
    }

    private List<Integer> getImageList() {
        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.evento1);
        images.add(R.drawable.segunda);
        images.add(R.drawable.evento2);
        images.add(R.drawable.flipaos);
        images.add(R.drawable.tienda2);
        images.add(R.drawable.evento1);
        images.add(R.drawable.flipaos);
        images.add(R.drawable.segunda);
        images.add(R.drawable.evento2);
        images.add(R.drawable.tienda2);
        return images;
    }

    private List<String> getTitleList() {
        List<String> titles = new ArrayList<>();
        titles.add("Evento 1");
        titles.add("Tienda 1");
        titles.add("Evento 2 ");
        titles.add("Flipaos");
        titles.add("Tienda 2");
        titles.add("Evento 3");
        titles.add("Flipaos");
        titles.add("Tienda 3");
        titles.add("Evento 3");
        titles.add("Tienda 4");
        return titles;
    }
}