package com.example.cinedex_v2.UI.UsersFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.example.cinedex_v2.Data.DTOs.Resena.ResenaResponseDto;
import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.R;
import com.example.cinedex_v2.UI.Activities.Actividad_Login;
import com.example.cinedex_v2.UI.AdaptersUser.PerfilPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilFragment extends Fragment {

    private TextView tvNombre, tvEmail, tvCount;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvNombre = view.findViewById(R.id.tv_perfil_nombre);
        tvEmail = view.findViewById(R.id.tv_perfil_email);
        tvCount = view.findViewById(R.id.tv_contador_resenas);

        // 1. Recuperar datos (Las mismas claves que en Login)
        SharedPreferences prefs = requireActivity().getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        String nombres = prefs.getString("NOMBRES", "Usuario");
        String apellidos = prefs.getString("APELLIDOS", "");
        String email = prefs.getString("EMAIL_USUARIO", "sin_correo@cinedex.com");
        int idUsuario = prefs.getInt("ID_USUARIO", -1);

        // 2. Mostrar en pantalla
        tvNombre.setText(nombres + " " + apellidos);
        tvEmail.setText(email);

        // 3. Cargar contador de reseñas
        if (idUsuario != -1) cargarCantidadResenas(idUsuario);

        // 4. Configurar Tabs
        TabLayout tabLayout = view.findViewById(R.id.tab_layout_perfil);
        ViewPager2 viewPager = view.findViewById(R.id.view_pager_perfil);
        PerfilPagerAdapter pagerAdapter = new PerfilPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("Reseñas");
            else tab.setText("Mis Listas");
        }).attach();

        // 5. Cerrar Sesión
        view.findViewById(R.id.btn_cerrar_sesion).setOnClickListener(v -> {
            prefs.edit().clear().apply();
            Intent intent = new Intent(getActivity(), Actividad_Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void cargarCantidadResenas(int idUsuario) {
        CineDexApiClient.getApiService().getResenasPorUsuario(idUsuario).enqueue(new Callback<List<ResenaResponseDto>>() {
            @Override
            public void onResponse(Call<List<ResenaResponseDto>> call, Response<List<ResenaResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tvCount.setText(response.body().size() + " Reseñas");
                }
            }
            @Override
            public void onFailure(Call<List<ResenaResponseDto>> call, Throwable t) {}
        });
    }
}