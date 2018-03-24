package ru.noties.lifebus.arch;

import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;

class LifebusArchImpl extends LifebusArch {

    private final LifecycleOwner owner;

    LifebusArchImpl(@NonNull LifecycleOwner owner) {
        this.owner = owner;
    }

    @NonNull
    @Override
    public LifebusArch on(@NonNull Lifecycle.Event event, @NonNull Action action) {
        if (Lifecycle.Event.ON_ANY == event) {
            throw new IllegalStateException("Cannot register listener for a Lifecycle.Event.ON_ANY");
        }
        owner.getLifecycle().addObserver(new Observer(event, action));
        return this;
    }

    private static class Observer implements DefaultLifecycleObserver {

        private final Lifecycle.Event event;
        private final Action action;

        Observer(@NonNull Lifecycle.Event event, @NonNull Action action) {
            this.event = event;
            this.action = action;
        }

        @Override
        public void onCreate(@NonNull LifecycleOwner owner) {
            notifyIfMatches(owner, Lifecycle.Event.ON_CREATE);
        }

        @Override
        public void onStart(@NonNull LifecycleOwner owner) {
            notifyIfMatches(owner, Lifecycle.Event.ON_START);
        }

        @Override
        public void onResume(@NonNull LifecycleOwner owner) {
            notifyIfMatches(owner, Lifecycle.Event.ON_RESUME);
        }

        @Override
        public void onPause(@NonNull LifecycleOwner owner) {
            notifyIfMatches(owner, Lifecycle.Event.ON_PAUSE);
        }

        @Override
        public void onStop(@NonNull LifecycleOwner owner) {
            notifyIfMatches(owner, Lifecycle.Event.ON_STOP);
        }

        @Override
        public void onDestroy(@NonNull LifecycleOwner owner) {
            if (!notifyIfMatches(owner, Lifecycle.Event.ON_DESTROY)) {
                unregister(owner);
            }
        }

        private boolean notifyIfMatches(@NonNull LifecycleOwner owner, @NonNull Lifecycle.Event currentEvent) {
            final boolean result = event == currentEvent;
            if (result) {
                // trigger notification
                action.apply();
                // unregister
                unregister(owner);
            }
            return result;
        }

        private void unregister(@NonNull LifecycleOwner owner) {
            owner.getLifecycle().removeObserver(this);
        }
    }
}
