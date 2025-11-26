package com.example.cinedex_v2.Data.Models;

import com.example.cinedex_v2.Data.DTOs.Funcion.FuncionResponse;
import java.util.List;

// ESTO SOLO VIVE EN ANDROID
public class PeliculaAgrupada {
    private int idPelicula; // Necesario para identificarla
    private String nombrePelicula;
    private String urlPoster;
    private String clasificacion; // Ej: "+14"
    private String duracion;      // Ej: "120 min"
    private String formato;       // Ej: "2D"
    private String idioma;        // Ej: "DOB"

    // Esta es la clave: Una lista de horarios DENTRO de la película
    private List<FuncionResponse> funciones;

    public PeliculaAgrupada(int idPelicula, String nombre, String url, String clasif, String dur, String fmt, String idi, List<FuncionResponse> funciones) {
        this.idPelicula = idPelicula;
        this.nombrePelicula = nombre;
        this.urlPoster = url;
        this.clasificacion = clasif;
        this.duracion = dur;
        this.formato = fmt;
        this.idioma = idi;
        this.funciones = funciones;
    }

    // Getters
    public int getIdPelicula() { return idPelicula; }
    public String getNombrePelicula() { return nombrePelicula; }
    public String getUrlPoster() { return urlPoster; }
    public String getClasificacion() { return clasificacion; }
    public String getDuracion() { return duracion; }
    public String getFormato() { return formato; }
    public String getIdioma() { return idioma; }
    public List<FuncionResponse> getFunciones() { return funciones; }
}