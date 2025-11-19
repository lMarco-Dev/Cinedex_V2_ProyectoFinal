package com.example.cinedex_v2.Data.DTOs.Funcion;

import java.io.Serializable;
import java.util.Date;

public class FuncionResponse implements Serializable {
    private int idFuncion;
    private int idCine;
    private int idPelicula;

    // Datos visuales extra del Backend
    private String nombrePelicula;
    private String urlImagenPelicula;

    private String sala;
    private Date fechaHora;
    private String idioma;
    private String formato;
    private double precio;

    // Getters y Setters
    public int getIdFuncion() { return idFuncion; }
    public void setIdFuncion(int idFuncion) { this.idFuncion = idFuncion; }

    public int getIdCine() { return idCine; }
    public void setIdCine(int idCine) { this.idCine = idCine; }

    public int getIdPelicula() { return idPelicula; }
    public void setIdPelicula(int idPelicula) { this.idPelicula = idPelicula; }

    public String getNombrePelicula() { return nombrePelicula; }
    public void setNombrePelicula(String nombrePelicula) { this.nombrePelicula = nombrePelicula; }

    public String getUrlImagenPelicula() { return urlImagenPelicula; }
    public void setUrlImagenPelicula(String urlImagenPelicula) { this.urlImagenPelicula = urlImagenPelicula; }

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