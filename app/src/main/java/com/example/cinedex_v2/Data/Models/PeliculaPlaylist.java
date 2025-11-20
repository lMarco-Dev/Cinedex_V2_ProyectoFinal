package com.example.cinedex_v2.Data.Models;

public class PeliculaPlaylist {

    private int id;
    private int idPlaylist;
    private int idPelicula;

    // Constructor vacío
    public PeliculaPlaylist() {}

    // Constructor completo
    public PeliculaPlaylist(int id, int idPlaylist, int idPelicula) {
        this.id = id;
        this.idPlaylist = idPlaylist;
        this.idPelicula = idPelicula;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdPlaylist() { return idPlaylist; }
    public void setIdPlaylist(int idPlaylist) { this.idPlaylist = idPlaylist; }

    public int getIdPelicula() { return idPelicula; }
    public void setIdPelicula(int idPelicula) { this.idPelicula = idPelicula; }
}