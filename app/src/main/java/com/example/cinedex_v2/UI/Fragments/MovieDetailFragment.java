// Archivo: UI/Fragments/MovieDetailFragment.java
package com.example.cinedex_v2.UI.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.cinedex_v2.Data.DTOs.Pelicula.PeliculaResponse;
import com.example.cinedex_v2.Data.DTOs.Resena.ResenaRequestDto;
import com.example.cinedex_v2.Data.DTOs.Resena.MensajeRespuestaDto;
import com.example.cinedex_v2.Data.DTOs.Resena.ResenaResponseDto;
import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.Data.Network.CineDexApiService;
import com.example.cinedex_v2.R;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Implementa la interfaz del DIÁLOGO
public class MovieDetailFragment extends Fragment implements ResenaDialogFragment.ResenaDialogListener {

    private int movieId;

    // Servicio de API
    private CineDexApiService cineDexApiService;

    // Vistas
    private ImageView detailBackdrop, detailPoster;
    private TextView detailTitle, detailDescription;
    private RatingBar detailRating;
    private Button detailReviewButton;
    private Toolbar toolbar;
    private TextView detailTextRating, detailTextRuntime, detailTextYear;

    // Variable para guardar la película actual
    private PeliculaResponse peliculaActual;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.movieId = getArguments().getInt("movieId");
        }
        cineDexApiService = CineDexApiClient.getApiService();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ly_fragment_movie_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Enlazar las vistas
        toolbar = view.findViewById(R.id.detail_toolbar);
        detailBackdrop = view.findViewById(R.id.detail_backdrop);
        detailPoster = view.findViewById(R.id.detail_poster);
        detailTitle = view.findViewById(R.id.detail_title);
        detailRating = view.findViewById(R.id.detail_rating);
        detailDescription = view.findViewById(R.id.detail_description);
        detailReviewButton = view.findViewById(R.id.detail_review_button);
        detailTextRating = view.findViewById(R.id.detail_text_rating);
        detailTextYear = view.findViewById(R.id.detail_text_year);
        detailTextRuntime = view.findViewById(R.id.detail_text_runtime);

        setupToolbar();

        detailReviewButton.setOnClickListener(v -> mostrarDialogResena());

        fetchMovieDetails();
    }

    private void setupToolbar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());
    }

    private void fetchMovieDetails() {
        Call<PeliculaResponse> call = cineDexApiService.getPelicula(movieId);
        call.enqueue(new Callback<PeliculaResponse>() {
            @Override
            public void onResponse(Call<PeliculaResponse> call, Response<PeliculaResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                } else {
                    detailDescription.setText("No se pudieron cargar los detalles.");
                }
            }

            @Override
            public void onFailure(Call<PeliculaResponse> call, Throwable t) {
                detailDescription.setText("Error de conexión.");
            }
        });
    }

    private void updateUI(PeliculaResponse pelicula) {
        this.peliculaActual = pelicula;

        detailTitle.setText(pelicula.getTitulo());
        detailDescription.setText(pelicula.getDescripcion());

        float rating = (float) pelicula.getNotaPromedio();
        detailRating.setRating(rating);
        detailTextRating.setText(String.format(Locale.US, "%.1f", pelicula.getNotaPromedio()));

        detailTextYear.setText("N/A"); // opcional: agregar año si lo tienes en tu backend
        detailTextRuntime.setText(pelicula.getDuracionMin() != null ? pelicula.getDuracionMin() + " min" : "N/A");

        if (getContext() != null) {
            Glide.with(getContext())
                    .load(pelicula.getUrlPoster())
                    .placeholder(R.drawable.bg_poster_placeholder)
                    .into(detailPoster);

            Glide.with(getContext())
                    .load(pelicula.getUrlPoster())
                    .placeholder(R.drawable.bg_poster_placeholder)
                    .into(detailBackdrop);
        }
    }

    private void mostrarDialogResena() {
        ResenaDialogFragment dialog = new ResenaDialogFragment();
        dialog.setResenaDialogListener(this);
        dialog.show(getParentFragmentManager(), "ResenaDialog");
    }

    @Override
    public void onResenaGuardada(String comentario, float puntaje) {
        int idUsuario = getUsuarioIdLogueado();

        if (this.peliculaActual == null || idUsuario == -1) {
            Toast.makeText(getContext(), "Error: Datos de la película no cargados.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (puntaje < 0.5f) puntaje = 0.5f;
        if (puntaje > 5.0f) puntaje = 5.0f;

        String texto = comentario != null ? comentario : "";
        if (texto.length() > 1000) texto = texto.substring(0, 1000);

        ResenaRequestDto request = new ResenaRequestDto(
                idUsuario,
                this.peliculaActual.getIdPelicula(),
                texto,
                puntaje
        );

        enviarResena(request);
    }

    private void enviarResena(ResenaRequestDto request) {
        Toast.makeText(getContext(), "Guardando reseña...", Toast.LENGTH_SHORT).show();

        // Llamada al método correcto de la API
        Call<ResenaResponseDto> call = cineDexApiService.crearResena(request);

        call.enqueue(new Callback<ResenaResponseDto>() {
            @Override
            public void onResponse(Call<ResenaResponseDto> call, Response<ResenaResponseDto> response) {
                if (getContext() == null) return;

                String mensaje;
                if (response.isSuccessful() && response.body() != null) {
                    mensaje = "Reseña guardada correctamente.";
                } else {
                    mensaje = "Error al guardar reseña. Código: " + response.code();
                }

                Toast.makeText(getContext(), mensaje, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResenaResponseDto> call, Throwable t) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    private int getUsuarioIdLogueado() {
        SharedPreferences prefs = getActivity().getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        return prefs.getInt("ID_USUARIO", -1);
    }
}
