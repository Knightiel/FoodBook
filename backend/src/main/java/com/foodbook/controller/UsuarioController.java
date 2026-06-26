package com.foodbook.controller;

import com.foodbook.dto.request.AlterarSenhaRequest;
import com.foodbook.dto.request.AtualizarUsuarioRequest;
import com.foodbook.dto.response.ApiResponse;
import com.foodbook.dto.response.UsuarioPerfilResponse;
import com.foodbook.dto.response.UsuarioResponse;
import com.foodbook.entity.Usuario;
import com.foodbook.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Perfil e gerenciamento de conta")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/me")
    @Operation(summary = "Retornar perfil do usuário autenticado")
    public ResponseEntity<ApiResponse<UsuarioPerfilResponse>> meuPerfil(
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.buscarPerfil(usuario.getId())));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar perfil público de um usuário")
    public ResponseEntity<ApiResponse<UsuarioPerfilResponse>> buscarPerfil(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.buscarPerfil(id)));
    }

    @PutMapping("/me")
    @Operation(summary = "Atualizar dados do usuário autenticado")
    public ResponseEntity<ApiResponse<UsuarioResponse>> atualizar(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody AtualizarUsuarioRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.atualizar(usuario.getId(), request)));
    }

    @PatchMapping("/me/foto")
    @Operation(summary = "Atualizar foto de perfil")
    public ResponseEntity<ApiResponse<UsuarioResponse>> atualizarFoto(
            @AuthenticationPrincipal Usuario usuario,
            @RequestParam("foto") MultipartFile foto) {
        return ResponseEntity.ok(ApiResponse.ok(usuarioService.atualizarFoto(usuario.getId(), foto)));
    }

    @PatchMapping("/me/senha")
    @Operation(summary = "Alterar senha")
    public ResponseEntity<ApiResponse<Void>> alterarSenha(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody AlterarSenhaRequest request) {
        usuarioService.alterarSenha(usuario.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok("Senha alterada com sucesso"));
    }

    @DeleteMapping("/me")
    @Operation(summary = "Excluir conta")
    public ResponseEntity<ApiResponse<Void>> excluir(
            @AuthenticationPrincipal Usuario usuario) {
        usuarioService.excluir(usuario.getId());
        return ResponseEntity.ok(ApiResponse.ok("Conta excluída com sucesso"));
    }
}
