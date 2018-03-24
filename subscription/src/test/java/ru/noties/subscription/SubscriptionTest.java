package ru.noties.subscription;

import android.support.annotation.NonNull;

import org.junit.Test;

import ru.noties.subscription.Subscription.UnsubscribeAction;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static ru.noties.subscription.Subscription.create;

public class SubscriptionTest {

    @Test
    public void initially_active() {
        assertTrue(create(mock(UnsubscribeAction.class)).isActive());
    }

    @Test
    public void unsubscribe_deactivates() {

        final UnsubscribeAction action = mock(UnsubscribeAction.class);
        final Subscription subscription = Subscription.create(action);
        subscription.unsubscribe();

        assertFalse(subscription.isActive());

        verify(action, times(1)).apply(subscription);
    }

    @Test
    public void visitor_accepted() {

        final Subscription.Visitor visitor = mock(Subscription.Visitor.class);
        final Subscription subscription = Subscription.create(mock(UnsubscribeAction.class));

        subscription.accept(visitor);

        verify(visitor, times(1)).visit(subscription);
    }

    @Test
    public void visitor_unsubscribes() {

        final Subscription.Visitor visitor = new Subscription.Visitor() {
            @Override
            public void visit(@NonNull Subscription subscription) {
                subscription.unsubscribe();
            }
        };

        final UnsubscribeAction action = mock(UnsubscribeAction.class);
        final Subscription subscription = Subscription.create(action);

        subscription.accept(visitor);

        assertFalse(subscription.isActive());

        verify(action, times(1)).apply(subscription);
    }
}
