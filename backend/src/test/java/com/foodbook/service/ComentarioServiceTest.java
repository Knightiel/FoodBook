package com.foodbook.service;

import com.foodbook.dto.request.ComentarioRequest;
import com.foodbook.dto.response.ComentarioResponse;
import com.foodbook.entity.*;
import com.foodbook.exception.BusinessException;
import com.foodbook.mapper.ComentarioMapper;
import com.foodbook.repository.ComentarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComentarioServiceTest {

    @Mock private ComentarioRepository comentarioRepository;
    @Mock private ReceitaService receitaService;
    @Mock private ComentarioMapper comentarioMapper;

    @InjectMocks private ComentarioService comentarioService;

    private Usuario autor;
    private Usuario donoReceita;
    private Receita receita;
    private Comentario comentario;

    @BeforeEach
    void setUp() {
        autor = Usuario.builder().id(1L).build();
        donoReceita = Usuario.builder().id(2L).build();
        receita = Receita.builder().id(10L).usuario(donoReceita).build();
        comentario = Comentario.builder()
                .id(100L)
                .texto("Ótima receita!")
                .usuario(autor)
                .receita(receita)
                .build();
    }

    @Test
    void adicionar_deveSalvar_quandoDadosValidos() {
        when(receitaService.buscarEntidade(10L)).thenReturn(receita);
        when(comentarioRepository.save(any())).thenReturn(comentario);
        when(comentarioMapper.toResponse(any())).thenReturn(
                new ComentarioResponse(100L, "Ótima receita!", null, 10L, null));

        ComentarioResponse response = comentarioService.adicionar(10L, new ComentarioRequest("Ótima receita!"), autor);

        assertThat(response.id()).isEqualTo(100L);
        verify(comentarioRepository).save(any(Comentario.class));
    }

    @Test
    void excluir_devePermitir_quandoEhAutor() {
        when(comentarioRepository.findById(100L)).thenReturn(Optional.of(comentario));
        comentarioService.excluir(100L, autor.getId());
        verify(comentarioRepository).delete(comentario);
    }

    @Test
    void excluir_devePermitir_quandoEhDonoReceita() {
        when(comentarioRepository.findById(100L)).thenReturn(Optional.of(comentario));
        comentarioService.excluir(100L, donoReceita.getId());
        verify(comentarioRepository).delete(comentario);
    }

    @Test
    void excluir_deveLancarExcecao_quandoNaoTemPermissao() {
        Long terceiro = 99L;
        when(comentarioRepository.findById(100L)).thenReturn(Optional.of(comentario));

        assertThatThrownBy(() -> comentarioService.excluir(100L, terceiro))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("permissão");

        verify(comentarioRepository, never()).delete(any());
    }
}
