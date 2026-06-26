package com.foodbook.service;

import com.foodbook.dto.response.IngredienteResponse;
import com.foodbook.entity.Ingrediente;
import com.foodbook.exception.BusinessException;
import com.foodbook.mapper.IngredienteMapper;
import com.foodbook.repository.IngredienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredienteService {

    private final IngredienteRepository ingredienteRepository;
    private final IngredienteMapper ingredienteMapper;

    @Transactional(readOnly = true)
    public List<IngredienteResponse> buscarPorTermo(String termo) {
        return ingredienteRepository.buscarPorTermo(termo).stream()
                .map(ingredienteMapper::toResponse)
                .toList();
    }

    @Transactional
    public Ingrediente buscarOuCriar(String nome, String unidade) {
        return ingredienteRepository.findByNomeIgnoreCase(nome.trim())
                .orElseGet(() -> ingredienteRepository.save(
                        Ingrediente.builder()
                                .nome(nome.trim())
                                .unidadePadrao(unidade)
                                .build()
                ));
    }

    public Ingrediente buscarPorId(Long id) {
        return ingredienteRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("Ingrediente"));
    }
}
