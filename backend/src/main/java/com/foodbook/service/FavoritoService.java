package com.foodbook.service;

import com.foodbook.dto.response.FavoritoResponse;
import com.foodbook.dto.response.PageResponse;
import com.foodbook.dto.response.ReceitaResumoResponse;
import com.foodbook.entity.Favorito;
import com.foodbook.entity.FavoritoId;
import com.foodbook.entity.Receita;
import com.foodbook.entity.Usuario;
import com.foodbook.exception.BusinessException;
import com.foodbook.mapper.ReceitaMapper;
import com.foodbook.repository.CurtidaRepository;
import com.foodbook.repository.FavoritoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoritoService {

    private final FavoritoRepository favoritoRepository;
    private final CurtidaRepository curtidaRepository;
    private final ReceitaService receitaService;
    private final ReceitaMapper receitaMapper;

    @Transactional
    public FavoritoResponse favoritar(Long receitaId, Usuario usuario) {
        Receita receita = receitaService.buscarEntidade(receitaId);
        FavoritoId id = new FavoritoId(usuario.getId(), receitaId);

        if (favoritoRepository.existsById(id)) {
            throw BusinessException.conflict("Receita já está nos seus favoritos");
        }

        favoritoRepository.save(Favorito.builder()
                .id(id)
                .usuario(usuario)
                .receita(receita)
                .build());

        log.info("Receita {} favoritada pelo usuário {}", receitaId, usuario.getId());
        return new FavoritoResponse(receitaId, true);
    }

    @Transactional
    public FavoritoResponse removerFavorito(Long receitaId, Usuario usuario) {
        FavoritoId id = new FavoritoId(usuario.getId(), receitaId);

        if (!favoritoRepository.existsById(id)) {
            throw BusinessException.badRequest("Receita não está nos seus favoritos");
        }

        favoritoRepository.deleteById(id);

        log.info("Favorito removido da receita {} pelo usuário {}", receitaId, usuario.getId());
        return new FavoritoResponse(receitaId, false);
    }

    @Transactional(readOnly = true)
    public PageResponse<ReceitaResumoResponse> listarFavoritos(Long usuarioId, Pageable pageable) {
        Page<ReceitaResumoResponse> pagina = favoritoRepository
                .findReceitasFavoritasByUsuarioId(usuarioId, pageable)
                .map(receita -> {
                    ReceitaResumoResponse resumo = receitaMapper.toResumoResponse(receita);
                    boolean curtido = curtidaRepository
                            .existsByIdUsuarioIdAndIdReceitaId(usuarioId, receita.getId());
                    return new ReceitaResumoResponse(
                            resumo.id(), resumo.titulo(), resumo.descricao(), resumo.imagemUrl(),
                            resumo.tempoPreparo(), resumo.porcoes(), resumo.categoria(), resumo.autor(),
                            resumo.totalCurtidas(), resumo.totalComentarios(),
                            curtido, true, resumo.criadoEm()
                    );
                });
        return PageResponse.from(pagina);
    }
}
