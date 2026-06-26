package com.foodbook.dto;

public class ComentarioRequestDto {
    private final String texto;
    public ComentarioRequestDto(String texto) { this.texto = texto; }
    public String getTexto() { return texto; }
}
