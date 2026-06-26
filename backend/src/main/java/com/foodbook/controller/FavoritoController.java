package com.foodbook.controller;

import com.foodbook.dto.response.ApiResponse;
import com.foodbook.dto.response.FavoritoResponse;
import com.foodbook.dto.response.PageResponse;
import com.foodbook.dto.response.ReceitaResumoResponse;
import com.foodbook.entity.Usuario;
import com.foodbook.service.FavoritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Favoritos", description = "Gerenciar receitas favoritas")
@SecurityRequirement(name = "bearerAuth")
public class FavoritoController {

    private final FavoritoService favoritoService;

    @GetMapping("/usuarios/me/favoritos")
    @Operation(summary = "Listar receitas favoritas do usuário autenticado")
    public ResponseEntity<ApiResponse<PageResponse<ReceitaResumoResponse>>> listarFavoritos(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(ApiResponse.ok(
                favoritoService.listarFavoritos(usuario.getId(), PageRequest.of(page, size))));
    }

    @PostMapping("/receitas/{receitaId}/favoritos")
    @Operation(summary = "Adicionar receita aos favoritos")
    public ResponseEntity<ApiResponse<FavoritoResponse>> favoritar(
            @PathVariable Long receitaId,
            @AuthenticationPrincipal Usuario usuario) {

        return ResponseEntity.ok(ApiResponse.ok(favoritoService.favoritar(receitaId, usuario)));
    }

    @DeleteMapping("/receitas/{receitaId}/favoritos")
    @Operation(summary = "Remover receita dos favoritos")
    public ResponseEntity<ApiResponse<FavoritoResponse>> remover(
            @PathVariable Long receitaId,
            @AuthenticationPrincipal Usuario usuario) {

        return ResponseEntity.ok(ApiResponse.ok(favoritoService.removerFavorito(receitaId, usuario)));
    }
}
