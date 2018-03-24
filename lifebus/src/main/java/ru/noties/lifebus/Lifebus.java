package ru.noties.lifebus;

import android.support.annotation.NonNull;

/**
 * A simple class that allows dispatching of one-shot arbitrary events. Events are dispatched
 * only once and then receiver is unsubscribed.
 *
 * @see #create(LifebusSource)
 * @see ru.noties.lifebus.activity.ActivityLifebus
 * @see ru.noties.lifebus.fragment.FragmentLifebus
 */
public abstract class Lifebus<E extends Enum<E>> {

    /**
     * Listener that will be invoked when specific event is dispatched
     */
    public interface Action {
        void apply();
    }

    /**
     * Factory method to create an instance of the {@link Lifebus} with specified {@link LifebusSource}
     *
     * @param source {@link LifebusSource} to operate with
     * @param <E>    type of an event
     * @return an instance of {@link Lifebus}
     */
    @NonNull
    public static <E extends Enum<E>> Lifebus<E> create(@NonNull LifebusSource<E> source) {
        return new LifebusImpl<>(source);
    }

    /**
     * Registers {@link Action} to be invoked when specified event is delivered by {@link LifebusSource}.
     * Note that after {@link Action} is invoked (event is delivered) subscription will be expired
     * (unsubscribed), so further notifications must call this method again.
     *
     * @param e      event to listen to
     * @param action {@link Action} to be invoked when event is triggered
     * @return self for chaining
     */
    @NonNull
    public abstract Lifebus<E> on(@NonNull E e, @NonNull Action action);
}
