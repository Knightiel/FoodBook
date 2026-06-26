package com.foodbook.dto;

public class ReceitaResumoDto {
    private Long id;
    private String titulo;
    private String descricao;
    private String imagemUrl;
    private int tempoPreparo;
    private int porcoes;
    private CategoriaDto categoria;
    private UsuarioDto autor;
    private long totalCurtidas;
    private long totalComentarios;
    private boolean curtidoPorMim;
    private boolean favoritadoPorMim;
    private String criadoEm;

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public String getImagemUrl() { return imagemUrl; }
    public int getTempoPreparo() { return tempoPreparo; }
    public int getPorcoes() { return porcoes; }
    public CategoriaDto getCategoria() { return categoria; }
    public UsuarioDto getAutor() { return autor; }
    public long getTotalCurtidas() { return totalCurtidas; }
    public long getTotalComentarios() { return totalComentarios; }
    public boolean isCurtidoPorMim() { return curtidoPorMim; }
    public boolean isFavoritadoPorMim() { return favoritadoPorMim; }
    public String getCriadoEm() { return criadoEm; }

    public void setCurtidoPorMim(boolean curtidoPorMim) { this.curtidoPorMim = curtidoPorMim; }
    public void setFavoritadoPorMim(boolean favoritadoPorMim) { this.favoritadoPorMim = favoritadoPorMim; }
    public void setTotalCurtidas(long totalCurtidas) { this.totalCurtidas = totalCurtidas; }
}
