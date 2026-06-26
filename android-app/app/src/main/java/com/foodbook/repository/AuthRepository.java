package com.foodbook.repository;

import com.foodbook.dto.AuthResponseDto;
import com.foodbook.dto.LoginRequestDto;
import com.foodbook.dto.RegisterRequestDto;
import com.foodbook.network.ApiClient;
import com.foodbook.network.ApiService;
import com.foodbook.utils.Resource;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private static AuthRepository instance;
    private final ApiService apiService;

    private AuthRepository() {
        apiService = ApiClient.createService(ApiService.class);
    }

    public static AuthRepository getInstance() {
        if (instance == null) {
            instance = new AuthRepository();
        }
        return instance;
    }

    public void login(String email, String senha,
                      RepositoryCallback<AuthResponseDto> callback) {
        callback.onLoading();
        apiService.login(new LoginRequestDto(email, senha))
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        handleResponse(response, callback);
                    }
                    @Override
                    public void onFailure(Call call, Throwable t) {
                        callback.onResult(Resource.error("Sem conexão com o servidor"));
                    }
                });
    }

    public void registrar(String nome, String email, String senha, String confirmar,
                          RepositoryCallback<AuthResponseDto> callback) {
        callback.onLoading();
        apiService.registrar(new RegisterRequestDto(nome, email, senha, confirmar))
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        handleResponse(response, callback);
                    }
                    @Override
                    public void onFailure(Call call, Throwable t) {
                        callback.onResult(Resource.error("Sem conexão com o servidor"));
                    }
                });
    }

    @SuppressWarnings("unchecked")
    private <T> void handleResponse(Response response, RepositoryCallback<T> callback) {
        if (response.isSuccessful() && response.body() != null) {
            var apiResponse = (com.foodbook.dto.ApiResponseDto<T>) response.body();
            if (apiResponse.isSuccess()) {
                callback.onResult(Resource.success(apiResponse.getData()));
            } else {
                callback.onResult(Resource.error(apiResponse.getMessage()));
            }
        } else {
            callback.onResult(Resource.error(parseErrorMessage(response)));
        }
    }

    private String parseErrorMessage(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String body = response.errorBody().string();
                org.json.JSONObject json = new org.json.JSONObject(body);
                return json.optString("message", "Erro desconhecido");
            }
        } catch (Exception ignored) {}
        return "Erro " + response.code();
    }

    public interface RepositoryCallback<T> {
        void onLoading();
        void onResult(Resource<T> result);
    }
}
