package ru.noties.subscription;

import android.support.annotation.NonNull;

/**
 * An implementation of {@link Subscription} that acts like one with ability to collect
 * multiple {@link Subscription}\'s to be unsubscribed via single call to {@link #unsubscribe()}
 *
 * @see #create()
 * @see #add()
 * @see #remove(Subscription)
 */
public abstract class CompositeSubscription extends Subscription {

    /**
     * Factory method to obtain an instance of {@link CompositeSubscription}.
     * <p>
     * Returned implementations is <b>not thread-safe</b>
     *
     * @return {@link CompositeSubscription}
     */
    @NonNull
    public static CompositeSubscription create() {
        return new CompositeSubscriptionImpl();
    }

    /**
     * {@code
     * final CompositeSubscription composite = CompositeSubscription.create();
     * subscription()
     * .accept(composite.add());
     * }
     *
     * @return {@link ru.noties.subscription.Subscription.Visitor} to add a {@link Subscription}
     * to this {@link CompositeSubscription}
     * @throws IllegalStateException if this subscription has been unsubscribed already
     * @see #remove(Subscription)
     */
    @NonNull
    public abstract Visitor add() throws IllegalStateException;

    /**
     * @param subscription {@link Subscription} to remove from this {@link CompositeSubscription}
     * @see #add()
     */
    public abstract void remove(@NonNull Subscription subscription);

    @NonNull
    @Override
    public CompositeSubscription accept(@NonNull Visitor visitor) {
        super.accept(visitor);
        return this;
    }
}
