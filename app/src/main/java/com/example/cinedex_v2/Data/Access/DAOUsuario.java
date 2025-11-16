package com.example.cinedex_v2.Data.Access;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.cinedex_v2.Data.Models.Usuario;
import com.example.cinedex_v2.Data.DTOs.Usuario.UsuarioRegisterRequestDto;

import java.util.ArrayList;
import java.util.List;

public class DAOUsuario {

    private BDHelper helper;

    public DAOUsuario(Context context) {
        helper = new BDHelper(context);
        Log.d("Estado","[BDHelper]: Inicializado Correctamente");
    }

    // Insertar usuario desde Register DTO
    public long insertar(UsuarioRegisterRequestDto dto, String contrasenaHash) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(BDHelper.COL_NOMBRE_USUARIO, dto.getNombreUsuario());
        cv.put(BDHelper.COL_EMAIL, dto.getEmail());
        cv.put(BDHelper.COL_CONTRASENA, contrasenaHash);
        cv.put(BDHelper.COL_NOMBRES, dto.getNombres());
        cv.put(BDHelper.COL_APELLIDOS, dto.getApellidos());
        cv.put(BDHelper.COL_ROL, "usuario"); // Valor por defecto
        cv.put(BDHelper.COL_AVATAR, ""); // valor por defecto si no hay avatar

        long id = db.insert(BDHelper.TABLA_USUARIO, null, cv);
        db.close();
        return id;
    }

    // Listar todos los usuarios
    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + BDHelper.TABLA_USUARIO, null);

        if (c.moveToFirst()) {
            do {
                Usuario u = new Usuario();
                u.setIdUsuario(c.getInt(c.getColumnIndexOrThrow(BDHelper.ID_USUARIO)));
                u.setNombreUsuario(c.getString(c.getColumnIndexOrThrow(BDHelper.COL_NOMBRE_USUARIO)));
                u.setEmail(c.getString(c.getColumnIndexOrThrow(BDHelper.COL_EMAIL)));
                u.setContrasenaHash(c.getString(c.getColumnIndexOrThrow(BDHelper.COL_CONTRASENA)));
                u.setNombres(c.getString(c.getColumnIndexOrThrow(BDHelper.COL_NOMBRES)));
                u.setApellidos(c.getString(c.getColumnIndexOrThrow(BDHelper.COL_APELLIDOS)));
                u.setRol(c.getString(c.getColumnIndexOrThrow(BDHelper.COL_ROL)));
                u.setUrlAvatar(c.getString(c.getColumnIndexOrThrow(BDHelper.COL_AVATAR)));
                lista.add(u);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return lista;
    }

    // Actualizar usuario desde Update DTO
    public boolean actualizar(int idUsuario, UsuarioRegisterRequestDto dto, String urlAvatar) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(BDHelper.COL_NOMBRE_USUARIO, dto.getNombreUsuario());
        cv.put(BDHelper.COL_NOMBRES, dto.getNombres());
        cv.put(BDHelper.COL_APELLIDOS, dto.getApellidos());
        cv.put(BDHelper.COL_AVATAR, urlAvatar);

        int filas = db.update(BDHelper.TABLA_USUARIO, cv, BDHelper.ID_USUARIO + "=?",
                new String[]{String.valueOf(idUsuario)});
        db.close();
        return filas > 0;
    }

    // Eliminar
    public boolean eliminar(int idUsuario){
        SQLiteDatabase db = helper.getWritableDatabase();
        int filas = db.delete(BDHelper.TABLA_USUARIO,
                BDHelper.ID_USUARIO + "=?",
                new String[]{String.valueOf(idUsuario)});
        db.close();
        return filas > 0;
    }

    // Verificar existencia por nombreUsuario
    public boolean existeUsuario(String nombreUsuario){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT 1 FROM " + BDHelper.TABLA_USUARIO + " WHERE " + BDHelper.COL_NOMBRE_USUARIO + " = ?",
                new String[]{nombreUsuario}
        );
        boolean existe = cursor.moveToFirst();
        cursor.close();
        db.close();
        return existe;
    }
}
