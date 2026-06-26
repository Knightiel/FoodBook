package com.foodbook.repository;

import com.foodbook.dto.*;
import com.foodbook.network.ApiClient;
import com.foodbook.network.ApiService;

import java.util.List;

public class ReceitaRepository extends BaseRepository {

    private static ReceitaRepository instance;
    private final ApiService apiService;

    private ReceitaRepository() {
        apiService = ApiClient.createService(ApiService.class);
    }

    public static ReceitaRepository getInstance() {
        if (instance == null) instance = new ReceitaRepository();
        return instance;
    }

    public void buscarFeed(int page, int size, RepositoryCallback<PageResponseDto<ReceitaResumoDto>> cb) {
        enqueue(apiService.feed(page, size), cb);
    }

    public void buscarDetalhe(long id, RepositoryCallback<ReceitaDetalheDto> cb) {
        enqueue(apiService.detalheReceita(id), cb);
    }

    public void buscarPorUsuario(long usuarioId, int page, int size,
                                  RepositoryCallback<PageResponseDto<ReceitaResumoDto>> cb) {
        enqueue(apiService.receitasDoUsuario(usuarioId, page, size), cb);
    }

    public void curtir(long receitaId, RepositoryCallback<CurtidaDto> cb) {
        enqueue(apiService.curtir(receitaId), cb);
    }

    public void removerCurtida(long receitaId, RepositoryCallback<CurtidaDto> cb) {
        enqueue(apiService.removerCurtida(receitaId), cb);
    }

    public void favoritar(long receitaId, RepositoryCallback<FavoritoDto> cb) {
        enqueue(apiService.favoritar(receitaId), cb);
    }

    public void removerFavorito(long receitaId, RepositoryCallback<FavoritoDto> cb) {
        enqueue(apiService.removerFavorito(receitaId), cb);
    }

    public void buscarComentarios(long receitaId, int page, int size,
                                   RepositoryCallback<PageResponseDto<ComentarioDto>> cb) {
        enqueue(apiService.comentarios(receitaId, page, size), cb);
    }

    public void comentar(long receitaId, String texto, RepositoryCallback<ComentarioDto> cb) {
        enqueue(apiService.comentar(receitaId, new ComentarioRequestDto(texto)), cb);
    }

    public void excluirComentario(long receitaId, long comentarioId, RepositoryCallback<Void> cb) {
        enqueue(apiService.excluirComentario(receitaId, comentarioId), cb);
    }

    public void excluirReceita(long id, RepositoryCallback<Void> cb) {
        enqueue(apiService.excluirReceita(id), cb);
    }
}
