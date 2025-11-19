package com.example.cinedex_v2.UI.UsersFragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.cinedex_v2.Data.DTOs.Resena.ResenaResponseDto;
import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.Data.Network.CineDexApiService;
import com.example.cinedex_v2.R;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailFragment extends Fragment {

    private int movieId;
    private CineDexApiService apiService;

    // Vistas
    private ImageView detailBackdrop, detailPoster;
    private TextView detailTitle, detailDescription;
    private RatingBar detailRating;
    private Button detailReviewButton;
    private Toolbar toolbar;
    private TextView detailTextRating, detailTextRuntime, detailTextYear;

    private PeliculaResponse peliculaActual;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.movieId = getArguments().getInt("movieId");
        }
        apiService = CineDexApiClient.getApiService();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ly_fragment_movie_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Vincular vistas
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

        // Click en "Escribir Reseña" -> Abre el diálogo
        detailReviewButton.setOnClickListener(v -> mostrarDialogoResena());

        cargarDetallePelicula();
    }

    private void setupToolbar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }
        toolbar.setNavigationOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());
    }

    private void cargarDetallePelicula() {
        Call<PeliculaResponse> call = apiService.getPelicula(movieId);
        call.enqueue(new Callback<PeliculaResponse>() {
            @Override
            public void onResponse(Call<PeliculaResponse> call, Response<PeliculaResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                } else {
                    if(getContext()!=null) Toast.makeText(getContext(), "Error al cargar película", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<PeliculaResponse> call, Throwable t) {
                if(getContext()!=null) Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(PeliculaResponse pelicula) {
        this.peliculaActual = pelicula;

        detailTitle.setText(pelicula.getTitulo());
        detailDescription.setText(pelicula.getDescripcion());
        detailRating.setRating((float) pelicula.getNotaPromedio());
        detailTextRating.setText(String.format(Locale.US, "%.1f", pelicula.getNotaPromedio()));

        // Si tu API tuviera año, lo pondrías aquí. Si no, N/A.
        detailTextYear.setText("N/A");

        detailTextRuntime.setText(pelicula.getDuracionMin() != null ? pelicula.getDuracionMin() + " min" : "N/A");

        if (getContext() != null) {
            Glide.with(getContext())
                    .load(pelicula.getUrlPoster())
                    .placeholder(R.drawable.bg_poster_placeholder) // Asegúrate de tener esta imagen o usa ic_launcher_background
                    .into(detailPoster);

            Glide.with(getContext())
                    .load(pelicula.getUrlPoster())
                    .placeholder(R.drawable.bg_poster_placeholder)
                    .into(detailBackdrop);
        }
    }

    // =================================================================
    //  MÉTODO PARA ABRIR EL DIÁLOGO DE RESEÑA (Integrado aquí)
    // =================================================================
    private void mostrarDialogoResena() {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // Inflamos TU diseño XML (dialog_nueva_resena.xml)
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

        // Acciones
        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnGuardar.setOnClickListener(v -> {
            float puntaje = ratingBar.getRating();
            String comentario = etComentario.getText().toString().trim();

            if (puntaje == 0) {
                Toast.makeText(getContext(), "Por favor, califica la película", Toast.LENGTH_SHORT).show();
                return;
            }

            enviarResenaApi(puntaje, comentario, dialog);
        });

        dialog.show();
    }

    private void enviarResenaApi(float puntaje, String comentario, AlertDialog dialog) {
        if (getActivity() == null) return;

        SharedPreferences prefs = getActivity().getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        int idUsuario = prefs.getInt("ID_USUARIO", -1);

        if (idUsuario == -1) {
            Toast.makeText(getContext(), "Debes iniciar sesión para reseñar", Toast.LENGTH_SHORT).show();
            return;
        }

        ResenaRequestDto request = new ResenaRequestDto(
                idUsuario,
                peliculaActual.getIdPelicula(),
                comentario,
                puntaje
        );

        apiService.crearResena(request).enqueue(new Callback<ResenaResponseDto>() {
            @Override
            public void onResponse(Call<ResenaResponseDto> call, Response<ResenaResponseDto> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "¡Reseña guardada!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                    // Opcional: Recargar los datos de la película para actualizar el promedio
                    cargarDetallePelicula();
                } else {
                    Toast.makeText(getContext(), "Error al guardar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResenaResponseDto> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}