package com.optimus.eds;

import android.content.Context;
import android.support.annotation.NonNull;


import com.optimus.eds.source.ApiRepository;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created on 24/10/2017.
 */

public class Injection {

    public static ApiRepository provideRepository(@NonNull Context context) {
        checkNotNull(context);
        return ApiRepository.getInstance();
    }
}
