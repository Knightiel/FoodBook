package com.foodbook.service;

import com.foodbook.dto.request.IngredienteItemRequest;
import com.foodbook.dto.request.ReceitaRequest;
import com.foodbook.entity.*;
import com.foodbook.exception.BusinessException;
import com.foodbook.mapper.ReceitaMapper;
import com.foodbook.repository.*;
import com.foodbook.util.ImagemUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReceitaServiceTest {

    @Mock private ReceitaRepository receitaRepository;
    @Mock private CategoriaService categoriaService;
    @Mock private IngredienteService ingredienteService;
    @Mock private CurtidaRepository curtidaRepository;
    @Mock private FavoritoRepository favoritoRepository;
    @Mock private ReceitaMapper receitaMapper;
    @Mock private ImagemUtil imagemUtil;

    @InjectMocks
    private ReceitaService receitaService;

    private Usuario autor;
    private Receita receita;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        autor = Usuario.builder().id(1L).nome("João").email("joao@email.com").build();
        categoria = Categoria.builder().id(1L).nome("Massas").build();
        receita = Receita.builder()
                .id(10L)
                .titulo("Macarrão")
                .usuario(autor)
                .categoria(categoria)
                .build();
    }

    @Test
    void excluir_deveLancarExcecao_quandoNaoEhProprietario() {
        Long outroUsuarioId = 99L;
        when(receitaRepository.findById(10L)).thenReturn(Optional.of(receita));

        assertThatThrownBy(() -> receitaService.excluir(10L, outroUsuarioId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("permissão");

        verify(receitaRepository, never()).delete(any());
    }

    @Test
    void excluir_deveExcluir_quandoEhProprietario() {
        when(receitaRepository.findById(10L)).thenReturn(Optional.of(receita));

        receitaService.excluir(10L, autor.getId());

        verify(receitaRepository).delete(receita);
    }

    @Test
    void buscarPorIngredientes_deveLancarExcecao_quandoListaVazia() {
        assertThatThrownBy(() -> receitaService.buscarPorIngredientes(List.of(), false, 1L, null))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ingrediente");
    }

    @Test
    void buscarEntidade_deveLancarExcecao_quandoNaoEncontrada() {
        when(receitaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> receitaService.buscarEntidade(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Receita");
    }
}
