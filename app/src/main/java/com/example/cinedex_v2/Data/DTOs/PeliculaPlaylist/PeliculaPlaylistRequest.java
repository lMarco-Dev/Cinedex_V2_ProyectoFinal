package com.example.cinedex_v2.Data.DTOs.PeliculaPlaylist;

public class PeliculaPlaylistRequest {
    private int idPlaylist;
    private int idPelicula;

    public PeliculaPlaylistRequest(int idPlaylist, int idPelicula) {
        this.idPlaylist = idPlaylist;
        this.idPelicula = idPelicula;
    }

    // Getters y Setters
    public int getIdPlaylist() { return idPlaylist; }
    public void setIdPlaylist(int idPlaylist) { this.idPlaylist = idPlaylist; }

    public int getIdPelicula() { return idPelicula; }
    public void setIdPelicula(int idPelicula) { this.idPelicula = idPelicula; }
}