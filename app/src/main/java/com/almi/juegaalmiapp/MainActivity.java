package com.almi.juegaalmiapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.almi.juegaalmiapp.adaptadores.NotificationAdapter;
import com.almi.juegaalmiapp.fragmentos.*;
import com.almi.juegaalmiapp.modelo.Client;
import com.almi.juegaalmiapp.modelo.Game;
import com.almi.juegaalmiapp.modelo.Notification;
import com.almi.juegaalmiapp.viewmodel.SharedViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements LoginDialogFragment.LoginListener {

    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private ImageView iconProfile, iconCart, iconNotifications;
    private SharedViewModel sharedViewModel;
    private SharedPreferences sharedPreferences;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ShakeDetector shakeDetector;
    private List<Game> recentGames = new ArrayList<>(); // Almacena la lista de juegos recientes
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private Uri photoUri;
    private ImageView userIcon;


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
        iconNotifications.setVisibility(View.GONE);

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

        // Configuración del detector de vibración
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            shakeDetector = new ShakeDetector(this::openRandomGameDetail);
        }
        // Inicializar el launcher para tomar la foto
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        userIcon.setImageURI(photoUri); // Mostrar la imagen en la interfaz

                        // Subir la imagen al servidor
                        uploadPictureToServer(photoUri);
                    } else {
                        Toast.makeText(this, "Captura de foto cancelada", Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(shakeDetector);
    }

    private void openRandomGameDetail() {
        // Log para confirmar que se detecta la sacudida
        Log.d("MainActivity", "Shake detected! Applying animation.");

        // Cargar la animación desde el XML y aplicarla al elemento deseado (por ejemplo, iconNotifications)



            Random random = new Random();
            int randomNumber = random.nextInt(51) + 10;

            String productId = String.valueOf(randomNumber);

            ProductDetailFragment fragment = ProductDetailFragment.newInstance(productId);
            fragment.setAnimate(true);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
    }

    private Bitmap getResizedBitmap(Uri photoUri, int maxWidth, int maxHeight) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(photoUri);
        Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);

        // Obtener las proporciones originales
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();

        float aspectRatio = (float) width / (float) height;

        // Calcular nuevo tamaño manteniendo la proporción
        if (width > maxWidth || height > maxHeight) {
            if (width > height) {
                width = maxWidth;
                height = Math.round(width / aspectRatio);
            } else {
                height = maxHeight;
                width = Math.round(height * aspectRatio);
            }
        }

        return Bitmap.createScaledBitmap(originalBitmap, width, height, true);
    }

    private File bitmapToFile(Bitmap bitmap) throws IOException {
        File file = new File(getCacheDir(), "resized_image.jpg");
        FileOutputStream out = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out); // Comprimir al 80% para más reducción
        out.flush();
        out.close();
        return file;
    }



    public void setRecentGames(List<Game> games) {
        this.recentGames = games;
    }

    private void updateUI(boolean isLoggedIn) {
        if (isLoggedIn) {
            iconCart.setVisibility(View.VISIBLE);
        } else {
            iconCart.setVisibility(View.GONE);
        }
    }



    private void showUserMenu(View anchor) {
        View menuView = LayoutInflater.from(this).inflate(R.layout.menu_user, null);
        PopupWindow popupWindow = new PopupWindow(menuView,
                (int) (getResources().getDisplayMetrics().widthPixels * 0.85),
                (int) (getResources().getDisplayMetrics().heightPixels * 0.55),
                true);

        TextView userNameTextView = menuView.findViewById(R.id.user_name);
        TextView userEmailTextView = menuView.findViewById(R.id.user_email);

        sharedViewModel.getName().observe(this, name -> {
            String surname = sharedViewModel.getSurname().getValue();
            userNameTextView.setText(name + " " + (surname != null ? surname : ""));
        });

        sharedViewModel.getEmail().observe(this, userEmailTextView::setText);

        popupWindow.showAtLocation(anchor, Gravity.CENTER, 0, 20);

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
        userIcon = menuView.findViewById(R.id.user_icon);
        userIcon.setOnClickListener(v -> requestCameraPermission());
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

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Log.d("CameraDebug", "Intent encontrado: " + (takePictureIntent.resolveActivity(getPackageManager()) != null));

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("MainActivity", "Error al crear el archivo de imagen", ex);
            }

            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this, "com.almi.juegaalmiapp.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                takePictureLauncher.launch(takePictureIntent);
            } else {
                Toast.makeText(this, "No se pudo crear el archivo para guardar la imagen", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No se encontró una aplicación de cámara", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadPictureToServer(Uri photoUri) {
        try {
            // Redimensionar la imagen
            Bitmap resizedBitmap = getResizedBitmap(photoUri, 800, 800); // Tamaño máximo: 800x800
            File resizedFile = bitmapToFile(resizedBitmap);

            // Crear MultipartBody.Part
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), resizedFile);
            MultipartBody.Part body = MultipartBody.Part.createFormData("picture", resizedFile.getName(), requestBody);

            // Obtener el token
            String token = "Bearer " + new ClienteService(this).getToken();

            // Llamar al servicio
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<Void> call = apiService.uploadClientPicture(token, body);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Imagen subida con éxito", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("MainActivity", "Error en la respuesta: " + response.message());
                        Toast.makeText(MainActivity.this, "Error del servidor: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("MainActivity", "Error en la red: " + t.getMessage(), t);
                    Toast.makeText(MainActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException e) {
            Toast.makeText(this, "Error al procesar la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("MainActivity", "Error redimensionando la imagen", e);
        }
    }



    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Permiso de cámara requerido para tomar fotos", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
