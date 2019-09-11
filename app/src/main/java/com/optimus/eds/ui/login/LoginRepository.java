package com.optimus.eds.ui.login;

import android.app.Application;

import com.optimus.eds.model.BaseResponse;
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
                            String previousUsername = preferenceUtil.getUsername();

                            if(!previousUsername.equals(username))
                                preferenceUtil.clearAllPreferences();
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
                        error(e);


                        // error.postValue(e.getMessage());
                    }
                });

        return liveData;

    }

    public LiveData<BaseResponse> postFirebaseToken(String token,String imei){
        MutableLiveData<BaseResponse> response = new MutableLiveData<>();
        api.postFirebaseToken(token,imei) .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new SingleObserver<BaseResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(BaseResponse tokenResponse) {
                response.postValue(tokenResponse);
            }

            @Override
            public void onError(Throwable e) {
            response.postValue(new BaseResponse(false,"Something went wrong, please login again!",2));
            }
        });
        return response;
    }

    public void error(Throwable e){
        if(e instanceof HttpException){
            int code = ((HttpException)e).code();
            if(code==400)
                error.postValue("The username or password is incorrect.");
        }

    }

    public MutableLiveData<String> getError() {
        return error;
    }
}
