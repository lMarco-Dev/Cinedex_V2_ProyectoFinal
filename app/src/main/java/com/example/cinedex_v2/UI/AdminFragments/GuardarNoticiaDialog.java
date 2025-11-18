package com.example.cinedex_v2.UI.AdminFragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.cinedex_v2.Data.DTOs.Noticia.NoticiaRequest;
import com.example.cinedex_v2.Data.DTOs.Noticia.NoticiaResponse;
import com.example.cinedex_v2.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Date; // Importante para la fecha

public class GuardarNoticiaDialog extends BottomSheetDialogFragment {

    private static final String ARG_NOTICIA = "arg_noticia";
    private NoticiaResponse noticiaActual;
    private boolean isEditMode = false;

    // --- Vistas ---
    private ImageView ivNoticiaPreview;
    private MaterialButton btnSeleccionarImagen;
    private TextInputEditText etTitulo, etResumen, etYoutube; // <-- AGREGADO etYoutube
    private TextView tvDialogTitulo;
    private View btnGuardar;

    private ActivityResultLauncher<String> mGetContent;
    private Uri imagenSeleccionadaUri = null;

    public interface OnNoticiaGuardadaListener {
        void onNoticiaGuardada(NoticiaRequest request, @Nullable Uri imagenUri, @Nullable Integer noticiaIdToUpdate);
    }
    private OnNoticiaGuardadaListener listener;

    public static GuardarNoticiaDialog newInstance(@Nullable NoticiaResponse noticia) {
        GuardarNoticiaDialog dialog = new GuardarNoticiaDialog();
        Bundle args = new Bundle();
        if (noticia != null) {
            args.putSerializable(ARG_NOTICIA, noticia);
        }
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ARG_NOTICIA)) {
            noticiaActual = (NoticiaResponse) getArguments().getSerializable(ARG_NOTICIA);
            isEditMode = true;
        }

        try {
            listener = (OnNoticiaGuardadaListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getTargetFragment().toString()
                    + " debe implementar OnNoticiaGuardadaListener");
        }

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imagenSeleccionadaUri = uri;
                        Glide.with(getContext()).load(imagenSeleccionadaUri).into(ivNoticiaPreview);
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_guardar_noticias, container, false);
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
        etResumen = view.findViewById(R.id.et_resumen);
        etYoutube = view.findViewById(R.id.et_youtube); // <-- VINCULAR NUEVO CAMPO
        ivNoticiaPreview = view.findViewById(R.id.iv_noticia_preview);
        btnSeleccionarImagen = view.findViewById(R.id.btn_seleccionar_imagen);
    }

    private void configurarVistas() {
        btnSeleccionarImagen.setOnClickListener(v -> mGetContent.launch("image/*"));
        btnGuardar.setOnClickListener(v -> guardarNoticia());
    }

    private void popularDatosParaEdicion() {
        tvDialogTitulo.setText("Editar Noticia");
        Glide.with(getContext()).load(noticiaActual.getUrlImagen()).into(ivNoticiaPreview);
        etTitulo.setText(noticiaActual.getTitulo());
        etResumen.setText(noticiaActual.getResumen());

        // Rellenar link de youtube si existe
        if(noticiaActual.getUrlYoutube() != null) {
            etYoutube.setText(noticiaActual.getUrlYoutube());
        }
    }

    private void guardarNoticia() {
        String titulo = etTitulo.getText().toString().trim();
        String resumen = etResumen.getText().toString().trim();
        String youtube = etYoutube.getText().toString().trim(); // Obtener texto de youtube

        if (titulo.isEmpty() || resumen.isEmpty()) {
            Toast.makeText(getContext(), "Título y Resumen son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        NoticiaRequest request = new NoticiaRequest();
        request.setTitulo(titulo);
        request.setResumen(resumen);

        // Asignar link de youtube (puede ser vacío si es opcional)
        request.setUrlYoutube(youtube.isEmpty() ? null : youtube);

        // *** CRUCIAL: Asignar fecha actual ***
        request.setFechaPublicacion(new Date());

        // Imagen antigua si no se cambia (lógica inicial)
        if (isEditMode && imagenSeleccionadaUri == null) {
            request.setUrlImagen(noticiaActual.getUrlImagen());
        }

        Integer idParaActualizar = isEditMode ? noticiaActual.getIdNoticia() : null;

        listener.onNoticiaGuardada(request, imagenSeleccionadaUri, idParaActualizar);
        dismiss();
    }
}