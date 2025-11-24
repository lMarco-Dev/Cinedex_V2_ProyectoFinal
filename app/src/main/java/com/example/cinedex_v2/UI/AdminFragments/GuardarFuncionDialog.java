package com.example.cinedex_v2.UI.AdminFragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.Data.DTOs.Funcion.FuncionRequest;
import com.example.cinedex_v2.Data.DTOs.Pelicula.PeliculaResponse;
import com.example.cinedex_v2.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GuardarFuncionDialog extends BottomSheetDialogFragment {

    private static final String ARG_ID_CINE = "arg_id_cine";
    private int idCineActual;

    // Vistas
    private AutoCompleteTextView acPelicula, acSala, acIdioma, acFormato;
    private TextInputEditText etFecha, etPrecio;
    private ChipGroup chipGroupHoras;
    private View btnAgregarHora, btnGuardar;

    // Datos
    private List<PeliculaResponse> listaPeliculas = new ArrayList<>();
    private List<String> horasSeleccionadas = new ArrayList<>(); // Ej: "14:30", "18:00"
    private Calendar fechaSeleccionada = Calendar.getInstance();
    private int idPeliculaSeleccionada = -1;

    // Listas estáticas para dropdowns
    private static final String[] SALAS = {"Sala 1", "Sala 2", "Sala 3", "Sala 4", "Sala IMAX", "Sala 4DX"};
    private static final String[] IDIOMAS = {"DOB", "SUB"};
    private static final String[] FORMATOS = {"2D", "3D", "IMAX"};

    // Interfaz para devolver la lista de funciones al fragmento
    public interface OnFuncionesGuardadasListener {
        void onFuncionesGuardadas(List<FuncionRequest> requests);
    }
    private OnFuncionesGuardadasListener listener;

    public static GuardarFuncionDialog newInstance(int idCine) {
        GuardarFuncionDialog dialog = new GuardarFuncionDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_ID_CINE, idCine);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idCineActual = getArguments().getInt(ARG_ID_CINE);
        }
        try {
            listener = (OnFuncionesGuardadasListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment debe implementar listener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_guardar_funcion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Vincular vistas
        acPelicula = view.findViewById(R.id.ac_pelicula);
        acSala = view.findViewById(R.id.ac_sala);
        acIdioma = view.findViewById(R.id.ac_idioma);
        acFormato = view.findViewById(R.id.ac_formato);
        etFecha = view.findViewById(R.id.et_fecha);
        etPrecio = view.findViewById(R.id.et_precio);
        chipGroupHoras = view.findViewById(R.id.chip_group_horas);
        btnAgregarHora = view.findViewById(R.id.btn_agregar_hora);
        btnGuardar = view.findViewById(R.id.btn_guardar);

        configurarDropdowns();
        cargarPeliculas();
        configurarListeners();
    }

    private void configurarDropdowns() {
        acSala.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, SALAS));
        acIdioma.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, IDIOMAS));
        acFormato.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, FORMATOS));
    }

    private void cargarPeliculas() {
        CineDexApiClient.getApiService().getPeliculas().enqueue(new Callback<List<PeliculaResponse>>() {
            @Override
            public void onResponse(Call<List<PeliculaResponse>> call, Response<List<PeliculaResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> nombresPeliculas = new ArrayList<>();
                    listaPeliculas.clear(); // Limpiamos la lista global

                    // DEBUG: Imprimir cuántas películas llegaron
                    android.util.Log.d("DEBUG_CINE", "Total películas recibidas: " + response.body().size());

                    for (PeliculaResponse p : response.body()) {

                        // DEBUG: Ver qué tipo de estreno tiene cada una
                        android.util.Log.d("DEBUG_CINE", "Peli: " + p.getTitulo() + " | Tipo: " + p.getTipoEstreno());

                        // FILTRO FLEXIBLE:
                        // Verificamos que no sea null y que contenga la palabra "cine" (ignorando mayúsculas)
                        if (p.getTipoEstreno() != null && p.getTipoEstreno().toLowerCase().contains("cine")) {
                            listaPeliculas.add(p);
                            nombresPeliculas.add(p.getTitulo());
                        }
                    }

                    // Si después del filtro la lista está vacía, avisamos
                    if (nombresPeliculas.isEmpty()) {
                        Toast.makeText(getContext(), "No hay películas 'En cines' registradas", Toast.LENGTH_LONG).show();
                    }

                    // Configurar el Adapter
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, nombresPeliculas);
                    acPelicula.setAdapter(adapter);

                    // Truco para forzar que se muestre si el usuario ya lo tenía abierto
                    if(acPelicula.hasFocus()) {
                        acPelicula.showDropDown();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<PeliculaResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Error al conectar con API", Toast.LENGTH_SHORT).show();
                android.util.Log.e("DEBUG_CINE", "Error API: " + t.getMessage());
            }
        });
    }

    private void configurarListeners() {
        // Date Picker
        etFecha.setOnClickListener(v -> {
            new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                fechaSeleccionada.set(year, month, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                etFecha.setText(sdf.format(fechaSeleccionada.getTime()));
            },
                    fechaSeleccionada.get(Calendar.YEAR),
                    fechaSeleccionada.get(Calendar.MONTH),
                    fechaSeleccionada.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Time Picker (Agregar Hora)
        btnAgregarHora.setOnClickListener(v -> {
            new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
                String horaStr = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                agregarChipHora(horaStr);
            }, 12, 0, false).show();
        });

        // Guardar
        btnGuardar.setOnClickListener(v -> procesarGuardado());
    }

    private void agregarChipHora(String hora) {
        if (horasSeleccionadas.contains(hora)) return; // No duplicados

        horasSeleccionadas.add(hora);

        // Crear Chip visual
        Chip chip = new Chip(requireContext());
        chip.setText(hora);
        chip.setCloseIconVisible(true);
        chip.setCheckable(false);
        chip.setOnCloseIconClickListener(v -> {
            chipGroupHoras.removeView(chip);
            horasSeleccionadas.remove(hora);
        });

        chipGroupHoras.addView(chip);
    }

    private void procesarGuardado() {
        String nombrePelicula = acPelicula.getText().toString().trim();
        String sala = acSala.getText().toString().trim();
        String precioStr = etPrecio.getText().toString().trim();
        String idioma = acIdioma.getText().toString().trim();
        String formato = acFormato.getText().toString().trim();

        // --- CORRECCIÓN: RECUPERAR ID SI SE PERDIÓ ---
        if (idPeliculaSeleccionada == -1) {
            for (PeliculaResponse p : listaPeliculas) {
                if (p.getTitulo().equalsIgnoreCase(nombrePelicula)) {
                    idPeliculaSeleccionada = p.getIdPelicula();
                    break;
                }
            }
        }
        // ----------------------------------------------

        // DEBUG: Ver en el Logcat qué está fallando
        android.util.Log.d("DEBUG_GUARDAR", "ID Pelicula: " + idPeliculaSeleccionada);
        android.util.Log.d("DEBUG_GUARDAR", "Sala: " + sala);
        android.util.Log.d("DEBUG_GUARDAR", "Precio: " + precioStr);
        android.util.Log.d("DEBUG_GUARDAR", "Horas: " + horasSeleccionadas.size());
        android.util.Log.d("DEBUG_GUARDAR", "Idioma: " + idioma);
        android.util.Log.d("DEBUG_GUARDAR", "Formato: " + formato);

        // VALIDACIÓN COMPLETA
        if (idPeliculaSeleccionada == -1) {
            Toast.makeText(getContext(), "Error: Selecciona una película de la lista", Toast.LENGTH_SHORT).show();
            return;
        }

        if (sala.isEmpty() || precioStr.isEmpty() || idioma.isEmpty() || formato.isEmpty()) {
            Toast.makeText(getContext(), "Faltan completar campos de texto", Toast.LENGTH_SHORT).show();
            return;
        }

        if (horasSeleccionadas.isEmpty()) {
            Toast.makeText(getContext(), "Debes agregar al menos una hora", Toast.LENGTH_SHORT).show();
            return;
        }

        double precio = Double.parseDouble(precioStr);
        List<FuncionRequest> listaRequests = new ArrayList<>();

        // BUCLE: Crear un request por cada hora seleccionada
        for (String hora : horasSeleccionadas) {
            FuncionRequest req = new FuncionRequest();
            req.setIdCine(idCineActual);
            req.setIdPelicula(idPeliculaSeleccionada);
            req.setSala(sala);
            req.setPrecio(precio);
            req.setIdioma(idioma);
            req.setFormato(formato);

            // Combinar Fecha + Hora
            Calendar fechaFinal = (Calendar) fechaSeleccionada.clone();
            String[] partesHora = hora.split(":");
            fechaFinal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(partesHora[0]));
            fechaFinal.set(Calendar.MINUTE, Integer.parseInt(partesHora[1]));
            fechaFinal.set(Calendar.SECOND, 0);

            req.setFechaHora(fechaFinal.getTime());

            listaRequests.add(req);
        }

        // Enviar la lista al fragmento
        if (listener != null) {
            listener.onFuncionesGuardadas(listaRequests);
        }
        dismiss();
    }
}