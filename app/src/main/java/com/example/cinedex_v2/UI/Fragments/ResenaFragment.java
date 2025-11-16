package com.example.cinedex_v2.UI.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cinedex_v2.Data.DTOs.Resena.ResenaResponseDto;
import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.Data.Network.CineDexApiService;
import com.example.cinedex_v2.R;
import com.example.cinedex_v2.UI.Adapters.ResenaAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResenaFragment extends Fragment {

    private RecyclerView rvResenas;
    private ResenaAdapter adapter;
    private List<ResenaResponseDto> listaDeResenas;
    private CineDexApiService apiService;

    public ResenaFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listaDeResenas = new ArrayList<>();
        apiService = CineDexApiClient.getApiService();
        adapter = new ResenaAdapter(listaDeResenas);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ly_fragment_resena, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvResenas = view.findViewById(R.id.rv_resenas_fragment);
        rvResenas.setLayoutManager(new LinearLayoutManager(getContext()));
        rvResenas.setAdapter(adapter);

        cargarResenasDesdeApi();
    }

    private void cargarResenasDesdeApi() {
        apiService.getResenas().enqueue(new Callback<List<ResenaResponseDto>>() {
            @Override
            public void onResponse(@NonNull Call<List<ResenaResponseDto>> call,
                                   @NonNull Response<List<ResenaResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API_RESEÑAS", "Respuesta JSON: " + new com.google.gson.Gson().toJson(response.body()));

                    listaDeResenas.clear();
                    listaDeResenas.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Error al cargar reseñas", Toast.LENGTH_SHORT).show();
                    Log.e("ResenaFragment", "Error API: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ResenaResponseDto>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show();
                Log.e("ResenaFragment", "Fallo red: " + t.getMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarResenasDesdeApi();
    }
}
