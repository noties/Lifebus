package io.noties.lifebus.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import io.noties.lifebus.BaseLifebus;
import io.noties.lifebus.Lifebus;

/**
 * A factory to obtain an instance if {@link Lifebus} to listen to {@link FragmentEvent}. Please note
 * that library does not include support-fragments artifact dependency, so it must be done manually
 * (if you do not have them already).
 * <p>
 * since 1.0.2 extends Lifebus&lt;FragmentEvent&gt;
 *
 * @see FragmentEvent
 */
@SuppressWarnings("WeakerAccess")
public abstract class FragmentLifebus extends BaseLifebus<Fragment, FragmentEvent> {

    /**
     * Factory method to create an instance of {@link Lifebus} to listen to {@link FragmentEvent}.
     * Please note that provided Fragment must be attached (fragment.getFragmentManager()). If you wish
     * to initialize {@link Lifebus} before attaching to a FragmentManager consider using {@link #create(FragmentManager, Fragment)}
     * factory method
     *
     * @param fragment android.support.v4.app.Fragment to register on.
     * @return an instance of {@link Lifebus}
     * @see #create(FragmentManager, Fragment)
     */
    @NonNull
    public static Lifebus<FragmentEvent> create(@NonNull Fragment fragment) {
        final FragmentManager manager = fragment.getFragmentManager();
        if (manager == null) {
            throw new IllegalStateException("Provided fragment '" + fragment.getClass().getName() + "' " +
                    "is not attached to a fragment manager. Consider using #create(FragmentManager, Fragment) " +
                    "factory method or wait until fragment is attached");
        }
        return new Impl(manager, fragment);
    }

    /**
     * @param manager  android.support.v4.app.FragmentManager
     * @param fragment android.support.v4.app.Fragment
     * @return an instance of {@link Lifebus}
     * @see #create(Fragment)
     */
    @NonNull
    public static Lifebus<FragmentEvent> create(@NonNull FragmentManager manager, @NonNull Fragment fragment) {
        return new Impl(manager, fragment);
    }

    protected FragmentLifebus(@NonNull Fragment owner, @NonNull Class<FragmentEvent> event, @NonNull FragmentEvent disposeEvent) {
        super(owner, event, disposeEvent);
    }

    static class Impl extends FragmentLifebus {

        Impl(@NonNull FragmentManager manager, @NonNull Fragment fragment) {
            super(fragment, FragmentEvent.class, FragmentEvent.DETACH);

            manager.registerFragmentLifecycleCallbacks(new FragmentLifecycleCallbacksImpl(), false);
        }

        private class FragmentLifecycleCallbacksImpl extends FragmentManager.FragmentLifecycleCallbacks {

            @Override
            public void onFragmentAttached(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull Context context) {
                triggerEventNotification(f, FragmentEvent.ATTACH);
            }

            @Override
            public void onFragmentCreated(@NonNull FragmentManager fm, @NonNull Fragment f, Bundle savedInstanceState) {
                triggerEventNotification(f, FragmentEvent.CREATE);
            }

            @Override
            public void onFragmentViewCreated(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull View v, Bundle savedInstanceState) {
                triggerEventNotification(f, FragmentEvent.VIEW_CREATED);
            }

            @Override
            public void onFragmentStarted(@NonNull FragmentManager fm, @NonNull Fragment f) {
                triggerEventNotification(f, FragmentEvent.START);
            }

            @Override
            public void onFragmentResumed(@NonNull FragmentManager fm, @NonNull Fragment f) {
                triggerEventNotification(f, FragmentEvent.RESUME);
            }

            @Override
            public void onFragmentPaused(@NonNull FragmentManager fm, @NonNull Fragment f) {
                triggerEventNotification(f, FragmentEvent.PAUSE);
            }

            @Override
            public void onFragmentStopped(@NonNull FragmentManager fm, @NonNull Fragment f) {
                triggerEventNotification(f, FragmentEvent.STOP);
            }

            @Override
            public void onFragmentSaveInstanceState(@NonNull FragmentManager fm, @NonNull Fragment f, @NonNull Bundle outState) {
                triggerEventNotification(f, FragmentEvent.SAVE_INSTANCE_STATE);
            }

            @Override
            public void onFragmentViewDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
                triggerEventNotification(f, FragmentEvent.VIEW_DESTROYED);
            }

            @Override
            public void onFragmentDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
                triggerEventNotification(f, FragmentEvent.DESTROY);
            }

            @Override
            public void onFragmentDetached(@NonNull FragmentManager fm, @NonNull Fragment f) {
                if (triggerEventNotification(f, FragmentEvent.DETACH)) {
                    fm.unregisterFragmentLifecycleCallbacks(this);
                }
            }
        }
    }
}
