package com.foodbook.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ComentarioRequest(

        @NotBlank(message = "Texto do comentário é obrigatório")
        @Size(min = 1, max = 1000, message = "Comentário deve ter entre 1 e 1000 caracteres")
        String texto
) {}
