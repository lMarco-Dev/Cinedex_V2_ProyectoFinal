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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.cinedex_v2.R; // (Asegúrate que R sea de tu paquete)
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
    private MaterialButton btnGuardar;

    private ActivityResultLauncher<String> mGetContent;
    private Uri imagenSeleccionadaUri = null;

    private static final String[] GENEROS = new String[] {
            "Acción", "Aventura", "Comedia", "Drama", "Ciencia Ficción", "Fantasía", "Terror", "Romance", "Documental"
    };

    private static final String[] PLATAFORMAS = new String[] {
            "Netflix", "Disney+", "HBO Max", "Prime Video", "Star+", "Apple TV+", "Crunchyroll"
    };

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
        if (getArguments() != null && getArguments().containsKey(ARG_PELICULA)) {
            peliculaActual = (PeliculaResponse) getArguments().getSerializable(ARG_PELICULA);
            isEditMode = true;
        }

        try {
            listener = (OnPeliculaGuardadaListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getParentFragment().toString() + " debe implementar OnPeliculaGuardadaListener");
        }

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        if (uri != null) {
                            imagenSeleccionadaUri = uri;
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

    private void popularDatosParaEdicion() {
        tvDialogTitulo.setText("Editar Película");

        Glide.with(getContext()).load(peliculaActual.getUrlPoster()).into(ivPosterPreview);

        etTitulo.setText(peliculaActual.getTitulo());
        etDescripcion.setText(peliculaActual.getDescripcion());
        acCategoria.setText(peliculaActual.getCategoria(), false);
        etDirector.setText(peliculaActual.getDirector());
        etPais.setText(peliculaActual.getPais());
        etDuracion.setText(String.valueOf(peliculaActual.getDuracionMin()));

        if ("Plataforma de streaming".equals(peliculaActual.getTipoEstreno())) {
            toggleTipoEstreno.check(R.id.btn_plataforma);
            layoutPlataformas.setVisibility(View.VISIBLE);
            acPlataformas.setText(peliculaActual.getPlataformasStreaming(), false);
        } else {
            toggleTipoEstreno.check(R.id.btn_cines);
            layoutPlataformas.setVisibility(View.GONE);
        }
    }

    private void guardarPelicula() {
        String titulo = etTitulo.getText().toString().trim();
        String categoria = acCategoria.getText().toString().trim();
        String duracionStr = etDuracion.getText().toString().trim();

        if (titulo.isEmpty() || categoria.isEmpty() || duracionStr.isEmpty()) {
            Toast.makeText(getContext(), "Título, Categoría y Duración son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        PeliculaRequest request = new PeliculaRequest();
        request.setTitulo(titulo);
        request.setDescripcion(etDescripcion.getText().toString().trim());
        request.setCategoria(categoria);
        request.setDirector(etDirector.getText().toString().trim());
        request.setPais(etPais.getText().toString().trim());
        request.setDuracionMin(Integer.parseInt(duracionStr));

        if (isEditMode && imagenSeleccionadaUri == null) {
            request.setUrlPoster(peliculaActual.getUrlPoster());
        }

        int selectedButtonId = toggleTipoEstreno.getCheckedButtonId();

        if (selectedButtonId == R.id.btn_cines) {
            request.setTipoEstreno("En cines");
            request.setPlataformasStreaming("");
        } else if (selectedButtonId == R.id.btn_plataforma) {
            request.setTipoEstreno("Plataforma de streaming");
            request.setPlataformasStreaming(acPlataformas.getText().toString().trim());
        }

        Integer idParaActualizar = isEditMode ? peliculaActual.getIdPelicula() : null;

        listener.onPeliculaGuardada(request, imagenSeleccionadaUri, idParaActualizar);
        dismiss();
    }
}