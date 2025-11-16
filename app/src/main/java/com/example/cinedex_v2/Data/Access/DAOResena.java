package com.example.cinedex_v2.Data.Access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.cinedex_v2.Data.Models.Resena;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DAOResena {

    private BDHelper helper;

    public DAOResena(Context ctx){
        helper = new BDHelper(ctx);
    }

    // Insertar reseña local
    public boolean insertarLocal(int idUsuario, int idPelicula, String comentario, double puntuacion) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("IdUsuario", idUsuario);
        cv.put("IdPelicula", idPelicula);
        cv.put("Comentario", comentario);
        cv.put("Puntuacion", puntuacion);

        // Guardar la fecha actual
        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        cv.put("FechaRegistro", fecha);

        long fila = db.insert("ResenaLocal", null, cv);
        db.close();
        return fila > 0;
    }

    // Listar reseñas por película
    public List<Resena> listarPorPelicula(int idPelicula){
        List<Resena> lista = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT IdResena, IdUsuario, IdPelicula, Comentario, Puntuacion, FechaRegistro FROM ResenaLocal WHERE IdPelicula = ? ORDER BY FechaRegistro DESC",
                new String[]{String.valueOf(idPelicula)}
        );

        if(c.moveToFirst()){
            do {
                Resena r = new Resena();
                r.setIdResena(c.getInt(c.getColumnIndexOrThrow("IdResena")));
                r.setIdUsuario(c.getInt(c.getColumnIndexOrThrow("IdUsuario")));
                r.setIdPelicula(c.getInt(c.getColumnIndexOrThrow("IdPelicula")));
                r.setComentario(c.getString(c.getColumnIndexOrThrow("Comentario")));
                r.setPuntuacion(c.getDouble(c.getColumnIndexOrThrow("Puntuacion")));
                r.setFecha(c.getString(c.getColumnIndexOrThrow("FechaRegistro")));

                lista.add(r);
            } while(c.moveToNext());
        }

        c.close();
        db.close();
        return lista;
    }

    // Listar reseñas por usuario
    public List<Resena> listarPorUsuario(int idUsuario){
        List<Resena> lista = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT IdResena, IdUsuario, IdPelicula, Comentario, Puntuacion, FechaRegistro FROM ResenaLocal WHERE IdUsuario = ? ORDER BY FechaRegistro DESC",
                new String[]{String.valueOf(idUsuario)}
        );

        if(c.moveToFirst()){
            do {
                Resena r = new Resena();
                r.setIdResena(c.getInt(c.getColumnIndexOrThrow("IdResena")));
                r.setIdUsuario(c.getInt(c.getColumnIndexOrThrow("IdUsuario")));
                r.setIdPelicula(c.getInt(c.getColumnIndexOrThrow("IdPelicula")));
                r.setComentario(c.getString(c.getColumnIndexOrThrow("Comentario")));
                r.setPuntuacion(c.getDouble(c.getColumnIndexOrThrow("Puntuacion")));
                r.setFecha(c.getString(c.getColumnIndexOrThrow("FechaRegistro")));

                lista.add(r);
            } while(c.moveToNext());
        }

        c.close();
        db.close();
        return lista;
    }

    // Contar reseñas por puntuación
    public int contarPorPuntuacion(int idPelicula, int estrella){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT COUNT(*) FROM ResenaLocal WHERE IdPelicula = ? AND ROUND(Puntuacion) = ?",
                new String[]{String.valueOf(idPelicula), String.valueOf(estrella)}
        );

        int count = 0;
        if(c.moveToFirst()) count = c.getInt(0);

        c.close();
        db.close();
        return count;
    }

    // Eliminar reseña
    public boolean eliminar(int idResena){
        SQLiteDatabase db = helper.getWritableDatabase();
        int filas = db.delete("ResenaLocal", "IdResena = ?", new String[]{String.valueOf(idResena)});
        db.close();
        return filas > 0;
    }

    // Actualizar reseña
    public boolean actualizar(Resena r){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("Comentario", r.getComentario());
        cv.put("Puntuacion", r.getPuntuacion());

        int filas = db.update("ResenaLocal", cv, "IdResena = ?", new String[]{String.valueOf(r.getIdResena())});
        db.close();
        return filas > 0;
    }
}
