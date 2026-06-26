package com.foodbook.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record IngredienteItemRequest(

        @NotNull(message = "ID do ingrediente é obrigatório")
        Long ingredienteId,

        @NotNull(message = "Quantidade é obrigatória")
        @DecimalMin(value = "0.001", message = "Quantidade deve ser maior que zero")
        BigDecimal quantidade,

        @NotBlank(message = "Unidade é obrigatória")
        @Size(max = 30, message = "Unidade deve ter no máximo 30 caracteres")
        String unidade,

        @Size(max = 200, message = "Observação deve ter no máximo 200 caracteres")
        String observacao
) {}
