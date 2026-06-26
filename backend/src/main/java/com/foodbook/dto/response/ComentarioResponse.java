package com.foodbook.dto.response;

import java.time.LocalDateTime;

public record ComentarioResponse(
        Long id,
        String texto,
        UsuarioResponse autor,
        Long receitaId,
        LocalDateTime criadoEm
) {}
