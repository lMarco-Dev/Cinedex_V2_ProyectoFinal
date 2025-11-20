package com.example.cinedex_v2.UI.AdminFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.Data.Network.CineDexApiService;
import com.example.cinedex_v2.Data.DTOs.Playlist.PlaylistRequest;
import com.example.cinedex_v2.Data.DTOs.Playlist.PlaylistResponse;
import com.example.cinedex_v2.Data.DTOs.Playlist.PlaylistOrdenDto;
import com.example.cinedex_v2.R;
import com.example.cinedex_v2.UI.AdaptersAdmin.PlaylistAdminAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistFragment extends Fragment
        implements PlaylistAdminAdapter.OnPlaylistClickListener, GuardarPlaylistDialog.OnPlaylistGuardadaListener {

    private RecyclerView rvPlaylists;
    private ProgressBar progressBar;
    private PlaylistAdminAdapter adapter;
    private List<PlaylistResponse> lista = new ArrayList<>();
    private CineDexApiService apiService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlists, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvPlaylists = view.findViewById(R.id.rv_playlists);
        progressBar = view.findViewById(R.id.progress_bar_playlists);
        apiService = CineDexApiClient.getApiService();

        adapter = new PlaylistAdminAdapter(lista, this);
        rvPlaylists.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPlaylists.setAdapter(adapter);

        setupDragAndDrop();

        view.findViewById(R.id.fab_agregar_playlist).setOnClickListener(v -> {
            GuardarPlaylistDialog dialog = GuardarPlaylistDialog.newInstance(null);
            dialog.setTargetFragment(this, 0);
            dialog.show(getParentFragmentManager(), "CrearPlaylist");
        });

        cargarPlaylists();
    }

    private void setupDragAndDrop() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int from = viewHolder.getAdapterPosition();
                int to = target.getAdapterPosition();
                adapter.moverItem(from, to);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {}

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                guardarOrdenEnApi(); // Guardar al soltar
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(rvPlaylists);
    }

    private void guardarOrdenEnApi() {
        List<PlaylistResponse> nuevaLista = adapter.getLista();
        List<PlaylistOrdenDto> ordenDto = new ArrayList<>();

        for (int i = 0; i < nuevaLista.size(); i++) {
            ordenDto.add(new PlaylistOrdenDto(nuevaLista.get(i).getIdPlaylist(), i + 1));
        }

        apiService.reordenarPlaylists(ordenDto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(!response.isSuccessful()) Toast.makeText(getContext(), "Error orden", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {}
        });
    }

    private void cargarPlaylists() {
        progressBar.setVisibility(View.VISIBLE);
        apiService.getPlaylists().enqueue(new Callback<List<PlaylistResponse>>() {
            @Override
            public void onResponse(Call<List<PlaylistResponse>> call, Response<List<PlaylistResponse>> response) {
                progressBar.setVisibility(View.GONE);
                if(response.isSuccessful() && response.body() != null) {
                    lista.clear();
                    lista.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<PlaylistResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onPlaylistGuardada(String nombre, Integer idToUpdate) {
        SharedPreferences prefs = getActivity().getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        int idUsuario = prefs.getInt("ID_USUARIO", 1);

        PlaylistRequest request = new PlaylistRequest(idUsuario, nombre);

        if(idToUpdate == null) {
            // Asumiendo que crear devuelve PlaylistResponse en la API pero lo manejamos genérico o casteamos
            apiService.crearPlaylist(request).enqueue(new Callback<PlaylistResponse>() {
                @Override
                public void onResponse(Call<PlaylistResponse> call, Response<PlaylistResponse> response) {
                    if(response.isSuccessful()) cargarPlaylists();
                }
                @Override
                public void onFailure(Call<PlaylistResponse> call, Throwable t) {}
            });
        } else {
            apiService.editarPlaylist(idToUpdate, request).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.isSuccessful()) cargarPlaylists();
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {}
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
                .setTitle("Eliminar Playlist")
                .setMessage("¿Borrar " + playlist.getNombre() + "?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    apiService.eliminarPlaylist(playlist.getIdPlaylist()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if(response.isSuccessful()) cargarPlaylists();
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {}
                    });
                }).show();
    }
}