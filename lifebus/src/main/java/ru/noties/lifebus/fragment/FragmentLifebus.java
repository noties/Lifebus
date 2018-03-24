package ru.noties.lifebus.fragment;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import ru.noties.lifebus.Lifebus;

/**
 * A factory to obtain an instance if {@link Lifebus} to listen to {@link FragmentEvent}. Please note
 * that library does not include support-fragments artifact dependency, so it must be done manually
 * (if you do not have them already).
 *
 * @see FragmentEvent
 * @see FragmentLifebusSource
 */
@SuppressWarnings("WeakerAccess")
public abstract class FragmentLifebus {

    /**
     * Factory method to create an instance of {@link Lifebus} to listen to {@link FragmentEvent}.
     * Please note that provided Fragment must be attached (fragment.getFragmentManager()). If you wish
     * to initialize {@link Lifebus} before attaching to a FragmentManager consider using {@link #create(FragmentManager, Fragment)}
     * factory method
     *
     * @param fragment android.support.v4.app.Fragment to register on.
     * @return an instance of {@link Lifebus}
     * @see FragmentLifebusSource
     * @see FragmentLifebusSource#create(Fragment)
     * @see #create(FragmentManager, Fragment)
     */
    @NonNull
    public static Lifebus<FragmentEvent> create(@NonNull Fragment fragment) {
        return Lifebus.create(FragmentLifebusSource.create(fragment));
    }

    /**
     * @param manager  android.support.v4.app.FragmentManager
     * @param fragment android.support.v4.app.Fragment
     * @return an instance of {@link Lifebus}
     * @see FragmentLifebusSource
     * @see FragmentLifebusSource#create(FragmentManager, Fragment)
     * @see #create(Fragment)
     */
    @NonNull
    public static Lifebus<FragmentEvent> create(@NonNull FragmentManager manager, @NonNull Fragment fragment) {
        return Lifebus.create(FragmentLifebusSource.create(manager, fragment));
    }

    private FragmentLifebus() {
    }
}
