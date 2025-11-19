package com.example.cinedex_v2.Data.DTOs.Funcion;

import java.util.Date;

public class FuncionRequest {
    private int idCine;
    private int idPelicula;
    private String sala;
    private Date fechaHora;
    private String idioma;   // "DOB", "SUB"
    private String formato;  // "2D", "3D"
    private double precio;

    // Getters y Setters
    public int getIdCine() { return idCine; }
    public void setIdCine(int idCine) { this.idCine = idCine; }

    public int getIdPelicula() { return idPelicula; }
    public void setIdPelicula(int idPelicula) { this.idPelicula = idPelicula; }

    public String getSala() { return sala; }
    public void setSala(String sala) { this.sala = sala; }

    public Date getFechaHora() { return fechaHora; }
    public void setFechaHora(Date fechaHora) { this.fechaHora = fechaHora; }

    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }

    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
}