package com.foodbook.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categoria", indexes = {
        @Index(name = "idx_categoria_nome", columnList = "nome", unique = true)
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nome;

    @Column(length = 100)
    private String icone;
}
