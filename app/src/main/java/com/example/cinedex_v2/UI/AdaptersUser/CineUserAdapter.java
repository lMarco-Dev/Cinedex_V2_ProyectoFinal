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
import com.example.cinedex_v2.Data.DTOs.Cine.CineResponse;
import com.example.cinedex_v2.R;
import java.util.List;

public class CineUserAdapter extends RecyclerView.Adapter<CineUserAdapter.ViewHolder> {

    private Context context;
    private List<CineResponse> lista;
    private OnCineClickListener listener;

    public interface OnCineClickListener {
        void onCineClick(CineResponse cine);
    }

    public CineUserAdapter(Context context, List<CineResponse> lista, OnCineClickListener listener) {
        this.context = context;
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Reutilizamos item_cine_simple (el que no tiene botones de editar/borrar)
        View view = LayoutInflater.from(context).inflate(R.layout.item_cine_simple, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CineResponse cine = lista.get(position);
        holder.tvNombre.setText(cine.getNombre());

        Glide.with(context)
                .load(cine.getUrlImagen())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivImagen);

        holder.itemView.setOnClickListener(v -> listener.onCineClick(cine));
    }

    @Override
    public int getItemCount() { return lista.size(); }

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