package com.foodbook.dto;

import java.math.BigDecimal;

public class IngredienteItemDto {
    private final Long ingredienteId;
    private final BigDecimal quantidade;
    private final String unidade;
    private final String observacao;

    public IngredienteItemDto(Long ingredienteId, BigDecimal quantidade, String unidade, String observacao) {
        this.ingredienteId = ingredienteId;
        this.quantidade = quantidade;
        this.unidade = unidade;
        this.observacao = observacao;
    }

    public Long getIngredienteId() { return ingredienteId; }
    public BigDecimal getQuantidade() { return quantidade; }
    public String getUnidade() { return unidade; }
    public String getObservacao() { return observacao; }
}
