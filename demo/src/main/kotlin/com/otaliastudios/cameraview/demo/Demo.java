package com.otaliastudios.cameraview.demo;

import android.app.Application;
import android.content.Context;

public class Demo extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
