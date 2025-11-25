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
import com.example.cinedex_v2.Data.DTOs.Resena.ResenaResponseDto;
import com.example.cinedex_v2.R;

import java.util.List;

public class ProfileReviewAdapter extends RecyclerView.Adapter<ProfileReviewAdapter.ViewHolder> {

    private List<ResenaResponseDto> lista;
    private Context context;
    private OnReviewClickListener listener;

    public interface OnReviewClickListener {
        void onReviewClick(ResenaResponseDto resena);
    }

    public ProfileReviewAdapter(Context context, List<ResenaResponseDto> lista, OnReviewClickListener listener) {
        this.context = context;
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_perfil_resena, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ResenaResponseDto resena = lista.get(position);

        holder.tvNota.setText(String.valueOf(resena.getPuntuacion()));

        Glide.with(context)
                .load(resena.getPosterPeliculaURL())
                .placeholder(R.drawable.movie_placeholder)
                .into(holder.ivPoster);

        holder.itemView.setOnClickListener(v -> listener.onReviewClick(resena));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvNota;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.iv_item_poster);
            tvNota = itemView.findViewById(R.id.tv_item_nota);
        }
    }
}
