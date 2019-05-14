package com.optimus.eds.source;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.optimus.eds.AnnotationExclusionStrategy;
import com.optimus.eds.Constant;
import com.optimus.eds.EdsApplication;
import com.optimus.eds.utils.Util;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by sidhu on 4/11/2018.
 */

public class RetrofitHelper implements Constant {

    public static final String BASE_URL_DEV = "http://192.168.8.100:51185/";
    public static final String BASE_URL = "http://optimuseds.com/API/";

    private static RetrofitHelper instance;
    public Retrofit retrofit;
    API service;


    private static final String TAG = "RetrofitHelper";
    private RetrofitHelper () {

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(new AuthorizationInterceptor());

       Gson builder = new GsonBuilder().setExclusionStrategies(new AnnotationExclusionStrategy()).create();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(builder))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClientBuilder.build())
                .build();

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

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            String header = Util.getAuthorizationHeader(EdsApplication.getInstance());
            if (header!=null) {
                Request request = original.newBuilder()
                        .header("Authorization", "Bearer " + header).build();
                Response response = chain.proceed(request);
                if (response.code()==401) {
                    //EventBus.getDefault().post(new LoginFailEvent());
                }

                return response;
            } else {

                return chain.proceed(original);
            }



        }
    }


}
