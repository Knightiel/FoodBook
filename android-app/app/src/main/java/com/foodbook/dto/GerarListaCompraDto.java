package com.foodbook.dto;

import java.util.List;

public class GerarListaCompraDto {
    private final String nome;
    private final List<Long> receitaIds;

    public GerarListaCompraDto(String nome, List<Long> receitaIds) {
        this.nome = nome;
        this.receitaIds = receitaIds;
    }

    public String getNome() { return nome; }
    public List<Long> getReceitaIds() { return receitaIds; }
}
