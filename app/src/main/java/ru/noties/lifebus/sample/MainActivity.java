package ru.noties.lifebus.sample;

import android.arch.lifecycle.Lifecycle;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import ru.noties.debug.Debug;
import ru.noties.lifebus.Lifebus;
import ru.noties.lifebus.activity.ActivityEvent;
import ru.noties.lifebus.activity.ActivityLifebus;
import ru.noties.lifebus.arch.LifebusArch;
import ru.noties.lifebus.fragment.FragmentEvent;
import ru.noties.lifebus.fragment.FragmentLifebus;

public class MainActivity extends AppCompatActivity {

    private Lifebus<ActivityEvent> lifebus;

    {
        final LifebusArch lifebusArch = LifebusArch.create(this);
        for (Lifecycle.Event event : Lifecycle.Event.values()) {
            lifebusArch.on(event, () -> Debug.i("activity arch, event: %s", event));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        lifebus = ActivityLifebus.create(this);
        lifebus.on(ActivityEvent.DESTROY, () -> Debug.i("DESTROY"));

        final FragmentManager fragmentManager = getSupportFragmentManager();
        // can create a lifebus for a Fragment
        Fragment fragment = fragmentManager.findFragmentById(Window.ID_ANDROID_CONTENT);
        if (fragment == null) {
            fragment = MainFragment.newInstance();
            fragmentManager
                    .beginTransaction()
                    .replace(Window.ID_ANDROID_CONTENT, fragment)
                    .commit();
        }

        final Lifebus<FragmentEvent> fragmentLifebus = FragmentLifebus.create(fragmentManager, fragment);
        fragmentLifebus.on(FragmentEvent.VIEW_CREATED, () -> Debug.i("Fragment, VIEW_CREATED"));
        fragmentLifebus.on(FragmentEvent.VIEW_DESTROYED, () -> Debug.i("Fragment, VIEW_DESTROYED"));
        fragmentLifebus.on(FragmentEvent.DETACH, () -> Debug.i("Fragment, DETACH"));
    }

    @Override
    public void onStart() {
        super.onStart();

        lifebus.on(ActivityEvent.STOP, () -> Debug.i("STOP"));
    }
}
