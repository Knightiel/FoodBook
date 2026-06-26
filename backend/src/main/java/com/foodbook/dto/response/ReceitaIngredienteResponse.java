package com.foodbook.dto.response;

import java.math.BigDecimal;

public record ReceitaIngredienteResponse(
        Long ingredienteId,
        String nomeIngrediente,
        BigDecimal quantidade,
        String unidade,
        String observacao
) {}
