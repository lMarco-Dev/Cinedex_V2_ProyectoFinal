package com.example.cinedex_v2.UI.AdaptersUser;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinedex_v2.Data.DTOs.Resena.ResenaResponseDto;
import com.example.cinedex_v2.R;

import java.util.List;

public class ResenaAdapter extends RecyclerView.Adapter<ResenaAdapter.ViewHolder> {

    private List<ResenaResponseDto> lista;

    public ResenaAdapter(List<ResenaResponseDto> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_resena, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ResenaResponseDto r = lista.get(position);

        // Setear datos de la reseña
        holder.txtComentario.setText(r.getComentario());
        holder.txtPelicula.setText(r.getTituloPelicula());
        holder.ratingBar.setRating((float) r.getPuntuacion());
        holder.fecha.setText(r.getFecha().toString());

        Glide.with(holder.itemView.getContext())
                .load(r.getPosterPeliculaURL())
                .placeholder(R.drawable.bg_poster_placeholder)
                .error(R.drawable.bg_poster_placeholder)
                .centerCrop()
                .into(holder.imgPoster);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtComentario, txtPelicula, fecha;
        RatingBar ratingBar;
        ImageView imgPoster;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtComentario = itemView.findViewById(R.id.txtComentario);
            txtPelicula = itemView.findViewById(R.id.txtPelicula);
            fecha = itemView.findViewById(R.id.txtFecha);
            ratingBar = itemView.findViewById(R.id.ratingBarItem);
            imgPoster = itemView.findViewById(R.id.imgPoster);
        }
    }
}
