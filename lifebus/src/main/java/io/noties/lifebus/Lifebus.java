package io.noties.lifebus;

import androidx.annotation.NonNull;

import io.noties.lifebus.activity.ActivityLifebus;
import io.noties.lifebus.fragment.FragmentLifebus;

/**
 * A simple class that allows dispatching of one-shot arbitrary events. Events are dispatched
 * only once and then receiver is unsubscribed.
 *
 * @see ActivityLifebus
 * @see FragmentLifebus
 */
public abstract class Lifebus<E extends Enum<E>> {

    /**
     * Listener that will be invoked when specific event is dispatched
     */
    public interface Action {
        void apply();
    }

    /**
     * Registers {@link Action} to be invoked when specified event is received.
     * Note that after {@link Action} is invoked (event is delivered) subscription will be expired
     * (unsubscribed), so further notifications must call this method again.
     *
     * @param e      event to listen to
     * @param action {@link Action} to be invoked when event is triggered
     * @return self for chaining
     */
    @SuppressWarnings("UnusedReturnValue")
    @NonNull
    public abstract Lifebus<E> on(@NonNull E e, @NonNull Action action);
}
