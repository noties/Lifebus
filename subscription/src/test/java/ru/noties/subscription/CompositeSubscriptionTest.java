package ru.noties.subscription;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class CompositeSubscriptionTest {

    @Test
    public void add_to_unsubscribed_throws() {

        final CompositeSubscription subscription = CompositeSubscription.create();

        mock(Subscription.class)
                .accept(subscription.add());

        assertTrue(subscription.isActive());

        subscription.unsubscribe();

        assertFalse(subscription.isActive());

        try {
            mock(Subscription.class)
                    .accept(subscription.add());
            assertTrue(false);
        } catch (IllegalStateException e) {
            assertTrue(true);
        }
    }
}
