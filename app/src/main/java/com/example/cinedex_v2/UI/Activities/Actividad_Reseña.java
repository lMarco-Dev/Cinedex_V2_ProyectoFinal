package com.example.cinedex_v2.UI.Activities;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinedex_v2.Data.DTOs.Resena.ResenaRequestDto;
import com.example.cinedex_v2.Data.DTOs.Resena.ResenaResponseDto;
import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.Data.Network.CineDexApiService;
import com.example.cinedex_v2.R;
import com.example.cinedex_v2.UI.AdaptersUser.ProfileReviewAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Actividad_Reseña extends AppCompatActivity {

    private RecyclerView rvResenas;
    private FloatingActionButton fabNuevaResena;

    private CineDexApiService apiService;
    private ProfileReviewAdapter adapter;
    private List<ResenaResponseDto> resenas = new ArrayList<>();

    // ID de la película actual (Harcodeado para probar, luego lo recibes por Intent)
    private int idPeliculaActual = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Usamos el layout de lista simple que te pasé en la respuesta anterior
        setContentView(R.layout.ly_actividad_resena_lista);

        rvResenas = findViewById(R.id.rvResenas);
        fabNuevaResena = findViewById(R.id.fabNuevaResena);

        apiService = CineDexApiClient.getApiService();

        // Configurar lista
        adapter = new ProfileReviewAdapter(
                this,
                resenas,
                resena -> {

                }
        );
        rvResenas.setLayoutManager(new LinearLayoutManager(this));
        rvResenas.setAdapter(adapter);

        // Cargar reseñas existentes
        cargarResenas();

        // Abrir diálogo al hacer clic en +
        fabNuevaResena.setOnClickListener(v -> mostrarDialogoResena());
    }

    private void mostrarDialogoResena() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflamos TU diseño XML corregido
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_nueva_resena, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        // Vincular vistas del diálogo
        RatingBar ratingBar = dialogView.findViewById(R.id.dialog_rating_bar);
        EditText etComentario = dialogView.findViewById(R.id.dialog_edit_text);
        Button btnCancelar = dialogView.findViewById(R.id.dialog_button_cancelar);
        Button btnGuardar = dialogView.findViewById(R.id.dialog_button_guardar);

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnGuardar.setOnClickListener(v -> {
            float puntaje = ratingBar.getRating();
            String comentario = etComentario.getText().toString().trim();

            if (puntaje == 0) {
                Toast.makeText(this, "Selecciona una calificación", Toast.LENGTH_SHORT).show();
                return;
            }

            enviarResenaApi(puntaje, comentario, dialog);
        });

        dialog.show();
    }

    private void enviarResenaApi(float puntaje, String comentario, AlertDialog dialog) {
        SharedPreferences prefs = getSharedPreferences("sesion_usuario", MODE_PRIVATE);
        int idUsuario = prefs.getInt("ID_USUARIO", -1);

        if (idUsuario == -1) {
            Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        ResenaRequestDto request = new ResenaRequestDto(
                idUsuario,
                idPeliculaActual,
                comentario,
                puntaje
        );

        apiService.crearResena(request).enqueue(new Callback<ResenaResponseDto>() {
            @Override
            public void onResponse(Call<ResenaResponseDto> call, Response<ResenaResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(Actividad_Reseña.this, "¡Reseña publicada!", Toast.LENGTH_SHORT).show();

                    // Agregar a la lista y cerrar
                    resenas.add(0, response.body());
                    adapter.notifyItemInserted(0);
                    rvResenas.scrollToPosition(0);

                    dialog.dismiss();
                } else {
                    Toast.makeText(Actividad_Reseña.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResenaResponseDto> call, Throwable t) {
                Toast.makeText(Actividad_Reseña.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarResenas() {
        // Aquí cargas las reseñas de la API usando getResenas() o getResenasPorPelicula()
        // ...
    }
}