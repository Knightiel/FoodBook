package com.foodbook.dto;

public class RegisterRequestDto {
    private final String nome;
    private final String email;
    private final String senha;
    private final String confirmarSenha;

    public RegisterRequestDto(String nome, String email, String senha, String confirmarSenha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.confirmarSenha = confirmarSenha;
    }

    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public String getConfirmarSenha() { return confirmarSenha; }
}
