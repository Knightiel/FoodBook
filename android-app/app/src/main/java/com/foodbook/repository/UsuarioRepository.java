package com.foodbook.repository;

import com.foodbook.dto.*;
import com.foodbook.network.ApiClient;
import com.foodbook.network.ApiService;

public class UsuarioRepository extends BaseRepository {

    private static UsuarioRepository instance;
    private final ApiService apiService;

    private UsuarioRepository() {
        apiService = ApiClient.createService(ApiService.class);
    }

    public static UsuarioRepository getInstance() {
        if (instance == null) instance = new UsuarioRepository();
        return instance;
    }

    public void buscarMeuPerfil(RepositoryCallback<UsuarioPerfilDto> cb) {
        enqueue(apiService.meuPerfil(), cb);
    }

    public void buscarPerfil(long id, RepositoryCallback<UsuarioPerfilDto> cb) {
        enqueue(apiService.perfil(id), cb);
    }

    public void atualizarPerfil(String nome, RepositoryCallback<UsuarioDto> cb) {
        enqueue(apiService.atualizarPerfil(new AtualizarUsuarioDto(nome)), cb);
    }

    public void buscarFavoritos(int page, int size,
                                 RepositoryCallback<PageResponseDto<ReceitaResumoDto>> cb) {
        enqueue(apiService.meusFavoritos(page, size), cb);
    }
}
