package com.foodbook.repository;

import com.foodbook.entity.Favorito;
import com.foodbook.entity.FavoritoId;
import com.foodbook.entity.Receita;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, FavoritoId> {

    boolean existsByIdUsuarioIdAndIdReceitaId(Long usuarioId, Long receitaId);

    long countByIdUsuarioId(Long usuarioId);

    @Query("SELECT f.receita FROM Favorito f WHERE f.usuario.id = :usuarioId ORDER BY f.criadoEm DESC")
    Page<Receita> findReceitasFavoritasByUsuarioId(@Param("usuarioId") Long usuarioId, Pageable pageable);
}
