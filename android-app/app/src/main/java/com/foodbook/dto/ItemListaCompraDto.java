package com.foodbook.dto;

import java.math.BigDecimal;

public class ItemListaCompraDto {
    private Long id;
    private Long ingredienteId;
    private String nomeIngrediente;
    private BigDecimal quantidade;
    private String unidade;
    private boolean comprado;

    public Long getId() { return id; }
    public Long getIngredienteId() { return ingredienteId; }
    public String getNomeIngrediente() { return nomeIngrediente; }
    public BigDecimal getQuantidade() { return quantidade; }
    public String getUnidade() { return unidade; }
    public boolean isComprado() { return comprado; }
    public void setComprado(boolean comprado) { this.comprado = comprado; }
}
