package com.foodbook.dto;

public class UsuarioPerfilDto {
    private Long id;
    private String nome;
    private String email;
    private String fotoUrl;
    private long totalReceitas;
    private long totalCurtidasRecebidas;
    private long totalFavoritos;
    private String criadoEm;

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getFotoUrl() { return fotoUrl; }
    public long getTotalReceitas() { return totalReceitas; }
    public long getTotalCurtidasRecebidas() { return totalCurtidasRecebidas; }
    public long getTotalFavoritos() { return totalFavoritos; }
    public String getCriadoEm() { return criadoEm; }
}
