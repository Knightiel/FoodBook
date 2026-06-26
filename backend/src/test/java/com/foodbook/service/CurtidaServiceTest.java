package com.foodbook.service;

import com.foodbook.dto.response.CurtidaResponse;
import com.foodbook.entity.*;
import com.foodbook.exception.BusinessException;
import com.foodbook.repository.CurtidaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurtidaServiceTest {

    @Mock private CurtidaRepository curtidaRepository;
    @Mock private ReceitaService receitaService;

    @InjectMocks private CurtidaService curtidaService;

    private Usuario usuario;
    private Receita receita;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder().id(1L).email("u@email.com").build();
        receita = Receita.builder().id(10L).usuario(usuario).build();
    }

    @Test
    void curtir_deveSalvar_quandoNaoExisteCurtida() {
        when(receitaService.buscarEntidade(10L)).thenReturn(receita);
        when(curtidaRepository.existsById(any())).thenReturn(false);
        when(curtidaRepository.countByIdReceitaId(10L)).thenReturn(1L);

        CurtidaResponse response = curtidaService.curtir(10L, usuario);

        assertThat(response.curtidoPorMim()).isTrue();
        assertThat(response.totalCurtidas()).isEqualTo(1L);
        verify(curtidaRepository).save(any(Curtida.class));
    }

    @Test
    void curtir_deveLancarExcecao_quandoJaCurtiu() {
        when(receitaService.buscarEntidade(10L)).thenReturn(receita);
        when(curtidaRepository.existsById(any())).thenReturn(true);

        assertThatThrownBy(() -> curtidaService.curtir(10L, usuario))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("já curtiu");

        verify(curtidaRepository, never()).save(any());
    }

    @Test
    void removerCurtida_deveDeletar_quandoExisteCurtida() {
        when(receitaService.buscarEntidade(10L)).thenReturn(receita);
        when(curtidaRepository.existsById(any())).thenReturn(true);
        when(curtidaRepository.countByIdReceitaId(10L)).thenReturn(0L);

        CurtidaResponse response = curtidaService.removerCurtida(10L, usuario);

        assertThat(response.curtidoPorMim()).isFalse();
        verify(curtidaRepository).deleteById(any());
    }

    @Test
    void removerCurtida_deveLancarExcecao_quandoNaoCurtiu() {
        when(receitaService.buscarEntidade(10L)).thenReturn(receita);
        when(curtidaRepository.existsById(any())).thenReturn(false);

        assertThatThrownBy(() -> curtidaService.removerCurtida(10L, usuario))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("não curtiu");

        verify(curtidaRepository, never()).deleteById(any());
    }
}
