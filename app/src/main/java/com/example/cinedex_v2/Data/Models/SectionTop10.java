package com.example.cinedex_v2.Data.Models;
import java.util.List;

// Este modelo es para la sección Top 10
public class SectionTop10 {
    private String titulo;
    private String subtitulo;
    private List<Pelicula> peliculas;

    public SectionTop10(String titulo, String subtitulo, List<Pelicula> peliculas) {
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.peliculas = peliculas;
    }
    public String getTitle() { return titulo; }
    public String getSubtitle() { return subtitulo; }
    public List<Pelicula> getMovies() { return peliculas; }
}