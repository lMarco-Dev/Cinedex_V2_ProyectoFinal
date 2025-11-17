package com.example.cinedex_v2.UI.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.cinedex_v2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Actividad_Admin extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_actividad_admin);

        bottomNav = findViewById(R.id.bottom_navigation_admin);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment_admin);

        navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(bottomNav, navController);
    }
}
