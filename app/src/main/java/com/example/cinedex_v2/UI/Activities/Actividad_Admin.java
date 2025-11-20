package com.example.cinedex_v2.UI.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.cinedex_v2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class Actividad_Admin extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private NavController navController;
    private MaterialButton btnPlaylists; // Referencia al botón "Listas"
    private TextView txtTitulo;
    private ImageView btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_actividad_admin);

        // 1. Vincular Vistas
        bottomNav = findViewById(R.id.bottom_navigation_admin);
        btnPlaylists = findViewById(R.id.btn_nav_playlists); // Asegúrate que este ID coincida con tu XML
        txtTitulo = findViewById(R.id.txt_panel_admin);
        btnAdd = findViewById(R.id.btn_add);

        // 2. Configurar Navigation Controller
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment_admin);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(bottomNav, navController);

            // 3. Lógica para el botón "LISTAS" (Esta es la parte que te faltaba)
            btnPlaylists.setOnClickListener(v -> {
                try {
                    // Navegar al fragmento de Playlists
                    navController.navigate(R.id.playlistsFragment);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            // 4. Controlar visibilidad de elementos según la pantalla actual
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                // Si estamos en la pantalla de Playlists...
                if (destination.getId() == R.id.playlistsFragment) {
                    txtTitulo.setText("Gestión de Playlists");
                    // Ocultamos el botón de listas (porque ya estamos ahí) y el botón + del header
                    btnPlaylists.setVisibility(View.GONE);
                    btnAdd.setVisibility(View.GONE);
                }
                // Si estamos en cualquier otra pantalla...
                else {
                    txtTitulo.setText("Panel Admin");
                    btnPlaylists.setVisibility(View.VISIBLE);
                    btnAdd.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
