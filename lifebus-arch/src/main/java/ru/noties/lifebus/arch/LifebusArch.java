package ru.noties.lifebus.arch;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;

public abstract class LifebusArch {

    public interface Action {
        void apply();
    }

    @NonNull
    public static LifebusArch create(@NonNull LifecycleOwner lifecycleOwner) {
        return new LifebusArchImpl(lifecycleOwner);
    }

    /**
     * @param event  to be notified about
     * @param action {@link Action} to be triggered when lifecycle event occurs
     * @return self for chaining
     * @throws IllegalStateException if Lifecycle.Event.ON_ANY is passed as an argument
     */
    @SuppressWarnings("UnusedReturnValue")
    @NonNull
    public abstract LifebusArch on(
            @NonNull Lifecycle.Event event,
            @NonNull Action action
    ) throws IllegalStateException;
}
