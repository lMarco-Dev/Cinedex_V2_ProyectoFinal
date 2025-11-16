package com.example.cinedex_v2.Data.Access;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenciasTerminos {

    private static final String PREF_NAME = "CineDexPrefs";
    private static final String KEY_TERMINOS_ACEPTADOS = "terminosAceptados";

    public static boolean terminosAceptados(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_TERMINOS_ACEPTADOS, false);
    }

    public static void guardarAceptacion(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_TERMINOS_ACEPTADOS, true).apply();
    }
}
