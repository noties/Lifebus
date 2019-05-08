package io.noties.lifebus.activity;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;

import io.noties.lifebus.fragment.FragmentLifebus;
import io.noties.lifebus.BaseLifebus;
import io.noties.lifebus.Lifebus;

/**
 * Factory to create a {@link Lifebus} instance that listens for {@link ActivityEvent}
 * <p>
 * since 1.0.2 extends Lifebus&lt;ActivityEvent&gt;
 *
 * @see FragmentLifebus
 * @see Lifebus
 * @see #create(Activity)
 * @see #create(Application, Activity)
 */
public abstract class ActivityLifebus extends BaseLifebus<Activity, ActivityEvent> {

    /**
     * Factory method to obtain a {@link Lifebus} that listens for {@link ActivityEvent}. Please note
     * that specified activity must have `getApplication()` non-null. If you want to register before
     * activity went through `onCreate` consider using {@link #create(Application, Activity)} factory
     * method
     *
     * @param activity android.app.Activity to register on
     * @return an instance of {@link Lifebus}
     * @see #create(Application, Activity)
     */
    @NonNull
    public static Lifebus<ActivityEvent> create(@NonNull Activity activity) {
        final Application application = activity.getApplication();
        if (application == null) {
            throw new IllegalStateException("Provided activity '" + activity.getClass().getName() + "' " +
                    "has no Application information. Consider moving this call to after Activity#onCreate " +
                    "or use #create(Application, Activity) factory method");
        }
        return new Impl(application, activity);
    }

    /**
     * Factory method to obtain a {@link Lifebus} that listens for {@link ActivityEvent}
     *
     * @param application android.app.Application
     * @param activity    android.app.Activity
     * @return an instance of {@link Lifebus}
     * @see #create(Activity)
     */
    @NonNull
    public static Lifebus<ActivityEvent> create(@NonNull Application application, @NonNull Activity activity) {
        return new Impl(application, activity);
    }


    ActivityLifebus(@NonNull Activity owner, @NonNull Class<ActivityEvent> event, @NonNull ActivityEvent disposeEvent) {
        super(owner, event, disposeEvent);
    }

    static class Impl extends ActivityLifebus {

        Impl(@NonNull Application application, @NonNull Activity activity) {
            super(activity, ActivityEvent.class, ActivityEvent.DESTROY);

            application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacksImpl());
        }

        private class ActivityLifecycleCallbacksImpl implements Application.ActivityLifecycleCallbacks {

            @Override
            public void onActivityCreated(Activity a, Bundle savedInstanceState) {
                triggerEventNotification(a, ActivityEvent.CREATE);
            }

            @Override
            public void onActivityStarted(Activity a) {
                triggerEventNotification(a, ActivityEvent.START);
            }

            @Override
            public void onActivityResumed(Activity a) {
                triggerEventNotification(a, ActivityEvent.RESUME);
            }

            @Override
            public void onActivityPaused(Activity a) {
                triggerEventNotification(a, ActivityEvent.PAUSE);
            }

            @Override
            public void onActivityStopped(Activity a) {
                triggerEventNotification(a, ActivityEvent.STOP);
            }

            @Override
            public void onActivitySaveInstanceState(Activity a, Bundle outState) {
                triggerEventNotification(a, ActivityEvent.SAVE_INSTANCE_STATE);
            }

            @Override
            public void onActivityDestroyed(Activity a) {
                if (triggerEventNotification(a, ActivityEvent.DESTROY)) {
                    // unregister
                    a.getApplication().unregisterActivityLifecycleCallbacks(this);
                }
            }
        }
    }
}
