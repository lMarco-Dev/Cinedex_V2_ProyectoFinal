package com.example.cinedex_v2.UI.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cinedex_v2.Data.Access.PreferenciasTerminos;
import com.example.cinedex_v2.Data.DTOs.Usuario.UsuarioLoginRequestDto;
import com.example.cinedex_v2.Data.DTOs.Usuario.UsuarioResponseDto;
import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.Data.Network.CineDexApiService;
import com.example.cinedex_v2.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Actividad_Login extends AppCompatActivity {

    EditText etUsuario, etPassword;
    FrameLayout btnIniciarSesion;
    TextView txtIrARegistro;
    ImageView fondoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ly_actividad_login);

        // ---- Conectar Vistas ----
        etUsuario = findViewById(R.id.campo_usuario);
        etPassword = findViewById(R.id.campo_contrasena);
        btnIniciarSesion = findViewById(R.id.btn_ingresar);
        txtIrARegistro = findViewById(R.id.txtRegistrar);
        fondoLogin = findViewById(R.id.fondo_login);

        // ---- Ir al registro ----
        txtIrARegistro.setOnClickListener(v -> {
            Intent intent = new Intent(this, Actividad_Registrarse.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // ---- Botón iniciar sesión ----
        btnIniciarSesion.setOnClickListener(v -> {
            String username = etUsuario.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if(username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Llene todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            intentarLogin(username, password);
        });
    }


    /* =============================================================
                        INTENTO DE LOGIN (ADMIN O USER)
       ============================================================= */
    private void intentarLogin(String username, String password) {

        UsuarioLoginRequestDto dto = new UsuarioLoginRequestDto(username, password);
        CineDexApiService api = CineDexApiClient.getApiService();

        // ---- Si el usuario es "admin", usamos el endpoint para Admin ----
        Call<UsuarioResponseDto> llamadaApi;

        if (username.equals("admin")) {
            llamadaApi = api.loginAdmin(dto);
        } else {
            llamadaApi = api.login(dto);
        }

        llamadaApi.enqueue(new Callback<UsuarioResponseDto>() {
            @Override
            public void onResponse(Call<UsuarioResponseDto> call, Response<UsuarioResponseDto> response) {

                if (response.isSuccessful() && response.body() != null) {

                    UsuarioResponseDto usuario = response.body();

                    guardarSesion(usuario);

                    Toast.makeText(Actividad_Login.this,
                            "Bienvenido, " + usuario.getNombres() + "!",
                            Toast.LENGTH_SHORT).show();

                    redirigirSegunRol(usuario);

                } else {
                    Toast.makeText(Actividad_Login.this,
                            "Usuario o contraseña incorrecta",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UsuarioResponseDto> call, Throwable t) {
                Log.e("LOGIN_ERROR", t.getMessage());
                Toast.makeText(Actividad_Login.this,
                        "Error de conexión con el servidor",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    /* =============================================================
                         GUARDAR DATOS DE SESIÓN
       ============================================================= */
    private void guardarSesion(UsuarioResponseDto usuario) {

        SharedPreferences prefs = getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("ID_USUARIO", usuario.getIdUsuario());
        editor.putString("NOMBRE_USUARIO", usuario.getNombreUsuario());
        editor.putString("NOMBRES", usuario.getNombres());
        editor.putString("APELLIDOS", usuario.getApellidos());
        editor.putString("NOMBRE_ROL", usuario.getRol());
        editor.putBoolean("ESTA_LOGUEADO", true);

        editor.apply();
    }


    /* =============================================================
                         REDIRECCIONAR SEGÚN ROL
       ============================================================= */
    private void redirigirSegunRol(UsuarioResponseDto usuario) {

        Intent intent;

        if (usuario.getRol().equals("Admin")) {
            intent = new Intent(this, Actividad_Admin.class);
        } else {

            if (PreferenciasTerminos.terminosAceptados(this)) {
                intent = new Intent(this, Actividad_Principal.class);
            } else {
                intent = new Intent(this, Actividad_Terminos.class);
            }
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }
}
