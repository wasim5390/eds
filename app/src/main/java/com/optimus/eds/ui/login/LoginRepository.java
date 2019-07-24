package com.optimus.eds.ui.login;

import android.app.Application;

import com.optimus.eds.source.API;
import com.optimus.eds.source.RetrofitHelper;
import com.optimus.eds.source.TokenResponse;
import com.optimus.eds.utils.PreferenceUtil;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

public class LoginRepository {

    public static LoginRepository instance;
    private API api;
    private MutableLiveData<String> error;
    private PreferenceUtil preferenceUtil;



    public static LoginRepository getInstance(Application application) {
        if(instance==null)
            instance = new LoginRepository(application);
        return instance;
    }
    public LoginRepository(Application application) {
        api = RetrofitHelper.getInstance().getApi();
        preferenceUtil  = PreferenceUtil.getInstance(application);
        error = new MutableLiveData<>();
    }

    public LiveData<TokenResponse> login(String username,String password){
        MutableLiveData<TokenResponse> liveData = new MutableLiveData<>();
        api.getToken("password",username,password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new SingleObserver<TokenResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(TokenResponse tokenResponse) {
                        if(tokenResponse.isSuccess()) {
                            preferenceUtil.saveToken(tokenResponse.getAccessToken());
                            preferenceUtil.saveUserName(username);
                            preferenceUtil.savePassword(password);
                            liveData.postValue(tokenResponse);
                        }
                        else {
                           // error.postValue(tokenResponse.getErrorMessage());
                            error.postValue("Unable to Login, Please contact Administrator!");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(e instanceof HttpException){
                            int code = ((HttpException)e).code();
                            if(code==400)
                                error.postValue("The username or password is incorrect.");
                        }

                       // error.postValue(e.getMessage());
                    }
                });

        return liveData;

    }

    public MutableLiveData<String> getError() {
        return error;
    }
}
