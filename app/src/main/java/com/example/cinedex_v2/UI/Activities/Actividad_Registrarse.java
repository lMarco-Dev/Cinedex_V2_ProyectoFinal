package com.example.cinedex_v2.UI.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cinedex_v2.Data.DTOs.Usuario.UsuarioRegisterRequestDto;
import com.example.cinedex_v2.Data.DTOs.Usuario.UsuarioResponseDto;
import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.Data.Network.CineDexApiService;
import com.example.cinedex_v2.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Actividad_Registrarse extends AppCompatActivity {

    EditText etNombreUsuario, etNombres, etApellidos, etEmail, etPassword, etConfirmPassword;
    FrameLayout btnRegister;
    CineDexApiService apiService;

    TextView tvGoToLogin;

    /* ==================================================================
                        CONECTAMOS LAS CARAS Y EL BOTON
      ================================================================== */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_actividad_registrarse);

        apiService = CineDexApiClient.getApiService();

        etNombreUsuario = findViewById(R.id.etNombreUsuario);
        etNombres = findViewById(R.id.etNombres);
        etApellidos = findViewById(R.id.etApellidos);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        tvGoToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, Actividad_Login.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // ------------ Cuando preciona el boton registrarse
        btnRegister.setOnClickListener(v -> {
            String username = etNombreUsuario.getText().toString().trim();
            String nombres = etNombres.getText().toString().trim();
            String apellidos = etApellidos.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            String confirmPass = etConfirmPassword.getText().toString().trim();

            // Validaciones
            if(username.isEmpty() || nombres.isEmpty() || apellidos.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Por favor, llenar todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!pass.equals(confirmPass)){
                etConfirmPassword.setError("No coinciden");
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            // Empaquetamos todos los datos
            UsuarioRegisterRequestDto registroDto = new UsuarioRegisterRequestDto(username, email, pass, nombres, apellidos);
            intentarRegistro(registroDto, pass); // <-- Pasamos el DTO y la contraseña
        });
    }

    /* ==================================================================
                       COMUNICACIÓN CON EL API
     ================================================================== */
    private void intentarRegistro(UsuarioRegisterRequestDto registroDto, String contrasenaPlana) {

        apiService.register(registroDto).enqueue(new Callback<UsuarioResponseDto>(){
            /* ==================================================================
                                CAMINO FELIZ
            ================================================================== */
            @Override
            public void onResponse(Call<UsuarioResponseDto> call, Response<UsuarioResponseDto> response) {

                if (response.isSuccessful() && response.body() != null) {

                    // ¡ÉXITO DE API!
                    UsuarioResponseDto usuarioCreado = response.body();


                    // 2. GUARDAR LA SESIÓN EN SHAREDPREFERENCES
                    SharedPreferences prefs = getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("ID_USUARIO", usuarioCreado.getIdUsuario());
                    editor.putString("NOMBRE_USUARIO", usuarioCreado.getNombreUsuario());
                    editor.putString("NOMBRES", usuarioCreado.getNombres());
                    editor.putString("APELLIDOS", usuarioCreado.getApellidos());
                    editor.putString("NOMBRE_ROL", usuarioCreado.getRol());
                    editor.putBoolean("ESTA_LOGUEADO", true);
                    editor.apply();

                    // 3. Enviar al usuario a la app
                    Toast.makeText(Actividad_Registrarse.this, "¡Registro exitoso! Iniciando sesión...", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Actividad_Registrarse.this, Actividad_Principal.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                }
                else {
                    String errorMensaje = "Error desconocido al registrar.";
                    if (response.errorBody() != null) {
                        try {
                            errorMensaje = response.errorBody().string();
                        } catch (java.io.IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.e("API_REGISTRO_FALLO", "Código: " + response.code() + " | Mensaje: " + errorMensaje);
                    if (response.code() == 409) {
                        Toast.makeText(Actividad_Registrarse.this, "Usuario o correo ya existe.", Toast.LENGTH_LONG).show();
                    } else if (response.code() == 400) {
                        Toast.makeText(Actividad_Registrarse.this, "Datos inválidos (Revise Logcat).", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(Actividad_Registrarse.this, "Error en el servidor (Revise Logcat).", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UsuarioResponseDto> call, Throwable t) {
                // (Tu código de manejo de fallos está bien)
                String falloMensaje = (t.getMessage() != null) ? t.getMessage() : "Error de conexión";
                Toast.makeText(Actividad_Registrarse.this, "No se pudo conectar al servidor.", Toast.LENGTH_LONG).show();
                Log.e("API_REGISTRO_FALLO", "Fallo de red: " + falloMensaje);
            }
        });
    }
}