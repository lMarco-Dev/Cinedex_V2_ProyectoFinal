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
import com.example.cinedex_v2.Data.DTOs.Evento.EventoResponse;
import com.example.cinedex_v2.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.EventoViewHolder> {

    private List<EventoResponse> eventosList;
    private Context context;
    private OnEventoClickListener listener;
    private boolean isEditable;

    //Interfaz para EventoFragment
    public interface OnEventoClickListener {
        void onEditarClick(EventoResponse evento);
        void onEliminarClick(EventoResponse evento);
        void onItemClick(EventoResponse evento);
    }

    public EventoAdapter(Context context, List<EventoResponse> eventosList, boolean isEditable,OnEventoClickListener listener){
        this.context = context;
        this.eventosList = eventosList;
        this.isEditable = isEditable;
        this.listener = listener;
    }

    // Transforma el Date a un String legible
    private SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, hh:mm a", new Locale("es", "ES"));

    /* -----------------------------------------------------
          OnCreateViewHolder: Crea la tarjeta vacia
       ----------------------------------------------------- */
    @NonNull
    @Override
    public EventoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_evento, parent, false);
        return new EventoViewHolder(view);
    }

    /* -----------------------------------------------------
         OnBindViewHolder: toma la tarjeta vacía y la rellana con los datos
      ----------------------------------------------------- */
    @Override
    public void onBindViewHolder(@NonNull EventoViewHolder holder, int position) {
        // 1. Obtener el objeto de datos para esta posición
        EventoResponse evento = eventosList.get(position);

        // 2. Rellenar los TextViews
        holder.tvEventoTitulo.setText(evento.getTitulo());
        holder.tvEventoUbicacion.setText(evento.getUbicacion());

        // 3. Formatear y rellenar la fecha
        if (evento.getFechaHora() != null) {
            String fechaFormateada = sdf.format(evento.getFechaHora());
            holder.tvEventoFecha.setText(fechaFormateada);
        } else {
            holder.tvEventoFecha.setText("Fecha no disponible");
        }

        // 4. Cargar la imagen del evento usando Glide
        Glide.with(context)
                .load(evento.getUrlImagen())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_delete)
                .into(holder.ivEventoImagen);

        // 5. Asignar los listeners para los botones de Editar y Eliminar
        if(isEditable) {
            holder.btnEditar.setVisibility(View.VISIBLE);
            holder.btnEliminar.setVisibility(View.VISIBLE);

            holder.btnEditar.setOnClickListener(v -> listener.onEditarClick(evento));
            holder.btnEliminar.setOnClickListener(v -> listener.onEliminarClick(evento));
        } else {
            holder.btnEditar.setVisibility(View.GONE);
            holder.btnEliminar.setVisibility(View.GONE);

            holder.itemView.setOnClickListener(v -> listener.onItemClick(evento));
        }
    }

    @Override
    public int getItemCount() {
        return eventosList.size();
    }

    public class EventoViewHolder extends RecyclerView.ViewHolder {

        // 1. Declaramos una variable por cada View con ID en tu XML
        ImageView ivEventoImagen;
        TextView tvEventoTitulo;
        TextView tvEventoFecha;
        TextView tvEventoUbicacion;
        ImageButton btnEditar;
        ImageButton btnEliminar;

        // 2. El constructor recibe la vista del item (item_evento.xml inflado)
        public EventoViewHolder(@NonNull View itemView) {
            super(itemView);

            // 3. Conectamos las variables con los IDs del XML
            //    Usamos itemView.findViewById() para buscar solo dentro de este item
            ivEventoImagen = itemView.findViewById(R.id.iv_evento_imagen);
            tvEventoTitulo = itemView.findViewById(R.id.tv_evento_titulo);
            tvEventoFecha = itemView.findViewById(R.id.tv_evento_fecha);
            tvEventoUbicacion = itemView.findViewById(R.id.tv_evento_ubicacion);
            btnEditar = itemView.findViewById(R.id.btn_editar);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar);
        }
    }

}
