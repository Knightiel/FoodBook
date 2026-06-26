package com.foodbook.service;

import com.foodbook.dto.response.CurtidaResponse;
import com.foodbook.entity.Curtida;
import com.foodbook.entity.CurtidaId;
import com.foodbook.entity.Receita;
import com.foodbook.entity.Usuario;
import com.foodbook.exception.BusinessException;
import com.foodbook.repository.CurtidaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurtidaService {

    private final CurtidaRepository curtidaRepository;
    private final ReceitaService receitaService;

    @Transactional
    public CurtidaResponse curtir(Long receitaId, Usuario usuario) {
        Receita receita = receitaService.buscarEntidade(receitaId);
        CurtidaId id = new CurtidaId(usuario.getId(), receitaId);

        if (curtidaRepository.existsById(id)) {
            throw BusinessException.conflict("Você já curtiu esta receita");
        }

        curtidaRepository.save(Curtida.builder()
                .id(id)
                .usuario(usuario)
                .receita(receita)
                .build());

        long total = curtidaRepository.countByIdReceitaId(receitaId);
        log.info("Receita {} curtida pelo usuário {}", receitaId, usuario.getId());
        return new CurtidaResponse(receitaId, total, true);
    }

    @Transactional
    public CurtidaResponse removerCurtida(Long receitaId, Usuario usuario) {
        CurtidaId id = new CurtidaId(usuario.getId(), receitaId);

        if (!curtidaRepository.existsById(id)) {
            throw BusinessException.badRequest("Você não curtiu esta receita");
        }

        curtidaRepository.deleteById(id);

        long total = curtidaRepository.countByIdReceitaId(receitaId);
        log.info("Curtida removida da receita {} pelo usuário {}", receitaId, usuario.getId());
        return new CurtidaResponse(receitaId, total, false);
    }

    @Transactional(readOnly = true)
    public CurtidaResponse buscarStatus(Long receitaId, Long usuarioId) {
        receitaService.buscarEntidade(receitaId);
        long total = curtidaRepository.countByIdReceitaId(receitaId);
        boolean curtido = curtidaRepository.existsByIdUsuarioIdAndIdReceitaId(usuarioId, receitaId);
        return new CurtidaResponse(receitaId, total, curtido);
    }
}
