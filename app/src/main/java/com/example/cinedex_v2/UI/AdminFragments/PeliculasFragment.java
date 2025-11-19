package com.example.cinedex_v2.UI.AdminFragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinedex_v2.Data.Cloudinary.CloudinaryUploader;
import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.Data.Network.CineDexApiService;
import com.example.cinedex_v2.R;
import com.example.cinedex_v2.UI.AdaptersAdmin.PeliculaAdapter;
import com.example.cinedex_v2.Data.DTOs.Pelicula.PeliculaRequest;
import com.example.cinedex_v2.Data.DTOs.Pelicula.PeliculaResponse;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PeliculasFragment extends Fragment
        implements PeliculaAdapter.OnPeliculaClickListener, GuardarPeliculaDialog.OnPeliculaGuardadaListener {

    private RecyclerView rvPeliculas;
    private PeliculaAdapter adapter;
    private List<PeliculaResponse> listaPeliculas;
    private ProgressBar progressBar;
    private CineDexApiService apiService;

    private ImageView btnAdd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_peliculas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ---- Recuperar elementos del Header que están en la Actividad ----
        btnAdd = requireActivity().findViewById(R.id.btn_add);
        TextView titulo = requireActivity().findViewById(R.id.txt_panel_admin);

        // Cambiar título dinámico
        if (titulo != null) titulo.setText("Películas");

        // Asignar acción al botón "+"
        if (btnAdd != null) {
            btnAdd.setOnClickListener(v -> {
                GuardarPeliculaDialog dialog = GuardarPeliculaDialog.newInstance(null);
                dialog.setTargetFragment(PeliculasFragment.this, 0);
                dialog.show(getParentFragmentManager(), "GuardarPeliculaDialog");
            });
        }

        // ---- Recycler y carga ----
        rvPeliculas = view.findViewById(R.id.rv_peliculas);
        progressBar = view.findViewById(R.id.progress_bar_peliculas);

        apiService = CineDexApiClient.getApiService();

        listaPeliculas = new ArrayList<>();
        adapter = new PeliculaAdapter(getContext(), listaPeliculas, this);
        rvPeliculas.setAdapter(adapter);

        cargarPeliculasDesdeApi();
    }

    // -------------------------------------------------------------
    // ---------------------- API CALLS ----------------------------
    // -------------------------------------------------------------

    private void cargarPeliculasDesdeApi() {
        mostrarCarga(true);

        Call<List<PeliculaResponse>> call = apiService.getPeliculas();

        call.enqueue(new Callback<List<PeliculaResponse>>() {
            @Override
            public void onResponse(Call<List<PeliculaResponse>> call, Response<List<PeliculaResponse>> response) {
                mostrarCarga(false);

                if (response.isSuccessful() && response.body() != null) {
                    listaPeliculas.clear();
                    listaPeliculas.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error al cargar películas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PeliculaResponse>> call, Throwable t) {
                mostrarCarga(false);
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* ==============================================================
                        SUBIR A CLOUDINARY Y GUARDAR
       ============================================================== */
    @Override
    public void onPeliculaGuardada(PeliculaRequest request, @Nullable Uri imagenUri, @Nullable Integer peliculaIdToUpdate) {

        // CASO 1: SELECCIONÓ IMAGEN NUEVA (Hay que subirla)
        if (imagenUri != null) {
            mostrarCarga(true); // Bloqueamos pantalla
            File file = CloudinaryUploader.getFileFromUri(requireContext(), imagenUri);

            new Thread(() -> {
                try {
                    // 1. Subida de imagen
                    String url = CloudinaryUploader.uploadImage(file);

                    // 2. Volver al hilo principal
                    requireActivity().runOnUiThread(() -> {
                        request.setUrlPoster(url);
                        // 3. Guardar en API (SOLO AQUI)
                        guardarPeliculaEnApi(request, peliculaIdToUpdate);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    requireActivity().runOnUiThread(() -> {
                        mostrarCarga(false);
                        Toast.makeText(getContext(), "Error al subir la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();

        }
        // CASO 2: NO SELECCIONÓ IMAGEN (Mantenemos la vieja o nulo)
        else {
            if (peliculaIdToUpdate != null) {
                // Modo Edición: Buscar URL antigua
                String urlAntigua = listaPeliculas.stream()
                        .filter(p -> p.getIdPelicula() == peliculaIdToUpdate)
                        .findFirst()
                        .map(PeliculaResponse::getUrlPoster)
                        .orElse(null);
                request.setUrlPoster(urlAntigua);
            }

            // Guardar en API inmediatamente (SOLO AQUI)
            guardarPeliculaEnApi(request, peliculaIdToUpdate);
        }

        // ¡IMPORTANTE! Aquí abajo NO debe haber ninguna llamada a guardarPeliculaEnApi()
    }

    private void guardarPeliculaEnApi(PeliculaRequest request, @Nullable Integer peliculaIdToUpdate) {
        mostrarCarga(true);

        Call<Void> call;

        if (peliculaIdToUpdate == null) {
            call = apiService.crearPelicula(request);
        } else {
            call = apiService.editarPelicula(peliculaIdToUpdate, request);
        }

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Película guardada correctamente", Toast.LENGTH_SHORT).show();
                    cargarPeliculasDesdeApi();
                } else {
                    mostrarCarga(false);
                    Toast.makeText(getContext(), "Error al guardar película", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                mostrarCarga(false);
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // -------------------------------------------------------------
    // ---------------------- UI HELPERS ---------------------------
    // -------------------------------------------------------------

    private void mostrarCarga(boolean cargando) {
        progressBar.setVisibility(cargando ? View.VISIBLE : View.GONE);
        rvPeliculas.setVisibility(cargando ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onEditarClick(PeliculaResponse pelicula) {
        GuardarPeliculaDialog dialog = GuardarPeliculaDialog.newInstance(pelicula);
        dialog.setTargetFragment(this, 0);
        dialog.show(getParentFragmentManager(), "EditarPeliculaDialog");
    }

    @Override
    public void onEliminarClick(PeliculaResponse pelicula) {
        // Diálogo de confirmación
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Confirmar Eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar la película: \"" + pelicula.getTitulo() + "\"? Esta acción no se puede deshacer.")
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    mostrarCarga(true);
                    Call<Void> call = apiService.eliminarPelicula(pelicula.getIdPelicula());

                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Película eliminada", Toast.LENGTH_SHORT).show();
                                cargarPeliculasDesdeApi();
                            } else {
                                mostrarCarga(false);
                                Toast.makeText(getContext(), "Error al eliminar la película", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            mostrarCarga(false);
                            Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .show();
    }
}