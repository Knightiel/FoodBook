package com.foodbook.service;

import com.foodbook.dto.request.LoginRequest;
import com.foodbook.dto.request.RegisterRequest;
import com.foodbook.dto.response.AuthResponse;
import com.foodbook.dto.response.UsuarioResponse;
import com.foodbook.entity.Usuario;
import com.foodbook.exception.BusinessException;
import com.foodbook.mapper.UsuarioMapper;
import com.foodbook.repository.UsuarioRepository;
import com.foodbook.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UsuarioMapper usuarioMapper;

    @Transactional
    public AuthResponse registrar(RegisterRequest request) {
        validarCadastro(request);

        Usuario usuario = Usuario.builder()
                .nome(request.nome().trim())
                .email(request.email().toLowerCase().trim())
                .senha(passwordEncoder.encode(request.senha()))
                .build();

        usuarioRepository.save(usuario);
        log.info("Usuário registrado: {}", usuario.getEmail());

        return gerarTokensParaUsuario(usuario);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email().toLowerCase().trim(),
                        request.senha()
                )
        );

        Usuario usuario = usuarioRepository.findByEmail(request.email().toLowerCase().trim())
                .orElseThrow(() -> BusinessException.notFound("Usuário"));

        log.info("Login realizado: {}", usuario.getEmail());
        return gerarTokensParaUsuario(usuario);
    }

    public AuthResponse refreshToken(String refreshToken) {
        final String userEmail = jwtUtil.extractUsername(refreshToken);

        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> BusinessException.notFound("Usuário"));

        if (!jwtUtil.isTokenValid(refreshToken, usuario)) {
            throw BusinessException.badRequest("Refresh token inválido ou expirado");
        }

        String newAccessToken = jwtUtil.generateToken(usuario);
        UsuarioResponse usuarioResponse = usuarioMapper.toResponse(usuario);
        return AuthResponse.of(newAccessToken, refreshToken, usuarioResponse);
    }

    private AuthResponse gerarTokensParaUsuario(Usuario usuario) {
        String accessToken = jwtUtil.generateToken(usuario);
        String refreshToken = jwtUtil.generateRefreshToken(usuario);
        UsuarioResponse usuarioResponse = usuarioMapper.toResponse(usuario);
        return AuthResponse.of(accessToken, refreshToken, usuarioResponse);
    }

    private void validarCadastro(RegisterRequest request) {
        if (!request.senha().equals(request.confirmarSenha())) {
            throw BusinessException.badRequest("As senhas não coincidem");
        }
        if (usuarioRepository.existsByEmail(request.email().toLowerCase().trim())) {
            throw BusinessException.conflict("E-mail já cadastrado");
        }
    }
}
