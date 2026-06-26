package com.foodbook.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.foodbook.dto.*;
import com.foodbook.repository.BaseRepository;
import com.foodbook.repository.ReceitaRepository;
import com.foodbook.repository.UsuarioRepository;
import com.foodbook.utils.Resource;
import com.foodbook.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ProfileViewModel extends AndroidViewModel {

    private final UsuarioRepository usuarioRepository;
    private final ReceitaRepository receitaRepository;
    private final SessionManager sessionManager;

    private final MutableLiveData<Resource<UsuarioPerfilDto>> perfil = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<ReceitaResumoDto>>> receitas = new MutableLiveData<>();
    private final MutableLiveData<Resource<UsuarioDto>> atualizacaoResult = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        usuarioRepository = UsuarioRepository.getInstance();
        receitaRepository = ReceitaRepository.getInstance();
        sessionManager = SessionManager.getInstance(application);
    }

    public LiveData<Resource<UsuarioPerfilDto>> getPerfil() { return perfil; }
    public LiveData<Resource<List<ReceitaResumoDto>>> getReceitas() { return receitas; }
    public LiveData<Resource<UsuarioDto>> getAtualizacaoResult() { return atualizacaoResult; }

    public void carregarMeuPerfil() {
        usuarioRepository.buscarMeuPerfil(new BaseRepository.RepositoryCallback<>() {
            @Override public void onLoading() { perfil.postValue(Resource.loading()); }
            @Override public void onResult(Resource<UsuarioPerfilDto> result) {
                perfil.postValue(result);
            }
        });
        carregarReceitas(sessionManager.getUserId());
    }

    public void carregarPerfil(long usuarioId) {
        usuarioRepository.buscarPerfil(usuarioId, new BaseRepository.RepositoryCallback<>() {
            @Override public void onLoading() { perfil.postValue(Resource.loading()); }
            @Override public void onResult(Resource<UsuarioPerfilDto> result) {
                perfil.postValue(result);
            }
        });
        carregarReceitas(usuarioId);
    }

    private void carregarReceitas(long usuarioId) {
        receitaRepository.buscarPorUsuario(usuarioId, 0, 30, new BaseRepository.RepositoryCallback<>() {
            @Override public void onLoading() { receitas.postValue(Resource.loading()); }
            @Override public void onResult(Resource<PageResponseDto<ReceitaResumoDto>> result) {
                if (result.isSuccess() && result.getData() != null) {
                    receitas.postValue(Resource.success(result.getData().getContent()));
                } else {
                    receitas.postValue(Resource.error(result.getMessage()));
                }
            }
        });
    }

    public void atualizarNome(String nome) {
        usuarioRepository.atualizarPerfil(nome, new BaseRepository.RepositoryCallback<>() {
            @Override public void onLoading() { atualizacaoResult.postValue(Resource.loading()); }
            @Override public void onResult(Resource<UsuarioDto> result) {
                atualizacaoResult.postValue(result);
            }
        });
    }

    public boolean isMeuPerfil(long usuarioId) {
        return sessionManager.getUserId() == usuarioId;
    }

    public long getMeuId() {
        return sessionManager.getUserId();
    }
}
