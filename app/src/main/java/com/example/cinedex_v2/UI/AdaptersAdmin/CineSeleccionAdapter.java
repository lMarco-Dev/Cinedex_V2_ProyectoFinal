package com.example.cinedex_v2.UI.AdaptersAdmin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinedex_v2.Data.DTOs.Cine.CineResponse;
import com.example.cinedex_v2.R;

import java.util.List;

public class CineSeleccionAdapter extends RecyclerView.Adapter<CineSeleccionAdapter.ViewHolder> {

    private Context context;
    private List<CineResponse> listaCines;
    private OnCineSeleccionadoListener listener;

    // Interfaz simplificada: Solo necesitamos saber cuál se seleccionó
    public interface OnCineSeleccionadoListener {
        void onCineClick(CineResponse cine);
    }

    public CineSeleccionAdapter(Context context, List<CineResponse> listaCines, OnCineSeleccionadoListener listener) {
        this.context = context;
        this.listaCines = listaCines;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // AQUÍ USAMOS EL NUEVO ITEM SIN BOTONES
        View view = LayoutInflater.from(context).inflate(R.layout.item_cine_simple, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CineResponse cine = listaCines.get(position);

        holder.tvNombre.setText(cine.getNombre());

        Glide.with(context)
                .load(cine.getUrlImagen())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_delete)
                .into(holder.ivImagen);

        // Al hacer clic en TODA la tarjeta, disparamos el evento
        holder.itemView.setOnClickListener(v -> listener.onCineClick(cine));
    }

    @Override
    public int getItemCount() {
        return listaCines.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImagen;
        TextView tvNombre;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImagen = itemView.findViewById(R.id.iv_cine_imagen);
            tvNombre = itemView.findViewById(R.id.tv_cine_nombre);
        }
    }
}