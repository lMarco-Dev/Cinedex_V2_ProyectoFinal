package com.example.cinedex_v2.UI.AdaptersUser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinedex_v2.Data.DTOs.Funcion.FuncionResponse;
import com.example.cinedex_v2.Data.Models.PeliculaAgrupada;
import com.example.cinedex_v2.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.ChipGroup; // Usaremos ChipGroup para los botones, es más fácil

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CarteleraAdapter extends RecyclerView.Adapter<CarteleraAdapter.ViewHolder> {

    private Context context;
    private List<PeliculaAgrupada> listaPeliculas;
    private OnFuncionClickListener listener;

    public interface OnFuncionClickListener {
        void onFuncionClick(FuncionResponse funcion);
    }

    public CarteleraAdapter(Context context, List<PeliculaAgrupada> listaPeliculas, OnFuncionClickListener listener) {
        this.context = context;
        this.listaPeliculas = listaPeliculas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cartelera_pelicula, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PeliculaAgrupada item = listaPeliculas.get(position);

        // 1. Datos de la Película
        holder.tvTitulo.setText(item.getNombrePelicula());

        // Construir subtítulo (Ej: "1h 30m | +14")
        String detalles = item.getDuracion() + " | " + item.getClasificacion();
        holder.tvDetalles.setText(detalles);

        // Cargar Poster
        Glide.with(context)
                .load(item.getUrlPoster())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivPoster);

        // 2. GENERAR BOTONES DE HORARIO (Dinámico)
        holder.containerHorarios.removeAllViews();

        for (FuncionResponse funcion : item.getFunciones()) {
            // Inflamos el diseño del botón individual
            View viewBoton = LayoutInflater.from(context).inflate(R.layout.item_boton_hora, holder.containerHorarios, false);

            MaterialButton btnHora = viewBoton.findViewById(R.id.btn_hora_funcion);

            // Formatear hora (Ej: "04:20 PM")
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            btnHora.setText(sdf.format(funcion.getFechaHora()));

            // Click en la hora -> Comprar
            btnHora.setOnClickListener(v -> listener.onFuncionClick(funcion));

            holder.containerHorarios.addView(viewBoton);
        }
    }

    @Override
    public int getItemCount() {
        return listaPeliculas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvTitulo, tvDetalles;
        // Usamos ChipGroup o un LinearLayout que envuelva (FlowLayout)
        // Para simplificar si no tienes librerias extra, usaremos ChipGroup de Material
        ChipGroup containerHorarios;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.iv_cartelera_poster);
            tvTitulo = itemView.findViewById(R.id.tv_cartelera_titulo);
            tvDetalles = itemView.findViewById(R.id.tv_cartelera_detalles);
            containerHorarios = itemView.findViewById(R.id.container_horarios);
        }
    }
}