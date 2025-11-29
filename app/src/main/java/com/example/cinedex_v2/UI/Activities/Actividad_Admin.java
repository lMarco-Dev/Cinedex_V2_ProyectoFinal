package com.example.cinedex_v2.UI.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    // Vistas del Header
    private LinearLayout headerAdmin;
    private MaterialButton btnPlaylists;
    private TextView txtTitulo;
    private ImageView btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_actividad_admin);

        // 1. Vincular Vistas
        bottomNav = findViewById(R.id.bottom_navigation_admin);
        headerAdmin = findViewById(R.id.header_admin);
        btnPlaylists = findViewById(R.id.btn_nav_playlists);
        txtTitulo = findViewById(R.id.txt_panel_admin);
        btnAdd = findViewById(R.id.btn_add);

        // 2. Configurar Navigation Controller
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_admin);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // A. Configuración Inicial del Menú
            NavigationUI.setupWithNavController(bottomNav, navController);

            // B. Interceptar el clic en "Salir" (Logout)
            bottomNav.setOnItemSelectedListener(item -> {
                if (item.getItemId() == R.id.action_logout) {
                    cerrarSesion(); // Llama a tu método de logout
                    return true;
                } else {
                    // Para el resto de items, deja que Navigation maneje el cambio
                    return NavigationUI.onNavDestinationSelected(item, navController);
                }
            });

            // C. Acción del botón "Listas" (Header)
            if(btnPlaylists != null){
                btnPlaylists.setOnClickListener(v -> navController.navigate(R.id.playlistsFragment));
            }

            // D. Controlar visibilidad de elementos según la pantalla actual
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {

                int id = destination.getId();

                // --- CASO 1: PANTALLA DE PELÍCULAS (Muestra todo) ---
                if (id == R.id.peliculasFragment) {
                    if(btnPlaylists != null) btnPlaylists.setVisibility(View.VISIBLE);
                    if(btnAdd != null) btnAdd.setVisibility(View.VISIBLE);
                    txtTitulo.setText("Películas");
                    headerAdmin.setVisibility(View.VISIBLE);
                }
                // --- CASO 2: OTROS CRUDS (Oculta botones extra) ---
                else if (id == R.id.eventosFragment) {
                    ocultarBotonesExtra();
                    txtTitulo.setText("Eventos");
                    headerAdmin.setVisibility(View.VISIBLE);
                }
                else if (id == R.id.noticiasFragment) {
                    ocultarBotonesExtra();
                    txtTitulo.setText("Noticias");
                    headerAdmin.setVisibility(View.VISIBLE);
                }
                else if (id == R.id.cinesFragment) {
                    ocultarBotonesExtra();
                    txtTitulo.setText("Cines");
                    headerAdmin.setVisibility(View.VISIBLE);
                }
                // --- CASO 3: SELECCIÓN DE CINE (Oculta botones extra) ---
                else if (id == R.id.inicioFuncionesFragment) {
                    ocultarBotonesExtra();
                    txtTitulo.setText("Seleccionar Cine");
                    headerAdmin.setVisibility(View.VISIBLE);
                }
                // --- CASO 4: DETALLE DE FUNCIONES (Oculta Header completo) ---
                else if (id == R.id.funcionesFragment) {
                    // Aquí ocultamos todo el header porque el fragmento tiene su propio Toolbar con flecha
                    headerAdmin.setVisibility(View.GONE);
                }
                // --- CASO 5: PLAYLISTS (Oculta botones extra) ---
                else if (id == R.id.playlistsFragment) {
                    ocultarBotonesExtra();
                    // Como Playlists tiene su propio toolbar con flecha, también podrías ocultar el header completo:
                    headerAdmin.setVisibility(View.GONE);
                }
                // --- CASO DEFAULT ---
                else {
                    headerAdmin.setVisibility(View.VISIBLE);
                    ocultarBotonesExtra();
                }
            });
        }
    }

    private void ocultarBotonesExtra() {
        if(btnPlaylists != null) btnPlaylists.setVisibility(View.GONE);
        if(btnAdd != null) btnAdd.setVisibility(View.GONE);
    }

    private void cerrarSesion() {
        // 1. Borrar datos de SharedPreferences
        SharedPreferences prefs = getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear(); // Borra todo
        editor.apply();

        // 2. Ir al Login y borrar historial para que no pueda volver atrás
        Intent intent = new Intent(Actividad_Admin.this, Actividad_Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}