package com.example.cinedex_v2.UI.AdminFragments;

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
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.Data.DTOs.Cine.CineResponse;
import com.example.cinedex_v2.R;
import com.example.cinedex_v2.UI.AdaptersAdmin.CineSeleccionAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentInicioFunciones extends Fragment implements CineSeleccionAdapter.OnCineSeleccionadoListener {

    // Vistas
    private AutoCompleteTextView acCiudad;
    private RecyclerView rvCines;

    // Datos y Adaptador
    private CineSeleccionAdapter adapter;
    private List<CineResponse> listaTodosLosCines = new ArrayList<>(); // Mantiene TODOS los datos de la API
    private List<CineResponse> listaFiltrada = new ArrayList<>();      // Mantiene SOLO lo que se ve en pantalla

    // Ciudades disponibles
    private static final String[] CIUDADES = {
            "Cajamarca", "Trujillo", "Lima", "Chiclayo", "Arequipa", "Cusco", "Piura"
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Asegúrate que el XML se llame 'fragment_inicio_funciones'
        return inflater.inflate(R.layout.fragment_inicio_funciones, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Configurar Título del Header
        TextView titulo = requireActivity().findViewById(R.id.txt_panel_admin);
        if (titulo != null) titulo.setText("Seleccionar Cine");

        // 2. Vincular Vistas
        acCiudad = view.findViewById(R.id.ac_filtro_ciudad);
        rvCines = view.findViewById(R.id.rv_cines_inicio);

        // 3. Configurar Dropdown de Ciudades
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, CIUDADES);
        acCiudad.setAdapter(cityAdapter);

        // Evento: Cuando seleccionan una ciudad, filtramos la lista
        acCiudad.setOnItemClickListener((parent, view1, position, id) -> {
            String ciudadSeleccionada = parent.getItemAtPosition(position).toString();
            filtrarCines(ciudadSeleccionada);
        });

        // 4. Configurar RecyclerView con el Adaptador de SELECCIÓN
        rvCines.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new CineSeleccionAdapter(getContext(), listaFiltrada, this);
        rvCines.setAdapter(adapter);

        // 5. Cargar datos
        cargarCinesDesdeApi();
    }

    private void cargarCinesDesdeApi() {
        CineDexApiClient.getApiService().getCines().enqueue(new Callback<List<CineResponse>>() {
            @Override
            public void onResponse(Call<List<CineResponse>> call, Response<List<CineResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Guardamos la lista completa en memoria
                    listaTodosLosCines.clear();
                    listaTodosLosCines.addAll(response.body());

                    // Aplicamos el filtro inicial (Por defecto "Cajamarca")
                    String ciudadActual = acCiudad.getText().toString();
                    if (ciudadActual.isEmpty()) ciudadActual = "Cajamarca";

                    filtrarCines(ciudadActual);
                }
            }

            @Override
            public void onFailure(Call<List<CineResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Filtra la lista completa y actualiza el adaptador
     */
    private void filtrarCines(String ciudad) {
        listaFiltrada.clear();

        for (CineResponse cine : listaTodosLosCines) {
            // Comparamos ignorando mayúsculas/minúsculas
            if (cine.getCiudad().equalsIgnoreCase(ciudad)) {
                listaFiltrada.add(cine);
            }
        }

        if (listaFiltrada.isEmpty()) {
            Toast.makeText(getContext(), "No hay cines en " + ciudad, Toast.LENGTH_SHORT).show();
        }

        adapter.notifyDataSetChanged();
    }

    // ===============================================================
    //              EVENTO AL HACER CLIC EN UN CINE
    // ===============================================================
    @Override
    public void onCineClick(CineResponse cine) {
        // 1. Preparamos los datos en un Bundle
        Bundle bundle = new Bundle();
        bundle.putInt("id_cine", cine.getIdCine());
        bundle.putString("nombre_cine", cine.getNombre());

        // 2. Navegamos usando el Controlador de Navegación
        try {
            // ¡CORREGIDO! Usamos el ID del fragmento destino definido en el nav_graph
            Navigation.findNavController(requireView())
                    .navigate(R.id.funcionesFragment, bundle);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error de navegación: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}