package com.foodbook.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.foodbook.dto.AuthResponseDto;
import com.foodbook.repository.AuthRepository;
import com.foodbook.utils.Resource;
import com.foodbook.utils.SessionManager;

public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;
    private final SessionManager sessionManager;

    private final MutableLiveData<Resource<AuthResponseDto>> authResult = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        sessionManager = SessionManager.getInstance(application);
        authRepository = AuthRepository.getInstance();
    }

    public LiveData<Resource<AuthResponseDto>> getAuthResult() {
        return authResult;
    }

    public void login(String email, String senha) {
        if (!validarEmail(email)) {
            authResult.setValue(Resource.error("E-mail inválido"));
            return;
        }
        if (senha == null || senha.length() < 6) {
            authResult.setValue(Resource.error("Senha deve ter pelo menos 6 caracteres"));
            return;
        }

        authRepository.login(email.trim(), senha, new AuthRepository.RepositoryCallback<>() {
            @Override public void onLoading() { authResult.postValue(Resource.loading()); }
            @Override public void onResult(Resource<AuthResponseDto> result) {
                if (result.isSuccess()) {
                    salvarSessao(result.getData());
                }
                authResult.postValue(result);
            }
        });
    }

    public void registrar(String nome, String email, String senha, String confirmar) {
        if (nome == null || nome.trim().length() < 2) {
            authResult.setValue(Resource.error("Nome deve ter pelo menos 2 caracteres"));
            return;
        }
        if (!validarEmail(email)) {
            authResult.setValue(Resource.error("E-mail inválido"));
            return;
        }
        if (senha == null || senha.length() < 6) {
            authResult.setValue(Resource.error("Senha deve ter pelo menos 6 caracteres"));
            return;
        }
        if (!senha.equals(confirmar)) {
            authResult.setValue(Resource.error("As senhas não coincidem"));
            return;
        }

        authRepository.registrar(nome.trim(), email.trim(), senha, confirmar,
                new AuthRepository.RepositoryCallback<>() {
                    @Override public void onLoading() { authResult.postValue(Resource.loading()); }
                    @Override public void onResult(Resource<AuthResponseDto> result) {
                        if (result.isSuccess()) {
                            salvarSessao(result.getData());
                        }
                        authResult.postValue(result);
                    }
                });
    }

    private void salvarSessao(AuthResponseDto data) {
        if (data != null && data.getUsuario() != null) {
            sessionManager.saveSession(
                    data.getAccessToken(),
                    data.getUsuario().getId(),
                    data.getUsuario().getNome(),
                    data.getUsuario().getEmail()
            );
        }
    }

    private boolean validarEmail(String email) {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches();
    }
}
