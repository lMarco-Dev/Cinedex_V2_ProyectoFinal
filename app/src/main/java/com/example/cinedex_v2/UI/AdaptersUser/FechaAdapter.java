package com.example.cinedex_v2.UI.AdaptersUser;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinedex_v2.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FechaAdapter extends RecyclerView.Adapter<FechaAdapter.ViewHolder> {

    private List<Date> fechas;
    private OnFechaClickListener listener;
    private int selectedPosition = 0; // Por defecto seleccionamos la primera (Hoy)

    public interface OnFechaClickListener {
        void onFechaClick(Date fecha);
    }

    public FechaAdapter(List<Date> fechas, OnFechaClickListener listener) {
        this.fechas = fechas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fecha_cartelera, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Date fecha = fechas.get(position);

        // Formateadores
        // "EEE" -> "lun", "mar"
        // "dd" -> "25", "26"
        // "MMMM" -> "noviembre"
        SimpleDateFormat sdfDia = new SimpleDateFormat("EEE", new Locale("es", "ES"));
        SimpleDateFormat sdfNumero = new SimpleDateFormat("dd", Locale.getDefault());
        SimpleDateFormat sdfMes = new SimpleDateFormat("MMMM", new Locale("es", "ES"));

        String diaSemana = sdfDia.format(fecha);
        if (position == 0) diaSemana = "Hoy"; // Truco visual
        else if (position == 1) diaSemana = "Mañana";

        holder.tvDia.setText(diaSemana.substring(0, 1).toUpperCase() + diaSemana.substring(1)); // Capitalizar
        holder.tvNumero.setText(sdfNumero.format(fecha));
        holder.tvMes.setText(sdfMes.format(fecha));

        // --- CAMBIO DE COLOR AL SELECCIONAR ---
        if (selectedPosition == position) {
            // Seleccionado (Fondo Negro, Texto Blanco)
            holder.itemView.setBackgroundResource(R.drawable.bg_fecha_selected);
            holder.tvDia.setTextColor(Color.WHITE);
            holder.tvNumero.setTextColor(Color.WHITE);
            holder.tvMes.setTextColor(Color.WHITE);
        } else {
            // No Seleccionado (Fondo Blanco, Texto Negro)
            holder.itemView.setBackgroundResource(R.drawable.bg_fecha_unselected);
            holder.tvDia.setTextColor(Color.GRAY);
            holder.tvNumero.setTextColor(Color.BLACK);
            holder.tvMes.setTextColor(Color.GRAY);
        }

        holder.itemView.setOnClickListener(v -> {
            int previousItem = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousItem);
            notifyItemChanged(selectedPosition);
            listener.onFechaClick(fecha);
        });
    }

    @Override
    public int getItemCount() {
        return fechas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDia, tvNumero, tvMes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDia = itemView.findViewById(R.id.tv_dia_semana);
            tvNumero = itemView.findViewById(R.id.tv_numero_dia);
            tvMes = itemView.findViewById(R.id.tv_mes);
        }
    }
}