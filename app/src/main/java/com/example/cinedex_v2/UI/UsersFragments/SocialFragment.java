package com.example.cinedex_v2.UI.UsersFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cinedex_v2.R;
import com.example.cinedex_v2.UI.AdaptersUser.SocialPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/* -------------------------------------------------------------------
            Contenedor Principal -> Organiza la estructura
   ------------------------------------------------------------------- */
public class SocialFragment extends Fragment {

    private TabLayout tabLayout; // -> Barra de opciones
    private ViewPager2 viewPager; // -> Donde mostramos el contenido

    // 1. Configuración
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_social_container, container, false);
    }

    // 2. Configuración
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        // 1. Vinculamos las vistas
        tabLayout = view.findViewById(R.id.tab_layout_social); // -> La barra superior
        viewPager = view.findViewById(R.id.view_pager_social); // -> El área deslizable inferior

        // 2. Configurar el motor (Adapter)
        SocialPagerAdapter adapter = new SocialPagerAdapter(this);
        viewPager.setAdapter(adapter); // -> indicamos que adaptador usar

        // 3. La conexión del TabLayout con el ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {

            if(position == 0){ // ->  Cuando se abre por primera vez
                tab.setText("Noticias");
                tab.setIcon(R.drawable.ic_news);
            } else {
                tab.setText("Eventos");
                tab.setIcon(R.drawable.ic_events);
            }
        }).attach(); // -> Finaliza y activa la configuración
    }

}