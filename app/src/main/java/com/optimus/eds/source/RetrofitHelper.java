package com.optimus.eds.source;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.optimus.eds.AnnotationExclusionStrategy;
import com.optimus.eds.Constant;
import com.optimus.eds.EdsApplication;
import com.optimus.eds.utils.PreferenceUtil;
import com.optimus.eds.utils.Util;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by sidhu on 4/4/2019.
 */

public class RetrofitHelper implements Constant {
    // public static final String BASE_URL = "http://192.168.100.9:34551/EDS.API/";
    // public static final String BASE_URL = "http://optimuseds.com/UATAPI/"; // staging
     public static final String BASE_URL = "http://optimuseds.com/API/";

    private static RetrofitHelper instance;
    public Retrofit retrofit;
    API service;


    private static final String TAG = "RetrofitHelper";
    private RetrofitHelper () {

        retrofit = getRetrofit();
        service = retrofit.create(API.class);
    }

    public static RetrofitHelper getInstance() {
        if (instance==null) {
            instance = new RetrofitHelper();
        }
        return instance;
    }



    public API getApi() {
        return service;
    }


    public class AuthorizationInterceptor implements Interceptor {
        Response response;
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            PreferenceUtil preferenceUtil = PreferenceUtil.getInstance(EdsApplication.getContext());
            //String token = Util.getAuthorizationHeader(EdsApplication.getInstance());
/*
            if (token!=null) {

                request = originalRequest.newBuilder()
                        .header("Authorization", "Bearer " + token).build();*/

            if(request.headers().get("Authorization")==null || request.headers().get("Authorization").isEmpty()){
                String token = Util.getAuthorizationHeader(EdsApplication.getContext());
                Headers headers = request.headers().newBuilder().add("Authorization","Bearer "+ token).build();
                request = request.newBuilder().headers(headers).build();
            }

            response = chain.proceed(request);
            if (response.code()== 401 && !preferenceUtil.getUsername().isEmpty()) {
                API tokenApi =  getRetrofit().create(API.class);
                retrofit2.Response<TokenResponse> tokenResponse= tokenApi.refreshToken("password",preferenceUtil.getUsername(),preferenceUtil.getPassword()).execute();
                if(tokenResponse.isSuccessful()){
                    TokenResponse tokenResponseObj=tokenResponse.body();

                    try {
                        request= response.request().newBuilder()
                                .header("Authorization", "Bearer " + tokenResponseObj.getAccessToken()).build();
                        response = chain.proceed(request);
                        PreferenceUtil.getInstance(EdsApplication.getContext()).saveToken(tokenResponseObj.getAccessToken());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    // goto Login as we cannot refresh token
                }

            }

            return response;
        }
           /* else {
                return chain.proceed(originalRequest);
            }*/



        // }
    }

    private Retrofit getRetrofit(){
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(new AuthorizationInterceptor());
        Gson builder = new GsonBuilder().setExclusionStrategies(new AnnotationExclusionStrategy()).create();
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(builder))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClientBuilder.build())
                .build();
    }




}
