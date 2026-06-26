package com.foodbook.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ingrediente", indexes = {
        @Index(name = "idx_ingrediente_nome", columnList = "nome", unique = true)
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ingrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String nome;

    @Column(name = "unidade_padrao", length = 20)
    private String unidadePadrao;
}
