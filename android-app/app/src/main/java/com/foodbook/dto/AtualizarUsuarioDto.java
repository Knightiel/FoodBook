package com.foodbook.dto;

public class AtualizarUsuarioDto {
    private final String nome;
    public AtualizarUsuarioDto(String nome) { this.nome = nome; }
    public String getNome() { return nome; }
}
