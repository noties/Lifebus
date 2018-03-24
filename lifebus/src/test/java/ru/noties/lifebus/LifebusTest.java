package ru.noties.lifebus;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import ru.noties.subscription.Subscription;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class LifebusTest {

    enum Event {
        FIRST
    }

    @Test
    public void on_registers_listener() {

        final LifebusSource source = mock(LifebusSource.class);
        final Subscription subscription = Subscription.create(mock(Subscription.UnsubscribeAction.class));
        final ArgumentCaptor<LifebusSource.Listener> captor = ArgumentCaptor.forClass(LifebusSource.Listener.class);

        //noinspection unchecked
        when(source.registerListener(any(LifebusSource.Listener.class))).thenReturn(subscription);

        final Lifebus lifebus = Lifebus.create(source);
        final Lifebus.Action action = mock(Lifebus.Action.class);

        //noinspection unchecked
        lifebus.on(Event.FIRST, action);

        //noinspection unchecked
        verify(source, times(1)).registerListener(captor.capture());

        assertTrue(subscription.isActive());

        //noinspection unchecked
        captor.getValue().onEvent(Event.FIRST);

        verify(action, times(1)).apply();

        assertFalse(subscription.isActive());
    }
}
