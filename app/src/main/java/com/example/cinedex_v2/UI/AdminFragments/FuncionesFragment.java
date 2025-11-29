package com.example.cinedex_v2.UI.AdminFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.Data.Network.CineDexApiService;
import com.example.cinedex_v2.Data.DTOs.Funcion.FuncionRequest;
import com.example.cinedex_v2.Data.DTOs.Funcion.FuncionResponse;
import com.example.cinedex_v2.R;
import com.example.cinedex_v2.UI.AdaptersAdmin.FuncionAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FuncionesFragment extends Fragment
        implements FuncionAdapter.OnFuncionClickListener, GuardarFuncionDialog.OnFuncionesGuardadasListener {

    // Variables de datos
    private int idCineSeleccionado;
    private String nombreCineSeleccionado;

    // Vistas
    private RecyclerView rvFunciones;
    private FuncionAdapter adapter;
    private List<FuncionResponse> listaFunciones;
    private ProgressBar progressBar;
    private TextView tvNoFunciones;
    private FloatingActionButton fabAgregar;

    // Servicio API
    private CineDexApiService apiService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout
        return inflater.inflate(R.layout.fragment_funciones, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Recuperar datos del Bundle (enviados por InicioFuncionesFragment)
        if (getArguments() != null) {
            idCineSeleccionado = getArguments().getInt("id_cine");
            nombreCineSeleccionado = getArguments().getString("nombre_cine");
        }

        // --- CONFIGURAR TOOLBAR LOCAL ---
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_funciones_admin);
        if (toolbar != null) {
            // Ponemos el título: "Cartelera: Cineplanet"
            toolbar.setTitle("Cartelera: " + nombreCineSeleccionado);

            // Acción de la flecha atrás
            toolbar.setNavigationOnClickListener(v -> {
                Navigation.findNavController(view).popBackStack();
            });
        }

        // 2. Configurar Título del Header (Panel Admin)
        TextView titulo = requireActivity().findViewById(R.id.txt_panel_admin);
        if (titulo != null) titulo.setText("Cartelera: " + nombreCineSeleccionado);

        // 3. Vincular Vistas
        rvFunciones = view.findViewById(R.id.rv_funciones);
        progressBar = view.findViewById(R.id.progress_bar_funciones);
        tvNoFunciones = view.findViewById(R.id.tv_no_funciones);
        fabAgregar = view.findViewById(R.id.fab_agregar_funcion);

        // 4. Configurar RecyclerView
        apiService = CineDexApiClient.getApiService();
        listaFunciones = new ArrayList<>();
        adapter = new FuncionAdapter(getContext(), listaFunciones, this);
        rvFunciones.setAdapter(adapter);

        // 5. Botón Agregar -> Abre el Diálogo
        fabAgregar.setOnClickListener(v -> {
            GuardarFuncionDialog dialog = GuardarFuncionDialog.newInstance(idCineSeleccionado);
            // Establecemos este fragmento como el target para recibir la respuesta
            dialog.setTargetFragment(FuncionesFragment.this, 0);
            dialog.show(getParentFragmentManager(), "GuardarFuncion");
        });

        // 6. Cargar datos iniciales
        cargarFuncionesPorCine();
    }

    // ===============================================================
    //                     CARGA DE DATOS (GET)
    // ===============================================================
    private void cargarFuncionesPorCine() {
        mostrarCarga(true);

        apiService.getPorCine(idCineSeleccionado).enqueue(new Callback<List<FuncionResponse>>() {
            @Override
            public void onResponse(Call<List<FuncionResponse>> call, Response<List<FuncionResponse>> response) {
                mostrarCarga(false);
                if (response.isSuccessful() && response.body() != null) {
                    listaFunciones.clear();
                    listaFunciones.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    // Mostrar mensaje si la lista está vacía
                    if(listaFunciones.isEmpty()) tvNoFunciones.setVisibility(View.VISIBLE);
                    else tvNoFunciones.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<FuncionResponse>> call, Throwable t) {
                mostrarCarga(false);
                Toast.makeText(getContext(), "Error al cargar cartelera", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ===============================================================
    //               GUARDADO MASIVO (Respuesta del Diálogo)
    // ===============================================================
    @Override
    public void onFuncionesGuardadas(List<FuncionRequest> requests) {
        if (requests.isEmpty()) return;

        mostrarCarga(true);

        // Variables para controlar el proceso asíncrono múltiple
        final int[] completados = {0};
        final int total = requests.size();
        final boolean[] huboError = {false};

        // Bucle para enviar cada horario a la API
        for (FuncionRequest req : requests) {
            apiService.crearFuncion(req).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (!response.isSuccessful()) {
                        huboError[0] = true;
                    }
                    chequearFinProceso(completados, total, huboError);
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    huboError[0] = true;
                    chequearFinProceso(completados, total, huboError);
                }
            });
        }
    }

    // Método auxiliar para saber cuándo terminaron todas las peticiones
    private void chequearFinProceso(int[] completados, int total, boolean[] huboError) {
        completados[0]++;
        if (completados[0] == total) {
            mostrarCarga(false);
            cargarFuncionesPorCine(); // Recargar la lista para ver los nuevos items

            if (huboError[0])
                Toast.makeText(getContext(), "Se guardaron con algunos errores", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getContext(), "¡Funciones guardadas exitosamente!", Toast.LENGTH_SHORT).show();
        }
    }

    // ===============================================================
    //                  ACCIONES DE LOS ÍTEMS
    // ===============================================================
    @Override
    public void onEditarClick(FuncionResponse funcion) {
        // Por ahora solo mensaje, la edición de funciones suele ser compleja
        Toast.makeText(getContext(), "Editar función: " + funcion.getSala(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEliminarClick(FuncionResponse funcion) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Eliminar Función")
                .setMessage("¿Eliminar función de '" + funcion.getNombrePelicula() + "' en " + funcion.getSala() + "?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    mostrarCarga(true);
                    apiService.eliminarFuncion(funcion.getIdFuncion()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if(response.isSuccessful()) cargarFuncionesPorCine();
                            else mostrarCarga(false);
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) { mostrarCarga(false); }
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // ===============================================================
    //                      UTILIDADES UI
    // ===============================================================
    private void mostrarCarga(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            rvFunciones.setVisibility(View.GONE);
            fabAgregar.hide();
            tvNoFunciones.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            rvFunciones.setVisibility(View.VISIBLE);
            fabAgregar.show();
        }
    }
}