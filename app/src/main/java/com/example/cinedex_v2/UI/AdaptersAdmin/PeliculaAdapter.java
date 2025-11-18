package com.example.cinedex_v2.UI.AdaptersAdmin; // (Usa tu paquete de adaptadores)

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cinedex_v2.R; // (Asegúrate que R sea de tu paquete)
import com.example.cinedex_v2.Data.DTOs.Pelicula.PeliculaResponse; // (Usa tu DTO)
import java.util.List;
import java.util.Locale;

public class PeliculaAdapter extends RecyclerView.Adapter<PeliculaAdapter.PeliculaViewHolder> {

    private List<PeliculaResponse> peliculasList;
    private Context context;
    private OnPeliculaClickListener listener;

    // Esta interfaz es la que necesita tu PeliculasFragment
    public interface OnPeliculaClickListener {
        void onEditarClick(PeliculaResponse pelicula);
        void onEliminarClick(PeliculaResponse pelicula);
    }

    public PeliculaAdapter(Context context, List<PeliculaResponse> peliculasList, OnPeliculaClickListener listener) {
        this.context = context;
        this.peliculasList = peliculasList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PeliculaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Asegúrate de usar el layout 'item_pelicula.xml' (el que tiene los botones)
        View view = LayoutInflater.from(context).inflate(R.layout.item_pelicula, parent, false);
        return new PeliculaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PeliculaViewHolder holder, int position) {
        PeliculaResponse pelicula = peliculasList.get(position);

        holder.tvTitulo.setText(pelicula.getTitulo());
        String info = pelicula.getCategoria() + " - " + pelicula.getDuracionMin() + " min";
        holder.tvCategoriaDuracion.setText(info);
        holder.tvDirectorPais.setText(pelicula.getDirector() + " · " + pelicula.getPais());
        holder.tvNota.setText(String.format(Locale.US, "%.1f", pelicula.getNotaPromedio()));

        Glide.with(context)
                .load(pelicula.getUrlPoster())
                .placeholder(R.drawable.ic_launcher_background) // Debes tener un placeholder
                .error(R.drawable.ic_delete) // Debes tener un ícono de error
                .into(holder.ivPoster);

        holder.btnEditar.setOnClickListener(v -> listener.onEditarClick(pelicula));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminarClick(pelicula));
    }

    @Override
    public int getItemCount() {
        return peliculasList.size();
    }

    public class PeliculaViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvTitulo, tvCategoriaDuracion, tvDirectorPais, tvNota;
        ImageButton btnEditar, btnEliminar;

        public PeliculaViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.iv_poster);
            tvTitulo = itemView.findViewById(R.id.tv_titulo_pelicula);
            tvCategoriaDuracion = itemView.findViewById(R.id.tv_categoria_duracion);
            tvDirectorPais = itemView.findViewById(R.id.tv_director_pais);
            tvNota = itemView.findViewById(R.id.tv_nota);
            btnEditar = itemView.findViewById(R.id.btn_editar);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar);
        }
    }

}