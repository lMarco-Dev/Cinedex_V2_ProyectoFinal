package com.example.cinedex_v2.UI.UsersFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinedex_v2.Data.DTOs.Playlist.PlaylistRequest;
import com.example.cinedex_v2.Data.DTOs.Playlist.PlaylistResponse;
import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.Data.Network.CineDexApiService;
import com.example.cinedex_v2.R;
import com.example.cinedex_v2.UI.AdaptersAdmin.PlaylistAdminAdapter;
import com.example.cinedex_v2.UI.AdminFragments.GuardarPlaylistDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MisPlaylistsFragment extends Fragment
        implements PlaylistAdminAdapter.OnPlaylistClickListener, GuardarPlaylistDialog.OnPlaylistGuardadaListener {

    private RecyclerView rvPlaylists;
    private PlaylistAdminAdapter adapter;
    private List<PlaylistResponse> misListas = new ArrayList<>();
    private CineDexApiService apiService;
    private int idUsuario;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlists, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = requireActivity().getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        idUsuario = prefs.getInt("ID_USUARIO", -1);
        apiService = CineDexApiClient.getApiService();

        rvPlaylists = view.findViewById(R.id.rv_playlists);

        // Usamos el adapter del admin (reutilizar código es bueno)
        adapter = new PlaylistAdminAdapter(misListas, this);
        rvPlaylists.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPlaylists.setAdapter(adapter);

        // Botón Crear
        view.findViewById(R.id.fab_agregar_playlist).setOnClickListener(v -> {
            GuardarPlaylistDialog dialog = GuardarPlaylistDialog.newInstance(null);
            dialog.setTargetFragment(this, 0);
            dialog.show(getParentFragmentManager(), "CrearLista");
        });

        if(idUsuario != -1) cargarMisPlaylists();
    }

    private void cargarMisPlaylists() {
        // Trae todas y filtra en el cliente (o usa endpoint por usuario si tienes)
        apiService.getPlaylists().enqueue(new Callback<List<PlaylistResponse>>() {
            @Override
            public void onResponse(Call<List<PlaylistResponse>> call, Response<List<PlaylistResponse>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    misListas.clear();
                    for(PlaylistResponse p : response.body()) {
                        // FILTRO: Solo las mías
                        if(p.getIdUsuario() == idUsuario) {
                            misListas.add(p);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<PlaylistResponse>> call, Throwable t) {}
        });
    }

    @Override
    public void onPlaylistGuardada(String nombre, Integer idToUpdate) {
        PlaylistRequest req = new PlaylistRequest(idUsuario, nombre);
        if(idToUpdate == null) {
            apiService.crearPlaylist(req).enqueue(new Callback<PlaylistResponse>() {
                @Override public void onResponse(Call<PlaylistResponse> c, Response<PlaylistResponse> r) { cargarMisPlaylists(); }
                @Override public void onFailure(Call<PlaylistResponse> c, Throwable t) {}
            });
        } else {
            apiService.editarPlaylist(idToUpdate, req).enqueue(new Callback<Void>() {
                @Override public void onResponse(Call<Void> c, Response<Void> r) { cargarMisPlaylists(); }
                @Override public void onFailure(Call<Void> c, Throwable t) {}
            });
        }
    }

    @Override
    public void onEditarClick(PlaylistResponse playlist) {
        GuardarPlaylistDialog dialog = GuardarPlaylistDialog.newInstance(playlist);
        dialog.setTargetFragment(this, 0);
        dialog.show(getParentFragmentManager(), "Editar");
    }

    @Override
    public void onEliminarClick(PlaylistResponse playlist) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Borrar lista")
                .setMessage("¿Eliminar '" + playlist.getNombre() + "'?")
                .setPositiveButton("Sí", (d, w) -> {
                    apiService.eliminarPlaylist(playlist.getIdPlaylist()).enqueue(new Callback<Void>() {
                        @Override public void onResponse(Call<Void> c, Response<Void> r) { cargarMisPlaylists(); }
                        @Override public void onFailure(Call<Void> c, Throwable t) {}
                    });
                }).show();
    }

    @Override
    public void onItemClick(PlaylistResponse playlist) {
        Bundle bundle = new Bundle();
        bundle.putInt("id_playlist", playlist.getIdPlaylist());
        bundle.putString("nombre_playlist", playlist.getNombre());
        try {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.playlistDetailFragment, bundle);
        } catch (Exception e) { e.printStackTrace(); }
    }
}