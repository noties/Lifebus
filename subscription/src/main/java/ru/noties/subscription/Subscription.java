package ru.noties.subscription;

import android.support.annotation.NonNull;

/**
 * Simple abstraction to represent a subscription
 *
 * @see #create(UnsubscribeAction)
 * @see CompositeSubscription
 */
public abstract class Subscription {

    /**
     * Visitor for a {@link Subscription}. Can be useful to unsubscribe in a fluent way:
     * {@code
     * subscription()
     * .accept(s -> onSomeEvent(() -> s.unsubscribe()));
     * }
     */
    public interface Visitor {

        /**
         * @param subscription {@link Subscription} to visit
         */
        void visit(@NonNull Subscription subscription);
    }

    /**
     * Simple interface to create a {@link Subscription} with a lambda:
     * {@code
     * Subscription.create(s -> doThisWhenUnsubscribes());
     * }
     */
    public interface UnsubscribeAction {
        void apply(@NonNull Subscription subscription);
    }

    /**
     * Factory method to create a {@link Subscription}. Please note that returned {@link Subscription}
     * is <b>not thread-safe</b>, so one have to manually wrap it with own synchronization logic
     *
     * @param unsubscribeAction {@link UnsubscribeAction} to invoke when {@link #unsubscribe()}
     *                          will be called
     * @return a {@link Subscription}
     */
    @NonNull
    public static Subscription create(@NonNull UnsubscribeAction unsubscribeAction) {
        return new SubscriptionImpl(unsubscribeAction);
    }

    /**
     * Requests this {@link Subscription} to unsubscribe
     */
    public abstract void unsubscribe();

    /**
     * @return boolean indicating if this {@link Subscription} is still active
     */
    public abstract boolean isActive();


    /**
     * @param visitor {@link Visitor} to accept
     * @return self for chaining
     * @see CompositeSubscription#add()
     */
    @NonNull
    public Subscription accept(@NonNull Visitor visitor) {
        visitor.visit(this);
        return this;
    }
}
