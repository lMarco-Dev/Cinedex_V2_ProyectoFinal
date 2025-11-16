package com.example.cinedex_v2.UI.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.R;
import com.example.cinedex_v2.UI.Adapters.MainAdapter;
import com.example.cinedex_v2.Data.DTOs.Pelicula.PeliculaResponse;
import com.example.cinedex_v2.Data.Models.Pelicula;
import com.example.cinedex_v2.Data.Models.Section;
import com.example.cinedex_v2.Data.Network.CineDexApiService;
import com.example.cinedex_v2.Data.Network.CineDexApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView mainRecyclerView;
    private MainAdapter mainAdapter;
    private CineDexApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.ly_fragment_home, container, false);

        mainRecyclerView = view.findViewById(R.id.recycler_popular_movies);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mainAdapter = new MainAdapter(new ArrayList<>(), getContext());
        mainRecyclerView.setAdapter(mainAdapter);

        // Inicializamos Retrofit
        apiService = CineDexApiClient.getApiService();

        fetchAllMovies();

        return view;
    }

    private void fetchAllMovies() {
        apiService.getPeliculas().enqueue(new Callback<List<PeliculaResponse>>() {
            @Override
            public void onResponse(Call<List<PeliculaResponse>> call, Response<List<PeliculaResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PeliculaResponse> peliculasResponse = response.body();

                    // Convertimos PeliculaResponse a Pelicula
                    List<Pelicula> peliculas = new ArrayList<>();
                    for (PeliculaResponse pr : peliculasResponse) {
                        peliculas.add(new Pelicula(
                                pr.getIdPelicula(),
                                pr.getTitulo(),
                                pr.getDescripcion(),
                                pr.getUrlPoster(),
                                pr.getCategoria(),
                                pr.getTipoEstreno(),
                                pr.getPlataformasStreaming(),
                                pr.getDirector(),
                                pr.getPais(),
                                pr.getDuracionMin(),
                                pr.getNotaPromedio()
                        ));
                    }

                    // Creamos la sección
                    Section sectionTodas = new Section("Todas las películas", peliculas);

                    // Agregamos al MainAdapter
                    List<Object> finalList = new ArrayList<>();
                    finalList.add(sectionTodas);

                    mainAdapter.setSections(finalList);
                } else {
                    Log.e("HomeFragment", "Error al obtener películas: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<PeliculaResponse>> call, Throwable t) {
                Log.e("HomeFragment", "Fallo en la llamada a la API", t);
            }
        });
    }
}
