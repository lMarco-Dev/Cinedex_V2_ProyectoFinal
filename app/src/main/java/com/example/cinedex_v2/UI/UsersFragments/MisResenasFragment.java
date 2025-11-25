package com.example.cinedex_v2.UI.UsersFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cinedex_v2.Data.DTOs.Resena.ResenaResponseDto;
import com.example.cinedex_v2.Data.Network.CineDexApiClient;
import com.example.cinedex_v2.R;
import com.example.cinedex_v2.UI.AdaptersUser.ProfileReviewAdapter;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MisResenasFragment extends Fragment {

    private RecyclerView rvResenas;
    private ProfileReviewAdapter adapter;
    private List<ResenaResponseDto> misResenas = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mis_resenas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvResenas = view.findViewById(R.id.rv_mis_resenas);
        rvResenas.setLayoutManager(new GridLayoutManager(getContext(), 3));

        adapter = new ProfileReviewAdapter(getContext(), misResenas, resena -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("resena_data", resena);
            try {
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                        .navigate(R.id.reviewDetailFragment, bundle);
            } catch (Exception e) { e.printStackTrace(); }
        });
        rvResenas.setAdapter(adapter);

        cargarDatos();
    }

    private void cargarDatos() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("sesion_usuario", Context.MODE_PRIVATE);
        int idUsuario = prefs.getInt("ID_USUARIO", -1);

        if(idUsuario != -1) {
            CineDexApiClient.getApiService().getResenasPorUsuario(idUsuario)
                    .enqueue(new Callback<List<ResenaResponseDto>>() {
                        @Override
                        public void onResponse(Call<List<ResenaResponseDto>> call, Response<List<ResenaResponseDto>> response) {
                            if(response.isSuccessful() && response.body() != null) {
                                misResenas.clear();
                                misResenas.addAll(response.body());
                                adapter.notifyDataSetChanged();
                            }
                        }
                        @Override
                        public void onFailure(Call<List<ResenaResponseDto>> call, Throwable t) {}
                    });
        }
    }

}