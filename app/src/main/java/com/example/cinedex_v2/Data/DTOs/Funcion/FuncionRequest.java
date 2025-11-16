package com.example.cinedex_v2.Data.DTOs.Funcion;

import java.util.Date;

public class FuncionRequest {
    private int idPelicula;
    private int idCine;
    private String sala;
    private Date fechaHora;
    private String idioma;

    // Getters y Setters
    public int getIdPelicula() { return idPelicula; }
    public void setIdPelicula(int idPelicula) { this.idPelicula = idPelicula; }

    public int getIdCine() { return idCine; }
    public void setIdCine(int idCine) { this.idCine = idCine; }

    public String getSala() { return sala; }
    public void setSala(String sala) { this.sala = sala; }

    public Date getFechaHora() { return fechaHora; }
    public void setFechaHora(Date fechaHora) { this.fechaHora = fechaHora; }

    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }
}
