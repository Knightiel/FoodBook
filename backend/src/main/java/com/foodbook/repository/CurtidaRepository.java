package com.foodbook.repository;

import com.foodbook.entity.Curtida;
import com.foodbook.entity.CurtidaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurtidaRepository extends JpaRepository<Curtida, CurtidaId> {

    boolean existsByIdUsuarioIdAndIdReceitaId(Long usuarioId, Long receitaId);

    long countByIdReceitaId(Long receitaId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(c) FROM Curtida c WHERE c.receita.usuario.id = :usuarioId")
    long countByReceitaUsuarioId(@org.springframework.data.repository.query.Param("usuarioId") Long usuarioId);
}
