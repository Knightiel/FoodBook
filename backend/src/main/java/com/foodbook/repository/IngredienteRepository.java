package com.foodbook.repository;

import com.foodbook.entity.Ingrediente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {

    Optional<Ingrediente> findByNomeIgnoreCase(String nome);

    @Query("SELECT i FROM Ingrediente i WHERE LOWER(i.nome) LIKE LOWER(CONCAT('%', :termo, '%'))")
    List<Ingrediente> buscarPorTermo(@Param("termo") String termo);
}
