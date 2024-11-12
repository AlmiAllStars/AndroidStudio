package com.almi.juegaalmiapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.almi.juegaalmiapp.adaptadores.NotificationAdapter;
import com.almi.juegaalmiapp.fragmentos.AboutFragment;
import com.almi.juegaalmiapp.fragmentos.AccountFragment;
import com.almi.juegaalmiapp.fragmentos.GalleryFragment;
import com.almi.juegaalmiapp.fragmentos.HomeFragment;
import com.almi.juegaalmiapp.fragmentos.ListaPedidosFragment;
import com.almi.juegaalmiapp.fragmentos.LoginDialogFragment;
import com.almi.juegaalmiapp.fragmentos.ProductDetailFragment;
import com.almi.juegaalmiapp.fragmentos.StoresFragment;
import com.almi.juegaalmiapp.modelo.Client;
import com.almi.juegaalmiapp.modelo.Notification;
import com.almi.juegaalmiapp.viewmodel.SharedViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoginDialogFragment.LoginListener {

    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private ImageView iconProfile, iconCart, iconNotifications;
    private SharedViewModel sharedViewModel;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        iconProfile = findViewById(R.id.icon_profile);
        iconCart = findViewById(R.id.icon_cart);
        iconNotifications = findViewById(R.id.icon_notifications);

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            ClienteService clienteService = new ClienteService(this);
            SharedViewModel sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

            // Obtener cliente desde ClienteService y actualizar SharedViewModel
            Client client = clienteService.getClient();
            if (client != null) {
                sharedViewModel.setName(client.getName());
                sharedViewModel.setSurname(client.getSurname());
                sharedViewModel.setEmail(client.getEmail());
                sharedViewModel.setPhone(client.getPhone());
                sharedViewModel.setAddress(client.getAddress());
                sharedViewModel.setPostalCode(String.valueOf(client.getPostal_code()));
            } else {
                Log.e("MainActivity", "No se encontraron datos del cliente.");
            }
        }

        updateUI(sharedPreferences.getBoolean("isLoggedIn", false));

        iconNotifications.setOnClickListener(this::showNotificationsPopup);

        iconCart.setOnClickListener(v -> {
            List<CartItem> cartItems = new ArrayList<>();
            cartItems.add(new CartItem("Game 1", 3.15, 1, R.drawable.carrito));
            cartItems.add(new CartItem("Console 2", 2.50, 1, R.drawable.carrito));
            cartItems.add(new CartItem("Accessory 3", 1.20, 1, R.drawable.carrito));
            cartItems.add(new CartItem("Game 2", 10.15, 1, R.drawable.carrito));

            CartDialog cartDialog = new CartDialog(this);
            cartDialog.show();
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null && getSupportFragmentManager().getFragments().isEmpty()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .addToBackStack(null)
                    .commit();
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.nav_gallery) {
                selectedFragment = new GalleryFragment();
            } else if (item.getItemId() == R.id.nav_stores) {
                selectedFragment = new StoresFragment();
            } else if (item.getItemId() == R.id.nav_about) {
                selectedFragment = new AboutFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .addToBackStack(null)
                        .commit();
            }

            return true;
        });

        iconProfile.setOnClickListener(v -> {
            boolean currentLoginStatus = sharedPreferences.getBoolean("isLoggedIn", false);
            if (currentLoginStatus) {
                showUserMenu(v);
            } else {
                LoginDialogFragment loginDialog = new LoginDialogFragment();
                loginDialog.setLoginListener(this);
                loginDialog.show(getSupportFragmentManager(), "loginDialog");
            }
        });
    }

    private void updateUI(boolean isLoggedIn) {
        if (isLoggedIn) {
            iconCart.setVisibility(View.VISIBLE);
            iconNotifications.setVisibility(View.VISIBLE);
        } else {
            iconCart.setVisibility(View.GONE);
            iconNotifications.setVisibility(View.GONE);
        }
    }

    private void showUserMenu(View anchor) {
        View menuView = LayoutInflater.from(this).inflate(R.layout.menu_user, null);
        PopupWindow popupWindow = new PopupWindow(menuView,
                (int) (getResources().getDisplayMetrics().widthPixels * 0.85),
                (int) (getResources().getDisplayMetrics().heightPixels * 0.6),
                true);

        TextView userNameTextView = menuView.findViewById(R.id.user_name);
        TextView userEmailTextView = menuView.findViewById(R.id.user_email);

        sharedViewModel.getName().observe(this, name -> {
            String surname = sharedViewModel.getSurname().getValue();
            userNameTextView.setText(name + " " + (surname != null ? surname : ""));
        });

        sharedViewModel.getEmail().observe(this, userEmailTextView::setText);

        popupWindow.showAtLocation(anchor, Gravity.CENTER, 0, 0);

        ImageButton closeButton = menuView.findViewById(R.id.close_menu_button);
        closeButton.setOnClickListener(v -> popupWindow.dismiss());

        menuView.findViewById(R.id.button_account).setOnClickListener(view -> {
            popupWindow.dismiss();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AccountFragment())
                    .addToBackStack(null)
                    .commit();
        });

        menuView.findViewById(R.id.button_logout).setOnClickListener(view -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();
            updateUI(false);
            popupWindow.dismiss();
        });

        menuView.findViewById(R.id.button_orders).setOnClickListener(view -> {
            popupWindow.dismiss();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ListaPedidosFragment())
                    .addToBackStack(null)
                    .commit();
        });

        menuView.findViewById(R.id.button_logout).setOnClickListener(view -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();
            updateUI(false);
            popupWindow.dismiss();
        });
    }

    private void showNotificationsPopup(View view) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_notification, null);
        PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        RecyclerView recyclerView = popupView.findViewById(R.id.recycler_view_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Notification> notifications = new ArrayList<>();
        notifications.add(new Notification("Nuevo mensaje", R.drawable.carrito));
        notifications.add(new Notification("Pedido enviado", R.drawable.casa));
        notifications.add(new Notification("Actualización disponible", R.drawable.store));
        notifications.add(new Notification("Amistad aceptada", R.drawable.usuario));

        NotificationAdapter adapter = new NotificationAdapter(notifications);
        recyclerView.setAdapter(adapter);

        popupWindow.showAsDropDown(view, 0, 0);
    }

    @Override
    public void onLoginSuccess() {
        updateUI(true);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
