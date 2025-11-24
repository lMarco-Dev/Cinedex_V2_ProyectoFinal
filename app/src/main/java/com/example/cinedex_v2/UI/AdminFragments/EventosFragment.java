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
import com.example.cinedex_v2.UI.AdaptersAdmin.EventoAdapter;
import com.example.cinedex_v2.Data.DTOs.Evento.EventoRequest;
import com.example.cinedex_v2.Data.DTOs.Evento.EventoResponse;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventosFragment extends Fragment
        implements EventoAdapter.OnEventoClickListener, GuardarEventoDialog.OnEventoGuardadoListener {

    private RecyclerView rvEventos;
    private EventoAdapter adapter;
    private List<EventoResponse> listaEventos;
    private ProgressBar progressBarEventos;
    private CineDexApiService apiService;
    private FloatingActionButton fabAgregarEvento;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_eventos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView titulo = requireActivity().findViewById(R.id.txt_panel_admin);
        titulo.setText("Eventos");

        rvEventos = view.findViewById(R.id.rv_eventos);
        progressBarEventos = view.findViewById(R.id.progress_bar_eventos);
        fabAgregarEvento = view.findViewById(R.id.fab_agregar_evento);

        apiService = CineDexApiClient.getApiService();
        listaEventos = new ArrayList<>();

        // Pasamos 'this' como el listener y 'true' porque ES editable (Panel Admin)
        adapter = new EventoAdapter(getContext(), listaEventos, true, this);
        rvEventos.setAdapter(adapter);

        fabAgregarEvento.setOnClickListener(v -> {
            GuardarEventoDialog dialog = GuardarEventoDialog.newInstance(null);
            dialog.setTargetFragment(EventosFragment.this, 0);
            dialog.show(getParentFragmentManager(), "GuardarEventoDialog");
        });

        cargarEventosDesdeApi();
    }

    private void cargarEventosDesdeApi() {
        mostrarCarga(true);
        Call<List<EventoResponse>> call = apiService.getEventos();

        call.enqueue(new Callback<List<EventoResponse>>() {
            @Override
            public void onResponse(Call<List<EventoResponse>> call, Response<List<EventoResponse>> response) {
                mostrarCarga(false);
                if (response.isSuccessful() && response.body() != null) {
                    listaEventos.clear();
                    listaEventos.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error al cargar eventos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<EventoResponse>> call, Throwable t) {
                mostrarCarga(false);
                Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEventoGuardado(EventoRequest request, @Nullable Uri imagenUri, @Nullable Integer eventoIdToUpdate) {
        if (imagenUri != null) {
            mostrarCarga(true);
            File file = CloudinaryUploader.getFileFromUri(requireContext(), imagenUri);

            new Thread(() -> {
                try {
                    String url = CloudinaryUploader.uploadImage(file);
                    requireActivity().runOnUiThread(() -> {
                        request.setUrlImagen(url);
                        guardarEventoEnApi(request, eventoIdToUpdate);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                        mostrarCarga(false);
                    });
                }
            }).start();
        } else {
            if (eventoIdToUpdate != null) {
                request.setUrlImagen(
                        listaEventos.stream()
                                .filter(e -> e.getIdEvento() == eventoIdToUpdate)
                                .findFirst()
                                .map(EventoResponse::getUrlImagen)
                                .orElse(null)
                );
            }
            guardarEventoEnApi(request, eventoIdToUpdate);
        }
    }

    private void guardarEventoEnApi(EventoRequest request, @Nullable Integer eventoIdToUpdate) {
        mostrarCarga(true);
        Call<Void> call;

        if (eventoIdToUpdate == null) {
            call = apiService.crearEvento(request);
        } else {
            call = apiService.editarEvento(eventoIdToUpdate, request);
        }

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Evento guardado", Toast.LENGTH_SHORT).show();
                    cargarEventosDesdeApi();
                } else {
                    mostrarCarga(false);
                    Toast.makeText(getContext(), "Error al guardar el evento", Toast.LENGTH_SHORT).show();
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("API_ERROR", "Error: " + errorBody);
                    } catch (Exception e) {}
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                mostrarCarga(false);
                Toast.makeText(getContext(), "Error de red al guardar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // -------------------------------------------------------------
    // ----------------- RESPUESTAS DEL ADAPTER --------------------
    // -------------------------------------------------------------

    @Override
    public void onEditarClick(EventoResponse evento) {
        GuardarEventoDialog dialog = GuardarEventoDialog.newInstance(evento);
        dialog.setTargetFragment(this, 0);
        dialog.show(getParentFragmentManager(), "EditarEventoDialog");
    }

    @Override
    public void onEliminarClick(EventoResponse evento) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Confirmar Eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar el evento: \"" + evento.getTitulo() + "\"?")
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    mostrarCarga(true);
                    Call<Void> call = apiService.eliminarEvento(evento.getIdEvento());
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Evento eliminado", Toast.LENGTH_SHORT).show();
                                cargarEventosDesdeApi();
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

    // 👇 ESTE ES EL MÉTODO QUE TE FALTABA 👇
    @Override
    public void onItemClick(EventoResponse evento) {
        // En el panel de admin no hacemos nada al hacer clic en la tarjeta,
        // porque usamos los botones de editar/borrar.
    }

    private void mostrarCarga(boolean cargando) {
        if (cargando) {
            progressBarEventos.setVisibility(View.VISIBLE);
            rvEventos.setVisibility(View.GONE);
        } else {
            progressBarEventos.setVisibility(View.GONE);
            rvEventos.setVisibility(View.VISIBLE);
        }
    }
}