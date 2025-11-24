package com.example.cinedex_v2.UI.AdaptersUser;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.cinedex_v2.UI.UsersFragments.ListaEventosFragment;
import com.example.cinedex_v2.UI.UsersFragments.ListaNoticiasFragment;

/* -------------------------------------------------------------------
                        Controlador de Tráfico
   ------------------------------------------------------------------- */
public class SocialPagerAdapter extends FragmentStateAdapter {

    // 1. Recibe el fragment padre.
    public SocialPagerAdapter(@NonNull Fragment fragment){
        super(fragment); // Conecta este adaptador con el ciclo de vida del fragment
    }

    // 2. El cerebro -> Se ejecuta cuando un usuario toca una pestaña
    @NonNull
    @Override
    public Fragment createFragment(int position){
        if(position == 0) {
            return new ListaNoticiasFragment();
        } else {
            return new ListaEventosFragment();
        }
    }

    // 3. El contador -> Indica cuantas páginas o pestañas hay
    @Override
    public int getItemCount() {
        return 2; // Tenemos 2 pestañas
    }

}
