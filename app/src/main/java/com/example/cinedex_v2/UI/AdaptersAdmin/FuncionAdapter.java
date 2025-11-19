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
import com.example.cinedex_v2.Data.DTOs.Funcion.FuncionResponse;
import com.example.cinedex_v2.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FuncionAdapter extends RecyclerView.Adapter<FuncionAdapter.ViewHolder> {

    private Context context;
    private List<FuncionResponse> listaFunciones;
    private OnFuncionClickListener listener;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd MMM • hh:mm a", new Locale("es", "ES"));

    public interface OnFuncionClickListener {
        void onEditarClick(FuncionResponse funcion);
        void onEliminarClick(FuncionResponse funcion);
    }

    public FuncionAdapter(Context context, List<FuncionResponse> listaFunciones, OnFuncionClickListener listener) {
        this.context = context;
        this.listaFunciones = listaFunciones;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_funcion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FuncionResponse funcion = listaFunciones.get(position);

        holder.tvPelicula.setText(funcion.getNombrePelicula());
        holder.tvFecha.setText(sdf.format(funcion.getFechaHora()));

        String detalles = String.format("%s • %s • %s", funcion.getSala(), funcion.getFormato(), funcion.getIdioma());
        holder.tvDetalles.setText(detalles);

        holder.tvPrecio.setText("S/ " + String.format("%.2f", funcion.getPrecio()));

        Glide.with(context)
                .load(funcion.getUrlImagenPelicula())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivPoster);

        holder.btnEditar.setOnClickListener(v -> listener.onEditarClick(funcion));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminarClick(funcion));
    }

    @Override
    public int getItemCount() {
        return listaFunciones.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvPelicula, tvFecha, tvDetalles, tvPrecio;
        ImageButton btnEditar, btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.iv_funcion_poster);
            tvPelicula = itemView.findViewById(R.id.tv_funcion_pelicula);
            tvFecha = itemView.findViewById(R.id.tv_funcion_fecha);
            tvDetalles = itemView.findViewById(R.id.tv_funcion_detalles);
            tvPrecio = itemView.findViewById(R.id.tv_funcion_precio);
            btnEditar = itemView.findViewById(R.id.btn_editar);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar);
        }
    }
}