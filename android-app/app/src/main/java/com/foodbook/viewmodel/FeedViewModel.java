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

public class FeedViewModel extends AndroidViewModel {

    private final ReceitaRepository receitaRepository;

    private final MutableLiveData<Resource<List<ReceitaResumoDto>>> receitas = new MutableLiveData<>();
    private final MutableLiveData<Resource<CurtidaDto>> curtidaResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<FavoritoDto>> favoritoResult = new MutableLiveData<>();

    private final List<ReceitaResumoDto> listaAtual = new ArrayList<>();
    private int paginaAtual = 0;
    private boolean ultimaPagina = false;
    private boolean carregando = false;

    private static final int PAGE_SIZE = 20;

    public FeedViewModel(@NonNull Application application) {
        super(application);
        receitaRepository = ReceitaRepository.getInstance();
    }

    public LiveData<Resource<List<ReceitaResumoDto>>> getReceitas() { return receitas; }
    public LiveData<Resource<CurtidaDto>> getCurtidaResult() { return curtidaResult; }
    public LiveData<Resource<FavoritoDto>> getFavoritoResult() { return favoritoResult; }
    public boolean isUltimaPagina() { return ultimaPagina; }
    public boolean isCarregando() { return carregando; }

    public void carregarFeed() {
        paginaAtual = 0;
        ultimaPagina = false;
        listaAtual.clear();
        buscarPagina();
    }

    public void carregarMais() {
        if (ultimaPagina || carregando) return;
        buscarPagina();
    }

    private void buscarPagina() {
        receitaRepository.buscarFeed(paginaAtual, PAGE_SIZE,
                new BaseRepository.RepositoryCallback<>() {
                    @Override public void onLoading() {
                        carregando = true;
                        if (paginaAtual == 0) receitas.postValue(Resource.loading());
                    }
                    @Override public void onResult(Resource<PageResponseDto<ReceitaResumoDto>> result) {
                        carregando = false;
                        if (result.isSuccess() && result.getData() != null) {
                            PageResponseDto<ReceitaResumoDto> page = result.getData();
                            listaAtual.addAll(page.getContent());
                            ultimaPagina = page.isLast();
                            paginaAtual++;
                            receitas.postValue(Resource.success(new ArrayList<>(listaAtual)));
                        } else {
                            receitas.postValue(Resource.error(result.getMessage()));
                        }
                    }
                });
    }

    public void toggleCurtida(ReceitaResumoDto receita) {
        if (receita.isCurtidoPorMim()) {
            receitaRepository.removerCurtida(receita.getId(),
                    new BaseRepository.RepositoryCallback<>() {
                        @Override public void onLoading() {}
                        @Override public void onResult(Resource<CurtidaDto> result) {
                            curtidaResult.postValue(result);
                        }
                    });
        } else {
            receitaRepository.curtir(receita.getId(),
                    new BaseRepository.RepositoryCallback<>() {
                        @Override public void onLoading() {}
                        @Override public void onResult(Resource<CurtidaDto> result) {
                            curtidaResult.postValue(result);
                        }
                    });
        }
    }

    public void toggleFavorito(ReceitaResumoDto receita) {
        if (receita.isFavoritadoPorMim()) {
            receitaRepository.removerFavorito(receita.getId(),
                    new BaseRepository.RepositoryCallback<>() {
                        @Override public void onLoading() {}
                        @Override public void onResult(Resource<FavoritoDto> result) {
                            favoritoResult.postValue(result);
                        }
                    });
        } else {
            receitaRepository.favoritar(receita.getId(),
                    new BaseRepository.RepositoryCallback<>() {
                        @Override public void onLoading() {}
                        @Override public void onResult(Resource<FavoritoDto> result) {
                            favoritoResult.postValue(result);
                        }
                    });
        }
    }

    public void atualizarCurtidaNaLista(long receitaId, CurtidaDto curtida) {
        for (ReceitaResumoDto r : listaAtual) {
            if (r.getId().equals(receitaId)) {
                r.setCurtidoPorMim(curtida.isCurtidoPorMim());
                r.setTotalCurtidas(curtida.getTotalCurtidas());
                break;
            }
        }
        receitas.postValue(Resource.success(new ArrayList<>(listaAtual)));
    }

    public void atualizarFavoritoNaLista(long receitaId, FavoritoDto favorito) {
        for (ReceitaResumoDto r : listaAtual) {
            if (r.getId().equals(receitaId)) {
                r.setFavoritadoPorMim(favorito.isFavoritadoPorMim());
                break;
            }
        }
        receitas.postValue(Resource.success(new ArrayList<>(listaAtual)));
    }
}
