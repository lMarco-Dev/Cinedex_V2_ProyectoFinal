package com.example.cinedex_v2.UI.AdaptersUser;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.cinedex_v2.UI.UsersFragments.MisPlaylistsFragment;
import com.example.cinedex_v2.UI.UsersFragments.MisResenasFragment;

public class PerfilPagerAdapter extends FragmentStateAdapter {

    public PerfilPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Pestaña 0: Reseñas (Grid de fotos)
        // Pestaña 1: Listas (CRUD de Playlists)
        if (position == 0) {
            return new MisResenasFragment();
        } else {
            return new MisPlaylistsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Tenemos 2 pestañas
    }
}