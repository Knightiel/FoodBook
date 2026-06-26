package com.foodbook.service;

import com.foodbook.dto.request.ComentarioRequest;
import com.foodbook.dto.response.ComentarioResponse;
import com.foodbook.entity.Comentario;
import com.foodbook.entity.Receita;
import com.foodbook.entity.Usuario;
import com.foodbook.exception.BusinessException;
import com.foodbook.mapper.ComentarioMapper;
import com.foodbook.repository.ComentarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final ReceitaService receitaService;
    private final ComentarioMapper comentarioMapper;

    @Transactional(readOnly = true)
    public Page<ComentarioResponse> listarPorReceita(Long receitaId, Pageable pageable) {
        receitaService.buscarEntidade(receitaId);
        return comentarioRepository.findByReceitaIdOrderByCriadoEmDesc(receitaId, pageable)
                .map(comentarioMapper::toResponse);
    }

    @Transactional
    public ComentarioResponse adicionar(Long receitaId, ComentarioRequest request, Usuario autor) {
        Receita receita = receitaService.buscarEntidade(receitaId);

        Comentario comentario = Comentario.builder()
                .texto(request.texto().trim())
                .usuario(autor)
                .receita(receita)
                .build();

        comentarioRepository.save(comentario);
        log.info("Comentário adicionado na receita {} pelo usuário {}", receitaId, autor.getId());
        return comentarioMapper.toResponse(comentario);
    }

    @Transactional
    public void excluir(Long comentarioId, Long usuarioId) {
        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> BusinessException.notFound("Comentário"));

        boolean ehAutor = comentario.getUsuario().getId().equals(usuarioId);
        boolean ehDonoReceita = comentario.getReceita().getUsuario().getId().equals(usuarioId);

        if (!ehAutor && !ehDonoReceita) {
            throw BusinessException.forbidden("Você não tem permissão para excluir este comentário");
        }

        comentarioRepository.delete(comentario);
        log.info("Comentário {} excluído pelo usuário {}", comentarioId, usuarioId);
    }
}
