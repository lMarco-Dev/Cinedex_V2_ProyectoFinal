package com.example.cinedex_v2.UI.AdaptersUser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinedex_v2.Data.DTOs.Playlist.PlaylistResponse;
import com.example.cinedex_v2.R;

import java.util.List;

public class HomePlaylistAdapter extends RecyclerView.Adapter<HomePlaylistAdapter.ViewHolder> {

    private Context context;
    private List<PlaylistResponse> playlists;
    private MovieAdapter.OnMovieClickListener movieClickListener;

    public HomePlaylistAdapter(Context context, List<PlaylistResponse> playlists, MovieAdapter.OnMovieClickListener listener) {
        this.context = context;
        this.playlists = playlists;
        this.movieClickListener = listener;
    }

    // Construimos el estante vacio!
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Usamos el layout de sección que ya tienes
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_section_dynamic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlaylistResponse playlist = playlists.get(position);

        // 1. Título de la Playlist (ej: "Mundo Mágico de Harry Potter")
        holder.tvTitulo.setText(playlist.getNombre());

        // 2. Configurar el Recycler Horizontal interno con las películas de esa playlist
        MovieAdapter childAdapter = new MovieAdapter(context, playlist.getPeliculas(), movieClickListener);

        holder.rvHorizontal.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.rvHorizontal.setAdapter(childAdapter);
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitulo;
        RecyclerView rvHorizontal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tv_section_title);
            rvHorizontal = itemView.findViewById(R.id.rv_section_horizontal);
        }
    }
}