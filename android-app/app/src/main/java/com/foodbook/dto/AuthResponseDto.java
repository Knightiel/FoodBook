package com.foodbook.dto;

public class AuthResponseDto {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private UsuarioDto usuario;

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public String getTokenType() { return tokenType; }
    public UsuarioDto getUsuario() { return usuario; }
}
