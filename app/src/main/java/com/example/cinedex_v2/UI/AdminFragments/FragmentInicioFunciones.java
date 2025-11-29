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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Asegúrate que el XML se llame 'fragment_inicio_funciones'
        return inflater.inflate(R.layout.fragment_inicio_funciones, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView titulo = requireActivity().findViewById(R.id.txt_panel_admin);
        if (titulo != null) titulo.setText("Seleccionar Cine");

        acCiudad = view.findViewById(R.id.ac_filtro_ciudad);
        rvCines = view.findViewById(R.id.rv_cines_inicio);

        // Configurar Listener del Dropdown
        acCiudad.setOnItemClickListener((parent, view1, position, id) -> {
            String ciudadSeleccionada = parent.getItemAtPosition(position).toString();
            filtrarCines(ciudadSeleccionada);
        });

        // Forzar que se abra al hacer click
        acCiudad.setOnClickListener(v -> acCiudad.showDropDown());

        rvCines.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new CineSeleccionAdapter(getContext(), listaFiltrada, this);
        rvCines.setAdapter(adapter);

        cargarCinesDesdeApi();
    }

    private void cargarCinesDesdeApi() {
        CineDexApiClient.getApiService().getCines().enqueue(new Callback<List<CineResponse>>() {
            @Override
            public void onResponse(Call<List<CineResponse>> call, Response<List<CineResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaTodosLosCines.clear();
                    listaTodosLosCines.addAll(response.body());

                    // --- LÓGICA DINÁMICA DE CIUDADES ---
                    configurarDropdownCiudades();

                    // Filtro inicial (Usamos la ciudad que tenga el texto o la primera disponible)
                    String ciudadActual = acCiudad.getText().toString();
                    if (ciudadActual.isEmpty() && !listaTodosLosCines.isEmpty()) {
                        ciudadActual = listaTodosLosCines.get(0).getCiudad(); // Usar la primera ciudad encontrada
                        acCiudad.setText(ciudadActual, false); // false para no disparar el filtro aún
                    }

                    filtrarCines(ciudadActual);
                }
            }

            @Override
            public void onFailure(Call<List<CineResponse>> call, Throwable t) {
                if(getContext()!=null) Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarDropdownCiudades() {
        // Usamos un SET para evitar ciudades repetidas (ej: si hay 2 cines en Lima, que solo salga "Lima" una vez)
        Set<String> ciudadesUnicas = new HashSet<>();

        for (CineResponse cine : listaTodosLosCines) {
            ciudadesUnicas.add(cine.getCiudad());
        }

        // Convertimos a lista para el adaptador
        List<String> listaCiudades = new ArrayList<>(ciudadesUnicas);
        Collections.sort(listaCiudades); // Ordenar alfabéticamente

        // Llenamos el adaptador del Dropdown
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, listaCiudades);
        acCiudad.setAdapter(cityAdapter);
    }

    private void filtrarCines(String ciudad) {
        listaFiltrada.clear();
        for (CineResponse cine : listaTodosLosCines) {
            if (cine.getCiudad().equalsIgnoreCase(ciudad)) {
                listaFiltrada.add(cine);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCineClick(CineResponse cine) {
        Bundle bundle = new Bundle();
        bundle.putInt("id_cine", cine.getIdCine());
        bundle.putString("nombre_cine", cine.getNombre());

        try {
            Navigation.findNavController(requireView()).navigate(R.id.funcionesFragment, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}