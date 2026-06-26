package com.foodbook.repository;

import com.foodbook.entity.ListaCompra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListaCompraRepository extends JpaRepository<ListaCompra, Long> {

    Page<ListaCompra> findByUsuarioIdOrderByCriadoEmDesc(Long usuarioId, Pageable pageable);
}
