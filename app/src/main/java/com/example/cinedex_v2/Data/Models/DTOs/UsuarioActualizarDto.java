package com.example.cinedex_v2.Data.Models.DTOs;

import com.google.gson.annotations.SerializedName;

public class UsuarioActualizarDto {

    @SerializedName("Nombres")
    private String nombres;

    @SerializedName("Apellidos")
    private String apellidos;

    public UsuarioActualizarDto(String nombres, String apellidos) {
        this.nombres = nombres;
        this.apellidos = apellidos;
    }
}
