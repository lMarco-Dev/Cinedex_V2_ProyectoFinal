package com.example.cinedex_v2.UI.UsersFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cinedex_v2.Data.DTOs.Comentario.ComentarioRequest;
import com.example.cinedex_v2.Data.DTOs.Comentario.ComentarioResponse;
import com.example.cinedex_v2.Data.DTOs.Resena.ResenaResponseDto;
import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.R;
import com.example.cinedex_v2.UI.AdaptersUser.ComentarioAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewDetailFragment extends Fragment {

    private ResenaResponseDto resena;
    private RecyclerView rvComentarios;
    private ComentarioAdapter adapter;
    private List<ComentarioResponse> listaComentarios = new ArrayList<>();
    private EditText etComentario;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            resena = (ResenaResponseDto) getArguments().getSerializable("resena_data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_review_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Toolbar
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_review);
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(view).popBackStack());

        // Vincular datos de la reseña
        if(resena != null){
            ImageView ivPoster = view.findViewById(R.id.iv_review_poster);
            TextView tvPelicula = view.findViewById(R.id.tv_review_pelicula);
            TextView tvTexto = view.findViewById(R.id.tv_review_texto);
            RatingBar rbNota = view.findViewById(R.id.rb_review_nota);

            tvPelicula.setText(resena.getTituloPelicula());
            tvTexto.setText(resena.getComentario());
            rbNota.setRating((float) resena.getPuntuacion());
            Glide.with(this).load(resena.getPosterPeliculaURL()).into(ivPoster);
        }

        // Configurar Lista Comentarios
        rvComentarios = view.findViewById(R.id.rv_comentarios);
        rvComentarios.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ComentarioAdapter(getContext(), listaComentarios);
        rvComentarios.setAdapter(adapter);

        // Configurar Input
        etComentario = view.findViewById(R.id.et_nuevo_comentario);
        ImageButton btnEnviar = view.findViewById(R.id.btn_enviar_comentario);

        btnEnviar.setOnClickListener(v -> enviarComentario());

        cargarComentarios();
    }

    private void cargarComentarios() {
        if(resena == null) return;

        CineDexApiClient.getApiService().getComentariosPorResena(resena.getIdResena()).enqueue(new Callback<List<ComentarioResponse>>() {
            @Override
            public void onResponse(Call<List<ComentarioResponse>> call, Response<List<ComentarioResponse>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    listaComentarios.clear();
                    listaComentarios.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<ComentarioResponse>> call, Throwable t) {}
        });
    }

    private void enviarComentario() {
        String texto = etComentario.getText().toString().trim();

        if(texto.isEmpty()) return;

        SharedPreferences prefs = requireActivity().getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        int idUsuario = prefs.getInt("ID_USUARIO", -1);

        if(idUsuario == -1) {
            Toast.makeText(getContext(), "Inicia sesión para comentar", Toast.LENGTH_SHORT).show();
            return;
        }

        ComentarioRequest request = new ComentarioRequest(idUsuario, resena.getIdResena(), texto);

        CineDexApiClient.getApiService().crearComentario(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    etComentario.setText("");
                    Toast.makeText(getContext(), "Comentario enviado!", Toast.LENGTH_SHORT).show();
                    cargarComentarios();
                } else {
                    Toast.makeText(getContext(), "Error al enviar", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}