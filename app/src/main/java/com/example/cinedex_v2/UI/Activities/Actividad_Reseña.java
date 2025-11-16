package com.example.cinedex_v2.UI.Activities;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinedex_v2.Data.DTOs.Pelicula.PeliculaResponse;
import com.example.cinedex_v2.Data.DTOs.Resena.ResenaRequestDto;
import com.example.cinedex_v2.Data.DTOs.Resena.ResenaResponseDto;
import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.Data.Network.CineDexApiService;
import com.example.cinedex_v2.R;
import com.example.cinedex_v2.UI.Adapters.ResenaAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Actividad_Reseña extends AppCompatActivity {

    private TextView tvNombreUsuario, tvCorreoUsuario, tvUbicacion, tvResumen;
    private Spinner spinnerPeliculas;
    private EditText etComentario;
    private RatingBar ratingBar;
    private Button btnUbicacion, btnPublicar;
    private RecyclerView rvResenas;

    private CineDexApiService apiService;
    private ResenaAdapter adapter;

    private List<ResenaResponseDto> resenas = new ArrayList<>();
    private List<PeliculaResponse> peliculasSpinner = new ArrayList<>();

    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<String> solicitarPermiso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_actividad_resena);

        // Vistas
        tvNombreUsuario = findViewById(R.id.tvNombreUsuario);
        tvCorreoUsuario = findViewById(R.id.tvCorreoUsuario);
        tvUbicacion = findViewById(R.id.tvUbicacion);
        tvResumen = findViewById(R.id.tvResumenEstrellas);
        spinnerPeliculas = findViewById(R.id.spinnerPeliculas);
        etComentario = findViewById(R.id.etComentario);
        ratingBar = findViewById(R.id.ratingBarPuntaje);
        btnUbicacion = findViewById(R.id.btnObtenerUbicacion);
        btnPublicar = findViewById(R.id.btnPublicarResena);
        rvResenas = findViewById(R.id.rvResenas);

        apiService = CineDexApiClient.getApiService();

        adapter = new ResenaAdapter(resenas);
        rvResenas.setLayoutManager(new LinearLayoutManager(this));
        rvResenas.setAdapter(adapter);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);



        cargarDatosUsuario();
        cargarPeliculasDesdeApi();
        cargarResenasDesdeApi();


        btnPublicar.setOnClickListener(v -> publicarResena());
    }

    private void cargarDatosUsuario() {
        SharedPreferences prefs = getSharedPreferences("sesion_usuario", MODE_PRIVATE);
        tvNombreUsuario.setText(prefs.getString("NOMBRE_USUARIO", "Usuario"));
        tvCorreoUsuario.setText(prefs.getString("EMAIL_USUARIO", "correo@ejemplo.com"));
    }

    private void cargarPeliculasDesdeApi() {
        apiService.getPeliculas().enqueue(new Callback<List<PeliculaResponse>>() {
            @Override
            public void onResponse(Call<List<PeliculaResponse>> call, Response<List<PeliculaResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    peliculasSpinner.clear();
                    peliculasSpinner.addAll(response.body());

                    List<String> titulos = new ArrayList<>();
                    for (PeliculaResponse p : peliculasSpinner) titulos.add(p.getTitulo());

                    spinnerPeliculas.setAdapter(new ArrayAdapter<>(
                            Actividad_Reseña.this,
                            android.R.layout.simple_spinner_dropdown_item,
                            titulos
                    ));
                }
            }

            @Override
            public void onFailure(Call<List<PeliculaResponse>> call, Throwable t) {
                Toast.makeText(Actividad_Reseña.this, "Error al cargar películas: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void publicarResena() {
        int posicion = spinnerPeliculas.getSelectedItemPosition();
        if (posicion == -1 || peliculasSpinner.isEmpty()) {
            Toast.makeText(this, "Selecciona una película", Toast.LENGTH_SHORT).show();
            return;
        }

        PeliculaResponse peliculaSeleccionada = peliculasSpinner.get(posicion);
        String comentario = etComentario.getText().toString().trim();
        float puntaje = ratingBar.getRating();

        if (comentario.isEmpty() || puntaje == 0) {
            Toast.makeText(this, "Completa el comentario y el puntaje", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("sesion_usuario", MODE_PRIVATE);
        int idUsuario = prefs.getInt("ID_USUARIO", -1);
        if (idUsuario == -1) {
            Toast.makeText(this, "Usuario no logueado", Toast.LENGTH_SHORT).show();
            return;
        }

        ResenaRequestDto request = new ResenaRequestDto(
                idUsuario,
                peliculaSeleccionada.getIdPelicula(),
                comentario,
                puntaje
        );

        apiService.crearResena(request).enqueue(new Callback<ResenaResponseDto>() {
            @Override
            public void onResponse(Call<ResenaResponseDto> call, Response<ResenaResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(Actividad_Reseña.this, "Reseña guardada correctamente", Toast.LENGTH_SHORT).show();
                    etComentario.setText("");
                    ratingBar.setRating(0);

                    // Actualizar la lista de reseñas agregando la nueva
                    resenas.add(response.body());
                    adapter.notifyDataSetChanged();
                    actualizarResumen();
                } else {
                    Toast.makeText(Actividad_Reseña.this, "Error al guardar reseña: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResenaResponseDto> call, Throwable t) {
                Toast.makeText(Actividad_Reseña.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void cargarResenasDesdeApi() {
        apiService.getResenas().enqueue(new Callback<List<ResenaResponseDto>>() {
            @Override
            public void onResponse(Call<List<ResenaResponseDto>> call, Response<List<ResenaResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    resenas.clear();
                    resenas.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    actualizarResumen();
                }
            }

            @Override
            public void onFailure(Call<List<ResenaResponseDto>> call, Throwable t) {
                Toast.makeText(Actividad_Reseña.this, "Error al cargar reseñas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarResumen() {
        if (resenas.isEmpty()) {
            tvResumen.setText("0 reseñas • ⭐ 0.0");
            return;
        }

        float total = 0;
        for (ResenaResponseDto r : resenas) total += r.getPuntuacion();

        float promedio = total / resenas.size();
        tvResumen.setText(resenas.size() + " reseñas • ⭐ " + String.format("%.1f", promedio));
    }
}
