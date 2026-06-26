package com.foodbook.dto.response;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        String fotoUrl,
        LocalDateTime criadoEm
) {}
