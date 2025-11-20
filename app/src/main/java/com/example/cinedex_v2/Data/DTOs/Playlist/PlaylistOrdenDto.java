package com.example.cinedex_v2.Data.DTOs.Playlist;

public class PlaylistOrdenDto {
    private int idPlaylist;
    private int nuevoOrden;

    public PlaylistOrdenDto(int idPlaylist, int nuevoOrden) {
        this.idPlaylist = idPlaylist;
        this.nuevoOrden = nuevoOrden;
    }

    // Getters y Setters
    public int getIdPlaylist() { return idPlaylist; }
    public void setIdPlaylist(int idPlaylist) { this.idPlaylist = idPlaylist; }

    public int getNuevoOrden() { return nuevoOrden; }
    public void setNuevoOrden(int nuevoOrden) { this.nuevoOrden = nuevoOrden; }
}