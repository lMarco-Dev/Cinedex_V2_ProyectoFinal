package com.example.cinedex_v2.UI.UsersFragments;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.cinedex_v2.Data.DTOs.Pelicula.PeliculaResponse;
import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JuegoFragment extends Fragment {

    private CardView btnCines, btnStreaming;
    private List<PeliculaResponse> listaCines = new ArrayList<>();
    private List<PeliculaResponse> listaStreaming = new ArrayList<>();
    private boolean isLoading = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_juego, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnCines = view.findViewById(R.id.card_reto_cines);
        btnStreaming = view.findViewById(R.id.card_reto_streaming);

        // Cargar datos al inicio
        cargarPeliculas();

        btnCines.setOnClickListener(v -> {
            if(isLoading) Toast.makeText(getContext(), "Cargando películas...", Toast.LENGTH_SHORT).show();
            else if(listaCines.isEmpty()) Toast.makeText(getContext(), "No hay películas en cines", Toast.LENGTH_SHORT).show();
            else iniciarRuleta(listaCines, "¡Tu película en Cines es!");
        });

        btnStreaming.setOnClickListener(v -> {
            if(isLoading) Toast.makeText(getContext(), "Cargando películas...", Toast.LENGTH_SHORT).show();
            else if(listaStreaming.isEmpty()) Toast.makeText(getContext(), "No hay películas en streaming", Toast.LENGTH_SHORT).show();
            else iniciarRuleta(listaStreaming, "¡Tu película para hoy es!");
        });
    }

    private void cargarPeliculas() {
        CineDexApiClient.getApiService().getPeliculas().enqueue(new Callback<List<PeliculaResponse>>() {
            @Override
            public void onResponse(Call<List<PeliculaResponse>> call, Response<List<PeliculaResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaCines.clear();
                    listaStreaming.clear();

                    for (PeliculaResponse p : response.body()) {
                        if (p.getTipoEstreno() != null) {
                            if (p.getTipoEstreno().equalsIgnoreCase("En cines")) {
                                listaCines.add(p);
                            } else if (p.getTipoEstreno().equalsIgnoreCase("Plataforma de streaming")) {
                                listaStreaming.add(p);
                            }
                        }
                    }
                    isLoading = false;
                }
            }
            @Override
            public void onFailure(Call<List<PeliculaResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                isLoading = false;
            }
        });
    }

    private void iniciarRuleta(List<PeliculaResponse> listaCandidatas, String tituloFinal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_resultado_reto, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false); // No cerrar mientras gira

        ImageView ivPoster = view.findViewById(R.id.iv_resultado_poster);
        TextView tvTitulo = view.findViewById(R.id.tv_titulo_resultado);
        TextView tvNombre = view.findViewById(R.id.tv_nombre_ganador);
        Button btnDetalle = view.findViewById(R.id.btn_ver_detalle);

        dialog.show();

        // --- ANIMACIÓN DE RULETA ---
        final Handler handler = new Handler(Looper.getMainLooper());
        final int[] contador = {0};
        final Random random = new Random();
        final int totalVueltas = 20; // Cuántas veces cambia la imagen
        final int delayBase = 100;   // Velocidad inicial (ms)

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (contador[0] < totalVueltas) {
                    // 1. Mostrar película random temporal
                    PeliculaResponse p = listaCandidatas.get(random.nextInt(listaCandidatas.size()));

                    if(getContext() != null) {
                        Glide.with(getContext()).load(p.getUrlPoster()).into(ivPoster);
                        tvNombre.setText(p.getTitulo());
                    }

                    contador[0]++;
                    // Efecto de desaceleración (se hace más lento al final)
                    int nuevoDelay = delayBase + (contador[0] * 10);
                    handler.postDelayed(this, nuevoDelay);
                } else {
                    // 2. ¡TENEMOS GANADOR!
                    PeliculaResponse ganador = listaCandidatas.get(random.nextInt(listaCandidatas.size()));

                    if(getContext() != null) {
                        Glide.with(getContext()).load(ganador.getUrlPoster()).into(ivPoster);
                        tvNombre.setText(ganador.getTitulo());
                        tvTitulo.setText(tituloFinal);
                        tvNombre.setTextColor(getResources().getColor(R.color.kick_green)); // Resaltar

                        btnDetalle.setVisibility(View.VISIBLE);
                        dialog.setCancelable(true);

                        btnDetalle.setOnClickListener(v -> {
                            dialog.dismiss();
                            // Ir al detalle
                            Bundle bundle = new Bundle();
                            bundle.putInt("movieId", ganador.getIdPelicula());
                            try {
                                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                                        .navigate(R.id.movieDetailFragment, bundle);
                            } catch (Exception e) {}
                        });
                    }
                }
            }
        };

        handler.post(runnable);
    }
}