package com.foodbook.repository;

import com.foodbook.entity.Receita;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceitaRepository extends JpaRepository<Receita, Long> {

    Page<Receita> findAllByOrderByCriadoEmDesc(Pageable pageable);

    Page<Receita> findByUsuarioIdOrderByCriadoEmDesc(Long usuarioId, Pageable pageable);

    Page<Receita> findByCategoriaIdOrderByCriadoEmDesc(Long categoriaId, Pageable pageable);

    @Query("""
            SELECT r FROM Receita r
            WHERE LOWER(r.titulo) LIKE LOWER(CONCAT('%', :titulo, '%'))
            ORDER BY r.criadoEm DESC
            """)
    long countByUsuarioId(Long usuarioId);

    Page<Receita> buscarPorTitulo(@Param("titulo") String titulo, Pageable pageable);

    /**
     * Busca por ingredientes com ranking de relevância (maior número de ingredientes em comum primeiro).
     * Retorna receitas que possuem ao menos 1 dos ingredientes informados, ordenadas por correspondência.
     */
    @Query(value = """
            SELECT r.*, COUNT(ri.ingrediente_id) AS correspondencias
            FROM receita r
            JOIN receita_ingrediente ri ON r.id = ri.receita_id
            WHERE ri.ingrediente_id IN (:ingredienteIds)
            GROUP BY r.id
            ORDER BY correspondencias DESC, r.criado_em DESC
            """,
            countQuery = """
            SELECT COUNT(DISTINCT r.id)
            FROM receita r
            JOIN receita_ingrediente ri ON r.id = ri.receita_id
            WHERE ri.ingrediente_id IN (:ingredienteIds)
            """,
            nativeQuery = true)
    Page<Receita> buscarPorIngredientes(@Param("ingredienteIds") List<Long> ingredienteIds, Pageable pageable);

    /**
     * Busca exata: retorna somente receitas que contêm TODOS os ingredientes informados.
     */
    @Query(value = """
            SELECT r.*
            FROM receita r
            JOIN receita_ingrediente ri ON r.id = ri.receita_id
            WHERE ri.ingrediente_id IN (:ingredienteIds)
            GROUP BY r.id
            HAVING COUNT(DISTINCT ri.ingrediente_id) = :total
            ORDER BY r.criado_em DESC
            """,
            nativeQuery = true)
    Page<Receita> buscarPorIngredientesExato(
            @Param("ingredienteIds") List<Long> ingredienteIds,
            @Param("total") long total,
            Pageable pageable);
}
