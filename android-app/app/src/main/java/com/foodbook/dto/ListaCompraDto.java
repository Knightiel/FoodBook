package com.foodbook.dto;

import java.util.List;

public class ListaCompraDto {
    private Long id;
    private String nome;
    private List<ItemListaCompraDto> itens;
    private String criadoEm;

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public List<ItemListaCompraDto> getItens() { return itens; }
    public String getCriadoEm() { return criadoEm; }
}
