package ru.noties.lifebus.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import ru.noties.debug.Debug;
import ru.noties.lifebus.Lifebus;
import ru.noties.lifebus.activity.ActivityEvent;
import ru.noties.lifebus.activity.ActivityLifebus;
import ru.noties.lifebus.fragment.FragmentEvent;
import ru.noties.lifebus.fragment.FragmentLifebus;

public class MainActivity extends AppCompatActivity {

    private Lifebus<ActivityEvent> lifebus;

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
        fragmentLifebus.on(FragmentEvent.CREATE_VIEW, () -> Debug.i("Fragment, CREATE_VIEW"));
        fragmentLifebus.on(FragmentEvent.DESTROY_VIEW, () -> Debug.i("Fragment, DESTROY_VIEW"));
        fragmentLifebus.on(FragmentEvent.DETACH, () -> Debug.i("Fragment, DETACH"));
    }

    @Override
    public void onStart() {
        super.onStart();

        lifebus.on(ActivityEvent.STOP, () -> Debug.i("STOP"));
    }
}
