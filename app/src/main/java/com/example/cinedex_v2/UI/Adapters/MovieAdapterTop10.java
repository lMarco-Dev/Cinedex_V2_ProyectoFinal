package com.example.cinedex_v2.UI.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.cinedex_v2.Data.Models.Pelicula;
import com.example.cinedex_v2.R;
import java.util.List;

public class MovieAdapterTop10 extends RecyclerView.Adapter<MovieAdapterTop10.MovieTop10ViewHolder> {

    private List<Pelicula> peliculas;
    private Context context;

    public MovieAdapterTop10(List<Pelicula> peliculas, Context context) {
        this.peliculas = peliculas;
        this.context = context;
    }

    @NonNull
    @Override
    public MovieTop10ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie_top10, parent, false);
        return new MovieTop10ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieTop10ViewHolder holder, int position) {
        Pelicula pelicula = peliculas.get(position);

        holder.rankNumber.setText(String.valueOf(position + 1));

        Glide.with(context)
                .load(pelicula.getUrlPoster())
                .into(holder.poster);
    }

    @Override
    public int getItemCount() {
        return peliculas.size();
    }

    public static class MovieTop10ViewHolder extends RecyclerView.ViewHolder {
        ImageView poster;
        TextView rankNumber;

        public MovieTop10ViewHolder(@NonNull View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.movie_poster_top10);
            rankNumber = itemView.findViewById(R.id.movie_rank_number);
        }
    }
}
