package ru.noties.subscription;

import android.support.annotation.NonNull;

class SubscriptionImpl extends Subscription {

    private final UnsubscribeAction unsubscribeAction;

    private boolean unsubscribed;

    SubscriptionImpl(@NonNull UnsubscribeAction unsubscribeAction) {
        this.unsubscribeAction = unsubscribeAction;
    }

    @Override
    public void unsubscribe() {
        if (isActive()) {
            unsubscribed = true;
            unsubscribeAction.apply(this);
        }
    }

    @Override
    public boolean isActive() {
        return !unsubscribed;
    }
}
