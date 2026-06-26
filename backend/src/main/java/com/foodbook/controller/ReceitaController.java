package com.foodbook.controller;

import com.foodbook.dto.request.ReceitaRequest;
import com.foodbook.dto.response.ApiResponse;
import com.foodbook.dto.response.PageResponse;
import com.foodbook.dto.response.ReceitaDetalheResponse;
import com.foodbook.dto.response.ReceitaResumoResponse;
import com.foodbook.entity.Usuario;
import com.foodbook.service.ReceitaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/receitas")
@RequiredArgsConstructor
@Tag(name = "Receitas", description = "CRUD de receitas culinárias")
public class ReceitaController {

    private final ReceitaService receitaService;

    @GetMapping
    @Operation(summary = "Listar feed de receitas paginado")
    public ResponseEntity<ApiResponse<PageResponse<ReceitaResumoResponse>>> listarFeed(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Long usuarioId = usuario != null ? usuario.getId() : null;
        Pageable pageable = PageRequest.of(page, size, Sort.by("criadoEm").descending());
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(receitaService.listarFeed(usuarioId, pageable))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar receita por ID")
    public ResponseEntity<ApiResponse<ReceitaDetalheResponse>> buscarPorId(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {

        Long usuarioId = usuario != null ? usuario.getId() : null;
        return ResponseEntity.ok(ApiResponse.ok(receitaService.buscarPorId(id, usuarioId)));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar receitas de um usuário")
    public ResponseEntity<ApiResponse<PageResponse<ReceitaResumoResponse>>> listarPorUsuario(
            @PathVariable Long usuarioId,
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Long autenticadoId = usuario != null ? usuario.getId() : null;
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(receitaService.listarPorUsuario(usuarioId, autenticadoId, pageable))));
    }

    @GetMapping("/categoria/{categoriaId}")
    @Operation(summary = "Listar receitas por categoria")
    public ResponseEntity<ApiResponse<PageResponse<ReceitaResumoResponse>>> listarPorCategoria(
            @PathVariable Long categoriaId,
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Long usuarioId = usuario != null ? usuario.getId() : null;
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(receitaService.listarPorCategoria(categoriaId, usuarioId, pageable))));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar receitas por título")
    public ResponseEntity<ApiResponse<PageResponse<ReceitaResumoResponse>>> buscarPorTitulo(
            @RequestParam String titulo,
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Long usuarioId = usuario != null ? usuario.getId() : null;
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.ok(
                PageResponse.from(receitaService.buscarPorTitulo(titulo, usuarioId, pageable))));
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Criar nova receita")
    public ResponseEntity<ApiResponse<ReceitaDetalheResponse>> criar(
            @Valid @RequestBody ReceitaRequest request,
            @AuthenticationPrincipal Usuario usuario) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Receita criada com sucesso", receitaService.criar(request, usuario)));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Atualizar receita")
    public ResponseEntity<ApiResponse<ReceitaDetalheResponse>> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ReceitaRequest request,
            @AuthenticationPrincipal Usuario usuario) {

        return ResponseEntity.ok(ApiResponse.ok(receitaService.atualizar(id, request, usuario.getId())));
    }

    @PatchMapping("/{id}/imagem")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Atualizar imagem da receita")
    public ResponseEntity<ApiResponse<ReceitaDetalheResponse>> atualizarImagem(
            @PathVariable Long id,
            @RequestParam("imagem") MultipartFile imagem,
            @AuthenticationPrincipal Usuario usuario) {

        return ResponseEntity.ok(ApiResponse.ok(receitaService.atualizarImagem(id, imagem, usuario.getId())));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Excluir receita")
    public ResponseEntity<ApiResponse<Void>> excluir(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario) {

        receitaService.excluir(id, usuario.getId());
        return ResponseEntity.ok(ApiResponse.ok("Receita excluída com sucesso"));
    }
}
