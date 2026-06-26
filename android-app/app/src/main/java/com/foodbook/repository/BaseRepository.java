package com.foodbook.repository;

import com.foodbook.dto.ApiResponseDto;
import com.foodbook.utils.Resource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseRepository {

    protected <T> void enqueue(Call<ApiResponseDto<T>> call, RepositoryCallback<T> callback) {
        callback.onLoading();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ApiResponseDto<T>> c, Response<ApiResponseDto<T>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseDto<T> body = response.body();
                    if (body.isSuccess()) {
                        callback.onResult(Resource.success(body.getData()));
                    } else {
                        callback.onResult(Resource.error(body.getMessage() != null
                                ? body.getMessage() : "Erro desconhecido"));
                    }
                } else {
                    callback.onResult(Resource.error(parseError(response)));
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDto<T>> c, Throwable t) {
                callback.onResult(Resource.error("Sem conexão com o servidor"));
            }
        });
    }

    private String parseError(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String body = response.errorBody().string();
                org.json.JSONObject json = new org.json.JSONObject(body);
                return json.optString("message", "Erro " + response.code());
            }
        } catch (Exception ignored) {}
        return "Erro " + response.code();
    }

    public interface RepositoryCallback<T> {
        void onLoading();
        void onResult(Resource<T> result);
    }
}
