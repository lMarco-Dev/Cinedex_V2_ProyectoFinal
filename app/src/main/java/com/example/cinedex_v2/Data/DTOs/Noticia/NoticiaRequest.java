package com.example.cinedex_v2.Data.DTOs.Noticia;

import java.util.Date; // Necesario para el fix de fecha

public class NoticiaRequest {
    private String titulo;
    private String resumen;
    private String urlImagen;
    private String urlYoutube; // <-- NUEVO
    private Date fechaPublicacion; // <-- Importante para que el Backend la reciba si la pide

    // Getters y Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getResumen() { return resumen; }
    public void setResumen(String resumen) { this.resumen = resumen; }

    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }

    public String getUrlYoutube() { return urlYoutube; } // <-- NUEVO
    public void setUrlYoutube(String urlYoutube) { this.urlYoutube = urlYoutube; } // <-- NUEVO

    public Date getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(Date fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }
}