package com.example.cinedex_v2.Data.Models;

import java.util.Date;

public class Noticia {
    private int idNoticia;
    private String titulo;
    private String resumen;
    private String urlImagen;
    private Date fechaPublicacion;

    // Constructor vacío
    public Noticia() {}

    // Constructor completo
    public Noticia(int idNoticia, String titulo, String resumen, String urlImagen, Date fechaPublicacion) {
        this.idNoticia = idNoticia;
        this.titulo = titulo;
        this.resumen = resumen;
        this.urlImagen = urlImagen;
        this.fechaPublicacion = fechaPublicacion;
    }

    // Getters y Setters
    public int getIdNoticia() { return idNoticia; }
    public void setIdNoticia(int idNoticia) { this.idNoticia = idNoticia; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getResumen() { return resumen; }
    public void setResumen(String resumen) { this.resumen = resumen; }

    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }

    public Date getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(Date fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }
}
