package com.foodbook.dto;

import java.util.List;

public class ReceitaDetalheDto {
    private Long id;
    private String titulo;
    private String descricao;
    private String modoPreparo;
    private String imagemUrl;
    private int tempoPreparo;
    private int porcoes;
    private CategoriaDto categoria;
    private UsuarioDto autor;
    private List<ReceitaIngredienteDto> ingredientes;
    private long totalCurtidas;
    private long totalComentarios;
    private boolean curtidoPorMim;
    private boolean favoritadoPorMim;
    private boolean origemExterna;
    private String criadoEm;

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public String getModoPreparo() { return modoPreparo; }
    public String getImagemUrl() { return imagemUrl; }
    public int getTempoPreparo() { return tempoPreparo; }
    public int getPorcoes() { return porcoes; }
    public CategoriaDto getCategoria() { return categoria; }
    public UsuarioDto getAutor() { return autor; }
    public List<ReceitaIngredienteDto> getIngredientes() { return ingredientes; }
    public long getTotalCurtidas() { return totalCurtidas; }
    public long getTotalComentarios() { return totalComentarios; }
    public boolean isCurtidoPorMim() { return curtidoPorMim; }
    public boolean isFavoritadoPorMim() { return favoritadoPorMim; }
    public boolean isOrigemExterna() { return origemExterna; }
    public String getCriadoEm() { return criadoEm; }

    public void setCurtidoPorMim(boolean curtidoPorMim) { this.curtidoPorMim = curtidoPorMim; }
    public void setFavoritadoPorMim(boolean favoritadoPorMim) { this.favoritadoPorMim = favoritadoPorMim; }
    public void setTotalCurtidas(long totalCurtidas) { this.totalCurtidas = totalCurtidas; }
}
