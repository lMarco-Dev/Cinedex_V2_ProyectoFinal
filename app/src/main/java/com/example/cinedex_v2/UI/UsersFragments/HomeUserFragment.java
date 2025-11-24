package com.example.cinedex_v2.UI.UsersFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.Data.DTOs.Playlist.PlaylistResponse;
import com.example.cinedex_v2.R;
import com.example.cinedex_v2.UI.AdaptersUser.HomePlaylistAdapter;
import com.example.cinedex_v2.UI.AdaptersUser.HomePlaylistAdapter;
import com.example.cinedex_v2.UI.AdaptersUser.MovieAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeUserFragment extends Fragment implements MovieAdapter.OnMovieClickListener {

    private RecyclerView rvMain;
    private HomePlaylistAdapter parentAdapter;
    private List<PlaylistResponse> listaPlaylists = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvMain = view.findViewById(R.id.rv_home_principal);
        rvMain.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializamos el adaptador padre
        parentAdapter = new HomePlaylistAdapter(getContext(), listaPlaylists, this);
        rvMain.setAdapter(parentAdapter);

        cargarHomeDinamico();
    }

    private void cargarHomeDinamico() {
        // Llamamos a la API de Playlists
        CineDexApiClient.getApiService().getPlaylists().enqueue(new Callback<List<PlaylistResponse>>() {
            @Override
            public void onResponse(Call<List<PlaylistResponse>> call, Response<List<PlaylistResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaPlaylists.clear();

                    // Solo agregamos playlists que tengan al menos 1 película
                    for (PlaylistResponse p : response.body()) {
                        if (p.getPeliculas() != null && !p.getPeliculas().isEmpty()) {
                            listaPlaylists.add(p);
                        }
                    }

                    parentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<PlaylistResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Click en una película (para ir al detalle)
    @Override
    public void onMovieClick(int movieId) {
        Bundle bundle = new Bundle();
        bundle.putInt("movieId", movieId);
        try {
            Navigation.findNavController(requireView()).navigate(R.id.movieDetailFragment, bundle);
        } catch (Exception e) { e.printStackTrace(); }
    }
}