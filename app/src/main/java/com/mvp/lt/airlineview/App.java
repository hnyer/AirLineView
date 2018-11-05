package com.mvp.lt.airlineview;

import android.app.Application;
import android.content.Context;



public class App extends Application {
    private static App instance;


    public static App getInstance() {
        return instance;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
