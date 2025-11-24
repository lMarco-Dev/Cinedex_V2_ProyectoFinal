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
import com.example.cinedex_v2.UI.AdaptersAdmin.NoticiaAdapter;
import com.example.cinedex_v2.Data.DTOs.Noticia.NoticiaRequest;
import com.example.cinedex_v2.Data.DTOs.Noticia.NoticiaResponse;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoticiasFragment extends Fragment
        implements NoticiaAdapter.OnNoticiaClickListener, GuardarNoticiaDialog.OnNoticiaGuardadaListener {

    private RecyclerView rvNoticias;
    private NoticiaAdapter adapter;
    private List<NoticiaResponse> listaNoticias;
    private ProgressBar progressBar;
    private CineDexApiService apiService;
    private FloatingActionButton fabAgregar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Asegúrate que fragment_noticias.xml tenga los IDs correctos
        return inflater.inflate(R.layout.fragment_noticias, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView titulo = requireActivity().findViewById(R.id.txt_panel_admin);
        titulo.setText("Noticias");

        rvNoticias = view.findViewById(R.id.rv_noticias);
        progressBar = view.findViewById(R.id.progress_bar_noticias);
        fabAgregar = view.findViewById(R.id.fab_agregar_noticia);

        apiService = CineDexApiClient.getApiService();
        listaNoticias = new ArrayList<>();
        adapter = new NoticiaAdapter(getContext(), listaNoticias, true, this);
        rvNoticias.setAdapter(adapter);

        fabAgregar.setOnClickListener(v -> {
            GuardarNoticiaDialog dialog = GuardarNoticiaDialog.newInstance(null);
            dialog.setTargetFragment(NoticiasFragment.this, 0);
            dialog.show(getParentFragmentManager(), "GuardarNoticiaDialog");
        });

        cargarNoticiasDesdeApi();
    }

    private void cargarNoticiasDesdeApi() {
        mostrarCarga(true);
        Call<List<NoticiaResponse>> call = apiService.getNoticias();

        call.enqueue(new Callback<List<NoticiaResponse>>() {
            @Override
            public void onResponse(Call<List<NoticiaResponse>> call, Response<List<NoticiaResponse>> response) {
                mostrarCarga(false);
                if (response.isSuccessful() && response.body() != null) {
                    listaNoticias.clear();
                    listaNoticias.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<NoticiaResponse>> call, Throwable t) {
                mostrarCarga(false);
                Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onNoticiaGuardada(NoticiaRequest request, @Nullable Uri imagenUri, @Nullable Integer idToUpdate) {

        // CASO 1: IMAGEN NUEVA (Subida Asíncrona)
        if (imagenUri != null) {
            mostrarCarga(true);
            File file = CloudinaryUploader.getFileFromUri(requireContext(), imagenUri);

            new Thread(() -> {
                try {
                    String url = CloudinaryUploader.uploadImage(file);
                    requireActivity().runOnUiThread(() -> {
                        request.setUrlImagen(url); // Asignar URL
                        guardarNoticiaEnApi(request, idToUpdate); // Llamar API *DESPUÉS*
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
        // CASO 2: SIN IMAGEN NUEVA (Llamada Directa)
        else {
            if (idToUpdate != null) {
                // Modo Editar: Buscar URL antigua
                String urlAntigua = listaNoticias.stream()
                        .filter(n -> n.getIdNoticia() == idToUpdate)
                        .findFirst()
                        .map(NoticiaResponse::getUrlImagen)
                        .orElse("https://i.imgur.com/GzP738B.png");
                request.setUrlImagen(urlAntigua);
            } else {
                // Modo Crear: Asignar Placeholder OBLIGATORIO para evitar error de BD
                request.setUrlImagen("https://i.imgur.com/GzP738B.png");
            }
            // Llamada directa porque ya tenemos la URL
            guardarNoticiaEnApi(request, idToUpdate);
        }
    }

    private void guardarNoticiaEnApi(NoticiaRequest request, @Nullable Integer idToUpdate) {
        mostrarCarga(true);
        Call<Void> call;

        if (idToUpdate == null) {
            call = apiService.crearNoticia(request);
        } else {
            call = apiService.editarNoticia(idToUpdate, request);
        }

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Noticia guardada exitosamente", Toast.LENGTH_SHORT).show();
                    cargarNoticiasDesdeApi();
                } else {
                    mostrarCarga(false);
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("API_ERROR", "Error: " + errorBody);
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

    @Override
    public void onEditarClick(NoticiaResponse noticia) {
        GuardarNoticiaDialog dialog = GuardarNoticiaDialog.newInstance(noticia);
        dialog.setTargetFragment(this, 0);
        dialog.show(getParentFragmentManager(), "EditarNoticia");
    }

    @Override
    public void onEliminarClick(NoticiaResponse noticia) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Eliminar Noticia")
                .setMessage("¿Eliminar " + noticia.getTitulo() + "?")
                .setNegativeButton("Cancelar", (d, w) -> d.dismiss())
                .setPositiveButton("Eliminar", (d, w) -> {
                    mostrarCarga(true);
                    apiService.eliminarNoticia(noticia.getIdNoticia()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) cargarNoticiasDesdeApi();
                            else {
                                mostrarCarga(false);
                                Toast.makeText(getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            mostrarCarga(false);
                        }
                    });
                }).show();
    }

    @Override
    public void onItemClick(NoticiaResponse noticia) {
        // Vacío intencionalmente. En Admin usamos los botones de editar/borrar.
    }

    private void mostrarCarga(boolean cargando) {
        progressBar.setVisibility(cargando ? View.VISIBLE : View.GONE);
        rvNoticias.setVisibility(cargando ? View.GONE : View.VISIBLE);
    }
}