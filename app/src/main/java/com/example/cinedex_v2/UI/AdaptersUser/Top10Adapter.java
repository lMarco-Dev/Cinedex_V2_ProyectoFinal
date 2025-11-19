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

public class Top10Adapter extends RecyclerView.Adapter<Top10Adapter.ViewHolder> {
    private Context context;
    private List<PeliculaResponse> list;

    public Top10Adapter(Context context, List<PeliculaResponse> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Usa tu XML 'item_movie_top10' que ya tienes (el del número gigante)
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie_top10, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvRank.setText(String.valueOf(position + 1));
        Glide.with(context).load(list.get(position).getUrlPoster()).into(holder.ivPoster);
    }

    @Override
    public int getItemCount() { return Math.min(list.size(), 10); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvRank;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.movie_poster_top10);
            tvRank = itemView.findViewById(R.id.movie_rank_number);
        }
    }
}