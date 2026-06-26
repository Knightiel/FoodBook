package com.foodbook.service;

import com.foodbook.dto.request.LoginRequest;
import com.foodbook.dto.request.RegisterRequest;
import com.foodbook.dto.response.AuthResponse;
import com.foodbook.entity.Usuario;
import com.foodbook.exception.BusinessException;
import com.foodbook.mapper.UsuarioMapper;
import com.foodbook.repository.UsuarioRepository;
import com.foodbook.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UsuarioMapper usuarioMapper;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("João Silva", "joao@email.com", "senha123", "senha123");
        usuario = Usuario.builder()
                .id(1L)
                .nome("João Silva")
                .email("joao@email.com")
                .senha("$2a$encoded")
                .build();
    }

    @Test
    void registrar_deveRetornarTokens_quandoDadosValidos() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$encoded");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(jwtUtil.generateToken(any())).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(any())).thenReturn("refresh-token");
        when(usuarioMapper.toResponse(any())).thenReturn(null);

        AuthResponse response = authService.registrar(registerRequest);

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void registrar_deveLancarExcecao_quandoEmailJaCadastrado() {
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        assertThatThrownBy(() -> authService.registrar(registerRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("E-mail já cadastrado");
    }

    @Test
    void registrar_deveLancarExcecao_quandoSenhasNaoCoincidem() {
        RegisterRequest requestInvalido = new RegisterRequest("João", "j@email.com", "senha123", "diferente");

        assertThatThrownBy(() -> authService.registrar(requestInvalido))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("As senhas não coincidem");
    }

    @Test
    void login_deveLancarExcecao_quandoCredenciaisInvalidas() {
        LoginRequest loginRequest = new LoginRequest("joao@email.com", "senhaErrada");
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void login_deveRetornarTokens_quandoCredenciaisValidas() {
        LoginRequest loginRequest = new LoginRequest("joao@email.com", "senha123");
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        when(jwtUtil.generateToken(any())).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(any())).thenReturn("refresh-token");
        when(usuarioMapper.toResponse(any())).thenReturn(null);

        AuthResponse response = authService.login(loginRequest);

        assertThat(response.accessToken()).isEqualTo("access-token");
    }
}
