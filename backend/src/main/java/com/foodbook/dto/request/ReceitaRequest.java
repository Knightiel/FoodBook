package com.foodbook.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record ReceitaRequest(

        @NotBlank(message = "Título é obrigatório")
        @Size(min = 3, max = 200, message = "Título deve ter entre 3 e 200 caracteres")
        String titulo,

        @Size(max = 2000, message = "Descrição deve ter no máximo 2000 caracteres")
        String descricao,

        @NotBlank(message = "Modo de preparo é obrigatório")
        String modoPreparo,

        @NotNull(message = "Tempo de preparo é obrigatório")
        @Min(value = 1, message = "Tempo de preparo deve ser pelo menos 1 minuto")
        @Max(value = 10000, message = "Tempo de preparo inválido")
        Integer tempoPreparo,

        @NotNull(message = "Número de porções é obrigatório")
        @Min(value = 1, message = "Deve ter pelo menos 1 porção")
        @Max(value = 100, message = "Número de porções inválido")
        Integer porcoes,

        @NotNull(message = "Categoria é obrigatória")
        Long categoriaId,

        @NotEmpty(message = "A receita deve ter pelo menos um ingrediente")
        @Valid
        List<IngredienteItemRequest> ingredientes
) {}
