package com.example.cinedex_v2.Data.Models;

public class Playlist {
    private int idPlaylist;
    private int idUsuario;
    private String nombre;
    private int orden;

    public Playlist() { }

    public Playlist(int idPlaylist, int idUsuario, String nombre, int orden) {
        this.idPlaylist = idPlaylist;
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.orden = orden;
    }

    // Getters y Setters
    public int getIdPlaylist() { return idPlaylist; }
    public void setIdPlaylist(int idPlaylist) { this.idPlaylist = idPlaylist; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }
}