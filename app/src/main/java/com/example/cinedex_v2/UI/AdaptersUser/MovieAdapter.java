package com.example.cinedex_v2.UI.AdaptersUser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.cinedex_v2.Data.DTOs.Pelicula.PeliculaResponse;
import com.example.cinedex_v2.R;
import java.util.List;
import java.util.Locale;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private Context context;
    private List<PeliculaResponse> list;

    // 1. Nueva variable para el listener
    private OnMovieClickListener listener;

    // 2. Interfaz para comunicar el clic
    public interface OnMovieClickListener {
        void onMovieClick(int movieId);
    }

    // 3. Constructor actualizado
    public MovieAdapter(Context context, List<PeliculaResponse> list, OnMovieClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener; // Guardamos el listener
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PeliculaResponse pelicula = list.get(position);

        // 1. CARGAR IMAGEN
        Glide.with(context)
                .load(pelicula.getUrlPoster())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivPoster);

        //2. Cargamos el texto
        holder.tvTitle.setText(pelicula.getTitulo());

        //3. La nota promedio
        holder.tvVote.setText(String.format(Locale.US, "%.1f ⭐", pelicula.getNotaPromedio()));

        // 4. AQUI ESTÁ LA MAGIA: Detectar el clic y avisar
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMovieClick(pelicula.getIdPelicula());
            }
        });
    }

    @Override
    public int getItemCount() { return list.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvTitle, tvVote; // <-- Declarar los TextViews

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Enlazar con los IDs de item_movie.xml
            ivPoster = itemView.findViewById(R.id.movie_poster);
            tvTitle = itemView.findViewById(R.id.movie_title);       // <-- Enlazar Título
            tvVote = itemView.findViewById(R.id.movie_vote_count);   // <-- Enlazar Nota
        }
    }
}