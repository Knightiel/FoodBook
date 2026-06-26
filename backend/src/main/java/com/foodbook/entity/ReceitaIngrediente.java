package com.foodbook.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "receita_ingrediente", indexes = {
        @Index(name = "idx_ri_ingrediente", columnList = "ingrediente_id")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceitaIngrediente {

    @EmbeddedId
    private ReceitaIngredienteId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("receitaId")
    @JoinColumn(name = "receita_id", nullable = false)
    private Receita receita;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("ingredienteId")
    @JoinColumn(name = "ingrediente_id", nullable = false)
    private Ingrediente ingrediente;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal quantidade;

    @Column(nullable = false, length = 30)
    private String unidade;

    @Column(length = 200)
    private String observacao;
}
