package com.example.cinedex_v2.UI.UsersFragments;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cinedex_v2.Data.DTOs.Evento.EventoResponse;
import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.R;
import com.example.cinedex_v2.UI.AdaptersAdmin.EventoAdapter;
import com.example.cinedex_v2.UI.AdaptersAdmin.NoticiaAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ListaEventosFragment extends Fragment implements EventoAdapter.OnEventoClickListener {

    private RecyclerView rvEventos;
    private ProgressBar progressBar;
    private TextView tvSinEventos;
    private EventoAdapter adapter;
    private List<EventoResponse> listaEventos = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lista_eventos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //1. Vincular vistas
        rvEventos = view.findViewById(R.id.rv_lista_eventos);
        progressBar = view.findViewById(R.id.pb_eventos);
        tvSinEventos = view.findViewById(R.id.tv_sin_eventos);

        //2. Configurar RecyclerView
        rvEventos.setLayoutManager(new LinearLayoutManager(getContext()));

        // ----- Pasamos 'false' para ocultar botones
        adapter = new EventoAdapter(getContext(), listaEventos, false, this);
        rvEventos.setAdapter(adapter);

        //3. Cargar datos
        cargarEventosDesdeApi();
    }

    private void cargarEventosDesdeApi() {
        mostrarCarga(true);

        CineDexApiClient.getApiService().getEventos().enqueue(new Callback<List<EventoResponse>>() {
            @Override
            public void onResponse(Call<List<EventoResponse>> call, Response<List<EventoResponse>> response) {
                mostrarCarga(false);

                if(response.isSuccessful() && response.body() != null){
                    listaEventos.clear();
                    listaEventos.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    if(listaEventos.isEmpty()){
                        tvSinEventos.setVisibility(View.VISIBLE);
                    } else {
                        tvSinEventos.setVisibility(View.GONE);
                    }
                } else {
                    if(getContext() != null) Toast.makeText(getContext(), "Error al cargar los eventos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<EventoResponse>> call, Throwable t) {
                mostrarCarga(false);
                if(getContext()!=null) Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ------------------- NAVEGACIÓN ---------------------
    @Override
    public void onItemClick(EventoResponse evento){
        Bundle bundle = new Bundle();
        bundle.putSerializable("evento_data", evento);
        try {
            Navigation.findNavController(requireView()).navigate(R.id.eventoDetailFragment, bundle);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override public void onEditarClick(EventoResponse evento) {}
    @Override public void onEliminarClick(EventoResponse evento) {}

    private void mostrarCarga(boolean show){
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            rvEventos.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            rvEventos.setVisibility(View.VISIBLE);
        }
    }
}