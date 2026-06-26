package com.foodbook.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "receita", indexes = {
        @Index(name = "idx_receita_usuario",   columnList = "usuario_id"),
        @Index(name = "idx_receita_categoria", columnList = "categoria_id"),
        @Index(name = "idx_receita_criado_em", columnList = "criado_em")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Receita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "modo_preparo", nullable = false, columnDefinition = "TEXT")
    private String modoPreparo;

    @Column(name = "tempo_preparo", nullable = false)
    private Integer tempoPreparo;

    @Column(nullable = false)
    private Integer porcoes;

    @Column(name = "imagem_url", length = 500)
    private String imagemUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "origem_externa", nullable = false)
    @Builder.Default
    private Boolean origemExterna = false;

    @OneToMany(mappedBy = "receita", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReceitaIngrediente> ingredientes = new ArrayList<>();

    @OneToMany(mappedBy = "receita", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comentario> comentarios = new ArrayList<>();

    @OneToMany(mappedBy = "receita", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Curtida> curtidas = new ArrayList<>();

    @OneToMany(mappedBy = "receita", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Favorito> favoritos = new ArrayList<>();

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
        atualizadoEm = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        atualizadoEm = LocalDateTime.now();
    }

    public int getTotalCurtidas() {
        return curtidas != null ? curtidas.size() : 0;
    }

    public int getTotalComentarios() {
        return comentarios != null ? comentarios.size() : 0;
    }
}
