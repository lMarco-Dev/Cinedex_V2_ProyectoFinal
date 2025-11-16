package com.example.cinedex_v2.Data.Network;

import com.example.cinedex_v2.Data.DTOs.Cine.CineRequest;
import com.example.cinedex_v2.Data.DTOs.Cine.CineResponse;
import com.example.cinedex_v2.Data.DTOs.Evento.EventoRequest;
import com.example.cinedex_v2.Data.DTOs.Evento.EventoResponse;
import com.example.cinedex_v2.Data.DTOs.Funcion.FuncionRequest;
import com.example.cinedex_v2.Data.DTOs.Funcion.FuncionResponse;
import com.example.cinedex_v2.Data.DTOs.Noticia.NoticiaRequest;
import com.example.cinedex_v2.Data.DTOs.Noticia.NoticiaResponse;
import com.example.cinedex_v2.Data.DTOs.Pelicula.PeliculaRequest;
import com.example.cinedex_v2.Data.DTOs.Pelicula.PeliculaResponse;
import com.example.cinedex_v2.Data.DTOs.Resena.ResenaRequestDto;
import com.example.cinedex_v2.Data.DTOs.Resena.ResenaResponseDto;
import com.example.cinedex_v2.Data.DTOs.Usuario.UsuarioLoginRequestDto;
import com.example.cinedex_v2.Data.DTOs.Usuario.UsuarioRegisterRequestDto;
import com.example.cinedex_v2.Data.DTOs.Usuario.UsuarioResponseDto;
import com.example.cinedex_v2.Data.DTOs.Usuario.UsuarioUpdateRequestDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CineDexApiService {

    // ================== PELÍCULAS ==================
    @GET("api/Peliculas")
    Call<List<PeliculaResponse>> getPeliculas();

    @GET("api/Peliculas/{id}")
    Call<PeliculaResponse> getPelicula(@Path("id") int id);

    @POST("api/Peliculas")
    Call<Void> crearPelicula(@Body PeliculaRequest pelicula);

    @PUT("api/Peliculas/{id}")
    Call<Void> editarPelicula(@Path("id") int id, @Body PeliculaRequest pelicula);

    @DELETE("api/Peliculas/{id}")
    Call<Void> eliminarPelicula(@Path("id") int id);

    // ================== USUARIOS ==================
    @POST("api/Usuarios/login")
    Call<UsuarioResponseDto> login(@Body UsuarioLoginRequestDto usuario);

    @POST("api/Usuarios/login-admin")
    Call<UsuarioResponseDto> loginAdmin(@Body UsuarioLoginRequestDto dto);

    @POST("api/Usuarios/register")
    Call<UsuarioResponseDto> register(@Body UsuarioRegisterRequestDto usuario);

    @PUT("api/Usuarios/{id}")
    Call<Void> actualizarUsuario(@Path("id") int id, @Body UsuarioUpdateRequestDto usuario);

    @GET("api/Usuarios/{id}")
    Call<UsuarioResponseDto> getUsuario(@Path("id") int id);

    // ================== CINES ==================
    @GET("api/Cines")
    Call<List<CineResponse>> getCines();

    @POST("api/Cines")
    Call<Void> crearCine(@Body CineRequest cine);

    @PUT("api/Cines/{id}")
    Call<Void> editarCine(@Path("id") int id, @Body CineRequest cine);

    @DELETE("api/Cines/{id}")
    Call<Void> eliminarCine(@Path("id") int id);

    // ================== FUNCIONES ==================
    @GET("api/Funciones")
    Call<List<FuncionResponse>> getFunciones();

    @POST("api/Funciones")
    Call<Void> crearFuncion(@Body FuncionRequest funcion);

    @PUT("api/Funciones/{id}")
    Call<Void> editarFuncion(@Path("id") int id, @Body FuncionRequest funcion);

    @DELETE("api/Funciones/{id}")
    Call<Void> eliminarFuncion(@Path("id") int id);

    // ================== EVENTOS ==================
    @GET("api/Eventos")
    Call<List<EventoResponse>> getEventos();

    @POST("api/Eventos")
    Call<Void> crearEvento(@Body EventoRequest evento);

    @PUT("api/Eventos/{id}")
    Call<Void> editarEvento(@Path("id") int id, @Body EventoRequest evento);

    @DELETE("api/Eventos/{id}")
    Call<Void> eliminarEvento(@Path("id") int id);

    // ================== NOTICIAS ==================
    @GET("api/Noticias")
    Call<List<NoticiaResponse>> getNoticias();

    @POST("api/Noticias")
    Call<Void> crearNoticia(@Body NoticiaRequest noticia);

    @PUT("api/Noticias/{id}")
    Call<Void> editarNoticia(@Path("id") int id, @Body NoticiaRequest noticia);

    @DELETE("api/Noticias/{id}")
    Call<Void> eliminarNoticia(@Path("id") int id);

    // ================== RESEÑAS ==================
    @GET("api/Resenas")
    Call<List<ResenaResponseDto>> getResenas();

    @GET("api/Resenas/usuario/{idUsuario}")
    Call<List<ResenaResponseDto>> getResenasPorUsuario(@Path("idUsuario") int idUsuario);

    @GET("api/Resenas/pelicula/{idPelicula}")
    Call<List<ResenaResponseDto>> getResenasPorPelicula(@Path("idPelicula") int idPelicula);

    @POST("api/Resenas")
    Call<ResenaResponseDto> crearResena(@Body ResenaRequestDto resena);

    @PUT("api/Resenas/{id}")
    Call<Void> editarResena(@Path("id") int id, @Body ResenaRequestDto resena);

    @DELETE("api/Resenas/{id}")
    Call<Void> eliminarResena(@Path("id") int id);



}
