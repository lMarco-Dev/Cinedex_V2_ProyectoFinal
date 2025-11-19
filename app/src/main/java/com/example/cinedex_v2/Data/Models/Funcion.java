package com.example.cinedex_v2.Data.Models;

import java.util.Date;

public class Funcion {
    private int idFuncion;
    private int idPelicula;
    private int idCine;
    private String sala;
    private Date fechaHora;
    private String idioma;

    // --- NUEVOS CAMPOS ---
    private String formato;
    private double precio;

    // Constructor vacío
    public Funcion() {}

    // Constructor completo actualizado
    public Funcion(int idFuncion, int idPelicula, int idCine, String sala, Date fechaHora, String idioma, String formato, double precio) {
        this.idFuncion = idFuncion;
        this.idPelicula = idPelicula;
        this.idCine = idCine;
        this.sala = sala;
        this.fechaHora = fechaHora;
        this.idioma = idioma;
        this.formato = formato;
        this.precio = precio;
    }

    // Getters y Setters existentes...
    public int getIdFuncion() { return idFuncion; }
    public void setIdFuncion(int idFuncion) { this.idFuncion = idFuncion; }

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

    // --- NUEVOS GETTERS Y SETTERS ---
    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
}