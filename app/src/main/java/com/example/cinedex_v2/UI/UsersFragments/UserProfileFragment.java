package com.example.cinedex_v2.UI.UsersFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinedex_v2.Data.DTOs.Resena.ResenaResponseDto;
import com.example.cinedex_v2.Data.DTOs.Usuario.UsuarioResponseDto;
import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.R;
import com.example.cinedex_v2.UI.AdaptersUser.ProfileReviewAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileFragment extends Fragment {

    private int userId;
    private TextView tvNombre, tvUsername, tvNoReviews;
    private RecyclerView rvResenas;
    private ProfileReviewAdapter adapter;
    private List<ResenaResponseDto> listaResenas = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt("userId");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configurar Toolbar (Flecha Atrás)
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_user_profile);
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(view).popBackStack());

        tvNombre = view.findViewById(R.id.tv_other_user_name);
        tvUsername = view.findViewById(R.id.tv_other_user_username);
        tvNoReviews = view.findViewById(R.id.tv_no_reviews);
        rvResenas = view.findViewById(R.id.rv_other_user_reviews);

        // Configurar Grid
        rvResenas.setLayoutManager(new GridLayoutManager(getContext(), 3));

        adapter = new ProfileReviewAdapter(getContext(), listaResenas, resena -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("resena_data", resena);
            Navigation.findNavController(view).navigate(R.id.reviewDetailFragment, bundle);
        });
        rvResenas.setAdapter(adapter);

        cargarDatosUsuario();
        cargarResenasUsuario();
    }

    private void cargarDatosUsuario() {
        CineDexApiClient.getApiService().getUsuario(userId).enqueue(new Callback<UsuarioResponseDto>() {
            @Override
            public void onResponse(Call<UsuarioResponseDto> call, Response<UsuarioResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UsuarioResponseDto u = response.body();
                    tvNombre.setText(u.getNombres() + " " + u.getApellidos());
                    tvUsername.setText("@" + u.getNombreUsuario());
                }
            }
            @Override
            public void onFailure(Call<UsuarioResponseDto> call, Throwable t) {}
        });
    }

    private void cargarResenasUsuario() {
        CineDexApiClient.getApiService().getResenasPorUsuario(userId).enqueue(new Callback<List<ResenaResponseDto>>() {
            @Override
            public void onResponse(Call<List<ResenaResponseDto>> call, Response<List<ResenaResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaResenas.clear();
                    listaResenas.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    if (listaResenas.isEmpty()) tvNoReviews.setVisibility(View.VISIBLE);
                    else tvNoReviews.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<List<ResenaResponseDto>> call, Throwable t) {}
        });
    }
}