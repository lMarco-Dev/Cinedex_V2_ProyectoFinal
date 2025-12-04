package com.example.cinedex_v2.UI.UsersFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.cinedex_v2.Data.DTOs.Resena.ResenaResponseDto;
import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.R;
import com.example.cinedex_v2.UI.AdaptersUser.PerfilPagerAdapter;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilFragment extends Fragment {

    private TextView tvNombre, tvEmail, tvCount;
    private ShapeableImageView ivFoto;

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
        ivFoto = view.findViewById(R.id.iv_perfil_foto);

        // BOTÓN AJUSTES ✔
        ImageButton btnSettings = view.findViewById(R.id.btn_settings);
        btnSettings.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_perfilFragment_to_settingsFragment)
        );

        // Recuperar datos
        SharedPreferences prefs = requireActivity().getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        String nombres = prefs.getString("NOMBRES", "Usuario");
        String apellidos = prefs.getString("APELLIDOS", "");
        String email = prefs.getString("EMAIL_USUARIO", "sin_correo@cinedex.com");
        int idUsuario = prefs.getInt("ID_USUARIO", -1);

        // Set datos
        tvNombre.setText(nombres + " " + apellidos);
        tvEmail.setText(email);

        // Cargar contador reseñas
        if (idUsuario != -1) cargarCantidadResenas(idUsuario);

        // Tabs
        TabLayout tabLayout = view.findViewById(R.id.tab_layout_perfil);
        ViewPager2 viewPager = view.findViewById(R.id.view_pager_perfil);
        PerfilPagerAdapter pagerAdapter = new PerfilPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? "Reseñas" : "Mis Listas");
        }).attach();
    }

    @Override
    public void onResume(){
        super.onResume();
        actualizarUI();
    }

    private void actualizarUI() {
        if (getActivity() == null) return;

        SharedPreferences prefs = requireActivity().getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        String nombreUsuario = prefs.getString("NOMBRE_USUARIO", "Usuario");
        String nombres = prefs.getString("NOMBRES", "");
        String apellidos = prefs.getString("APELLIDOS", "");
        String email = prefs.getString("EMAIL_USUARIO", "");
        String urlAvatar = prefs.getString("URL_AVATAR", ""); // Leer URL nueva
        int idUsuario = prefs.getInt("ID_USUARIO", -1);

        // Actualizar Textos
        if (!nombres.isEmpty()) {
            tvNombre.setText(nombres + " " + apellidos);
        } else {
            tvNombre.setText(nombreUsuario);
        }
        tvEmail.setText(email);

        // Actualizar Foto
        Glide.with(this)
                .load(urlAvatar)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .into(ivFoto);

        // Cargar contador
        if (idUsuario != -1) cargarCantidadResenas(idUsuario);
    }

    private void cargarCantidadResenas(int idUsuario) {
        CineDexApiClient.getApiService().getResenasPorUsuario(idUsuario)
                .enqueue(new Callback<List<ResenaResponseDto>>() {
                    @Override
                    public void onResponse(Call<List<ResenaResponseDto>> call, Response<List<ResenaResponseDto>> response) {
                        if (response.isSuccessful() && response.body() != null)
                            tvCount.setText(response.body().size() + " Reseñas");
                    }
                    @Override
                    public void onFailure(Call<List<ResenaResponseDto>> call, Throwable t) {}
                });
    }
}
