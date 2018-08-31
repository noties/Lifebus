package ru.noties.lifebus.activity;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.NonNull;

import ru.noties.lifebus.Lifebus;

/**
 * Factory to create a {@link Lifebus} instance that listens for {@link ActivityEvent}
 * <p>
 * since 1.0.2 extends Lifebus&lt;ActivityEvent&gt;
 *
 * @see ru.noties.lifebus.fragment.FragmentLifebus
 * @see Lifebus
 * @see ActivityLifebusSource
 * @see #create(Activity)
 * @see #create(Application, Activity)
 */
public abstract class ActivityLifebus extends Lifebus<ActivityEvent> {

    /**
     * Factory method to obtain a {@link Lifebus} that listens for {@link ActivityEvent}. Please note
     * that specified activity must have `getApplication()` non-null. If you want to register before
     * activity went through `onCreate` consider using {@link #create(Application, Activity)} factory
     * method
     *
     * @param activity android.app.Activity to register on
     * @return an instance of {@link Lifebus}
     * @see ActivityLifebusSource
     * @see #create(Application, Activity)
     */
    @NonNull
    public static Lifebus<ActivityEvent> create(@NonNull Activity activity) {
        return new Impl(Lifebus.create(ActivityLifebusSource.create(activity)));
    }

    /**
     * Factory method to obtain a {@link Lifebus} that listens for {@link ActivityEvent}
     *
     * @param application android.app.Application
     * @param activity    android.app.Activity
     * @return an instance of {@link Lifebus}
     * @see ActivityLifebusSource
     * @see #create(Activity)
     */
    @NonNull
    public static Lifebus<ActivityEvent> create(@NonNull Application application, @NonNull Activity activity) {
        return new Impl(Lifebus.create(ActivityLifebusSource.create(application, activity)));
    }

    static class Impl extends ActivityLifebus {

        private final Lifebus<ActivityEvent> lifebus;

        Impl(@NonNull Lifebus<ActivityEvent> lifebus) {
            this.lifebus = lifebus;
        }

        @NonNull
        @Override
        public Lifebus<ActivityEvent> on(@NonNull ActivityEvent event, @NonNull Action action) {
            lifebus.on(event, action);
            return this;
        }
    }
}
