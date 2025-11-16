package com.example.cinedex_v2.Data.Access;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class BDHelper extends SQLiteOpenHelper {

    // --- Constantes para la Base de Datos ---
    private static final String NOMBRE_BD = "cinedex.db";
    private static final int VERSION_BD = 1;

    // Constantes para la tabla Usuario
    public static final String TABLA_USUARIO = "Usuario";
    public static final String ID_USUARIO = "idUsuario";
    public static final String COL_NOMBRE_USUARIO = "nombreUsuario";
    public static final String COL_EMAIL = "email";
    public static final String COL_CONTRASENA = "contrasena";
    public static final String COL_NOMBRES = "nombres";
    public static final String COL_APELLIDOS = "apellidos";
    public static final String COL_ROL = "rol";            // nuevo
    public static final String COL_AVATAR = "urlAvatar";   // nuevo
    public static final String COL_RANGO_ACTUAL = "idRangoActual";

    // Tabla Usuario
    private final String tabla_User = "CREATE TABLE " + TABLA_USUARIO + " (" +
            ID_USUARIO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_NOMBRE_USUARIO + " VARCHAR(100) NOT NULL, " +
            COL_EMAIL + " VARCHAR(100) NOT NULL, " +
            COL_CONTRASENA + " VARCHAR(100) NOT NULL, " +
            COL_NOMBRES + " VARCHAR(100) NOT NULL, " +
            COL_APELLIDOS + " VARCHAR(100) NOT NULL, " +
            COL_ROL + " VARCHAR(50) NOT NULL, " +
            COL_AVATAR + " VARCHAR(200), " +
            COL_RANGO_ACTUAL + " INTEGER NOT NULL)";

    // Tabla ResenaLocal
    private final String tablaResena = "CREATE TABLE ResenaLocal(" +
            "IdResena INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "IdUsuario INTEGER NOT NULL, " +
            "IdPelicula INTEGER NOT NULL, " +
            "TituloPelicula VARCHAR(150), " +
            "Genero VARCHAR(80), " +
            "Anio INTEGER, " +
            "Sinopsis TEXT, " +
            "Actores TEXT, " +
            "Direccion TEXT, " +
            "Comentario TEXT, " +
            "Puntuacion REAL, " +
            "Latitud REAL, " +
            "Longitud REAL, " +
            "FechaRegistro TEXT" +
            ")";

    public BDHelper(@Nullable Context context) {
        super(context, NOMBRE_BD, null, VERSION_BD);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tabla_User);
        db.execSQL(tablaResena);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_USUARIO);
        db.execSQL(tabla_User);
        db.execSQL("DROP TABLE IF EXISTS ResenaLocal");
        db.execSQL(tablaResena);
    }
}
