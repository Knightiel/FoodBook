package com.foodbook.service;

import com.foodbook.dto.request.IngredienteItemRequest;
import com.foodbook.dto.request.ReceitaRequest;
import com.foodbook.dto.response.ReceitaDetalheResponse;
import com.foodbook.dto.response.ReceitaResumoResponse;
import com.foodbook.entity.*;
import com.foodbook.exception.BusinessException;
import com.foodbook.mapper.ReceitaMapper;
import com.foodbook.repository.*;
import com.foodbook.util.ImagemUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceitaService {

    private final ReceitaRepository receitaRepository;
    private final CategoriaService categoriaService;
    private final IngredienteService ingredienteService;
    private final CurtidaRepository curtidaRepository;
    private final FavoritoRepository favoritoRepository;
    private final ReceitaMapper receitaMapper;
    private final ImagemUtil imagemUtil;

    @Transactional(readOnly = true)
    public Page<ReceitaResumoResponse> listarFeed(Long usuarioId, Pageable pageable) {
        return receitaRepository.findAllByOrderByCriadoEmDesc(pageable)
                .map(r -> enriquecerResumo(receitaMapper.toResumoResponse(r), r.getId(), usuarioId));
    }

    @Transactional(readOnly = true)
    public ReceitaDetalheResponse buscarPorId(Long receitaId, Long usuarioId) {
        Receita receita = buscarEntidade(receitaId);
        ReceitaDetalheResponse response = receitaMapper.toDetalheResponse(receita);
        return enriquecerDetalhe(response, receitaId, usuarioId);
    }

    @Transactional(readOnly = true)
    public Page<ReceitaResumoResponse> listarPorUsuario(Long autorId, Long usuarioId, Pageable pageable) {
        return receitaRepository.findByUsuarioIdOrderByCriadoEmDesc(autorId, pageable)
                .map(r -> enriquecerResumo(receitaMapper.toResumoResponse(r), r.getId(), usuarioId));
    }

    @Transactional(readOnly = true)
    public Page<ReceitaResumoResponse> listarPorCategoria(Long categoriaId, Long usuarioId, Pageable pageable) {
        return receitaRepository.findByCategoriaIdOrderByCriadoEmDesc(categoriaId, pageable)
                .map(r -> enriquecerResumo(receitaMapper.toResumoResponse(r), r.getId(), usuarioId));
    }

    @Transactional(readOnly = true)
    public Page<ReceitaResumoResponse> buscarPorTitulo(String titulo, Long usuarioId, Pageable pageable) {
        return receitaRepository.buscarPorTitulo(titulo, pageable)
                .map(r -> enriquecerResumo(receitaMapper.toResumoResponse(r), r.getId(), usuarioId));
    }

    @Transactional(readOnly = true)
    public Page<ReceitaResumoResponse> buscarPorIngredientes(
            List<Long> ingredienteIds, boolean exato, Long usuarioId, Pageable pageable) {

        if (ingredienteIds == null || ingredienteIds.isEmpty()) {
            throw BusinessException.badRequest("Informe pelo menos um ingrediente");
        }

        Page<Receita> pagina = exato
                ? receitaRepository.buscarPorIngredientesExato(ingredienteIds, ingredienteIds.size(), pageable)
                : receitaRepository.buscarPorIngredientes(ingredienteIds, pageable);

        return pagina.map(r -> enriquecerResumo(receitaMapper.toResumoResponse(r), r.getId(), usuarioId));
    }

    @Transactional
    public ReceitaDetalheResponse criar(ReceitaRequest request, Usuario autor) {
        Categoria categoria = categoriaService.buscarEntidade(request.categoriaId());

        Receita receita = Receita.builder()
                .titulo(request.titulo().trim())
                .descricao(request.descricao())
                .modoPreparo(request.modoPreparo())
                .tempoPreparo(request.tempoPreparo())
                .porcoes(request.porcoes())
                .categoria(categoria)
                .usuario(autor)
                .origemExterna(false)
                .build();

        adicionarIngredientes(receita, request.ingredientes());
        receitaRepository.save(receita);

        log.info("Receita '{}' criada por usuário {}", receita.getTitulo(), autor.getId());
        return buscarPorId(receita.getId(), autor.getId());
    }

    @Transactional
    public ReceitaDetalheResponse atualizar(Long receitaId, ReceitaRequest request, Long usuarioId) {
        Receita receita = buscarEntidade(receitaId);
        validarProprietario(receita, usuarioId);

        receita.setTitulo(request.titulo().trim());
        receita.setDescricao(request.descricao());
        receita.setModoPreparo(request.modoPreparo());
        receita.setTempoPreparo(request.tempoPreparo());
        receita.setPorcoes(request.porcoes());
        receita.setCategoria(categoriaService.buscarEntidade(request.categoriaId()));

        receita.getIngredientes().clear();
        adicionarIngredientes(receita, request.ingredientes());

        receitaRepository.save(receita);
        return buscarPorId(receitaId, usuarioId);
    }

    @Transactional
    public ReceitaDetalheResponse atualizarImagem(Long receitaId, MultipartFile imagem, Long usuarioId) {
        Receita receita = buscarEntidade(receitaId);
        validarProprietario(receita, usuarioId);

        if (receita.getImagemUrl() != null) {
            imagemUtil.excluir(receita.getImagemUrl());
        }

        String nomeArquivo = imagemUtil.salvar(imagem);
        receita.setImagemUrl(nomeArquivo);
        receitaRepository.save(receita);

        return buscarPorId(receitaId, usuarioId);
    }

    @Transactional
    public void excluir(Long receitaId, Long usuarioId) {
        Receita receita = buscarEntidade(receitaId);
        validarProprietario(receita, usuarioId);

        if (receita.getImagemUrl() != null) {
            imagemUtil.excluir(receita.getImagemUrl());
        }

        receitaRepository.delete(receita);
        log.info("Receita {} excluída pelo usuário {}", receitaId, usuarioId);
    }

    public Receita buscarEntidade(Long id) {
        return receitaRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("Receita"));
    }

    private void adicionarIngredientes(Receita receita, List<IngredienteItemRequest> itens) {
        itens.forEach(item -> {
            Ingrediente ingrediente = ingredienteService.buscarPorId(item.ingredienteId());
            ReceitaIngredienteId id = new ReceitaIngredienteId(receita.getId(), ingrediente.getId());
            ReceitaIngrediente ri = ReceitaIngrediente.builder()
                    .id(id)
                    .receita(receita)
                    .ingrediente(ingrediente)
                    .quantidade(item.quantidade())
                    .unidade(item.unidade())
                    .observacao(item.observacao())
                    .build();
            receita.getIngredientes().add(ri);
        });
    }

    private ReceitaResumoResponse enriquecerResumo(ReceitaResumoResponse r, Long receitaId, Long usuarioId) {
        if (usuarioId == null) return r;
        boolean curtido = curtidaRepository.existsByIdUsuarioIdAndIdReceitaId(usuarioId, receitaId);
        boolean favoritado = favoritoRepository.existsByIdUsuarioIdAndIdReceitaId(usuarioId, receitaId);
        return new ReceitaResumoResponse(
                r.id(), r.titulo(), r.descricao(), r.imagemUrl(),
                r.tempoPreparo(), r.porcoes(), r.categoria(), r.autor(),
                r.totalCurtidas(), r.totalComentarios(),
                curtido, favoritado, r.criadoEm()
        );
    }

    private ReceitaDetalheResponse enriquecerDetalhe(ReceitaDetalheResponse r, Long receitaId, Long usuarioId) {
        if (usuarioId == null) return r;
        boolean curtido = curtidaRepository.existsByIdUsuarioIdAndIdReceitaId(usuarioId, receitaId);
        boolean favoritado = favoritoRepository.existsByIdUsuarioIdAndIdReceitaId(usuarioId, receitaId);
        return new ReceitaDetalheResponse(
                r.id(), r.titulo(), r.descricao(), r.modoPreparo(), r.imagemUrl(),
                r.tempoPreparo(), r.porcoes(), r.categoria(), r.autor(), r.ingredientes(),
                r.totalCurtidas(), r.totalComentarios(),
                curtido, favoritado, r.origemExterna(), r.criadoEm(), r.atualizadoEm()
        );
    }

    private void validarProprietario(Receita receita, Long usuarioId) {
        if (!receita.getUsuario().getId().equals(usuarioId)) {
            throw BusinessException.forbidden("Você não tem permissão para modificar esta receita");
        }
    }
}
