package ru.noties.lifebus.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import ru.noties.lifebus.LifebusSource;
import ru.noties.listeners.Listeners;
import ru.noties.subscription.CompositeSubscription;
import ru.noties.subscription.Subscription;

@SuppressWarnings("WeakerAccess")
public class FragmentLifebusSource implements LifebusSource<FragmentEvent> {

    /**
     * Factory method to create a {@link FragmentLifebusSource}
     *
     * @param fragment android.support.v4.app.Fragment to register on
     * @return an instance of {@link FragmentLifebusSource}
     * @see ru.noties.lifebus.Lifebus#create(LifebusSource)
     * @see FragmentLifebus#create(Fragment)
     * @see #create(FragmentManager, Fragment)
     */
    @NonNull
    public static FragmentLifebusSource create(@NonNull Fragment fragment) {
        final FragmentManager manager = fragment.getFragmentManager();
        if (manager == null) {
            throw new IllegalStateException("Provided fragment '" + fragment.getClass().getName() + "' " +
                    "is not attached to a fragment manager. Consider using #create(FragmentManager, Fragment) " +
                    "factory method or wait until fragment is attached");
        }
        return new FragmentLifebusSource(manager, fragment);
    }

    /**
     * Factory method to create a {@link FragmentLifebusSource}
     *
     * @param manager  android.support.v4.app.FragmentManager
     * @param fragment android.support.v4.app.Fragment
     * @return an instance of {@link FragmentLifebusSource}
     * @see ru.noties.lifebus.Lifebus#create(LifebusSource)
     * @see FragmentLifebus#create(FragmentManager, Fragment)
     * @see #create(Fragment)
     */
    @NonNull
    public static FragmentLifebusSource create(@NonNull FragmentManager manager, @NonNull Fragment fragment) {
        return new FragmentLifebusSource(manager, fragment);
    }

    // we will keep track of manager with which we have registered (so we can unregister)
    private FragmentManager manager;
    private Fragment fragment;

    private final Listeners<Listener<FragmentEvent>> listeners = Listeners.create(3);

    private final CompositeSubscription compositeSubscription = CompositeSubscription.create();


    private FragmentLifebusSource(@NonNull FragmentManager manager, @NonNull Fragment fragment) {
        this.fragment = fragment;
        this.manager = manager;

        manager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, false);
    }

    @NonNull
    @Override
    public Subscription registerListener(@NonNull final Listener<FragmentEvent> listener) {

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

    private FragmentManager.FragmentLifecycleCallbacks fragmentLifecycleCallbacks = new FragmentManager.FragmentLifecycleCallbacks() {

        @Override
        public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
            notifyFragmentEvent(f, FragmentEvent.ATTACH);
        }

        @Override
        public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
            notifyFragmentEvent(f, FragmentEvent.CREATE);
        }

        @Override
        public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
            notifyFragmentEvent(f, FragmentEvent.CREATE_VIEW);
        }

        @Override
        public void onFragmentStarted(FragmentManager fm, Fragment f) {
            notifyFragmentEvent(f, FragmentEvent.START);
        }

        @Override
        public void onFragmentResumed(FragmentManager fm, Fragment f) {
            notifyFragmentEvent(f, FragmentEvent.RESUME);
        }

        @Override
        public void onFragmentPaused(FragmentManager fm, Fragment f) {
            notifyFragmentEvent(f, FragmentEvent.PAUSE);
        }

        @Override
        public void onFragmentStopped(FragmentManager fm, Fragment f) {
            notifyFragmentEvent(f, FragmentEvent.STOP);
        }

        @Override
        public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
            notifyFragmentEvent(f, FragmentEvent.DESTROY_VIEW);
        }

        @Override
        public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
            notifyFragmentEvent(f, FragmentEvent.DESTROY);
        }

        @Override
        public void onFragmentDetached(FragmentManager fm, Fragment f) {
            notifyFragmentEvent(f, FragmentEvent.DETACH);
        }
    };

    private void notifyFragmentEvent(@NonNull Fragment f, @NonNull FragmentEvent event) {
        if (fragment == f) {

            for (Listener<FragmentEvent> listener : listeners.begin()) {
                listener.onEvent(event);
            }

            if (FragmentEvent.DETACH == event) {

                listeners.clear();

                compositeSubscription.unsubscribe();

                manager.unregisterFragmentLifecycleCallbacks(fragmentLifecycleCallbacks);

                fragment = null;
                manager = null;
            }
        }
    }
}
