package com.foodbook.dto;

public class LoginRequestDto {
    private final String email;
    private final String senha;

    public LoginRequestDto(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }

    public String getEmail() { return email; }
    public String getSenha() { return senha; }
}
