package com.example.cinedex_v2.UI.AdaptersUser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinedex_v2.Data.DTOs.Comentario.ComentarioResponse;
import com.example.cinedex_v2.R;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ComentarioAdapter extends RecyclerView.Adapter<ComentarioAdapter.ViewHolder> {

    private List<ComentarioResponse> lista;
    private Context context;

    public ComentarioAdapter(Context context, List<ComentarioResponse> lista) {
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comentario, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ComentarioResponse c = lista.get(position);
        holder.tvUsuario.setText(c.getNombreUsuario());
        holder.tvContenido.setText(c.getContenido());

        if(c.getFecha() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM HH:mm", Locale.getDefault());
            holder.tvFecha.setText(sdf.format(c.getFecha()));
        }
    }

    @Override
    public int getItemCount() { return lista.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsuario, tvFecha, tvContenido;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsuario = itemView.findViewById(R.id.tv_usuario_comentario);
            tvFecha = itemView.findViewById(R.id.tv_fecha_comentario);
            tvContenido = itemView.findViewById(R.id.tv_texto_comentario);
        }
    }
}