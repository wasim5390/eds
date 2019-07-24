package com.optimus.eds.ui.login;

import android.app.Application;

import com.optimus.eds.source.TokenResponse;
import com.optimus.eds.utils.PreferenceUtil;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class LoginViewModel extends AndroidViewModel {



    private PreferenceUtil preferenceUtil;
    private LoginRepository repository;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        preferenceUtil = PreferenceUtil.getInstance(application);
        repository = new LoginRepository(application);
    }

    public LiveData<TokenResponse> login(String username, String password){
       return repository.login(username,password);
    }


    public LiveData<String> getMsg() {
        return repository.getError();
    }
}
