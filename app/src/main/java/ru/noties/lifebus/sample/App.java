package ru.noties.lifebus.sample;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

import ru.noties.debug.AndroidLogDebugOutput;
import ru.noties.debug.Debug;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

//        if (!LeakCanary.isInAnalyzerProcess(this)) {
//            return;
//        }
//        LeakCanary.install(this);

        Debug.init(new AndroidLogDebugOutput(BuildConfig.DEBUG));
    }
}
