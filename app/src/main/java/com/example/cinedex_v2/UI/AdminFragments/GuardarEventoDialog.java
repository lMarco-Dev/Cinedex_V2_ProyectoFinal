package com.example.cinedex_v2.UI.AdminFragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.cinedex_v2.Data.DTOs.Evento.EventoRequest;
import com.example.cinedex_v2.Data.DTOs.Evento.EventoResponse;
import com.example.cinedex_v2.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class GuardarEventoDialog extends BottomSheetDialogFragment {

    private static final String ARG_EVENTO = "arg_evento";
    private EventoResponse eventoActual;
    private boolean isEditMode = false;

    // ------------------------------- Vistas --------------------------------------
    private ImageView ivEventoPreview;
    private MaterialButton btnSeleccionarImagen;
    private TextInputEditText etTitulo, etDescripcion, etUbicacion, etFechaHora;
    private TextView tvDialogTitulo;
    private View btnGuardar;

    // --- Lógica de Fecha/Hora ---
    private Calendar calendar;
    private SimpleDateFormat sdf;
    // ---------------------------------------------------------------------
    private ActivityResultLauncher<String> mGetContent;
    private Uri imagenSeleccionadaUri = null;

    // -------------------------------Interfaz de Comunicación--------------------------------------
    public interface OnEventoGuardadoListener {
        void onEventoGuardado(EventoRequest request, @NonNull Uri imagenUri, @Nullable Integer eventoIdToUpdate);
    }

    private OnEventoGuardadoListener listener;

    /* ---------------------------------------------------------------------
             Fabrica: Se le pasa los datos de forma segura para editar
       --------------------------------------------------------------------- */
    public static GuardarEventoDialog newInstance(@Nullable EventoResponse evento) {
        GuardarEventoDialog dialog = new GuardarEventoDialog();
        Bundle args = new Bundle();
        if (evento != null) {
            args.putSerializable(ARG_EVENTO, evento);
        }
        dialog.setArguments(args);
        return dialog;
    }
    /* ---------------------------------------------------------------------
             Maneja la logica principal al crear o editar
       --------------------------------------------------------------------- */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Inicializar calendario y formateador de fecha
        calendar = Calendar.getInstance();
        sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());

        // 2. Revisa si estamos en modo "Editar"
        if (getArguments() != null && getArguments().containsKey(ARG_EVENTO)) {
            eventoActual = (EventoResponse) getArguments().getSerializable(ARG_EVENTO);
            isEditMode = true;
            // Si editamos, ponemos el calendario en la fecha del evento
            if (eventoActual.getFechaHora() != null) {
                calendar.setTime(eventoActual.getFechaHora());
            }
        }

        // 3. Conecta el listener al "TargetFragment" (EventosFragment)
        try {
            listener = (OnEventoGuardadoListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getTargetFragment().toString()
                    + " debe implementar OnEventoGuardadoListener");
        }

        // 4. Prepara el selector de imágenes
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        if (uri != null) {
                            imagenSeleccionadaUri = uri;
                            Glide.with(getContext())
                                    .load(imagenSeleccionadaUri)
                                    .into(ivEventoPreview);
                        }
                    }
                });
    }
    /* ---------------------------------------------------------------------
             Metodo que convierte el xml a obejto y lo muestra
       --------------------------------------------------------------------- */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Asegúrate de crear este layout: "dialog_guardar_evento.xml"
        return inflater.inflate(R.layout.dialog_guardar_evento, container, false);
    }
    /* ---------------------------------------------------------------------
             OnViewCreated:
       --------------------------------------------------------------------- */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vincularVistas(view);
        configurarVistas();

        if (isEditMode) {
            popularDatosParaEdicion();
        }
    }
    /* ---------------------------------------------------------------------
            vincularVistas: Metodo de ayuda
       --------------------------------------------------------------------- */
    private void vincularVistas(View view) {
        tvDialogTitulo = view.findViewById(R.id.tv_dialog_titulo);
        btnGuardar = view.findViewById(R.id.btn_guardar);
        etTitulo = view.findViewById(R.id.et_titulo);
        etDescripcion = view.findViewById(R.id.et_descripcion);
        etUbicacion = view.findViewById(R.id.et_ubicacion);
        etFechaHora = view.findViewById(R.id.et_fecha_hora);
        ivEventoPreview = view.findViewById(R.id.iv_evento_preview);
        btnSeleccionarImagen = view.findViewById(R.id.btn_seleccionar_imagen);
    }

    private void configurarVistas() {
        // Configurar botón de seleccionar imagen
        btnSeleccionarImagen.setOnClickListener(v -> mGetContent.launch("image/*"));

        // Configurar botón de Guardar
        btnGuardar.setOnClickListener(v -> guardarEvento());

        // Configurar el selector de Fecha y Hora
        configurarSelectorFechaHora();
    }

    private void configurarSelectorFechaHora() {
        // 1. Prepara el listener de HORA
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                // Actualiza el campo de texto
                etFechaHora.setText(sdf.format(calendar.getTime()));
            }
        };

        // 2. Prepara el listener de FECHA
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // Al seleccionar la fecha, ABRE EL DIÁLOGO DE HORA
                new TimePickerDialog(getContext(), timeSetListener,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        false).show();
            }
        };

        // 3. Asigna el click al campo de texto para que ABRA EL DIÁLOGO DE FECHA
        etFechaHora.setOnClickListener(v -> {
            new DatePickerDialog(getContext(), dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // (Opcional) Evita que el teclado aparezca en este campo
        etFechaHora.setFocusable(false);
    }

    /**
     * Rellena el formulario si estamos en "Modo Edición"
     */
    private void popularDatosParaEdicion() {
        tvDialogTitulo.setText("Editar Evento");

        // Carga la imagen existente desde la URL
        Glide.with(getContext()).load(eventoActual.getUrlImagen()).into(ivEventoPreview);

        etTitulo.setText(eventoActual.getTitulo());
        etDescripcion.setText(eventoActual.getDescripcion());
        etUbicacion.setText(eventoActual.getUbicacion());

        // Muestra la fecha y hora existente
        if (eventoActual.getFechaHora() != null) {
            etFechaHora.setText(sdf.format(eventoActual.getFechaHora()));
        }
    }

    /**
     * Valida y recopila los datos del formulario para enviarlos al Fragment
     */
    private void guardarEvento() {
        String titulo = etTitulo.getText().toString().trim();
        String ubicacion = etUbicacion.getText().toString().trim();
        String fechaHoraStr = etFechaHora.getText().toString().trim();

        // Validación simple
        if (titulo.isEmpty() || ubicacion.isEmpty() || fechaHoraStr.isEmpty()) {
            Toast.makeText(getContext(), "Título, Ubicación y Fecha/Hora son obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Construye el objeto Request
        EventoRequest request = new EventoRequest();
        request.setTitulo(titulo);
        request.setDescripcion(etDescripcion.getText().toString().trim());
        request.setUbicacion(ubicacion);

        // Usamos la fecha/hora guardada en el objeto Calendar
        request.setFechaHora(calendar.getTime());

        // 2. Maneja la URL de la imagen
        if (isEditMode && imagenSeleccionadaUri == null) {
            request.setUrlImagen(eventoActual.getUrlImagen());
        }
        // Si es modo "Crear" o se seleccionó nueva imagen, la URI se manda por separado

        // 3. Determina si es una actualización
        Integer idParaActualizar = isEditMode ? eventoActual.getIdEvento() : null;

        // 4. Envía los datos de vuelta al EventosFragment
        listener.onEventoGuardado(request, imagenSeleccionadaUri, idParaActualizar);
        dismiss(); // Cierra el diálogo
    }
}
