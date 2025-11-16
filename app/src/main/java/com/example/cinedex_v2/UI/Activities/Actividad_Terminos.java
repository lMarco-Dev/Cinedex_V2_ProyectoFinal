package com.example.cinedex_v2.UI.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinedex_v2.Data.Access.PreferenciasTerminos;
import com.example.cinedex_v2.R;

public class Actividad_Terminos extends AppCompatActivity {

    private CheckBox checkAceptar;
    private FrameLayout btnContinuar; // <-- FrameLayout en lugar de Button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_actividad_terminos);

        checkAceptar = findViewById(R.id.cbAceptar);
        btnContinuar = findViewById(R.id.btnContinuar);

        btnContinuar.setEnabled(false);

        checkAceptar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            btnContinuar.setEnabled(isChecked);
        });

        btnContinuar.setOnClickListener(v -> {
            PreferenciasTerminos.guardarAceptacion(this);

            Intent intent = new Intent(Actividad_Terminos.this, Actividad_Principal.class);
            startActivity(intent);
            finish();
        });
    }
}
