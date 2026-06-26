package com.foodbook.controller;

import com.foodbook.dto.response.ApiResponse;
import com.foodbook.dto.response.CurtidaResponse;
import com.foodbook.entity.Usuario;
import com.foodbook.service.CurtidaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/receitas/{receitaId}/curtidas")
@RequiredArgsConstructor
@Tag(name = "Curtidas", description = "Curtir e descurtir receitas")
@SecurityRequirement(name = "bearerAuth")
public class CurtidaController {

    private final CurtidaService curtidaService;

    @GetMapping
    @Operation(summary = "Verificar status de curtida do usuário autenticado")
    public ResponseEntity<ApiResponse<CurtidaResponse>> status(
            @PathVariable Long receitaId,
            @AuthenticationPrincipal Usuario usuario) {

        return ResponseEntity.ok(ApiResponse.ok(
                curtidaService.buscarStatus(receitaId, usuario.getId())));
    }

    @PostMapping
    @Operation(summary = "Curtir receita")
    public ResponseEntity<ApiResponse<CurtidaResponse>> curtir(
            @PathVariable Long receitaId,
            @AuthenticationPrincipal Usuario usuario) {

        return ResponseEntity.ok(ApiResponse.ok(curtidaService.curtir(receitaId, usuario)));
    }

    @DeleteMapping
    @Operation(summary = "Remover curtida")
    public ResponseEntity<ApiResponse<CurtidaResponse>> remover(
            @PathVariable Long receitaId,
            @AuthenticationPrincipal Usuario usuario) {

        return ResponseEntity.ok(ApiResponse.ok(curtidaService.removerCurtida(receitaId, usuario)));
    }
}
