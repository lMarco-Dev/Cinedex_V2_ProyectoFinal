package com.example.cinedex_v2.Data.DTOs.Playlist;

public class PlaylistRequest {
    private int idUsuario;
    private String nombre;

    public PlaylistRequest(int idUsuario, String nombre) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
    }

    // Getters y Setters
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}