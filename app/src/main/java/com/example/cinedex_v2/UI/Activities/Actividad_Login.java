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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ly_actividad_login);

        etUsuario = findViewById(R.id.campo_usuario);
        etPassword = findViewById(R.id.campo_contrasena);
        btnIniciarSesion = findViewById(R.id.btn_ingresar);
        txtIrARegistro = findViewById(R.id.txtRegistrar);

        txtIrARegistro.setOnClickListener(v -> {
            Intent intent = new Intent(this, Actividad_Registrarse.class);
            startActivity(intent);
        });

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

    private void intentarLogin(String username, String password) {
        UsuarioLoginRequestDto dto = new UsuarioLoginRequestDto(username, password);
        CineDexApiService api = CineDexApiClient.getApiService();
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

                    // ¡AQUÍ GUARDAMOS LOS DATOS CORRECTAMENTE!
                    guardarSesion(usuario);

                    Toast.makeText(Actividad_Login.this, "Bienvenido, " + usuario.getNombres() + "!", Toast.LENGTH_SHORT).show();
                    redirigirSegunRol(usuario);
                } else {
                    Toast.makeText(Actividad_Login.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UsuarioResponseDto> call, Throwable t) {
                Toast.makeText(Actividad_Login.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarSesion(UsuarioResponseDto usuario) {
        SharedPreferences prefs = getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("ID_USUARIO", usuario.getIdUsuario());
        editor.putString("NOMBRE_USUARIO", usuario.getNombreUsuario()); // Login (user1)
        editor.putString("NOMBRES", usuario.getNombres());              // Nombre Real (Juan)
        editor.putString("APELLIDOS", usuario.getApellidos());          // Apellido (Perez)
        editor.putString("EMAIL_USUARIO", usuario.getEmail());          // Email
        editor.putString("NOMBRE_ROL", usuario.getRol());
        editor.putBoolean("ESTA_LOGUEADO", true);

        editor.apply(); // ¡Guardar!
    }

    private void redirigirSegunRol(UsuarioResponseDto usuario) {
        Intent intent;
        if ("Admin".equalsIgnoreCase(usuario.getRol())) {
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
        finish();
    }
}