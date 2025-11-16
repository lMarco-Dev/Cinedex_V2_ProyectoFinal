package com.example.cinedex_v2.Data.Models;

import java.util.List;

public class Section {
    private String titulo;
    private List<Pelicula> peliculaList;

    public Section(String titulo, List<Pelicula> peliculaList) {
        this.titulo = titulo;
        this.peliculaList = peliculaList;
    }

    public String getTitle() {
        return titulo;
    }

    public List<Pelicula> getMovieList() {
        return peliculaList;
    }
}