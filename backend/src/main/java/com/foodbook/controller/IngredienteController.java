package com.foodbook.controller;

import com.foodbook.dto.response.ApiResponse;
import com.foodbook.dto.response.IngredienteResponse;
import com.foodbook.service.IngredienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ingredientes")
@RequiredArgsConstructor
@Tag(name = "Ingredientes", description = "Busca de ingredientes para autocomplete")
public class IngredienteController {

    private final IngredienteService ingredienteService;

    @GetMapping("/buscar")
    @Operation(summary = "Buscar ingredientes por nome (autocomplete)")
    public ResponseEntity<ApiResponse<List<IngredienteResponse>>> buscar(
            @RequestParam String termo) {
        return ResponseEntity.ok(ApiResponse.ok(ingredienteService.buscarPorTermo(termo)));
    }
}
