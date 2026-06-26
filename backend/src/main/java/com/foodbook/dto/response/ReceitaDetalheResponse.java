package com.foodbook.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ReceitaDetalheResponse(
        Long id,
        String titulo,
        String descricao,
        String modoPreparo,
        String imagemUrl,
        Integer tempoPreparo,
        Integer porcoes,
        CategoriaResponse categoria,
        UsuarioResponse autor,
        List<ReceitaIngredienteResponse> ingredientes,
        long totalCurtidas,
        long totalComentarios,
        boolean curtidoPorMim,
        boolean favoritadoPorMim,
        boolean origemExterna,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {}
