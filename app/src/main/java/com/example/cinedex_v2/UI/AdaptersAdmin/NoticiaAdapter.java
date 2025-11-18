package com.example.cinedex_v2.UI.AdaptersAdmin;

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
import com.example.cinedex_v2.Data.DTOs.Noticia.NoticiaResponse;
import com.example.cinedex_v2.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NoticiaAdapter extends RecyclerView.Adapter<NoticiaAdapter.NoticiaViewHolder> {

    private List<NoticiaResponse> noticiasList;
    private Context context;
    private OnNoticiaClickListener listener;

    public interface OnNoticiaClickListener {
        void onEditarClick(NoticiaResponse noticia);
        void onEliminarClick(NoticiaResponse noticia);
    }

    public NoticiaAdapter(Context context, List<NoticiaResponse> noticiasList, OnNoticiaClickListener listener){
        this.context = context;
        this.noticiasList = noticiasList;
        this.listener = listener;
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", new Locale("es", "ES"));

    @NonNull
    @Override
    public NoticiaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_noticia, parent, false);
        return new NoticiaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticiaViewHolder holder, int position) {
        NoticiaResponse noticia = noticiasList.get(position);

        holder.tvTitulo.setText(noticia.getTitulo());

        if (noticia.getFechaPublicacion() != null) {
            holder.tvFecha.setText(sdf.format(noticia.getFechaPublicacion()));
        } else {
            holder.tvFecha.setText("Fecha desconocida");
        }

        // Mostrar icono de video si hay link de YouTube
        if (noticia.getUrlYoutube() != null && !noticia.getUrlYoutube().isEmpty()) {
            holder.ivHasVideo.setVisibility(View.VISIBLE);
        } else {
            holder.ivHasVideo.setVisibility(View.GONE);
        }

        Glide.with(context)
                .load(noticia.getUrlImagen())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_delete)
                .into(holder.ivImagen);

        holder.btnEditar.setOnClickListener(v -> listener.onEditarClick(noticia));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminarClick(noticia));
    }

    @Override
    public int getItemCount() {
        return noticiasList.size();
    }

    public class NoticiaViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImagen;
        ImageView ivHasVideo; // <-- NUEVO
        TextView tvTitulo, tvFecha;
        ImageButton btnEditar, btnEliminar;

        public NoticiaViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImagen = itemView.findViewById(R.id.iv_noticia_imagen);
            ivHasVideo = itemView.findViewById(R.id.iv_has_video); // <-- NUEVO
            tvTitulo = itemView.findViewById(R.id.tv_noticia_titulo);
            tvFecha = itemView.findViewById(R.id.tv_noticia_fecha);
            btnEditar = itemView.findViewById(R.id.btn_editar);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar);
        }
    }
}