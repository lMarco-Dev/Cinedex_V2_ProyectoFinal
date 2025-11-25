package com.example.cinedex_v2.Data.DTOs.Usuario;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class UsuarioResponseDto implements Serializable {

    @SerializedName("idUsuario")
    private int idUsuario;

    @SerializedName("nombreUsuario")
    private String nombreUsuario;

    @SerializedName("email")
    private String email;

    @SerializedName("urlAvatar")
    private String urlAvatar;

    // Ahora le decimos explícitamente que busque la clave "nombres" en el JSON
    @SerializedName("nombres")
    private String nombres;

    @SerializedName("apellidos")
    private String apellidos;

    @SerializedName("rol")
    private String rol;

    // ==========================================
    //           GETTERS Y SETTERS
    // ==========================================

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUrlAvatar() { return urlAvatar; }
    public void setUrlAvatar(String urlAvatar) { this.urlAvatar = urlAvatar; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}