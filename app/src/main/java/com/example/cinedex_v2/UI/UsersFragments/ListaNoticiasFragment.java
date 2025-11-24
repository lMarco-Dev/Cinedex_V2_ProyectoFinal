package com.example.cinedex_v2.UI.UsersFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cinedex_v2.Data.DTOs.Noticia.NoticiaResponse;
import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.R;
import com.example.cinedex_v2.UI.AdaptersAdmin.NoticiaAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaNoticiasFragment extends Fragment implements NoticiaAdapter.OnNoticiaClickListener {

    private RecyclerView rvNoticias;
    private ProgressBar progressBar;
    private TextView tvSinNoticias;
    private NoticiaAdapter adapter;
    private List<NoticiaResponse> listaNoticias = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lista_noticias, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Vincular vistas
        rvNoticias = view.findViewById(R.id.rv_lista_noticias);
        progressBar = view.findViewById(R.id.pb_noticias);
        tvSinNoticias = view.findViewById(R.id.tv_sin_noticias);

        // 2. Configurar RecyclerView
        rvNoticias.setLayoutManager(new LinearLayoutManager(getContext()));

        // --- CLAVE: Pasamos 'false' para ocultar botones de admin ---
        adapter = new NoticiaAdapter(getContext(), listaNoticias, false, this);
        rvNoticias.setAdapter(adapter);

        // 3. Cargar datos
        cargarNoticiaDesdeApi();
    }

    private void cargarNoticiaDesdeApi() {
        mostrarCarga(true);

        CineDexApiClient.getApiService().getNoticias().enqueue(new Callback<List<NoticiaResponse>>() {
            @Override
            public void onResponse(Call<List<NoticiaResponse>> call, Response<List<NoticiaResponse>> response) {
                mostrarCarga(false);
                if (response.isSuccessful() && response.body() != null) {
                    listaNoticias.clear();
                    listaNoticias.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    if (listaNoticias.isEmpty()) {
                        tvSinNoticias.setVisibility(View.VISIBLE);
                    } else {
                        tvSinNoticias.setVisibility(View.GONE);
                    }
                } else {
                    if(getContext()!=null) Toast.makeText(getContext(), "Error al cargar noticias", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<NoticiaResponse>> call, Throwable t) {
                mostrarCarga(false);
                if(getContext()!=null) Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ------------------- NAVEGACIÓN ---------------------
    @Override
    public void onItemClick(NoticiaResponse noticia){
        Bundle bundle = new Bundle();
        bundle.putSerializable("noticia_data", noticia);
        try {
            // Navegar al detalle
            Navigation.findNavController(requireView()).navigate(R.id.noticiaDetailFragment, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Métodos de admin vacíos (no se usarán aquí)
    @Override public void onEditarClick(NoticiaResponse noticia) {}
    @Override public void onEliminarClick(NoticiaResponse noticia) {}

    private void mostrarCarga(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            rvNoticias.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            rvNoticias.setVisibility(View.VISIBLE);
        }
    }
}