package com.example.cinedex_v2.UI.AdaptersAdmin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cinedex_v2.Data.DTOs.Playlist.PlaylistResponse;
import com.example.cinedex_v2.R;
import java.util.List;
import java.util.Collections; // Importante para swap

public class PlaylistAdminAdapter extends RecyclerView.Adapter<PlaylistAdminAdapter.ViewHolder> {

    private List<PlaylistResponse> lista;
    private OnPlaylistClickListener listener;

    public interface OnPlaylistClickListener {
        void onEditarClick(PlaylistResponse playlist);
        void onEliminarClick(PlaylistResponse playlist);
    }

    public PlaylistAdminAdapter(List<PlaylistResponse> lista, OnPlaylistClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlaylistResponse p = lista.get(position);
        holder.tvNombre.setText(p.getNombre());
        holder.tvInfo.setText(p.getCantidadPeliculas() + " películas");

        holder.btnEditar.setOnClickListener(v -> listener.onEditarClick(p));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminarClick(p));
    }

    // Método para Drag & Drop
    public void moverItem(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(lista, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(lista, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    public List<PlaylistResponse> getLista() { return lista; }

    @Override
    public int getItemCount() { return lista.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvInfo;
        ImageButton btnEditar, btnEliminar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_playlist_nombre);
            tvInfo = itemView.findViewById(R.id.tv_playlist_info);
            btnEditar = itemView.findViewById(R.id.btn_editar);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar);
        }
    }
}