package com.example.cinedex_v2.Data.DTOs.PeliculaPlaylist;

import java.io.Serializable;

public class PeliculaPlaylistResponse implements Serializable {
    private int idPeliculaPlaylist;
    private int idPlaylist;
    private int idPelicula;

    // Getters y Setters
    public int getIdPeliculaPlaylist() { return idPeliculaPlaylist; }
    public void setIdPeliculaPlaylist(int idPeliculaPlaylist) { this.idPeliculaPlaylist = idPeliculaPlaylist; }

    public int getIdPlaylist() { return idPlaylist; }
    public void setIdPlaylist(int idPlaylist) { this.idPlaylist = idPlaylist; }

    public int getIdPelicula() { return idPelicula; }
    public void setIdPelicula(int idPelicula) { this.idPelicula = idPelicula; }
}