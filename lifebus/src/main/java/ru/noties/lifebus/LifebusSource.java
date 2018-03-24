package ru.noties.lifebus;

import android.support.annotation.NonNull;

import ru.noties.subscription.Subscription;

/**
 * Abstraction for event holder and dispatcher logic
 *

 * @see ru.noties.lifebus.activity.ActivityLifebusSource
 * @see ru.noties.lifebus.fragment.FragmentLifebusSource
 */
public interface LifebusSource<E extends Enum<E>> {

    /**
     * Listener to be notified about dispatched events
     *

     * @see #registerListener(Listener)
     */
    interface Listener<E extends Enum<E>> {
        void onEvent(@NonNull E e);
    }

    /**
     * @param listener {@link Listener} to register
     * @return a Subscription that encapsulates current subscription
     */
    @NonNull
    Subscription registerListener(@NonNull Listener<E> listener);
}
