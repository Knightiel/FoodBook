package com.foodbook.dto.response;

import java.time.LocalDateTime;

public record ReceitaResumoResponse(
        Long id,
        String titulo,
        String descricao,
        String imagemUrl,
        Integer tempoPreparo,
        Integer porcoes,
        CategoriaResponse categoria,
        UsuarioResponse autor,
        long totalCurtidas,
        long totalComentarios,
        boolean curtidoPorMim,
        boolean favoritadoPorMim,
        LocalDateTime criadoEm
) {}
