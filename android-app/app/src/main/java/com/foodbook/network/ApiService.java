package com.foodbook.network;

import com.foodbook.dto.*;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // ── Auth ──────────────────────────────────────────────────
    @POST("api/v1/auth/login")
    Call<ApiResponseDto<AuthResponseDto>> login(@Body LoginRequestDto request);

    @POST("api/v1/auth/registrar")
    Call<ApiResponseDto<AuthResponseDto>> registrar(@Body RegisterRequestDto request);

    @POST("api/v1/auth/refresh")
    Call<ApiResponseDto<AuthResponseDto>> refresh(@Header("Refresh-Token") String refreshToken);

    // ── Usuário ───────────────────────────────────────────────
    @GET("api/v1/usuarios/me")
    Call<ApiResponseDto<UsuarioPerfilDto>> meuPerfil();

    @GET("api/v1/usuarios/{id}")
    Call<ApiResponseDto<UsuarioPerfilDto>> perfil(@Path("id") long id);

    @PUT("api/v1/usuarios/me")
    Call<ApiResponseDto<UsuarioDto>> atualizarPerfil(@Body AtualizarUsuarioDto request);

    // ── Receitas ──────────────────────────────────────────────
    @GET("api/v1/receitas")
    Call<ApiResponseDto<PageResponseDto<ReceitaResumoDto>>> feed(
            @Query("page") int page, @Query("size") int size);

    @GET("api/v1/receitas/{id}")
    Call<ApiResponseDto<ReceitaDetalheDto>> detalheReceita(@Path("id") long id);

    @GET("api/v1/receitas/usuario/{usuarioId}")
    Call<ApiResponseDto<PageResponseDto<ReceitaResumoDto>>> receitasDoUsuario(
            @Path("usuarioId") long usuarioId,
            @Query("page") int page, @Query("size") int size);

    @GET("api/v1/receitas/buscar")
    Call<ApiResponseDto<PageResponseDto<ReceitaResumoDto>>> buscarPorTitulo(
            @Query("titulo") String titulo,
            @Query("page") int page, @Query("size") int size);

    @GET("api/v1/receitas/categoria/{categoriaId}")
    Call<ApiResponseDto<PageResponseDto<ReceitaResumoDto>>> buscarPorCategoria(
            @Path("categoriaId") long categoriaId,
            @Query("page") int page, @Query("size") int size);

    @POST("api/v1/receitas/buscar/ingredientes")
    Call<ApiResponseDto<PageResponseDto<ReceitaResumoDto>>> buscarPorIngredientes(
            @Body BuscarIngredientesDto request,
            @Query("page") int page, @Query("size") int size);

    @POST("api/v1/receitas")
    Call<ApiResponseDto<ReceitaDetalheDto>> criarReceita(@Body ReceitaRequestDto request);

    @PUT("api/v1/receitas/{id}")
    Call<ApiResponseDto<ReceitaDetalheDto>> atualizarReceita(
            @Path("id") long id, @Body ReceitaRequestDto request);

    @DELETE("api/v1/receitas/{id}")
    Call<ApiResponseDto<Void>> excluirReceita(@Path("id") long id);

    // ── Curtidas ──────────────────────────────────────────────
    @POST("api/v1/receitas/{id}/curtidas")
    Call<ApiResponseDto<CurtidaDto>> curtir(@Path("id") long receitaId);

    @DELETE("api/v1/receitas/{id}/curtidas")
    Call<ApiResponseDto<CurtidaDto>> removerCurtida(@Path("id") long receitaId);

    // ── Favoritos ─────────────────────────────────────────────
    @POST("api/v1/receitas/{id}/favoritos")
    Call<ApiResponseDto<FavoritoDto>> favoritar(@Path("id") long receitaId);

    @DELETE("api/v1/receitas/{id}/favoritos")
    Call<ApiResponseDto<FavoritoDto>> removerFavorito(@Path("id") long receitaId);

    @GET("api/v1/usuarios/me/favoritos")
    Call<ApiResponseDto<PageResponseDto<ReceitaResumoDto>>> meusFavoritos(
            @Query("page") int page, @Query("size") int size);

    // ── Comentários ───────────────────────────────────────────
    @GET("api/v1/receitas/{id}/comentarios")
    Call<ApiResponseDto<PageResponseDto<ComentarioDto>>> comentarios(
            @Path("id") long receitaId,
            @Query("page") int page, @Query("size") int size);

    @POST("api/v1/receitas/{id}/comentarios")
    Call<ApiResponseDto<ComentarioDto>> comentar(
            @Path("id") long receitaId, @Body ComentarioRequestDto request);

    @DELETE("api/v1/receitas/{id}/comentarios/{comentarioId}")
    Call<ApiResponseDto<Void>> excluirComentario(
            @Path("id") long receitaId, @Path("comentarioId") long comentarioId);

    // ── Categorias ────────────────────────────────────────────
    @GET("api/v1/categorias")
    Call<ApiResponseDto<java.util.List<CategoriaDto>>> categorias();

    // ── Ingredientes ──────────────────────────────────────────
    @GET("api/v1/ingredientes/buscar")
    Call<ApiResponseDto<java.util.List<IngredienteDto>>> buscarIngredientes(@Query("termo") String termo);

    // ── Lista de Compras ──────────────────────────────────────
    @POST("api/v1/listas-compra")
    Call<ApiResponseDto<ListaCompraDto>> gerarListaCompra(@Body GerarListaCompraDto request);

    @GET("api/v1/listas-compra")
    Call<ApiResponseDto<PageResponseDto<ListaCompraDto>>> minhasListas(
            @Query("page") int page, @Query("size") int size);

    @PATCH("api/v1/listas-compra/{id}/itens/{itemId}")
    Call<ApiResponseDto<Void>> marcarItemComprado(
            @Path("id") long listaId, @Path("itemId") long itemId,
            @Body MarcarCompradoDto request);

    @DELETE("api/v1/listas-compra/{id}")
    Call<ApiResponseDto<Void>> excluirLista(@Path("id") long id);

    // ── Integração Externa ────────────────────────────────────
    @GET("api/v1/integracao/buscar")
    Call<ApiResponseDto<PageResponseDto<ReceitaResumoDto>>> buscarExterno(@Query("termo") String termo);

    @POST("api/v1/integracao/importar/{externalId}")
    Call<ApiResponseDto<ReceitaDetalheDto>> importarReceita(@Path("externalId") String externalId);
}
