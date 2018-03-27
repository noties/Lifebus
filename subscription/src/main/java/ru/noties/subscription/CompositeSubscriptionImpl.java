package ru.noties.subscription;

import android.support.annotation.NonNull;

import ru.noties.listeners.Listeners;

class CompositeSubscriptionImpl extends CompositeSubscription {

    private final Listeners<Subscription> subscriptions = Listeners.create(3);

    private boolean unsubscribed;

    @Override
    public void unsubscribe() {
        if (isActive()) {
            unsubscribed = true;
            for (Subscription subscription : subscriptions.begin()) {
                if (subscription.isActive()) {
                    subscription.unsubscribe();
                }
            }
            subscriptions.clear();
        }
    }

    @Override
    public boolean isActive() {
        return !unsubscribed;
    }

    @NonNull
    public Visitor add() {
        if (!isActive()) {
            throw new IllegalStateException("This CompositeSubscription has been already unsubscribed");
        }
        return visitor;
    }

    @Override
    public void remove(@NonNull Subscription subscription) {
        if (isActive()) {
            subscriptions.remove(subscription);
        }
    }

    private final Visitor visitor = new Visitor() {
        @Override
        public void visit(@NonNull Subscription subscription) {
            subscriptions.add(subscription);
        }
    };
}
