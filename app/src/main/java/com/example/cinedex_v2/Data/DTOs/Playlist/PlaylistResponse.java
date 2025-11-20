package com.example.cinedex_v2.Data.DTOs.Playlist;

import java.io.Serializable;

public class PlaylistResponse implements Serializable {
    private int idPlaylist;
    private int idUsuario;
    private String nombre;
    private int orden;
    private int cantidadPeliculas; // Este campo extra viene calculado del backend

    // Getters y Setters
    public int getIdPlaylist() { return idPlaylist; }
    public void setIdPlaylist(int idPlaylist) { this.idPlaylist = idPlaylist; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }

    public int getCantidadPeliculas() { return cantidadPeliculas; }
    public void setCantidadPeliculas(int cantidadPeliculas) { this.cantidadPeliculas = cantidadPeliculas; }
}