package com.foodbook.dto;

public class UsuarioDto {
    private Long id;
    private String nome;
    private String email;
    private String fotoUrl;
    private String criadoEm;

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getFotoUrl() { return fotoUrl; }
    public String getCriadoEm() { return criadoEm; }
}
