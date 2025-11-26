package com.example.cinedex_v2.UI.UsersFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cinedex_v2.Data.DTOs.Cine.CineResponse;
import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.R;
import com.example.cinedex_v2.UI.AdaptersUser.CineUserAdapter;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CinesUserFragment extends Fragment {

    private AutoCompleteTextView acCiudad;
    private RecyclerView rvCines;
    private CineUserAdapter adapter;
    private List<CineResponse> listaCompleta = new ArrayList<>();
    private List<CineResponse> listaFiltrada = new ArrayList<>();

    private static final String[] CIUDADES = {"Cajamarca", "Trujillo", "Lima", "Arequipa", "Cusco", "Piura"};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_cines, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        acCiudad = view.findViewById(R.id.ac_ciudad_usuario);
        rvCines = view.findViewById(R.id.rv_cines_usuario);

        ArrayAdapter<String> adapterCiudad = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, CIUDADES);
        acCiudad.setAdapter(adapterCiudad);

        acCiudad.setOnItemClickListener((parent, v, position, id) ->
                filtrarCines(parent.getItemAtPosition(position).toString())
        );

        rvCines.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // --- AQUÍ ESTABA EL ERROR ---
        adapter = new CineUserAdapter(getContext(), listaFiltrada, cine -> {
            Bundle bundle = new Bundle();
            bundle.putInt("id_cine", cine.getIdCine());
            bundle.putString("nombre_cine", cine.getNombre());

            // CORREGIDO: Navegar a 'carteleraFragment', NO a 'cinesUserFragment'
            Navigation.findNavController(view).navigate(R.id.carteleraFragment, bundle);
        });

        rvCines.setAdapter(adapter);

        cargarCines();
    }

    private void cargarCines() {
        CineDexApiClient.getApiService().getCines().enqueue(new Callback<List<CineResponse>>() {
            @Override
            public void onResponse(Call<List<CineResponse>> call, Response<List<CineResponse>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    listaCompleta.clear();
                    listaCompleta.addAll(response.body());
                    String ciudad = acCiudad.getText().toString();
                    if(ciudad.isEmpty()) ciudad = "Cajamarca";
                    filtrarCines(ciudad);
                }
            }
            @Override
            public void onFailure(Call<List<CineResponse>> call, Throwable t) {}
        });
    }

    private void filtrarCines(String ciudad) {
        listaFiltrada.clear();
        for(CineResponse c : listaCompleta) {
            if(c.getCiudad().equalsIgnoreCase(ciudad)) listaFiltrada.add(c);
        }
        adapter.notifyDataSetChanged();
    }
}