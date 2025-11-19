package com.example.cinedex_v2.UI.AdminFragments;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.cinedex_v2.UI.AdaptersAdmin.CineAdapter;
import com.example.cinedex_v2.Data.DTOs.Cine.CineRequest;
import com.example.cinedex_v2.Data.DTOs.Cine.CineResponse;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CinesFragment extends Fragment
        implements CineAdapter.OnCineClickListener, GuardarCineDialog.OnCineGuardadoListener {

    private RecyclerView rvCines;
    private CineAdapter adapter;
    private List<CineResponse> listaCines;
    private ProgressBar progressBar;
    private CineDexApiService apiService;
    private FloatingActionButton fabAgregar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Asegúrate que el nombre del layout coincida con el archivo XML que creamos
        return inflater.inflate(R.layout.fragment_cines, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Título del Panel
        TextView titulo = requireActivity().findViewById(R.id.txt_panel_admin);
        if (titulo != null) titulo.setText("Gestión de Cines");

        // Referencias a Vistas
        rvCines = view.findViewById(R.id.rv_cines);
        progressBar = view.findViewById(R.id.progress_bar_cines);
        fabAgregar = view.findViewById(R.id.fab_agregar_cine);

        // Configuración Retrofit
        apiService = CineDexApiClient.getApiService();

        // Configuración RecyclerView
        listaCines = new ArrayList<>();
        adapter = new CineAdapter(getContext(), listaCines, this);
        rvCines.setAdapter(adapter);

        // Botón Flotante (Agregar)
        fabAgregar.setOnClickListener(v -> {
            GuardarCineDialog dialog = GuardarCineDialog.newInstance(null); // null = Crear Nuevo
            dialog.setTargetFragment(CinesFragment.this, 0);
            dialog.show(getParentFragmentManager(), "GuardarCineDialog");
        });

        // Cargar datos iniciales
        cargarCinesDesdeApi();
    }

    // ===============================================================
    //                       API: CARGAR DATOS
    // ===============================================================
    private void cargarCinesDesdeApi() {
        mostrarCarga(true);

        // Llama a @GET("api/Cines")
        Call<List<CineResponse>> call = apiService.getCines();

        call.enqueue(new Callback<List<CineResponse>>() {
            @Override
            public void onResponse(Call<List<CineResponse>> call, Response<List<CineResponse>> response) {
                mostrarCarga(false);
                if (response.isSuccessful() && response.body() != null) {
                    listaCines.clear();
                    listaCines.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error al cargar cines: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CineResponse>> call, Throwable t) {
                mostrarCarga(false);
                Toast.makeText(getContext(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ===============================================================
    //                 LÓGICA DE GUARDADO (Con Cloudinary)
    // ===============================================================
    @Override
    public void onCineGuardado(CineRequest request, @Nullable Uri imagenUri, @Nullable Integer idToUpdate) {

        // --- CASO 1: IMAGEN NUEVA SELECCIONADA (Subida Asíncrona) ---
        if (imagenUri != null) {
            mostrarCarga(true);
            File file = CloudinaryUploader.getFileFromUri(requireContext(), imagenUri);

            new Thread(() -> {
                try {
                    // 1. Subir a Cloudinary (Tarda unos segundos)
                    String url = CloudinaryUploader.uploadImage(file);

                    // 2. Volver al hilo principal para llamar a la API con la URL lista
                    requireActivity().runOnUiThread(() -> {
                        request.setUrlImagen(url);
                        guardarCineEnApi(request, idToUpdate);
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    requireActivity().runOnUiThread(() -> {
                        mostrarCarga(false);
                        Toast.makeText(getContext(), "Error al subir imagen", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        }
        // --- CASO 2: NO HAY IMAGEN NUEVA (Llamada directa) ---
        else {
            if (idToUpdate != null) {
                // MODO EDITAR: Si el usuario no cambió la foto, mantenemos la antigua.
                // Buscamos la URL en la lista actual para no perderla.
                String urlAntigua = listaCines.stream()
                        .filter(c -> c.getIdCine() == idToUpdate)
                        .findFirst()
                        .map(CineResponse::getUrlImagen)
                        .orElse("https://i.imgur.com/GzP738B.png"); // Fallback
                request.setUrlImagen(urlAntigua);

            } else {
                // MODO CREAR: Asignamos Placeholder (Obligatorio para BD Not Null)
                request.setUrlImagen("https://i.imgur.com/GzP738B.png");
            }

            // Llamada directa a la API
            guardarCineEnApi(request, idToUpdate);
        }
    }

    private void guardarCineEnApi(CineRequest request, @Nullable Integer idToUpdate) {
        mostrarCarga(true);
        Call<Void> call;

        // Determinamos si es CREAR o EDITAR según el ID
        if (idToUpdate == null) {
            // Llama a @POST("api/Cines") - Devuelve Call<Void>
            call = apiService.crearCine(request);
        } else {
            // Llama a @PUT("api/Cines/{id}") - Devuelve Call<Void>
            call = apiService.editarCine(idToUpdate, request);
        }

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    String mensaje = (idToUpdate == null) ? "Cine creado" : "Cine actualizado";
                    Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
                    cargarCinesDesdeApi(); // Recargar lista para ver cambios
                } else {
                    mostrarCarga(false);
                    try {
                        // Log del error para debug
                        String errorBody = response.errorBody().string();
                        Log.e("API_ERROR", "Error al guardar cine: " + errorBody);
                    } catch (Exception e) {}
                    Toast.makeText(getContext(), "Error al guardar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                mostrarCarga(false);
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ===============================================================
    //                 ACCIONES DEL ADAPTER (Click en Botones)
    // ===============================================================
    @Override
    public void onEditarClick(CineResponse cine) {
        // Abrimos el diálogo pasándole el objeto Cine para que rellene los campos
        GuardarCineDialog dialog = GuardarCineDialog.newInstance(cine);
        dialog.setTargetFragment(this, 0);
        dialog.show(getParentFragmentManager(), "EditarCineDialog");
    }

    @Override
    public void onEliminarClick(CineResponse cine) {
        // Diálogo de confirmación antes de borrar
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Eliminar Cine")
                .setMessage("¿Estás seguro de eliminar el cine \"" + cine.getNombre() + "\"?")
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Eliminar", (dialog, which) -> {

                    mostrarCarga(true);

                    // Llama a @DELETE("api/Cines/{id}")
                    apiService.eliminarCine(cine.getIdCine()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Cine eliminado", Toast.LENGTH_SHORT).show();
                                cargarCinesDesdeApi(); // Recargar lista
                            } else {
                                mostrarCarga(false);
                                Toast.makeText(getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            mostrarCarga(false);
                            Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .show();
    }

    // ===============================================================
    //                      UTILIDADES UI
    // ===============================================================
    private void mostrarCarga(boolean cargando) {
        if (cargando) {
            progressBar.setVisibility(View.VISIBLE);
            rvCines.setVisibility(View.GONE);
            fabAgregar.hide(); // Ocultar botón + mientras carga
        } else {
            progressBar.setVisibility(View.GONE);
            rvCines.setVisibility(View.VISIBLE);
            fabAgregar.show();
        }
    }
}