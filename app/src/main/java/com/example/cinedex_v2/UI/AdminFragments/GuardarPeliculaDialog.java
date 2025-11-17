package com.example.cinedex_v2.UI.AdminFragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputLayout;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.cinedex_v2.R;
import com.example.cinedex_v2.Data.DTOs.Pelicula.PeliculaRequest;
import com.example.cinedex_v2.Data.DTOs.Pelicula.PeliculaResponse;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class GuardarPeliculaDialog extends BottomSheetDialogFragment {

    private static final String ARG_PELICULA = "arg_pelicula";
    private PeliculaResponse peliculaActual;
    private boolean isEditMode = false;

    // --- Vistas ---
    private ImageView ivPosterPreview;
    private MaterialButton btnSeleccionarImagen;
    private AutoCompleteTextView acCategoria;
    private MaterialButtonToggleGroup toggleTipoEstreno;
    private TextInputLayout layoutPlataformas;
    private AutoCompleteTextView acPlataformas;
    private TextInputEditText etTitulo, etDescripcion, etDirector, etPais, etDuracion;
    private TextView tvDialogTitulo;
    private FrameLayout btnGuardar;

    private ActivityResultLauncher<String> mGetContent;
    private Uri imagenSeleccionadaUri = null;

    private static final String[] GENEROS = new String[] {
            "Acción", "Aventura", "Comedia", "Drama", "Ciencia Ficción", "Fantasía", "Terror", "Romance", "Documental"
    };

    private static final String[] PLATAFORMAS = new String[] {
            "Netflix", "Disney+", "HBO Max", "Prime Video", "Star+", "Apple TV+", "Crunchyroll"
    };

    // --- Interfaz de Comunicación ---
    public interface OnPeliculaGuardadaListener {
        void onPeliculaGuardada(PeliculaRequest request, @Nullable Uri imagenUri, @Nullable Integer peliculaIdToUpdate);
    }
    private OnPeliculaGuardadaListener listener;

    public static GuardarPeliculaDialog newInstance(@Nullable PeliculaResponse pelicula) {
        GuardarPeliculaDialog dialog = new GuardarPeliculaDialog();
        Bundle args = new Bundle();
        if (pelicula != null) {
            args.putSerializable(ARG_PELICULA, pelicula);
        }
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Revisa si estamos en modo "Editar"
        if (getArguments() != null && getArguments().containsKey(ARG_PELICULA)) {
            peliculaActual = (PeliculaResponse) getArguments().getSerializable(ARG_PELICULA);
            isEditMode = true;
        }

        // 2. ¡CORREGIDO! Conecta el listener al "TargetFragment" (PeliculasFragment)
        try {
            listener = (OnPeliculaGuardadaListener) getTargetFragment();
        } catch (ClassCastException e) {
            // Este error saltará si olvidaste hacer 'dialog.setTargetFragment(...)'
            throw new ClassCastException(getTargetFragment().toString()
                    + " debe implementar OnPeliculaGuardadaListener");
        }

        // 3. Prepara el selector de imágenes
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        if (uri != null) {
                            imagenSeleccionadaUri = uri;
                            // Carga la imagen seleccionada en la vista previa
                            Glide.with(getContext())
                                    .load(imagenSeleccionadaUri)
                                    .into(ivPosterPreview);
                        }
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Usa el layout que puede tener el error de caché (si no lo arreglaste)
        // o el layout "parcheado" sin estilos.
        return inflater.inflate(R.layout.dialog_guardar_pelicula, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vincularVistas(view);
        configurarVistas();

        if (isEditMode) {
            popularDatosParaEdicion();
        }
    }

    private void vincularVistas(View view) {
        tvDialogTitulo = view.findViewById(R.id.tv_dialog_titulo);
        btnGuardar = view.findViewById(R.id.btn_guardar);
        etTitulo = view.findViewById(R.id.et_titulo);
        etDescripcion = view.findViewById(R.id.et_descripcion);
        etDirector = view.findViewById(R.id.et_director);
        etPais = view.findViewById(R.id.et_pais);
        etDuracion = view.findViewById(R.id.et_duracion);
        ivPosterPreview = view.findViewById(R.id.iv_poster_preview);
        btnSeleccionarImagen = view.findViewById(R.id.btn_seleccionar_imagen);
        acCategoria = view.findViewById(R.id.ac_categoria);
        toggleTipoEstreno = view.findViewById(R.id.toggle_tipo_estreno);
        layoutPlataformas = view.findViewById(R.id.layout_plataformas_streaming);
        acPlataformas = view.findViewById(R.id.ac_plataformas);
    }

    private void configurarVistas() {
        // Configurar Dropdown de Categoría
        ArrayAdapter<String> adapterGeneros = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, GENEROS);
        acCategoria.setAdapter(adapterGeneros);

        // Configurar botón de seleccionar imagen
        btnSeleccionarImagen.setOnClickListener(v -> mGetContent.launch("image/*"));

        // Configurar lógica condicional de Tipo Estreno
        toggleTipoEstreno.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btn_plataforma) {
                    layoutPlataformas.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.btn_cines) {
                    layoutPlataformas.setVisibility(View.GONE);
                }
            }
        });

        // Configurar botón de Guardar
        btnGuardar.setOnClickListener(v -> guardarPelicula());

        // Configurar Dropdown de Plataformas
        ArrayAdapter<String> adapterPlataformas = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, PLATAFORMAS);
        acPlataformas.setAdapter(adapterPlataformas);
    }

    /**
     * Rellena el formulario si estamos en "Modo Edición"
     */
    private void popularDatosParaEdicion() {
        tvDialogTitulo.setText("Editar Película");

        // Carga la imagen existente desde la URL
        Glide.with(getContext()).load(peliculaActual.getUrlPoster()).into(ivPosterPreview);

        etTitulo.setText(peliculaActual.getTitulo());
        etDescripcion.setText(peliculaActual.getDescripcion());
        acCategoria.setText(peliculaActual.getCategoria(), false); // false para que no filtre
        etDirector.setText(peliculaActual.getDirector());
        etPais.setText(peliculaActual.getPais());
        etDuracion.setText(String.valueOf(peliculaActual.getDuracionMin()));

        // Configura el ToggleButton y el dropdown de plataformas
        if ("Plataforma de streaming".equals(peliculaActual.getTipoEstreno())) {
            toggleTipoEstreno.check(R.id.btn_plataforma);
            layoutPlataformas.setVisibility(View.VISIBLE);
            acPlataformas.setText(peliculaActual.getPlataformasStreaming(), false);
        } else {
            toggleTipoEstreno.check(R.id.btn_cines);
            layoutPlataformas.setVisibility(View.GONE);
        }
    }

    /**
     * Valida y recopila los datos del formulario para enviarlos al Fragment
     */
    private void guardarPelicula() {
        String titulo = etTitulo.getText().toString().trim();
        String categoria = acCategoria.getText().toString().trim();
        String duracionStr = etDuracion.getText().toString().trim();

        // Validación simple
        if (titulo.isEmpty() || categoria.isEmpty() || duracionStr.isEmpty()) {
            Toast.makeText(getContext(), "Título, Categoría y Duración son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Construye el objeto Request
        PeliculaRequest request = new PeliculaRequest();
        request.setTitulo(titulo);
        request.setDescripcion(etDescripcion.getText().toString().trim());
        request.setCategoria(categoria);
        request.setDirector(etDirector.getText().toString().trim());
        request.setPais(etPais.getText().toString().trim());
        request.setDuracionMin(Integer.parseInt(duracionStr));

        // Si estamos editando y NO se seleccionó una imagen nueva,
        // mantenemos la URL antigua.
        if (isEditMode && imagenSeleccionadaUri == null) {
            request.setUrlPoster(peliculaActual.getUrlPoster());
        }
        // Si es modo "Crear" y no se seleccionó imagen, la URL irá null
        // (y tu API/Cloudinary le asignará una)

        // 2. Lee los Toggles y Plataformas
        int selectedButtonId = toggleTipoEstreno.getCheckedButtonId();
        if (selectedButtonId == R.id.btn_cines) {
            request.setTipoEstreno("En cines");
            request.setPlataformasStreaming(""); // Vacío si es en cines
        } else if (selectedButtonId == R.id.btn_plataforma) {
            request.setTipoEstreno("Plataforma de streaming");
            request.setPlataformasStreaming(acPlataformas.getText().toString().trim());
        }

        // 3. Determina si es una actualización
        Integer idParaActualizar = isEditMode ? peliculaActual.getIdPelicula() : null;

        // 4. Envía los datos de vuelta al PeliculasFragment
        listener.onPeliculaGuardada(request, imagenSeleccionadaUri, idParaActualizar);
        dismiss(); // Cierra el diálogo
    }
}