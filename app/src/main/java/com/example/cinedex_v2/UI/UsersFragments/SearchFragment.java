package com.example.cinedex_v2.UI.UsersFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinedex_v2.Data.DTOs.Pelicula.PeliculaResponse;
import com.example.cinedex_v2.Data.DTOs.Usuario.UsuarioResponseDto;
import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.R;
import com.example.cinedex_v2.UI.AdaptersUser.SearchMovieAdapter;
import com.example.cinedex_v2.UI.AdaptersUser.UserSearchAdapter;
import com.google.android.material.appbar.MaterialToolbar; // Importante
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private SearchView searchView;
    private TabLayout tabLayout;
    private RecyclerView rvResults;

    private SearchMovieAdapter movieAdapter;
    private UserSearchAdapter userAdapter;

    private List<PeliculaResponse> listaPeliculas = new ArrayList<>();
    private List<UsuarioResponseDto> listaUsuarios = new ArrayList<>();
    private int currentTab = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- CONFIGURAR TOOLBAR (NUEVO) ---
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_search);
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(view).popBackStack());

        searchView = view.findViewById(R.id.search_view);
        tabLayout = view.findViewById(R.id.tab_layout_search);
        rvResults = view.findViewById(R.id.rv_search_results);

        rvResults.setLayoutManager(new LinearLayoutManager(getContext()));

        // Listener de Búsqueda
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                realizarBusqueda(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length() > 2) realizarBusqueda(newText);
                return false;
            }
        });

        // Listener de Tabs
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                String query = searchView.getQuery().toString();
                if(!query.isEmpty()) realizarBusqueda(query);
                else limpiarLista();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void realizarBusqueda(String query) {
        if (currentTab == 0) buscarPeliculas(query);
        else buscarUsuarios(query);
    }

    private void buscarPeliculas(String query) {
        CineDexApiClient.getApiService().buscarPeliculas(query).enqueue(new Callback<List<PeliculaResponse>>() {
            @Override
            public void onResponse(Call<List<PeliculaResponse>> call, Response<List<PeliculaResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaPeliculas.clear();
                    listaPeliculas.addAll(response.body());

                    movieAdapter = new SearchMovieAdapter(getContext(), listaPeliculas, movieId -> {
                        Bundle bundle = new Bundle();
                        bundle.putInt("movieId", movieId);
                        try {
                            Navigation.findNavController(getView()).navigate(R.id.movieDetailFragment, bundle);
                        } catch (Exception e) {}
                    });
                    rvResults.setAdapter(movieAdapter);
                }
            }
            @Override
            public void onFailure(Call<List<PeliculaResponse>> call, Throwable t) {}
        });
    }

    private void buscarUsuarios(String query) {
        CineDexApiClient.getApiService().buscarUsuarios(query).enqueue(new Callback<List<UsuarioResponseDto>>() {
            @Override
            public void onResponse(Call<List<UsuarioResponseDto>> call, Response<List<UsuarioResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaUsuarios.clear();
                    listaUsuarios.addAll(response.body());

                    userAdapter = new UserSearchAdapter(getContext(), listaUsuarios, usuario -> {
                        // Acción al clic en usuario

                        // 1. Preparamos el ID del Usuario seleccionado
                        Bundle bundle = new Bundle();
                        bundle.putInt("userId", usuario.getIdUsuario());

                        // 2. Navegamos al Perfil público
                        try {
                            Navigation.findNavController(requireView())
                                    .navigate(R.id.userProfileFragment, bundle);
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    rvResults.setAdapter(userAdapter);
                }
            }
            @Override
            public void onFailure(Call<List<UsuarioResponseDto>> call, Throwable t) {}
        });
    }

    private void limpiarLista() {
        rvResults.setAdapter(null);
    }
}