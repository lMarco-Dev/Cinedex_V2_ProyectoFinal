package com.example.cinedex_v2.Data.DTOs.Comentario;

public class ComentarioRequest {
    private int idUsuario;
    private int idResena;
    private String contenido;

    public ComentarioRequest(int idUsuario, int idResena, String contenido) {
        this.idUsuario = idUsuario;
        this.idResena = idResena;
        this.contenido = contenido;
    }

    // Getters y Setters
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public int getIdResena() { return idResena; }
    public void setIdResena(int idResena) { this.idResena = idResena; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
}