package com.example.cinedex_v2.Data.DTOs.Resena;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ResenaResponseDto implements Serializable {

    @SerializedName("idResena") // <--- Minúscula
    private int idResena;

    @SerializedName("idUsuario") // <--- Minúscula
    private int idUsuario;

    @SerializedName("idPelicula") // <--- Minúscula
    private int idPelicula;

    @SerializedName("comentario") // <--- Minúscula
    private String comentario;

    @SerializedName("puntuacion") // <--- Minúscula
    private double puntuacion;

    @SerializedName("fecha") // <--- Minúscula
    private String fecha;

    @SerializedName("titulo") // <--- Minúscula (Esto era Titulo)
    private String tituloPelicula;

    // ESTE ERA EL CULPABLE PRINCIPAL:
    @SerializedName("posterPeliculaURL") // <--- ¡MINÚSCULA "p"!
    private String posterPeliculaURL;

    // --- GETTERS Y SETTERS (Estos pueden quedarse igual) ---
    public int getIdResena() { return idResena; }
    public void setIdResena(int idResena) { this.idResena = idResena; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public int getIdPelicula() { return idPelicula; }
    public void setIdPelicula(int idPelicula) { this.idPelicula = idPelicula; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public double getPuntuacion() { return puntuacion; }
    public void setPuntuacion(double puntuacion) { this.puntuacion = puntuacion; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getTituloPelicula() { return tituloPelicula; }
    public void setTituloPelicula(String tituloPelicula) { this.tituloPelicula = tituloPelicula; }

    public String getPosterPeliculaURL() { return posterPeliculaURL; }
    public void setPosterPeliculaURL(String posterPeliculaURL) { this.posterPeliculaURL = posterPeliculaURL; }
}