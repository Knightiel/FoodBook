package com.foodbook.dto;

import java.util.List;

public class ReceitaRequestDto {
    private final String titulo;
    private final String descricao;
    private final String modoPreparo;
    private final int tempoPreparo;
    private final int porcoes;
    private final Long categoriaId;
    private final List<IngredienteItemDto> ingredientes;

    public ReceitaRequestDto(String titulo, String descricao, String modoPreparo,
                              int tempoPreparo, int porcoes, Long categoriaId,
                              List<IngredienteItemDto> ingredientes) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.modoPreparo = modoPreparo;
        this.tempoPreparo = tempoPreparo;
        this.porcoes = porcoes;
        this.categoriaId = categoriaId;
        this.ingredientes = ingredientes;
    }

    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public String getModoPreparo() { return modoPreparo; }
    public int getTempoPreparo() { return tempoPreparo; }
    public int getPorcoes() { return porcoes; }
    public Long getCategoriaId() { return categoriaId; }
    public List<IngredienteItemDto> getIngredientes() { return ingredientes; }
}
