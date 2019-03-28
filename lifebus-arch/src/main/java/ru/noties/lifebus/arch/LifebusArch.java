package ru.noties.lifebus.arch;

import android.arch.lifecycle.DefaultLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public abstract class LifebusArch {

    public interface Action {
        void apply();
    }

    @NonNull
    public static LifebusArch create(@NonNull LifecycleOwner lifecycleOwner) {
        return new Impl(lifecycleOwner);
    }

    /**
     * @param event  to be notified about
     * @param action {@link Action} to be triggered when lifecycle event occurs
     * @return self for chaining
     */
    @SuppressWarnings("UnusedReturnValue")
    @NonNull
    public abstract LifebusArch on(
            @NonNull Lifecycle.Event event,
            @NonNull Action action);


    static class Impl extends LifebusArch {

        private LifecycleOwner lifecycleOwner;

        private final Map<Lifecycle.Event, List<Action>> map =
                new EnumMap<Lifecycle.Event, List<Action>>(Lifecycle.Event.class);

        private boolean isDisposed;

        Impl(@NonNull LifecycleOwner lifecycleOwner) {
            this.lifecycleOwner = lifecycleOwner;
            this.lifecycleOwner.getLifecycle().addObserver(new ObserverImpl());
        }

        @NonNull
        @Override
        public LifebusArch on(@NonNull Lifecycle.Event event, @NonNull Action action) throws IllegalStateException {

            if (isDisposed) {
                throw new IllegalStateException("Already disposed, event: " + event);
            }

            ensureEventActions(event).add(action);

            return this;
        }

        @NonNull
        private List<Action> ensureEventActions(@NonNull Lifecycle.Event event) {
            List<Action> actions = map.get(event);
            if (actions == null) {
                actions = new ArrayList<>(2);
                map.put(event, actions);
            }
            return actions;
        }

        private void triggerEventActions(@NonNull Lifecycle.Event event) {

            if (isDisposed) {
                throw new IllegalStateException("Already disposed, event: " + event);
            }

            final List<Action> actions = map.remove(event);

            if (actions != null) {
                for (Action action : actions) {
                    action.apply();
                }
            }

            if (event != Lifecycle.Event.ON_ANY) {
                triggerEventActions(Lifecycle.Event.ON_ANY);
            }
        }

        class ObserverImpl implements DefaultLifecycleObserver {
            @Override
            public void onCreate(@NonNull LifecycleOwner owner) {
                triggerEventActions(Lifecycle.Event.ON_CREATE);
            }

            @Override
            public void onStart(@NonNull LifecycleOwner owner) {
                triggerEventActions(Lifecycle.Event.ON_START);
            }

            @Override
            public void onResume(@NonNull LifecycleOwner owner) {
                triggerEventActions(Lifecycle.Event.ON_RESUME);
            }

            @Override
            public void onPause(@NonNull LifecycleOwner owner) {
                triggerEventActions(Lifecycle.Event.ON_PAUSE);
            }

            @Override
            public void onStop(@NonNull LifecycleOwner owner) {
                triggerEventActions(Lifecycle.Event.ON_STOP);
            }

            @Override
            public void onDestroy(@NonNull LifecycleOwner owner) {
                triggerEventActions(Lifecycle.Event.ON_DESTROY);

                owner.getLifecycle().removeObserver(this);
                map.clear();
                isDisposed = true;
            }
        }
    }
}
