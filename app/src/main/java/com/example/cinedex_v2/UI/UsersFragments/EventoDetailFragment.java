package com.example.cinedex_v2.UI.UsersFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.cinedex_v2.Data.DTOs.Evento.EventoResponse;
import com.example.cinedex_v2.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class EventoDetailFragment extends Fragment {

    private EventoResponse evento;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            evento = (EventoResponse) getArguments().getSerializable("evento_data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_evento_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configurar Toolbar
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_evento);
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(view).popBackStack());

        if (evento == null) return;

        // Vincular Vistas
        ImageView ivImagen = view.findViewById(R.id.iv_evento_imagen_grande);
        TextView tvTitulo = view.findViewById(R.id.tv_evento_titulo_grande);
        TextView tvFecha = view.findViewById(R.id.tv_evento_fecha_grande);
        TextView tvUbicacion = view.findViewById(R.id.tv_evento_ubicacion_grande);
        TextView tvDescripcion = view.findViewById(R.id.tv_evento_descripcion_grande);

        // Llenar Datos
        tvTitulo.setText(evento.getTitulo());
        tvUbicacion.setText(evento.getUbicacion());
        tvDescripcion.setText(evento.getDescripcion());

        if (evento.getFechaHora() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM, hh:mm a", new Locale("es", "ES"));
            tvFecha.setText(sdf.format(evento.getFechaHora()));
        } else {
            tvFecha.setText("Fecha por confirmar");
        }

        // Cargar Imagen
        Glide.with(this)
                .load(evento.getUrlImagen())
                .placeholder(R.drawable.ic_launcher_background)
                .into(ivImagen);
    }
}