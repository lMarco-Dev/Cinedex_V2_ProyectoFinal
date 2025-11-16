package com.example.cinedex_v2.Data.DTOs.Usuario;

import com.google.gson.annotations.SerializedName;

public class UsuarioLoginRequestDto {

    @SerializedName("NombreUsuario")
    private String nombreUsuario;

    @SerializedName("Contrasena")
    private String contrasena;

    public UsuarioLoginRequestDto(String nombreUsuario, String contrasena) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
    }

    // Getters y Setters correctos
    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
