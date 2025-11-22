package com.example.cinedex_v2.UI.AdminFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinedex_v2.Data.DTOs.PeliculaPlaylist.PeliculaPlaylistRequest;
import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.Data.DTOs.Pelicula.PeliculaResponse;
import com.example.cinedex_v2.R;
import com.example.cinedex_v2.UI.AdaptersUser.MovieAdapter;
import com.google.android.material.appbar.MaterialToolbar; // Importante
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistDetailFragment extends Fragment implements MovieAdapter.OnMovieClickListener {

    private int idPlaylist;
    private String nombrePlaylist;

    private RecyclerView rvPeliculas;
    private MovieAdapter adapter;
    private List<PeliculaResponse> listaPeliculasEnPlaylist = new ArrayList<>();
    private List<PeliculaResponse> todasLasPeliculasDB = new ArrayList<>();
    private int idPeliculaSeleccionadaEnDialogo = -1; // Variable temporal para el diálogo

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idPlaylist = getArguments().getInt("id_playlist");
            nombrePlaylist = getArguments().getString("nombre_playlist");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlist_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. CONFIGURAR TOOLBAR (BOTÓN ATRÁS Y TÍTULO)
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_playlist_detail);
        toolbar.setTitle(nombrePlaylist);
        toolbar.setNavigationOnClickListener(v -> {
            // Esto hace que vuelvas al fragmento anterior (PlaylistsFragment)
            Navigation.findNavController(view).popBackStack();
        });

        rvPeliculas = view.findViewById(R.id.rv_peliculas_playlist);
        rvPeliculas.setLayoutManager(new GridLayoutManager(getContext(), 3));

        adapter = new MovieAdapter(getContext(), listaPeliculasEnPlaylist, this);
        rvPeliculas.setAdapter(adapter);

        view.findViewById(R.id.fab_agregar_pelicula).setOnClickListener(v -> mostrarDialogoAgregar());

        // Cargar datos iniciales
        cargarPeliculasDeEstaPlaylist();
        cargarTodasLasPeliculasParaElBuscador();
    }

    private void cargarPeliculasDeEstaPlaylist() {
        // Opcional: Mostrar barra de carga si tienes una
        // progressBar.setVisibility(View.VISIBLE);

        CineDexApiClient.getApiService().getPeliculasDePlaylist(idPlaylist).enqueue(new Callback<List<PeliculaResponse>>() {
            @Override
            public void onResponse(Call<List<PeliculaResponse>> call, Response<List<PeliculaResponse>> response) {
                // Opcional: Ocultar barra de carga
                // progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    // 1. Limpiar la lista actual para evitar duplicados
                    listaPeliculasEnPlaylist.clear();

                    // 2. Agregar lo que llegó del servidor
                    listaPeliculasEnPlaylist.addAll(response.body());

                    // 3. ¡IMPORTANTE! Avisar al adaptador que hay datos nuevos
                    adapter.notifyDataSetChanged();

                    // Debug: Ver en consola cuántas llegaron
                    android.util.Log.d("DEBUG_PLAYLIST", "Películas cargadas: " + listaPeliculasEnPlaylist.size());

                } else {
                    Toast.makeText(getContext(), "Error al cargar datos: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PeliculaResponse>> call, Throwable t) {
                // progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                android.util.Log.e("DEBUG_PLAYLIST", "Error API: " + t.getMessage());
            }
        });
    }

    private void cargarTodasLasPeliculasParaElBuscador() {
        CineDexApiClient.getApiService().getPeliculas().enqueue(new Callback<List<PeliculaResponse>>() {
            @Override
            public void onResponse(Call<List<PeliculaResponse>> call, Response<List<PeliculaResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    todasLasPeliculasDB.clear();
                    todasLasPeliculasDB.addAll(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<PeliculaResponse>> call, Throwable t) {}
        });
    }

    // --- DIÁLOGO CON DROPDOWN (SPINNER) ---
    private void mostrarDialogoAgregar() {
        if (todasLasPeliculasDB.isEmpty()) {
            Toast.makeText(getContext(), "Cargando catálogo, intenta de nuevo...", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_agregar_pelicula_playlist, null);
        builder.setView(dialogView);

        AutoCompleteTextView acPelicula = dialogView.findViewById(R.id.ac_pelicula_busqueda);
        Button btnAgregar = dialogView.findViewById(R.id.btn_agregar_confirmar);

        // Preparamos la lista de nombres
        List<String> titulos = new ArrayList<>();
        for (PeliculaResponse p : todasLasPeliculasDB) titulos.add(p.getTitulo());

        ArrayAdapter<String> adapterDropdown = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, titulos);
        acPelicula.setAdapter(adapterDropdown);

        // Listener para capturar el ID cuando selecciona una opción
        acPelicula.setOnItemClickListener((parent, view, position, id) -> {
            // Buscamos la película seleccionada en la lista original para obtener su ID
            // Nota: El position del adapter coincide con el índice de 'todasLasPeliculasDB' si no filtramos
            idPeliculaSeleccionadaEnDialogo = todasLasPeliculasDB.get(position).getIdPelicula();
        });

        AlertDialog dialog = builder.create();

        btnAgregar.setOnClickListener(v -> {
            // Validación por si el usuario no seleccionó nada
            if(idPeliculaSeleccionadaEnDialogo == -1) {
                // Intento de recuperación manual por texto (por si acaso)
                String texto = acPelicula.getText().toString();
                for(PeliculaResponse p : todasLasPeliculasDB) {
                    if(p.getTitulo().equals(texto)) {
                        idPeliculaSeleccionadaEnDialogo = p.getIdPelicula();
                        break;
                    }
                }
            }

            if(idPeliculaSeleccionadaEnDialogo != -1) {
                guardarRelacionEnAPI(idPeliculaSeleccionadaEnDialogo);
                dialog.dismiss();
                idPeliculaSeleccionadaEnDialogo = -1; // Reiniciar
            } else {
                Toast.makeText(getContext(), "Por favor selecciona una película de la lista", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void guardarRelacionEnAPI(int idPelicula) {
        PeliculaPlaylistRequest req = new PeliculaPlaylistRequest(idPlaylist, idPelicula);

        CineDexApiClient.getApiService().agregarPeliculaAPlaylist(idPlaylist, req).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(getContext(), "Película agregada", Toast.LENGTH_SHORT).show();
                    // ¡AQUÍ ESTÁ LA CLAVE! Recargamos la lista para ver el cambio
                    cargarPeliculasDeEstaPlaylist();
                } else {
                    Toast.makeText(getContext(), "La película ya está en la lista", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- ELIMINAR ---
    @Override
    public void onMovieClick(int movieId) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Quitar Película")
                .setMessage("¿Quitar esta película de la playlist?")
                .setPositiveButton("Quitar", (d, w) -> {
                    CineDexApiClient.getApiService().removerPeliculaDePlaylist(idPlaylist, movieId).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if(response.isSuccessful()) {
                                Toast.makeText(getContext(), "Eliminada", Toast.LENGTH_SHORT).show();
                                cargarPeliculasDeEstaPlaylist(); // Recargar lista
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {}
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}