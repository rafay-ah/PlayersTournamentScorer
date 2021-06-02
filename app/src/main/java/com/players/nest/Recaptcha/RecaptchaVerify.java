package com.players.nest.Recaptcha;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecaptchaVerify {
    public LiveData<RecaptchaVerifyResponse> doRecaptchaValidation(@NonNull String baseUrl, @NonNull String response, @NonNull String key) {

        final MutableLiveData<RecaptchaVerifyResponse> data = new MutableLiveData<>();
        Map<String, String> params = new HashMap<>();
        params.put("response", response);
        params.put("secret", key);
        getRecaptchaValidationService(baseUrl).verifyResponse(params).enqueue(new Callback<RecaptchaVerifyResponse>() {
            @Override
            public void onResponse(@NonNull Call<RecaptchaVerifyResponse> call, @NonNull Response<RecaptchaVerifyResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<RecaptchaVerifyResponse> call, @NonNull Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    private RecaptchaVerificationService getRecaptchaValidationService(@NonNull String baseUrl) {
        return getRetrofit(baseUrl).create(RecaptchaVerificationService.class);
    }

    private Retrofit getRetrofit(@NonNull String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
