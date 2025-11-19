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
import com.example.cinedex_v2.Data.DTOs.Cine.CineResponse;
import com.example.cinedex_v2.R;

import java.util.List;

public class CineAdapter extends RecyclerView.Adapter<CineAdapter.CineViewHolder> {

    // Variables globales que necesitamos para trabajar
    private Context context;                // Para saber dónde estamos y cargar imágenes (Glide)
    private List<CineResponse> cinesList;   // La lista de datos que vamos a mostrar
    private OnCineClickListener listener;   // El "mensajero" para avisar cuando tocan un botón

    // 1. INTERFAZ: El contrato de comunicación
    // Define qué acciones puede reportar este adaptador hacia afuera (al Fragment).
    public interface OnCineClickListener {
        void onEditarClick(CineResponse cine);
        void onEliminarClick(CineResponse cine);
    }

    // 2. CONSTRUCTOR: Inicializamos todo
    public CineAdapter(Context context, List<CineResponse> cinesList, OnCineClickListener listener) {
        this.context = context;
        this.cinesList = cinesList;
        this.listener = listener;
    }

    // 3. ON CREATE: La "Fábrica de Cajas"
    // Este método se ejecuta solo unas pocas veces. Crea las "tarjetas vacías" (vistas)
    // inflando el archivo XML 'item_cine'.
    @NonNull
    @Override
    public CineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cine, parent, false);
        return new CineViewHolder(view);
    }

    // 4. ON BIND: El "Rellenador de Datos"
    // Este método se ejecuta MUCHAS veces (cada vez que haces scroll).
    // Toma una tarjeta vacía (holder) y le mete los datos de la posición actual.
    @Override
    public void onBindViewHolder(@NonNull CineViewHolder holder, int position) {
        // A. Sacamos el dato de la lista
        CineResponse cine = cinesList.get(position);

        // B. Ponemos el texto (El nombre del cine en el recuadro negro)
        holder.tvNombre.setText(cine.getNombre());

        // Nota: Ciudad y Dirección están ocultos en tu XML actual (GONE),
        // pero si decides mostrarlos después, aquí se asignarían:
        // holder.tvCiudad.setText(cine.getCiudad());

        // C. Cargamos la imagen usando Glide
        Glide.with(context)
                .load(cine.getUrlImagen())                  // La URL que viene del backend
                .placeholder(R.drawable.ic_launcher_background) // Qué mostrar mientras carga
                .error(R.drawable.ic_delete)                // Qué mostrar si la URL falla
                .centerCrop()                               // Recorta la imagen para llenar el cuadro
                .into(holder.ivImagen);

        // D. Configuramos los clics de los botones
        holder.btnEditar.setOnClickListener(v -> listener.onEditarClick(cine));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminarClick(cine));
    }

    // 5. GET ITEM COUNT: El "Contador"
    // Le dice al RecyclerView cuántos elementos hay en total para saber cuánto scroll crear.
    @Override
    public int getItemCount() {
        return cinesList.size();
    }

    // 6. VIEWHOLDER: El "Guardarropa"
    // Su única función es recordar dónde están los elementos visuales (TextView, ImageView)
    // dentro del XML para no tener que buscarlos (findViewById) cada vez que hacemos scroll.
    public class CineViewHolder extends RecyclerView.ViewHolder {

        ImageView ivImagen;
        TextView tvNombre;
        ImageButton btnEditar, btnEliminar;
        // Aunque estén ocultos en el XML, es buena práctica tenerlos referenciados por si acaso
        TextView tvCiudad, tvDireccion;

        public CineViewHolder(@NonNull View itemView) {
            super(itemView);

            // Buscamos los IDs que pusimos en 'item_cine.xml'
            ivImagen = itemView.findViewById(R.id.iv_cine_imagen);
            tvNombre = itemView.findViewById(R.id.tv_cine_nombre);

            btnEditar = itemView.findViewById(R.id.btn_editar);
            btnEliminar = itemView.findViewById(R.id.btn_eliminar);

            // Referencias ocultas (según tu diseño actual)
            tvCiudad = itemView.findViewById(R.id.tv_cine_ciudad);
            tvDireccion = itemView.findViewById(R.id.tv_cine_direccion);
        }
    }
}