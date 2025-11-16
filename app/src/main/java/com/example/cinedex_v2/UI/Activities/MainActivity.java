package com.example.cinedex_v2.UI.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cinedex_v2.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ---------------------------- Jalamos la ids de nuestro xml ------------------------------
        ImageView logo = findViewById(R.id.logoSplash);

        // ---------------------------- Cargamos la animaciones ------------------------------
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        // ---------------------------- Mostrar el logo y texto ------------------------------
        logo.startAnimation(fadeIn);

        // ------------ Secuencia temporizada para la bienvenida --------------------
        // Esperar 3 segundos antes de pasar al login con transición elegante
        new Handler().postDelayed(() -> {
            logo.startAnimation(fadeOut);

            // Luego de la animación, pasamos al login
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(MainActivity.this, Actividad_Login.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }, 800); // Duración para la transición
        }, 2500); // duración total del splash antes del fade out
    }
}