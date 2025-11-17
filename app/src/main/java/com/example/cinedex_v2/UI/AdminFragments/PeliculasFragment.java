package com.example.cinedex_v2.UI.AdminFragments; // (Usa tu paquete)

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cinedex_v2.R;
import com.example.cinedex_v2.UI.AdaptersAdmin.PeliculaAdapter;
import com.example.cinedex_v2.Data.DTOs.Pelicula.PeliculaRequest;
import com.example.cinedex_v2.Data.DTOs.Pelicula.PeliculaResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class PeliculasFragment extends Fragment
        implements PeliculaAdapter.OnPeliculaClickListener, GuardarPeliculaDialog.OnPeliculaGuardadaListener { // Interfaz actualizada

    private RecyclerView rvPeliculas;
    private FloatingActionButton fabAgregar;
    private PeliculaAdapter adapter;
    private List<PeliculaResponse> listaPeliculas;

    // (Aquí iría tu servicio de Retrofit y tu SDK de Cloudinary)
    // private ApiService apiService;
    // private MediaManager cloudinary;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_peliculas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Aquí inicializarías Cloudinary ---
        // initCloudinary();

        rvPeliculas = view.findViewById(R.id.rv_peliculas);
        fabAgregar = view.findViewById(R.id.fab_agregar_pelicula);

        listaPeliculas = new ArrayList<>();
        cargarDatosDePrueba();

        adapter = new PeliculaAdapter(getContext(), listaPeliculas, this);
        rvPeliculas.setAdapter(adapter);

        fabAgregar.setOnClickListener(v -> {
            GuardarPeliculaDialog dialog = GuardarPeliculaDialog.newInstance(null);
            dialog.show(getParentFragmentManager(), "GuardarPeliculaDialog");
        });

        // --- Aquí cargarías los datos de tu API ---
        // cargarPeliculasDesdeApi();
    }

    // ... (cargarDatosDePrueba y métodos del adaptador onEditar/onEliminar siguen igual) ...

    // --- ¡MÉTODO MÁS IMPORTANTE! ---
    // Implementación de la nueva interfaz del diálogo
    @Override
    public void onPeliculaGuardada(PeliculaRequest request, @Nullable Uri imagenUri, @Nullable Integer peliculaIdToUpdate) {

        // TODO: Mostrar un ProgressBar/Spinner de carga

        if (imagenUri != null) {
            // --- Caso 1: Se seleccionó una imagen nueva ---
            Log.d("PeliculasFragment", "Se seleccionó imagen: " + imagenUri.toString());
            // Aquí es donde llamas a Cloudinary para subir la imagen
            subirImagenACloudinary(imagenUri, request, peliculaIdToUpdate);
        } else {
            // --- Caso 2: No se seleccionó imagen nueva ---
            Log.d("PeliculasFragment", "No se seleccionó imagen, guardando datos directamente.");
            // La URL del póster ya está en el 'request' si era modo edición
            guardarPeliculaEnApi(request, peliculaIdToUpdate);
        }
    }

    private void subirImagenACloudinary(Uri imagenUri, PeliculaRequest request, @Nullable Integer peliculaIdToUpdate) {
        // --- ESTO ES UN EJEMPLO, LA SINTAXIS DE CLOUDINARY PUEDE VARIAR ---
        Log.d("PeliculasFragment", "Iniciando subida a Cloudinary...");
        Toast.makeText(getContext(), "Subiendo imagen...", Toast.LENGTH_SHORT).show();

        // (Aquí iría tu lógica del SDK de Cloudinary)
        /*
        MediaManager.get().upload(imagenUri)
            .callback(new UploadCallback() {
                @Override
                public void onStart(String requestId) {
                    // Empezó la subida
                }
                @Override
                public void onProgress(String requestId, long bytes, long totalBytes) { }

                @Override
                public void onSuccess(String requestId, Map resultData) {
                    // ¡Éxito! Obtenemos la URL segura
                    String url = (String) resultData.get("secure_url");
                    Log.d("PeliculasFragment", "Imagen subida: " + url);
                    
                    // 1. Asignamos la URL de Cloudinary al request
                    request.setUrlPoster(url);
                    
                    // 2. Ahora sí, guardamos en nuestra API .NET
                    guardarPeliculaEnApi(request, peliculaIdToUpdate);
                }

                @Override
                public void onError(String requestId, ErrorInfo error) {
                    Log.e("PeliculasFragment", "Error Cloudinary: " + error.getDescription());
                    Toast.makeText(getContext(), "Error al subir imagen", Toast.LENGTH_SHORT).show();
                    // TODO: Ocultar ProgressBar
                }
                @Override
                public void onReschedule(String requestId, ErrorInfo error) { }
            })
            .dispatch();
        */

        // --- FIN DEL EJEMPLO DE CLOUDINARY ---

        // ---- SIMULACIÓN MIENTRAS NO TIENES EL SDK ----
        // Finge que la subida tardó y te dio una URL
        Toast.makeText(getContext(), "Simulando subida... (Conecta Cloudinary)", Toast.LENGTH_SHORT).show();
        request.setUrlPoster("https://url.de.cloudinary.com/simulada.jpg");
        guardarPeliculaEnApi(request, peliculaIdToUpdate);
        // ---- FIN DE LA SIMULACIÓN ----
    }


    private void guardarPeliculaEnApi(PeliculaRequest request, @Nullable Integer peliculaIdToUpdate) {

        // TODO: Ocultar ProgressBar

        if (peliculaIdToUpdate != null) {
            // --- Lógica de ACTUALIZACIÓN (UPDATE) ---
            Log.d("PeliculasFragment", "Llamando a API para ACTUALIZAR Película ID: " + peliculaIdToUpdate);

            // (Aquí iría tu llamada de Retrofit: apiService.updatePelicula(peliculaIdToUpdate, request))

            // --- Simulación local ---
            actualizarPeliculaEnLista(request, peliculaIdToUpdate);
            Toast.makeText(getContext(), "Película '" + request.getTitulo() + "' actualizada (local)", Toast.LENGTH_SHORT).show();

        } else {
            // --- Lógica de CREACIÓN (CREATE) ---
            Log.d("PeliculasFragment", "Llamando a API para CREAR Película: " + request.getTitulo());

            // (Aquí iría tu llamada de Retrofit: apiService.createPelicula(request))

            // --- Simulación local ---
            agregarPeliculaALista(request);
            Toast.makeText(getContext(), "Película '" + request.getTitulo() + "' creada (local)", Toast.LENGTH_SHORT).show();
        }
    }

    // --- MÉTODOS DE SIMULACIÓN (para actualizar la UI localmente) ---

    private void agregarPeliculaALista(PeliculaRequest request) {
        // La API te devolvería un PeliculaResponse completo (con ID y Nota)
        // Lo simulamos aquí:
        PeliculaResponse nuevaPelicula = new PeliculaResponse();
        nuevaPelicula.setIdPelicula((int) (System.currentTimeMillis() % 100000));
        nuevaPelicula.setTitulo(request.getTitulo());
        nuevaPelicula.setDescripcion(request.getDescripcion());
        nuevaPelicula.setUrlPoster(request.getUrlPoster());
        nuevaPelicula.setCategoria(request.getCategoria());
        nuevaPelicula.setTipoEstreno(request.getTipoEstreno());
        nuevaPelicula.setPlataformasStreaming(request.getPlataformasStreaming());
        nuevaPelicula.setDirector(request.getDirector());
        nuevaPelicula.setPais(request.getPais());
        nuevaPelicula.setDuracionMin(request.getDuracionMin());
        nuevaPelicula.setNotaPromedio(0.0); // Nueva película

        listaPeliculas.add(0, nuevaPelicula);
        adapter.notifyItemInserted(0);
        rvPeliculas.scrollToPosition(0);
    }

    private void actualizarPeliculaEnLista(PeliculaRequest request, int peliculaId) {
        for (int i = 0; i < listaPeliculas.size(); i++) {
            if (listaPeliculas.get(i).getIdPelicula() == peliculaId) {
                // Actualizamos el objeto existente
                PeliculaResponse pelicula = listaPeliculas.get(i);
                pelicula.setTitulo(request.getTitulo());
                pelicula.setDescripcion(request.getDescripcion());
                pelicula.setUrlPoster(request.getUrlPoster());
                pelicula.setCategoria(request.getCategoria());
                pelicula.setTipoEstreno(request.getTipoEstreno());
                pelicula.setPlataformasStreaming(request.getPlataformasStreaming());
                pelicula.setDirector(request.getDirector());
                pelicula.setPais(request.getPais());
                pelicula.setDuracionMin(request.getDuracionMin());
                // (La nota promedio no se edita desde el app)

                adapter.notifyItemChanged(i);
                return;
            }
        }
    }


    // --- (Métodos onEditarClick, onEliminarClick, mostrarDialogoEliminar y cargarDatosDePrueba) ---
    // (Estos métodos siguen igual que en el paso anterior, solo asegúrate
    // de que `onEditarClick` pase el `PeliculaResponse` completo)

    @Override
    public void onEditarClick(PeliculaResponse pelicula) {
        GuardarPeliculaDialog dialog = GuardarPeliculaDialog.newInstance(pelicula);
        dialog.show(getParentFragmentManager(), "GuardarPeliculaDialog");
    }

    @Override
    public void onEliminarClick(PeliculaResponse pelicula) {
        mostrarDialogoEliminar(pelicula);
    }

    private void mostrarDialogoEliminar(PeliculaResponse pelicula) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Película")
                .setMessage("¿Estás seguro de que deseas eliminar \"" + pelicula.getTitulo() + "\"?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    // (Aquí llamarías a tu API: api.deletePelicula(pelicula.getIdPelicula()))
                    Log.d("PeliculasFragment", "Llamando a API para ELIMINAR Película ID: " + pelicula.getIdPelicula());

                    // --- Simulación local ---
                    int index = -1;
                    for (int i = 0; i < listaPeliculas.size(); i++) {
                        if (listaPeliculas.get(i).getIdPelicula() == pelicula.getIdPelicula()) {
                            index = i;
                            break;
                        }
                    }
                    if (index != -1) {
                        listaPeliculas.remove(index);
                        adapter.notifyItemRemoved(index);
                        Toast.makeText(getContext(), "Película eliminada (local)", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .setIcon(R.drawable.ic_delete)
                .show();
    }

    private void cargarDatosDePrueba() {
        // ... (igual que antes)
    }
}