package ru.noties.lifebus.sample;

import android.app.Application;

import ru.noties.debug.AndroidLogDebugOutput;
import ru.noties.debug.Debug;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Debug.init(new AndroidLogDebugOutput(BuildConfig.DEBUG));
    }
}
