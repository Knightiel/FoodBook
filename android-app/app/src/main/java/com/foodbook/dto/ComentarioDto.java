package com.foodbook.dto;

public class ComentarioDto {
    private Long id;
    private String texto;
    private UsuarioDto autor;
    private Long receitaId;
    private String criadoEm;

    public Long getId() { return id; }
    public String getTexto() { return texto; }
    public UsuarioDto getAutor() { return autor; }
    public Long getReceitaId() { return receitaId; }
    public String getCriadoEm() { return criadoEm; }
}
