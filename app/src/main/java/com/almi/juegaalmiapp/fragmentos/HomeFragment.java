package com.almi.juegaalmiapp.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.almi.juegaalmiapp.R;
import com.google.android.material.tabs.TabLayout;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);


        tabLayout.addTab(tabLayout.newTab().setText("Games"));
        tabLayout.addTab(tabLayout.newTab().setText("Consoles"));
        tabLayout.addTab(tabLayout.newTab().setText("Devices"));
        tabLayout.addTab(tabLayout.newTab().setText("Reparations"));

        replaceFragment(new GamesFragment());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selectedFragment = null;

                switch (tab.getPosition()) {
                    case 0:
                        selectedFragment = new GamesFragment();
                        break;
                    case 1:
                        selectedFragment = new ConsolesFragment();
                        break;
                    case 2:
                        selectedFragment = new DevicesFragment();
                        break;
                    case 3:
                        selectedFragment = new ReparationsFragment();
                        break;
                }

                if (selectedFragment != null) {
                    replaceFragment(selectedFragment);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_home, fragment);
        transaction.commit();
    }
}
