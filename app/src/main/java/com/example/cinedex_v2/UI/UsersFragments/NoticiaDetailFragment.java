package com.example.cinedex_v2.UI.UsersFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_noticia_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_noticia);
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(view).popBackStack());

        if(noticia == null) return;

        ImageView ivImagen = view.findViewById(R.id.iv_detalle_imagen);
        TextView tvTitulo = view.findViewById(R.id.tv_detalle_titulo);
        TextView tvFecha = view.findViewById(R.id.tv_detalle_fecha);
        TextView tvContenido = view.findViewById(R.id.tv_detalle_contenido);

        // Referencias para el video
        CardView cardVideo = view.findViewById(R.id.card_video_container);
        WebView webView = view.findViewById(R.id.webview_video);

        tvTitulo.setText(noticia.getTitulo());
        tvContenido.setText(noticia.getResumen());

        if (noticia.getFechaPublicacion() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM, yyyy", new Locale("es", "ES"));
            tvFecha.setText(sdf.format(noticia.getFechaPublicacion()));
        } else {
            tvFecha.setText("");
        }

        Glide.with(this)
                .load(noticia.getUrlImagen())
                .placeholder(R.drawable.ic_launcher_background)
                .into(ivImagen);

        // Lógica para incrustar video de YouTube
        String videoUrl = noticia.getUrlYoutube();
        if (videoUrl != null && !videoUrl.isEmpty()) {
            String videoId = extractYoutubeId(videoUrl);

            if (videoId != null) {
                cardVideo.setVisibility(View.VISIBLE);
                setupWebView(webView, videoId);
            } else {
                cardVideo.setVisibility(View.GONE);
            }
        } else {
            cardVideo.setVisibility(View.GONE);
        }
    }

    private void setupWebView(WebView webView, String videoId) {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        // Esto hace que el video se vea en pantalla completa dentro del cuadro
        webView.setWebChromeClient(new WebChromeClient());

        // URL especial para embeber videos
        String embedUrl = "https://www.youtube.com/embed/" + videoId;

        // HTML simple para cargar el iframe
        String html = "<iframe width=\"100%\" height=\"100%\" src=\"" + embedUrl + "\" frameborder=\"0\" allowfullscreen></iframe>";

        webView.loadData(html, "text/html", "utf-8");
    }

    // Método auxiliar para sacar el ID del video (ej: v=dQw4w9WgXcQ -> dQw4w9WgXcQ)
    private String extractYoutubeId(String url) {
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}