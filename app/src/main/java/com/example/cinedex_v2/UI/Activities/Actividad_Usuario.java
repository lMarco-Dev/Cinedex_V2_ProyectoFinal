// Archivo: UI/Activities/Actividad_Usuario.java
package com.example.cinedex_v2.UI.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinedex_v2.Data.DTOs.Resena.ResenaResponseDto;
import com.example.cinedex_v2.Data.DTOs.Usuario.UsuarioUpdateRequestDto;
import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.Data.Network.CineDexApiService;
import com.example.cinedex_v2.R;
import com.example.cinedex_v2.UI.Adapters.ResenaAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Actividad_Usuario extends AppCompatActivity {

    // Vistas de UI
    ImageView ivFoto;
    TextView tvNombreCompleto, tvCorreo;
    EditText etNombres, etApellidos, etCorreoEdit, etCambiarPass, etConfirmPass;
    Button btnEditarGuardar, btnCambiarPass, btnSeleccionarFoto;
    RecyclerView rvResenas;
    ResenaAdapter resenaAdapter;

    // Toggles
    TextView tvToggleEditarDatos, tvToggleCambiarPassword;
    LinearLayout layoutEditarDatos, layoutCambiarPassword;

    // Datos
    int usuarioId = -1;
    String authToken = "";

    ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_actividad_usuario);

        // --- Toolbar ---
        MaterialToolbar toolbar = findViewById(R.id.toolbarUsuario);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // --- Vistas ---
        ivFoto = findViewById(R.id.ivFotoUsuario);
        tvNombreCompleto = findViewById(R.id.tvNombreCompleto);
        tvCorreo = findViewById(R.id.tvCorreoPerfil);
        btnSeleccionarFoto = findViewById(R.id.btnSeleccionarFoto);

        tvToggleEditarDatos = findViewById(R.id.tvToggleEditarDatos);
        layoutEditarDatos = findViewById(R.id.layoutEditarDatos);
        etNombres = findViewById(R.id.etNombresPerfil);
        etApellidos = findViewById(R.id.etApellidosPerfil);
        etCorreoEdit = findViewById(R.id.etCorreoPerfil);
        btnEditarGuardar = findViewById(R.id.btnEditarGuardarPerfil);

        tvToggleCambiarPassword = findViewById(R.id.tvToggleCambiarPassword);
        layoutCambiarPassword = findViewById(R.id.layoutCambiarPassword);
        etCambiarPass = findViewById(R.id.etNuevaPassword);
        etConfirmPass = findViewById(R.id.etConfirmarPassword);
        btnCambiarPass = findViewById(R.id.btnCambiarPassword);

        rvResenas = findViewById(R.id.lvResenasUsuario);

        // --- Toggles ---
        tvToggleEditarDatos.setOnClickListener(v -> {
            if (layoutEditarDatos.getVisibility() == View.VISIBLE) layoutEditarDatos.setVisibility(View.GONE);
            else layoutEditarDatos.setVisibility(View.VISIBLE);
        });

        tvToggleCambiarPassword.setOnClickListener(v -> {
            if (layoutCambiarPassword.getVisibility() == View.VISIBLE) layoutCambiarPassword.setVisibility(View.GONE);
            else layoutCambiarPassword.setVisibility(View.VISIBLE);
        });

        // --- Cargar datos de SharedPreferences ---
        SharedPreferences prefs = getSharedPreferences("sesion_usuario", MODE_PRIVATE);
        usuarioId = prefs.getInt("ID_USUARIO", -1);
        String nombres = prefs.getString("NOMBRES", "");
        String apellidos = prefs.getString("APELLIDOS", "");
        String nombreUsuario = prefs.getString("NOMBRE_USUARIO", "");
        authToken = prefs.getString("AUTH_TOKEN", "");

        tvNombreCompleto.setText((nombres + " " + apellidos).trim());
        tvCorreo.setText(nombreUsuario);
        etNombres.setText(nombres);
        etApellidos.setText(apellidos);
        etCorreoEdit.setText(nombreUsuario);

        String fotoUriStr = prefs.getString("URI_FOTO_USUARIO", "");
        if (!TextUtils.isEmpty(fotoUriStr)) ivFoto.setImageURI(Uri.parse(fotoUriStr));

        // --- Selección de foto ---
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selected = result.getData().getData();
                        if (selected != null) {
                            ivFoto.setImageURI(selected);
                            SharedPreferences.Editor ed = getSharedPreferences("sesion_usuario", MODE_PRIVATE).edit();
                            ed.putString("URI_FOTO_USUARIO", selected.toString());
                            ed.apply();
                        }
                    }
                });

        btnSeleccionarFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

        // --- Editar datos ---
        btnEditarGuardar.setOnClickListener(v -> {
            String newNombres = etNombres.getText().toString().trim();
            String newApellidos = etApellidos.getText().toString().trim();

            if (newNombres.isEmpty() || newApellidos.isEmpty()) {
                Toast.makeText(this, "Nombres y apellidos no pueden estar vacíos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!authToken.isEmpty() && usuarioId != -1) {
                CineDexApiService api = CineDexApiClient.getApiService();
                UsuarioUpdateRequestDto dto = new UsuarioUpdateRequestDto();
                dto.setNombres(newNombres);
                dto.setApellidos(newApellidos);

                api.actualizarUsuario(usuarioId, dto).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(Actividad_Usuario.this, "Usuario actualizado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Actividad_Usuario.this, "Error API: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(Actividad_Usuario.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // --- Cambiar contraseña (puedes completar lógica) ---
        btnCambiarPass.setOnClickListener(v -> {
            // Aquí la lógica de cambio de contraseña
        });

        // --- Cargar reseñas ---
        actualizarListaResenas();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void actualizarListaResenas() {
        CineDexApiService api = CineDexApiClient.getApiService();
        api.getResenasPorUsuario(usuarioId).enqueue(new Callback<List<ResenaResponseDto>>() {
            @Override
            public void onResponse(Call<List<ResenaResponseDto>> call, Response<List<ResenaResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ResenaResponseDto> lista = response.body();

                    resenaAdapter = new ResenaAdapter(lista);
                    rvResenas.setLayoutManager(new LinearLayoutManager(Actividad_Usuario.this));
                    rvResenas.setAdapter(resenaAdapter);
                    rvResenas.setNestedScrollingEnabled(false);
                } else {
                    Toast.makeText(Actividad_Usuario.this, "No se encontraron reseñas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ResenaResponseDto>> call, Throwable t) {
                Toast.makeText(Actividad_Usuario.this, "Error al cargar reseñas", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
