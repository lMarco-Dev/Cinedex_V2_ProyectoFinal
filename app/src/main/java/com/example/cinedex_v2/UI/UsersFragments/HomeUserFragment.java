package com.example.cinedex_v2.UI.UsersFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation; // Importante para navegar
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.Data.DTOs.Pelicula.PeliculaResponse;
import com.example.cinedex_v2.R;
import com.example.cinedex_v2.UI.AdaptersUser.MovieAdapter;
import com.example.cinedex_v2.UI.AdaptersUser.Top10Adapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 1. Implementar la interfaz OnMovieClickListener
public class HomeUserFragment extends Fragment implements MovieAdapter.OnMovieClickListener {

    private RecyclerView rvMasVisto, rvAccion, rvTop10, rvRomance;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvMasVisto = view.findViewById(R.id.rv_mas_visto);
        rvAccion = view.findViewById(R.id.rv_accion);
        rvTop10 = view.findViewById(R.id.rv_top10);
        rvRomance = view.findViewById(R.id.rv_romance);
        cargarPeliculas();
    }

    private void cargarPeliculas() {
        CineDexApiClient.getApiService().getPeliculas().enqueue(new Callback<List<PeliculaResponse>>() {
            @Override
            public void onResponse(Call<List<PeliculaResponse>> call, Response<List<PeliculaResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PeliculaResponse> todas = response.body();
                    List<PeliculaResponse> listaAccion = new ArrayList<>();
                    List<PeliculaResponse> listaRomance = new ArrayList<>();

                    for (PeliculaResponse p : todas) {
                        if (p.getCategoria() != null) {
                            if (p.getCategoria().equalsIgnoreCase("Acción")) listaAccion.add(p);
                            if (p.getCategoria().equalsIgnoreCase("Romance")) listaRomance.add(p);
                        }
                    }

                    // 2. Pasar 'this' como listener al crear los adapters
                    setupRecycler(rvMasVisto, new MovieAdapter(getContext(), todas, HomeUserFragment.this));
                    setupRecycler(rvAccion, new MovieAdapter(getContext(), listaAccion, HomeUserFragment.this));
                    setupRecycler(rvRomance, new MovieAdapter(getContext(), listaRomance, HomeUserFragment.this));

                    // Para Top10 tendrías que actualizar su adapter igual que hicimos con MovieAdapter
                    // setupRecycler(rvTop10, new Top10Adapter(getContext(), todas, this));
                }
            }
            @Override
            public void onFailure(Call<List<PeliculaResponse>> call, Throwable t) { }
        });
    }

    private void setupRecycler(RecyclerView rv, RecyclerView.Adapter adapter) {
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(adapter);
    }

    // 3. Implementar el método de clic
    @Override
    public void onMovieClick(int movieId) {
        // Crear el paquete con el ID
        Bundle bundle = new Bundle();
        bundle.putInt("movieId", movieId);

        // Navegar al detalle (Asegúrate que el ID de la acción o destino sea correcto en tu nav_graph)
        try {
            Navigation.findNavController(requireView())
                    .navigate(R.id.movieDetailFragment, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}