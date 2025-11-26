package com.example.cinedex_v2.UI.Activities;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.cinedex_v2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Actividad_Principal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_actividad_principal);

        /* ===================================================================
                                CONEXIÓN PRINCIPAL
          =================================================================== */
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment); // -> Es la ventana
        NavController navController = navHostFragment.getNavController(); // -> Es lo que decide que se muestra en la ventana

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavigationUI.setupWithNavController(bottomNavigationView, navController); // -> Conecta la barra de navegación con el controlador


        /* ===================================================================
                                BARRA DE NAVEGACIÓN
          =================================================================== */
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {

            //Obtenemos el ID
            int id = destination.getId();

            //Decidimos a que vistas la barra de navegación sera visible o no visible
            if(id == R.id.homeFragment || id == R.id.socialFragment || id == R.id.juegoFragment || id == R.id.cinesUserFragment ||id == R.id.perfilFragment) {
                bottomNavigationView.setVisibility(View.VISIBLE);
            } else {
                bottomNavigationView.setVisibility(View.GONE);
            }
        });
    }
}