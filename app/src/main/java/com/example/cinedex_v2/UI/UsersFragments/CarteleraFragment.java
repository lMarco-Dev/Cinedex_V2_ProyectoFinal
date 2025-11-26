package com.example.cinedex_v2.UI.UsersFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cinedex_v2.Data.DTOs.Funcion.FuncionResponse;
import com.example.cinedex_v2.Data.Models.PeliculaAgrupada;
import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.R;
import com.example.cinedex_v2.UI.AdaptersUser.CarteleraAdapter;
import com.example.cinedex_v2.UI.AdaptersUser.FechaAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarteleraFragment extends Fragment {

    private int idCine;
    private String nombreCine;
    private RecyclerView rvFechas, rvCartelera;
    private ProgressBar progressBar;

    // Datos
    private List<Date> listaFechas = new ArrayList<>();
    private List<FuncionResponse> todasLasFunciones = new ArrayList<>();
    private List<PeliculaAgrupada> carteleraFiltrada = new ArrayList<>();
    private Date fechaSeleccionada;

    // Adapters
    private CarteleraAdapter carteleraAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idCine = getArguments().getInt("id_cine");
            nombreCine = getArguments().getString("nombre_cine");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Asegúrate de que tu XML se llame así
        return inflater.inflate(R.layout.fragment_cartelera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_cartelera);
        toolbar.setTitle(nombreCine);

        rvFechas = view.findViewById(R.id.rv_fechas);
        rvCartelera = view.findViewById(R.id.rv_cartelera);
        progressBar = view.findViewById(R.id.pb_cartelera);

        // 1. Generar Fechas
        generarFechas();
        fechaSeleccionada = listaFechas.get(0);

        // 2. Adapter de Fechas (Horizontal)
        FechaAdapter fechaAdapter = new FechaAdapter(listaFechas, fecha -> {
            fechaSeleccionada = fecha;
            procesarYMostrarFunciones();
        });
        rvFechas.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvFechas.setAdapter(fechaAdapter);

        // 3. Adapter de Cartelera (Vertical)
        carteleraAdapter = new CarteleraAdapter(getContext(), carteleraFiltrada, funcion -> {
            Toast.makeText(getContext(), "Seleccionaste: " + funcion.getSala(), Toast.LENGTH_SHORT).show();
            // Aquí iremos a la selección de butacas luego
        });
        rvCartelera.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCartelera.setAdapter(carteleraAdapter);

        // 4. Cargar datos de API
        cargarFunciones();
    }

    private void generarFechas() {
        listaFechas.clear();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            listaFechas.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    private void cargarFunciones() {
        progressBar.setVisibility(View.VISIBLE);
        CineDexApiClient.getApiService().getPorCine(idCine).enqueue(new Callback<List<FuncionResponse>>() {
            @Override
            public void onResponse(Call<List<FuncionResponse>> call, Response<List<FuncionResponse>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    todasLasFunciones = response.body();
                    procesarYMostrarFunciones();
                }
            }
            @Override
            public void onFailure(Call<List<FuncionResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void procesarYMostrarFunciones() {
        carteleraFiltrada.clear();
        if (todasLasFunciones == null || fechaSeleccionada == null) return;

        // A. Filtramos por la fecha seleccionada
        List<FuncionResponse> funcionesDia = new ArrayList<>();
        Calendar calSel = Calendar.getInstance();
        calSel.setTime(fechaSeleccionada);

        for (FuncionResponse f : todasLasFunciones) {
            Calendar calFun = Calendar.getInstance();
            calFun.setTime(f.getFechaHora());

            if (calFun.get(Calendar.DAY_OF_YEAR) == calSel.get(Calendar.DAY_OF_YEAR) &&
                    calFun.get(Calendar.YEAR) == calSel.get(Calendar.YEAR)) {
                funcionesDia.add(f);
            }
        }

        // B. Agrupamos por película
        Map<Integer, PeliculaAgrupada> mapa = new HashMap<>();
        for (FuncionResponse f : funcionesDia) {
            int idPeli = f.getIdPelicula();
            if (!mapa.containsKey(idPeli)) {
                // Creamos el grupo con los datos de la película
                mapa.put(idPeli, new PeliculaAgrupada(
                        f.getIdPelicula(),
                        f.getNombrePelicula(),
                        f.getUrlImagenPelicula(),
                        "ATP", // Clasificación (puedes traerla del backend si la agregas al DTO)
                        "120 min",
                        f.getFormato(),
                        f.getIdioma(),
                        new ArrayList<>()
                ));
            }
            // Agregamos la función al grupo
            mapa.get(idPeli).getFunciones().add(f);
        }

        // C. Actualizamos el adapter
        carteleraFiltrada.addAll(mapa.values());
        carteleraAdapter.notifyDataSetChanged();

        if (carteleraFiltrada.isEmpty()) {
            Toast.makeText(getContext(), "No hay funciones para esta fecha", Toast.LENGTH_SHORT).show();
        }
    }
}