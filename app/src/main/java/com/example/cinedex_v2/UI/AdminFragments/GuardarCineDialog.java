package com.example.cinedex_v2.UI.AdminFragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.cinedex_v2.Data.DTOs.Cine.CineRequest;
import com.example.cinedex_v2.Data.DTOs.Cine.CineResponse;
import com.example.cinedex_v2.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class GuardarCineDialog extends BottomSheetDialogFragment {

    private static final String ARG_CINE = "arg_cine";
    private CineResponse cineActual;
    private boolean isEditMode = false;

    private ImageView ivCinePreview;
    private MaterialButton btnSeleccionarImagen;
    private TextInputEditText etNombre, etDireccion;
    private AutoCompleteTextView acCiudad;
    private TextView tvDialogTitulo;
    private View btnGuardar;

    // --- Lista de Ciudades ---
    private static final String[] CIUDADES = new String[] {
            "Cajamarca", "Trujillo", "Lima", "Chiclayo", "Arequipa", "Cusco", "Piura"
    };

    private ActivityResultLauncher<String> mGetContent;
    private Uri imagenSeleccionadaUri = null;

    public interface OnCineGuardadoListener {
        void onCineGuardado(CineRequest request, @Nullable Uri imagenUri, @Nullable Integer idToUpdate);
    }
    private OnCineGuardadoListener listener;

    public static GuardarCineDialog newInstance(@Nullable CineResponse cine) {
        GuardarCineDialog dialog = new GuardarCineDialog();
        Bundle args = new Bundle();
        if (cine != null) {
            args.putSerializable(ARG_CINE, cine);
        }
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARG_CINE)) {
            cineActual = (CineResponse) getArguments().getSerializable(ARG_CINE);
            isEditMode = true;
        }
        try {
            listener = (OnCineGuardadoListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment debe implementar listener");
        }
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imagenSeleccionadaUri = uri;
                        Glide.with(getContext()).load(imagenSeleccionadaUri).into(ivCinePreview);
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_guardar_cine, container, false);
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
        etNombre = view.findViewById(R.id.et_nombre);
        acCiudad = view.findViewById(R.id.ac_ciudad);
        etDireccion = view.findViewById(R.id.et_direccion);
        ivCinePreview = view.findViewById(R.id.iv_cine_preview);
        btnSeleccionarImagen = view.findViewById(R.id.btn_seleccionar_imagen);
    }

    private void configurarVistas() {
        // Usamos requireContext() que es más seguro
        ArrayAdapter<String> adapterCiudades = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, CIUDADES);
        acCiudad.setAdapter(adapterCiudades);

        btnSeleccionarImagen.setOnClickListener(v -> mGetContent.launch("image/*"));
        btnGuardar.setOnClickListener(v -> guardarCine());

        // Aseguramos que al hacer clic se muestre la lista (aunque el estilo XML debería encargarse)
        acCiudad.setOnClickListener(v -> acCiudad.showDropDown());
    }

    private void popularDatosParaEdicion() {
        tvDialogTitulo.setText("Editar Cine");
        Glide.with(getContext()).load(cineActual.getUrlImagen()).into(ivCinePreview);
        etNombre.setText(cineActual.getNombre());
        acCiudad.setText(cineActual.getCiudad(), false); // false para que no se despliegue al setear texto
        etDireccion.setText(cineActual.getDireccion());
    }

    private void guardarCine() {
        String nombre = etNombre.getText().toString().trim();
        String ciudad = acCiudad.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();

        if (nombre.isEmpty() || ciudad.isEmpty() || direccion.isEmpty()) {
            Toast.makeText(getContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        CineRequest request = new CineRequest();
        request.setNombre(nombre);
        request.setCiudad(ciudad);
        request.setDireccion(direccion);

        if (isEditMode && imagenSeleccionadaUri == null) {
            request.setUrlImagen(cineActual.getUrlImagen());
        }

        Integer idParaActualizar = isEditMode ? cineActual.getIdCine() : null;
        listener.onCineGuardado(request, imagenSeleccionadaUri, idParaActualizar);
        dismiss();
    }
}