package com.foodbook.dto.response;

import java.time.LocalDateTime;

public record UsuarioPerfilResponse(
        Long id,
        String nome,
        String email,
        String fotoUrl,
        long totalReceitas,
        long totalCurtidasRecebidas,
        long totalFavoritos,
        LocalDateTime criadoEm
) {}
