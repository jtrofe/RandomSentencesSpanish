package com.example.main.randomsentences;

import android.app.Application;
import android.content.Context;

public class GameApplication extends Application {
    private static Context context;

    @Override
    public void onCreate(){
        super.onCreate();

        context = getApplicationContext();
    }

    public static Context AppContext(){
        return context;
    }
}
