package ru.noties.lifebus.activity;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;

import ru.noties.subscription.CompositeSubscription;
import ru.noties.lifebus.LifebusSource;
import ru.noties.subscription.Subscription;
import ru.noties.listeners.Listeners;

public class ActivityLifebusSource implements LifebusSource<ActivityEvent> {


    @NonNull
    public static LifebusSource<ActivityEvent> create(@NonNull Activity activity) {
        final Application application = activity.getApplication();
        if (application == null) {
            throw new IllegalStateException("Provided activity '" + activity.getClass().getName() + "' " +
                    "has no Application information. Consider moving this call to after Activity#onCreate " +
                    "or use #create(Application, Activity) factory method");
        }
        return new ActivityLifebusSource(activity.getApplication(), activity);
    }

    @NonNull
    public static LifebusSource<ActivityEvent> create(@NonNull Application application, @NonNull Activity activity) {
        return new ActivityLifebusSource(application, activity);
    }


    private Activity activity;

    private final Listeners<Listener<ActivityEvent>> listeners = Listeners.create(3);

    private final CompositeSubscription compositeSubscription = CompositeSubscription.create();


    private ActivityLifebusSource(@NonNull Application application, @NonNull Activity activity) {
        this.activity = activity;
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    @NonNull
    @Override
    public Subscription registerListener(@NonNull final Listener<ActivityEvent> listener) {

        final Subscription.UnsubscribeAction action = new Subscription.UnsubscribeAction() {
            @Override
            public void apply(@NonNull Subscription subscription) {
                listeners.remove(listener);
                compositeSubscription.remove(subscription);
            }
        };

        listeners.add(listener);

        return Subscription.create(action)
                .accept(compositeSubscription.add());
    }

    private final Application.ActivityLifecycleCallbacks activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            notifyActivityEvent(activity, ActivityEvent.CREATE);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            notifyActivityEvent(activity, ActivityEvent.START);
        }

        @Override
        public void onActivityResumed(Activity activity) {
            notifyActivityEvent(activity, ActivityEvent.RESUME);
        }

        @Override
        public void onActivityPaused(Activity activity) {
            notifyActivityEvent(activity, ActivityEvent.PAUSE);
        }

        @Override
        public void onActivityStopped(Activity activity) {
            notifyActivityEvent(activity, ActivityEvent.STOP);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            // no op
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            notifyActivityEvent(activity, ActivityEvent.DESTROY);
        }
    };

    private void notifyActivityEvent(@NonNull Activity a, @NonNull ActivityEvent event) {
        if (activity == a) {

            // notify
            for (Listener<ActivityEvent> listener : listeners.begin()) {
                listener.onEvent(event);
            }

            if (ActivityEvent.DESTROY == event) {

                // unregister, clear listeners, etc

                listeners.clear();

                compositeSubscription.unsubscribe();

                activity.getApplication()
                        .unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
                activity = null;
            }
        }
    }
}
