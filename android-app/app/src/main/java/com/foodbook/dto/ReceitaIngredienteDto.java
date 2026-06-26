package com.foodbook.dto;

import java.math.BigDecimal;

public class ReceitaIngredienteDto {
    private Long ingredienteId;
    private String nomeIngrediente;
    private BigDecimal quantidade;
    private String unidade;
    private String observacao;

    public Long getIngredienteId() { return ingredienteId; }
    public String getNomeIngrediente() { return nomeIngrediente; }
    public BigDecimal getQuantidade() { return quantidade; }
    public String getUnidade() { return unidade; }
    public String getObservacao() { return observacao; }
}
