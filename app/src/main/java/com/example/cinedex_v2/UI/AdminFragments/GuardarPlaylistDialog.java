package com.example.cinedex_v2.UI.AdminFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.cinedex_v2.Data.DTOs.Playlist.PlaylistResponse;
import com.example.cinedex_v2.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

public class GuardarPlaylistDialog extends BottomSheetDialogFragment {

    private static final String ARG_PLAYLIST = "arg_playlist";
    private PlaylistResponse playlistActual;
    private TextInputEditText etNombre;
    private View btnGuardar;

    public interface OnPlaylistGuardadaListener {
        void onPlaylistGuardada(String nombre, Integer idToUpdate);
    }
    private OnPlaylistGuardadaListener listener;

    public static GuardarPlaylistDialog newInstance(PlaylistResponse playlist) {
        GuardarPlaylistDialog f = new GuardarPlaylistDialog();
        Bundle args = new Bundle();
        if(playlist != null) args.putSerializable(ARG_PLAYLIST, playlist);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) playlistActual = (PlaylistResponse) getArguments().getSerializable(ARG_PLAYLIST);
        try { listener = (OnPlaylistGuardadaListener) getTargetFragment(); }
        catch (Exception e) { throw new ClassCastException("Listener required"); }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_guardar_playlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etNombre = view.findViewById(R.id.et_nombre_playlist);
        btnGuardar = view.findViewById(R.id.btn_guardar);
        TextView titulo = view.findViewById(R.id.tv_dialog_titulo);

        if(playlistActual != null) {
            etNombre.setText(playlistActual.getNombre());
            titulo.setText("Editar Playlist");
        }

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            if(nombre.isEmpty()) return;
            listener.onPlaylistGuardada(nombre, playlistActual != null ? playlistActual.getIdPlaylist() : null);
            dismiss();
        });
    }
}