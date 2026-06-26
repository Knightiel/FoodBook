package com.foodbook.dto.response;

public record CurtidaResponse(
        Long receitaId,
        long totalCurtidas,
        boolean curtidoPorMim
) {}
