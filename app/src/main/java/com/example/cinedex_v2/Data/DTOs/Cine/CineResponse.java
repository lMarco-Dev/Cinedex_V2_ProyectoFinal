package com.example.cinedex_v2.Data.DTOs.Cine;

import java.io.Serializable;

public class CineResponse implements Serializable { // <--- IMPORTANTE
    private int idCine;
    private String nombre;
    private String ciudad;
    private String direccion;
    private String urlImagen;

    // Getters y Setters
    public int getIdCine() { return idCine; }
    public void setIdCine(int idCine) { this.idCine = idCine; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getUrlImagen() { return urlImagen; } // <--- AGREGADO
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; } // <--- AGREGADO
}