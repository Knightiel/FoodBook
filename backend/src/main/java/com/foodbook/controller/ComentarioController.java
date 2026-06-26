package com.foodbook.controller;

import com.foodbook.dto.request.ComentarioRequest;
import com.foodbook.dto.response.ApiResponse;
import com.foodbook.dto.response.ComentarioResponse;
import com.foodbook.dto.response.PageResponse;
import com.foodbook.entity.Usuario;
import com.foodbook.service.ComentarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/receitas/{receitaId}/comentarios")
@RequiredArgsConstructor
@Tag(name = "Comentários", description = "Comentários em receitas")
public class ComentarioController {

    private final ComentarioService comentarioService;

    @GetMapping
    @Operation(summary = "Listar comentários de uma receita")
    public ResponseEntity<ApiResponse<PageResponse<ComentarioResponse>>> listar(
            @PathVariable Long receitaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(comentarioService.listarPorReceita(receitaId, pageable))));
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Adicionar comentário")
    public ResponseEntity<ApiResponse<ComentarioResponse>> adicionar(
            @PathVariable Long receitaId,
            @Valid @RequestBody ComentarioRequest request,
            @AuthenticationPrincipal Usuario usuario) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(comentarioService.adicionar(receitaId, request, usuario)));
    }

    @DeleteMapping("/{comentarioId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Excluir comentário (autor ou dono da receita)")
    public ResponseEntity<ApiResponse<Void>> excluir(
            @PathVariable Long receitaId,
            @PathVariable Long comentarioId,
            @AuthenticationPrincipal Usuario usuario) {

        comentarioService.excluir(comentarioId, usuario.getId());
        return ResponseEntity.ok(ApiResponse.ok("Comentário excluído"));
    }
}
