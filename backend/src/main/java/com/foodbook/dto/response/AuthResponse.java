package com.foodbook.dto.response;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        UsuarioResponse usuario
) {
    public static AuthResponse of(String accessToken, String refreshToken, UsuarioResponse usuario) {
        return new AuthResponse(accessToken, refreshToken, "Bearer", usuario);
    }
}
