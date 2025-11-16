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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

    // -------------- Conectamos los botones y la cara ----------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.ly_actividad_login);

        // -- Conectar Vistas ---
        etUsuario = findViewById(R.id.campo_usuario);
        etPassword = findViewById(R.id.campo_contrasena);
        btnIniciarSesion = findViewById(R.id.btn_ingresar);
        txtIrARegistro = findViewById(R.id.txtRegistrar);
        fondoLogin = findViewById(R.id.fondo_login);

        // -- Dirigir al registro --
        txtIrARegistro.setOnClickListener(v -> {
            Intent intent = new Intent(this, Actividad_Registrarse.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        //Boton iniciar Sesión
        btnIniciarSesion.setOnClickListener(v -> {
            String username = etUsuario.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            //Validaciones
            if(username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Llene todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            intentarLogin(username, password);
        });
    }

    /* =============================================================
                            CONECTAMOS A LA API
       ============================================================= */
    private void intentarLogin(String username, String password){
        //Creamos el DTO login
        UsuarioLoginRequestDto loginRequestDto = new UsuarioLoginRequestDto(username, password);

        //Llamamos a la API
        CineDexApiService apiService = CineDexApiClient.getApiService();

        apiService.login(loginRequestDto).enqueue(new Callback<UsuarioResponseDto>() {
            @Override
            public void onResponse(Call<UsuarioResponseDto> call, Response<UsuarioResponseDto> response) {
                /*-----------------------------------------------------------------
                                            LOGIN EXITOSO
                -------------------------------------------------------------------*/
                if(response.isSuccessful() && response.body() != null) {

                    //Si el login es exitoso
                    UsuarioResponseDto usuarioLogueado = response.body();

                    //Abre la mini memoria del telefono
                    SharedPreferences prefsSesion = getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefsSesion.edit();

                    //Guardamos los datos clase del usuario
                    editor.putInt("ID_USUARIO", usuarioLogueado.getIdUsuario());
                    editor.putString("NOMBRE_USUARIO", usuarioLogueado.getNombreUsuario());
                    editor.putString("NOMBRES", usuarioLogueado.getNombres());
                    editor.putString("APELLIDOS", usuarioLogueado.getApellidos());
                    editor.putString("NOMBRE_ROL", usuarioLogueado.getRol());
                    editor.putBoolean("ESTA_LOGUEADO", true);
                    editor.apply();

                    //Enviamos al usuario a la actividad principal
                    Toast.makeText(Actividad_Login.this, "Bienvenido, " + usuarioLogueado.getNombres() + "!", Toast.LENGTH_SHORT).show();

                    // Verifica la memoria para saber si acepto los terminos y condiciones
                    SharedPreferences prefsTerminos = getSharedPreferences("CineDexPrefs", MODE_PRIVATE);
                    boolean acepto = prefsTerminos.getBoolean("TERMINOS_ACEPTADOS", false);


                    Intent intent;
                    if (!acepto) {
                        // Si no ha aceptado, lo mandamos a Términos
                        intent = new Intent(Actividad_Login.this, Actividad_Terminos.class);
                        startActivity(intent);
                        // No cerramos Login, para que pueda volver
                    } else {
                        // Si ya aceptó, lo mandamos a la Principal
                        intent = new Intent(Actividad_Login.this, Actividad_Principal.class);

                        // para que no pueda presionar "Atrás" y volver al Login
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }

                    /*-----------------------------------------------------------------
                                            LOGIN FALLIDO
                    -------------------------------------------------------------------*/
                } else {
                    Log.e("[FALLO LOGIN]", "Código: " + response.code());
                    Toast.makeText(Actividad_Login.this, "Usuario o contraseña incorrecta", Toast.LENGTH_SHORT).show();
                }
            }

            /*-----------------------------------------------------------------
                                        FALLO DE CONEXIÓN
                -------------------------------------------------------------------*/
            @Override
            public void onFailure(Call<UsuarioResponseDto> call, Throwable t) {
                //Error de red
                Log.e("[LOGIN FALLO]", "Error de conexión: " + t.getMessage());
                Toast.makeText(Actividad_Login.this, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }
}