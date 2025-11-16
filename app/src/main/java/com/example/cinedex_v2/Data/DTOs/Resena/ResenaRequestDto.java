package com.example.cinedex_v2.Data.DTOs.Resena;

import com.google.gson.annotations.SerializedName;

public class ResenaRequestDto {
    @SerializedName("IdUsuario")
    private int idUsuario;

    @SerializedName("IdPelicula")
    private int idPelicula;

    @SerializedName("Comentario")
    private String comentario;

    @SerializedName("Puntuacion")
    private double puntuacion;

    // Constructor
    public ResenaRequestDto(int idUsuario, int idPelicula, String comentario, double puntuacion) {
        this.idUsuario = idUsuario;
        this.idPelicula = idPelicula;
        this.comentario = comentario;
        this.puntuacion = puntuacion;
    }

    // Getters y setters
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public int getIdPelicula() { return idPelicula; }
    public void setIdPelicula(int idPelicula) { this.idPelicula = idPelicula; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public double getPuntuacion() { return puntuacion; }
    public void setPuntuacion(double puntuacion) { this.puntuacion = puntuacion; }
}
