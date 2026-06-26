package com.foodbook.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.foodbook.dto.*;
import com.foodbook.repository.BaseRepository;
import com.foodbook.repository.ReceitaRepository;
import com.foodbook.utils.Resource;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailViewModel extends AndroidViewModel {

    private final ReceitaRepository receitaRepository;

    private final MutableLiveData<Resource<ReceitaDetalheDto>> receita = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<ComentarioDto>>> comentarios = new MutableLiveData<>();
    private final MutableLiveData<Resource<CurtidaDto>> curtidaResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<FavoritoDto>> favoritoResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<ComentarioDto>> novoComentario = new MutableLiveData<>();
    private final MutableLiveData<Boolean> excluido = new MutableLiveData<>();

    private final List<ComentarioDto> listaComentarios = new ArrayList<>();

    public RecipeDetailViewModel(@NonNull Application application) {
        super(application);
        receitaRepository = ReceitaRepository.getInstance();
    }

    public LiveData<Resource<ReceitaDetalheDto>> getReceita() { return receita; }
    public LiveData<Resource<List<ComentarioDto>>> getComentarios() { return comentarios; }
    public LiveData<Resource<CurtidaDto>> getCurtidaResult() { return curtidaResult; }
    public LiveData<Resource<FavoritoDto>> getFavoritoResult() { return favoritoResult; }
    public LiveData<Resource<ComentarioDto>> getNovoComentario() { return novoComentario; }
    public LiveData<Boolean> getExcluido() { return excluido; }

    public void carregarReceita(long id) {
        receitaRepository.buscarDetalhe(id, new BaseRepository.RepositoryCallback<>() {
            @Override public void onLoading() { receita.postValue(Resource.loading()); }
            @Override public void onResult(Resource<ReceitaDetalheDto> result) {
                receita.postValue(result);
            }
        });
    }

    public void carregarComentarios(long receitaId) {
        listaComentarios.clear();
        receitaRepository.buscarComentarios(receitaId, 0, 50, new BaseRepository.RepositoryCallback<>() {
            @Override public void onLoading() { comentarios.postValue(Resource.loading()); }
            @Override public void onResult(Resource<PageResponseDto<ComentarioDto>> result) {
                if (result.isSuccess() && result.getData() != null) {
                    listaComentarios.addAll(result.getData().getContent());
                    comentarios.postValue(Resource.success(new ArrayList<>(listaComentarios)));
                } else {
                    comentarios.postValue(Resource.error(result.getMessage()));
                }
            }
        });
    }

    public void toggleCurtida(ReceitaDetalheDto r) {
        BaseRepository.RepositoryCallback<CurtidaDto> cb = new BaseRepository.RepositoryCallback<>() {
            @Override public void onLoading() {}
            @Override public void onResult(Resource<CurtidaDto> result) { curtidaResult.postValue(result); }
        };
        if (r.isCurtidoPorMim()) receitaRepository.removerCurtida(r.getId(), cb);
        else receitaRepository.curtir(r.getId(), cb);
    }

    public void toggleFavorito(ReceitaDetalheDto r) {
        BaseRepository.RepositoryCallback<FavoritoDto> cb = new BaseRepository.RepositoryCallback<>() {
            @Override public void onLoading() {}
            @Override public void onResult(Resource<FavoritoDto> result) { favoritoResult.postValue(result); }
        };
        if (r.isFavoritadoPorMim()) receitaRepository.removerFavorito(r.getId(), cb);
        else receitaRepository.favoritar(r.getId(), cb);
    }

    public void enviarComentario(long receitaId, String texto) {
        if (texto == null || texto.trim().isEmpty()) return;
        receitaRepository.comentar(receitaId, texto.trim(), new BaseRepository.RepositoryCallback<>() {
            @Override public void onLoading() {}
            @Override public void onResult(Resource<ComentarioDto> result) {
                novoComentario.postValue(result);
                if (result.isSuccess() && result.getData() != null) {
                    listaComentarios.add(0, result.getData());
                    comentarios.postValue(Resource.success(new ArrayList<>(listaComentarios)));
                }
            }
        });
    }

    public void excluirComentario(long receitaId, long comentarioId) {
        receitaRepository.excluirComentario(receitaId, comentarioId, new BaseRepository.RepositoryCallback<>() {
            @Override public void onLoading() {}
            @Override public void onResult(Resource<Void> result) {
                if (result.isSuccess()) {
                    listaComentarios.removeIf(c -> c.getId() != null && c.getId() == comentarioId);
                    comentarios.postValue(Resource.success(new ArrayList<>(listaComentarios)));
                }
            }
        });
    }

    public void excluirReceita(long id) {
        receitaRepository.excluirReceita(id, new BaseRepository.RepositoryCallback<>() {
            @Override public void onLoading() {}
            @Override public void onResult(Resource<Void> result) {
                excluido.postValue(result.isSuccess());
            }
        });
    }
}
