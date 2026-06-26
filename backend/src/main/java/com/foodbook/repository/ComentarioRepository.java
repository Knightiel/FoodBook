package com.foodbook.repository;

import com.foodbook.entity.Comentario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    Page<Comentario> findByReceitaIdOrderByCriadoEmDesc(Long receitaId, Pageable pageable);

    long countByReceitaId(Long receitaId);
}
