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
        // 1. Infla el layout CORRECHO que creamos
        return inflater.inflate(R.layout.fragment_eventos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ---- Configurar Título de la Actividad ----
        TextView titulo = requireActivity().findViewById(R.id.txt_panel_admin);
        titulo.setText("Eventos");

        // ---- Vistas de ESTE Fragmento ----
        rvEventos = view.findViewById(R.id.rv_eventos);
        progressBarEventos = view.findViewById(R.id.progress_bar_eventos);
        fabAgregarEvento = view.findViewById(R.id.fab_agregar_evento);

        // ---- Configurar API y RecyclerView ----
        apiService = CineDexApiClient.getApiService();
        listaEventos = new ArrayList<>();

        // Pasamos 'this' como el listener
        adapter = new EventoAdapter(getContext(), listaEventos, this);
        rvEventos.setAdapter(adapter);

        // ---- Configurar Botón de Añadir ----
        fabAgregarEvento.setOnClickListener(v -> {
            GuardarEventoDialog dialog = GuardarEventoDialog.newInstance(null);
            dialog.setTargetFragment(EventosFragment.this, 0);
            dialog.show(getParentFragmentManager(), "GuardarEventoDialog");
        });

        // ---- Carga Inicial ----
        cargarEventosDesdeApi();
    }

    // -------------------------------------------------------------
    // ---------------------- API CALLS ----------------------------
    // -------------------------------------------------------------

    private void cargarEventosDesdeApi() {
        mostrarCarga(true);

        // Asegúrate de que este método 'getEventos()' exista en tu CineDexApiService
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

    /* ==============================================================
             LÓGICA DE GUARDADO (Respuesta del Diálogo)
       ============================================================== */

    @Override
    public void onEventoGuardado(EventoRequest request, @Nullable Uri imagenUri, @Nullable Integer eventoIdToUpdate) {

        if (imagenUri != null) {
            // --- Caso 1: Se seleccionó una imagen NUEVA ---
            // Primero subimos la imagen a Cloudinary
            mostrarCarga(true); // Mostramos carga durante la subida
            File file = CloudinaryUploader.getFileFromUri(requireContext(), imagenUri);

            new Thread(() -> {
                try {
                    // Subida de imagen
                    String url = CloudinaryUploader.uploadImage(file);

                    // Volvemos al hilo principal para actualizar la UI y llamar a la API
                    requireActivity().runOnUiThread(() -> {
                        request.setUrlImagen(url);
                        guardarEventoEnApi(request, eventoIdToUpdate); // Llamada a la API
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error al subir la imagen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        mostrarCarga(false);
                    });
                }
            }).start();

        } else {
            // --- Caso 2: NO se seleccionó imagen nueva ---

            // Si es modo EDICIÓN, debemos mantener la URL de la imagen antigua
            if (eventoIdToUpdate != null) {
                request.setUrlImagen(
                        listaEventos.stream()
                                .filter(e -> e.getIdEvento() == eventoIdToUpdate)
                                .findFirst()
                                .map(EventoResponse::getUrlImagen)
                                .orElse(null)
                );
            }
            // Si es CREACIÓN y no hay imagen, la URL irá null (nuestra API lo maneja)

            // Llamamos a la API directamente
            guardarEventoEnApi(request, eventoIdToUpdate);
        }
    }

    private void guardarEventoEnApi(EventoRequest request, @Nullable Integer eventoIdToUpdate) {
        mostrarCarga(true);

        Call<Void> call;

        if (eventoIdToUpdate == null) {
            // Modo CREAR
            // (Asegúrate que 'crearEvento' exista en tu ApiService)
            call = apiService.crearEvento(request);
        } else {
            // Modo EDITAR
            // (Asegúrate que 'editarEvento' exista en tu ApiService)
            call = apiService.editarEvento(eventoIdToUpdate, request);
        }

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // 'mostrarCarga(false)' se llama dentro de 'cargarEventosDesdeApi()'
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Evento guardado", Toast.LENGTH_SHORT).show();
                    cargarEventosDesdeApi(); // Recargamos la lista
                } else {
                    mostrarCarga(false);
                    Toast.makeText(getContext(), "Error al guardar el evento", Toast.LENGTH_SHORT).show();
                    // ----- ¡AÑADE ESTAS LÍNEAS PARA VER EL ERROR! -----
                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("API_ERROR", "Código: " + response.code());
                        Log.e("API_ERROR", "Mensaje: " + response.message());
                        Log.e("API_ERROR", "Cuerpo del Error: " + errorBody);
                    } catch (Exception e) {
                        Log.e("API_ERROR", "Error al leer el errorBody", e);
                    }
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
        // Abre el diálogo en modo EDICIÓN
        GuardarEventoDialog dialog = GuardarEventoDialog.newInstance(evento);
        dialog.setTargetFragment(this, 0);
        dialog.show(getParentFragmentManager(), "EditarEventoDialog");
    }

    @Override
    public void onEliminarClick(EventoResponse evento) {

        // 1. Crear el diálogo de confirmación
        new MaterialAlertDialogBuilder(requireContext()) // Usar requireContext() es más seguro
                .setTitle("Confirmar Eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar el evento: \"" + evento.getTitulo() + "\"? Esta acción no se puede deshacer.")

                // 2. Botón "Cancelar" (no hace nada, solo cierra)
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())

                // 3. Botón "Eliminar" (aquí va la lógica de la API)
                .setPositiveButton("Eliminar", (dialog, which) -> {

                    // Mostramos la barra de progreso
                    mostrarCarga(true);

                    // Llamamos a la API (tu servicio ya tiene 'eliminarEvento')
                    Call<Void> call = apiService.eliminarEvento(evento.getIdEvento());

                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            // Si la eliminación es exitosa, 'cargarEventosDesdeApi'
                            // se encargará de quitar la barra de progreso.
                            if (response.isSuccessful()) {
                                Toast.makeText(getContext(), "Evento eliminado", Toast.LENGTH_SHORT).show();
                                cargarEventosDesdeApi(); // Recarga la lista para que desaparezca
                            } else {
                                mostrarCarga(false);
                                Toast.makeText(getContext(), "Error al eliminar el evento", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            mostrarCarga(false);
                            Toast.makeText(getContext(), "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .show(); // 4. Mostrar el diálogo
    }

    // -------------------------------------------------------------
    // ---------------------- UI HELPERS ---------------------------
    // -------------------------------------------------------------

    private void mostrarCarga(boolean cargando) {
        progressBarEventos.setVisibility(cargando ? View.VISIBLE : View.GONE);
        rvEventos.setVisibility(cargando ? View.GONE : View.VISIBLE);
    }
}