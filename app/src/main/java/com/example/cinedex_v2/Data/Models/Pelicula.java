package com.example.cinedex_v2.Data.Models;

public class Pelicula {
    private int idPelicula;
    private String titulo;
    private String descripcion;
    private String urlPoster;
    private String categoria;
    private String tipoEstreno;
    private String plataformasStreaming;
    private String director;
    private String pais;
    private Integer duracionMin;
    private double notaPromedio;

    // Constructor vacío
    public Pelicula() {}

    // Constructor con todos los campos
    public Pelicula(int idPelicula, String titulo, String descripcion, String urlPoster,
                    String categoria, String tipoEstreno, String plataformasStreaming,
                    String director, String pais, Integer duracionMin, double notaPromedio) {
        this.idPelicula = idPelicula;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.urlPoster = urlPoster;
        this.categoria = categoria;
        this.tipoEstreno = tipoEstreno;
        this.plataformasStreaming = plataformasStreaming;
        this.director = director;
        this.pais = pais;
        this.duracionMin = duracionMin;
        this.notaPromedio = notaPromedio;
    }

    // Getters y Setters
    public int getIdPelicula() { return idPelicula; }
    public void setIdPelicula(int idPelicula) { this.idPelicula = idPelicula; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getUrlPoster() { return urlPoster; }
    public void setUrlPoster(String urlPoster) { this.urlPoster = urlPoster; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getTipoEstreno() { return tipoEstreno; }
    public void setTipoEstreno(String tipoEstreno) { this.tipoEstreno = tipoEstreno; }

    public String getPlataformasStreaming() { return plataformasStreaming; }
    public void setPlataformasStreaming(String plataformasStreaming) { this.plataformasStreaming = plataformasStreaming; }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public Integer getDuracionMin() { return duracionMin; }
    public void setDuracionMin(Integer duracionMin) { this.duracionMin = duracionMin; }

    public double getNotaPromedio() { return notaPromedio; }
    public void setNotaPromedio(double notaPromedio) { this.notaPromedio = notaPromedio; }
}
