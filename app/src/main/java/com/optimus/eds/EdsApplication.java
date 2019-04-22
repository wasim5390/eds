package com.optimus.eds;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

public class EdsApplication extends MultiDexApplication implements AppLifecycleHandler.LifeCycleDelegate {
    private AppLifecycleHandler lifeCycleHandler;
    private static EdsApplication instance;

    public static Context getInstance() {
        return instance==null?new EdsApplication():instance;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        lifeCycleHandler = new AppLifecycleHandler(this);
        registerLifecycleHandler(lifeCycleHandler);

    }
    private void registerLifecycleHandler(AppLifecycleHandler lifeCycleHandler) {
        registerActivityLifecycleCallbacks(lifeCycleHandler);
        registerComponentCallbacks(lifeCycleHandler);
    }

    @Override
    public void onAppBackgrounded() {

    }

    @Override
    public void onAppForegrounded() {

    }
}
