package com.example.cinedex_v2.UI.UsersFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.cinedex_v2.Data.DTOs.Usuario.UsuarioUpdateRequestDto;
import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.R;
import com.example.cinedex_v2.UI.Activities.Actividad_Login;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends Fragment {

    private EditText etNombres, etApellidos, etUsername, etAvatarUrl;
    private ShapeableImageView ivFoto;
    private int idUsuario;
    private SharedPreferences prefs;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Toolbar
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar_settings);
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(view).popBackStack());

        // Referencias UI
        ivFoto = view.findViewById(R.id.iv_settings_foto);
        etUsername = view.findViewById(R.id.et_settings_username);
        etNombres = view.findViewById(R.id.et_settings_nombre);
        etApellidos = view.findViewById(R.id.et_settings_apellidos);
        etAvatarUrl = view.findViewById(R.id.et_settings_avatar);

        // SharedPreferences
        prefs = requireActivity().getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        idUsuario = prefs.getInt("ID_USUARIO", -1);

        // Cargar datos en inputs
        etUsername.setText(prefs.getString("NOMBRE_USUARIO", ""));
        etNombres.setText(prefs.getString("NOMBRES", ""));
        etApellidos.setText(prefs.getString("APELLIDOS", ""));
        etAvatarUrl.setText(prefs.getString("URL_AVATAR", ""));

        // Foto
        Glide.with(this)
                .load(prefs.getString("URL_AVATAR", ""))
                .placeholder(R.drawable.ic_person)
                .into(ivFoto);

        // Guardar cambios
        view.findViewById(R.id.btn_guardar_cambios).setOnClickListener(v -> guardarCambios());

        // Cerrar sesión
        view.findViewById(R.id.btn_logout_settings).setOnClickListener(v -> {
            prefs.edit().clear().apply();
            Intent intent = new Intent(getActivity(), Actividad_Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void guardarCambios() {

        String newUsername = etUsername.getText().toString().trim();
        String newNombres = etNombres.getText().toString().trim();
        String newApellidos = etApellidos.getText().toString().trim();
        String newAvatar = etAvatarUrl.getText().toString().trim();

        if (newUsername.isEmpty() || newNombres.isEmpty() || newApellidos.isEmpty()) {
            Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Construcción del DTO EXACTAMENTE como tu backend espera
        UsuarioUpdateRequestDto request = new UsuarioUpdateRequestDto();
        request.setNombreUsuario(newUsername);
        request.setNombres(newNombres);
        request.setApellidos(newApellidos);
        request.setUrlAvatar(newAvatar);

        CineDexApiClient.getApiService()
                .actualizarUsuario(idUsuario, request)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {

                            // Guardar cambios localmente
                            prefs.edit()
                                    .putString("NOMBRE_USUARIO", newUsername)
                                    .putString("NOMBRES", newNombres)
                                    .putString("APELLIDOS", newApellidos)
                                    .putString("URL_AVATAR", newAvatar)
                                    .apply();

                            Toast.makeText(getContext(), "Datos actualizados", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
