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

public class SearchMovieAdapter extends RecyclerView.Adapter<SearchMovieAdapter.ViewHolder> {

    private Context context;
    private List<PeliculaResponse> list;
    private OnMovieClickListener listener;

    public interface OnMovieClickListener {
        void onMovieClick(int movieId);
    }

    public SearchMovieAdapter(Context context, List<PeliculaResponse> list, OnMovieClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Usamos el NUEVO diseño horizontal
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PeliculaResponse p = list.get(position);

        holder.tvTitle.setText(p.getTitulo());
        holder.tvCategory.setText(p.getCategoria() + " • " + (p.getDuracionMin() != null ? p.getDuracionMin() + " min" : "N/A"));
        holder.tvRating.setText(String.format(Locale.US, "%.1f", p.getNotaPromedio()));

        Glide.with(context)
                .load(p.getUrlPoster())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivPoster);

        holder.itemView.setOnClickListener(v -> listener.onMovieClick(p.getIdPelicula()));
    }

    @Override
    public int getItemCount() { return list.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvTitle, tvCategory, tvRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.iv_search_poster);
            tvTitle = itemView.findViewById(R.id.tv_search_title);
            tvCategory = itemView.findViewById(R.id.tv_search_category);
            tvRating = itemView.findViewById(R.id.tv_search_rating);
        }
    }
}