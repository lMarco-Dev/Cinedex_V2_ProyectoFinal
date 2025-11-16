package com.example.cinedex_v2.UI.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.cinedex_v2.Data.Models.Pelicula;
import com.example.cinedex_v2.R;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Pelicula> peliculas;
    private Context context;

    public MovieAdapter(List<Pelicula> peliculas, Context context) {
        this.peliculas = peliculas;
        this.context = context;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Pelicula pelicula = peliculas.get(position);

        holder.title.setText(pelicula.getTitulo());
        holder.voteCount.setText(String.format("%.1f ⭐", pelicula.getNotaPromedio()));

        Glide.with(context)
                .load(pelicula.getUrlPoster())
                .into(holder.poster);

        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("movieId", pelicula.getIdPelicula());
            Navigation.findNavController(v)
                    .navigate(R.id.action_homeFragment_to_movieDetailFragment, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return peliculas.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView title, voteCount;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.movie_poster);
            title = itemView.findViewById(R.id.movie_title);
            voteCount = itemView.findViewById(R.id.movie_vote_count);
        }
    }
}
