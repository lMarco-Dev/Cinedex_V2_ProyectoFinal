package com.example.cinedex_v2.Data.Models;

import java.util.Date;

public class Evento {
    private int idEvento;
    private String titulo;
    private String descripcion;
    private Date fechaHora;
    private String ubicacion;
    private String urlImagen;

    // Constructor vacío
    public Evento() {}

    // Constructor completo
    public Evento(int idEvento, String titulo, String descripcion, Date fechaHora,
                  String ubicacion, String urlImagen) {
        this.idEvento = idEvento;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaHora = fechaHora;
        this.ubicacion = ubicacion;
        this.urlImagen = urlImagen;
    }

    // Getters y Setters
    public int getIdEvento() { return idEvento; }
    public void setIdEvento(int idEvento) { this.idEvento = idEvento; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Date getFechaHora() { return fechaHora; }
    public void setFechaHora(Date fechaHora) { this.fechaHora = fechaHora; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }
}
