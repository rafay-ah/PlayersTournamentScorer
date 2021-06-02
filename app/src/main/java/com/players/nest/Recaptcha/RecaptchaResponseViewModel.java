package com.players.nest.Recaptcha;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class RecaptchaResponseViewModel extends AndroidViewModel {

    public RecaptchaResponseViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<RecaptchaVerifyResponse> getmRecaptchaObservable(@NonNull String baseUrl, @NonNull String response, @NonNull String key) {
        return new RecaptchaVerify().doRecaptchaValidation(baseUrl, response, key);
    }
}
