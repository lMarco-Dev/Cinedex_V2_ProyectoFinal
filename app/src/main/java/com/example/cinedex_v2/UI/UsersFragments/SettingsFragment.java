package com.example.cinedex_v2.UI.UsersFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.cinedex_v2.Data.Cloudinary.CloudinaryUploader; // Asegúrate de tener tu clase Helper
import com.example.cinedex_v2.Data.DTOs.Usuario.UsuarioUpdateRequestDto;
import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.R;
import com.example.cinedex_v2.UI.Activities.Actividad_Login;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends Fragment {

    private EditText etNombres, etApellidos, etUsername;
    private ShapeableImageView ivFoto;
    private ProgressBar progressBar; // Agrega un ProgressBar a tu XML si quieres feedback visual

    private int idUsuario;
    private SharedPreferences prefs;

    private ActivityResultLauncher<String> mGetContent;
    private Uri nuevaImagenUri = null;
    private String urlAvatarActual;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar selector de imagen
        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        if (uri != null) {
                            nuevaImagenUri = uri;
                            Glide.with(requireContext()).load(uri).into(ivFoto);
                        }
                    }
                });
    }

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
        // El campo de URL ya no es necesario editarlo manualmente, se llenará solo

        // SharedPreferences
        prefs = requireActivity().getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        idUsuario = prefs.getInt("ID_USUARIO", -1);

        // Cargar datos
        etUsername.setText(prefs.getString("NOMBRE_USUARIO", ""));
        etNombres.setText(prefs.getString("NOMBRES", ""));
        etApellidos.setText(prefs.getString("APELLIDOS", ""));
        urlAvatarActual = prefs.getString("URL_AVATAR", "");

        Glide.with(this)
                .load(urlAvatarActual)
                .placeholder(R.drawable.ic_person)
                .into(ivFoto);

        // Click en la foto para cambiarla
        ivFoto.setOnClickListener(v -> mGetContent.launch("image/*"));

        // Guardar
        view.findViewById(R.id.btn_guardar_cambios).setOnClickListener(v -> iniciarGuardado());

        // Cerrar sesión
        view.findViewById(R.id.btn_logout_settings).setOnClickListener(v -> {
            prefs.edit().clear().apply();
            Intent intent = new Intent(getActivity(), Actividad_Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void iniciarGuardado() {
        // Validaciones
        String newUsername = etUsername.getText().toString().trim();
        String newNombres = etNombres.getText().toString().trim();
        String newApellidos = etApellidos.getText().toString().trim();

        if (newUsername.isEmpty() || newNombres.isEmpty() || newApellidos.isEmpty()) {
            Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getContext(), "Guardando...", Toast.LENGTH_SHORT).show();

        // Lógica: Si hay nueva imagen -> Subir -> Obtener URL -> Guardar en API
        // Si no hay nueva imagen -> Usar URL vieja -> Guardar en API

        if (nuevaImagenUri != null) {
            subirImagenYActualizar(newUsername, newNombres, newApellidos);
        } else {
            actualizarDatosEnApi(newUsername, newNombres, newApellidos, urlAvatarActual);
        }
    }

    private void subirImagenYActualizar(String username, String nombres, String apellidos) {
        File file = CloudinaryUploader.getFileFromUri(requireContext(), nuevaImagenUri);

        new Thread(() -> {
            try {
                String urlNueva = CloudinaryUploader.uploadImage(file);

                // Volver al hilo principal para llamar a la API
                requireActivity().runOnUiThread(() ->
                        actualizarDatosEnApi(username, nombres, apellidos, urlNueva)
                );
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Error al subir imagen", Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

    private void actualizarDatosEnApi(String username, String nombres, String apellidos, String urlAvatar) {
        UsuarioUpdateRequestDto request = new UsuarioUpdateRequestDto();
        request.setNombreUsuario(username);
        request.setNombres(nombres);
        request.setApellidos(apellidos);
        request.setUrlAvatar(urlAvatar);

        CineDexApiClient.getApiService()
                .actualizarUsuario(idUsuario, request)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            // Guardar cambios localmente para que el perfil se actualice al volver
                            prefs.edit()
                                    .putString("NOMBRE_USUARIO", username)
                                    .putString("NOMBRES", nombres)
                                    .putString("APELLIDOS", apellidos)
                                    .putString("URL_AVATAR", urlAvatar)
                                    .apply();

                            Toast.makeText(getContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show();
                            // Opcional: Volver atrás automáticamente
                            Navigation.findNavController(requireView()).popBackStack();
                        } else {
                            Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}