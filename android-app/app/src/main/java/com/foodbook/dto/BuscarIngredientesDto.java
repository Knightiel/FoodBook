package com.foodbook.dto;

import java.util.List;

public class BuscarIngredientesDto {
    private final List<Long> ingredienteIds;
    private final boolean exato;

    public BuscarIngredientesDto(List<Long> ingredienteIds, boolean exato) {
        this.ingredienteIds = ingredienteIds;
        this.exato = exato;
    }

    public List<Long> getIngredienteIds() { return ingredienteIds; }
    public boolean isExato() { return exato; }
}
