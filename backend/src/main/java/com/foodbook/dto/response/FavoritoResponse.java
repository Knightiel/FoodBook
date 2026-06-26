package com.foodbook.dto.response;

public record FavoritoResponse(
        Long receitaId,
        boolean favoritadoPorMim
) {}
