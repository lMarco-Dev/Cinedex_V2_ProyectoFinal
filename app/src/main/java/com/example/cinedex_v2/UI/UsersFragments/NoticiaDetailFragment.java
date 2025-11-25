package com.example.cinedex_v2.UI.UsersFragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.cinedex_v2.Data.DTOs.Noticia.NoticiaResponse;
import com.example.cinedex_v2.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NoticiaDetailFragment extends Fragment {

    private NoticiaResponse noticia;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            noticia = (NoticiaResponse) getArguments().getSerializable("noticia_data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_noticia_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configurar Toolbar
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_noticia);
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(view).popBackStack());

        if (noticia == null) return;

        // Vincular Vistas
        ImageView ivImagen = view.findViewById(R.id.iv_detalle_imagen);
        TextView tvTitulo = view.findViewById(R.id.tv_detalle_titulo);
        TextView tvFecha = view.findViewById(R.id.tv_detalle_fecha);
        TextView tvContenido = view.findViewById(R.id.tv_detalle_contenido);
        CardView cardVideo = view.findViewById(R.id.card_video_container);
        ImageView ivMiniatura = view.findViewById(R.id.iv_miniatura_video);

        // Llenar Datos
        tvTitulo.setText(noticia.getTitulo());
        tvContenido.setText(noticia.getResumen());

        if (noticia.getFechaPublicacion() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM, yyyy", new Locale("es", "ES"));
            tvFecha.setText(sdf.format(noticia.getFechaPublicacion()));
        }

        Glide.with(this).load(noticia.getUrlImagen()).into(ivImagen);

        // Lógica de Video
        String videoUrl = noticia.getUrlYoutube();
        if (videoUrl != null && !videoUrl.isEmpty()) {
            String videoId = extractYoutubeId(videoUrl);

            if (videoId != null) {
                cardVideo.setVisibility(View.VISIBLE);

                // Cargar miniatura HQ
                String thumbUrl = "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";
                Glide.with(this).load(thumbUrl).centerCrop().into(ivMiniatura);

                // Click para abrir App externa
                cardVideo.setOnClickListener(v -> {
                    Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoId));
                    try {
                        startActivity(appIntent);
                    } catch (ActivityNotFoundException ex) {
                        startActivity(webIntent);
                    }
                });
            } else {
                cardVideo.setVisibility(View.GONE);
            }
        } else {
            cardVideo.setVisibility(View.GONE);
        }
    }

    private String extractYoutubeId(String url) {
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";
        Matcher matcher = Pattern.compile(pattern).matcher(url);
        return matcher.find() ? matcher.group() : null;
    }
}