package io.noties.lifebus.fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import io.noties.lifebus.Lifebus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class FragmentLifebusTest {

    private static final Field FRAGMENT_MANAGER;

    static {
        final Field f;
        try {
            f = Fragment.class.getDeclaredField("mFragmentManager");
            f.setAccessible(true);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        FRAGMENT_MANAGER = f;
    }

    private Fragment fragment;
    private FragmentManager manager;
    private Lifebus<FragmentEvent> lifebus;

    @Before
    public void beforer() {
        fragment = mock(Fragment.class);
        manager = mock(FragmentManager.class);

        // great, now this method is final :'(
//        when(fragment.getFragmentManager()).thenReturn(manager);
        try {
            FRAGMENT_MANAGER.set(fragment, manager);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }

        lifebus = FragmentLifebus.create(fragment);
    }

    @Test
    public void create_no_manager() {
        try {
            FragmentLifebus.create(mock(Fragment.class));
            fail();
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage(), e.getMessage().startsWith("Provided fragment "));
        }
    }

    @Test
    public void create_registers_callbacks() {
        verify(manager, times(1))
                .registerFragmentLifecycleCallbacks(any(FragmentManager.FragmentLifecycleCallbacks.class), anyBoolean());
    }

    @Test
    public void create_with_manager_registers_callbacks() {

        final Fragment fragment = mock(Fragment.class);
        final FragmentManager manager = mock(FragmentManager.class);

        FragmentLifebus.create(manager, fragment);

        verify(manager, times(1))
                .registerFragmentLifecycleCallbacks(any(FragmentManager.FragmentLifecycleCallbacks.class), anyBoolean());
    }

    @Test
    public void events_triggered() {

        final ArgumentCaptor<FragmentManager.FragmentLifecycleCallbacks> captor =
                ArgumentCaptor.forClass(FragmentManager.FragmentLifecycleCallbacks.class);

        verify(manager, times(1))
                .registerFragmentLifecycleCallbacks(captor.capture(), anyBoolean());

        final FragmentManager.FragmentLifecycleCallbacks callbacks = captor.getValue();

        final Map<FragmentEvent, TriggerEvent> triggers = triggers();

        final List<Lifebus.Action> actions = new ArrayList<>();

        for (FragmentEvent event : FragmentEvent.values()) {
            final Lifebus.Action action = mock(Lifebus.Action.class);
            actions.add(action);
            lifebus.on(event, action);
            triggers.get(event).trigger(callbacks, fragment);
        }

        assertEquals(FragmentEvent.values().length, actions.size());

        for (Lifebus.Action action : actions) {
            verify(action, times(1)).apply();
        }
    }

    @Test
    public void events_triggered_immediate() {

        final ArgumentCaptor<FragmentManager.FragmentLifecycleCallbacks> captor =
                ArgumentCaptor.forClass(FragmentManager.FragmentLifecycleCallbacks.class);

        verify(manager, times(1))
                .registerFragmentLifecycleCallbacks(captor.capture(), anyBoolean());

        final FragmentManager.FragmentLifecycleCallbacks callbacks = captor.getValue();

        final Map<FragmentEvent, TriggerEvent> triggers = triggers();

        for (FragmentEvent event : FragmentEvent.values()) {
            final Lifebus.Action action = mock(Lifebus.Action.class);
            lifebus.on(event, action);
            triggers.get(event).trigger(callbacks, fragment);
            verify(action, times(1)).apply();
        }
    }

    @Test
    public void disposed_on_detach() {

        final ArgumentCaptor<FragmentManager.FragmentLifecycleCallbacks> captor =
                ArgumentCaptor.forClass(FragmentManager.FragmentLifecycleCallbacks.class);

        verify(manager, times(1))
                .registerFragmentLifecycleCallbacks(captor.capture(), anyBoolean());

        final Lifebus.Action action = mock(Lifebus.Action.class);
        lifebus.on(FragmentEvent.DETACH, action);

        final FragmentManager.FragmentLifecycleCallbacks callbacks = captor.getValue();

        callbacks.onFragmentDetached(manager, fragment);

        // action must be triggered
        verify(action, times(1)).apply();

        verify(manager, times(1))
                .unregisterFragmentLifecycleCallbacks(eq(callbacks));

        try {
            lifebus.on(FragmentEvent.CREATE, mock(Lifebus.Action.class));
            fail();
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage(), e.getMessage().startsWith("Already disposed"));
        }
    }

    @Test
    public void detach_for_different_fragment_do_not_dispose() {

        final ArgumentCaptor<FragmentManager.FragmentLifecycleCallbacks> captor =
                ArgumentCaptor.forClass(FragmentManager.FragmentLifecycleCallbacks.class);

        verify(manager, times(1))
                .registerFragmentLifecycleCallbacks(captor.capture(), anyBoolean());

        final FragmentManager.FragmentLifecycleCallbacks callbacks = captor.getValue();

        callbacks.onFragmentDetached(manager, mock(Fragment.class));

        // must not throw
        lifebus.on(FragmentEvent.CREATE, mock(Lifebus.Action.class));
    }

    @Test
    public void event_different_fragment() {

        final ArgumentCaptor<FragmentManager.FragmentLifecycleCallbacks> captor =
                ArgumentCaptor.forClass(FragmentManager.FragmentLifecycleCallbacks.class);

        verify(manager, times(1))
                .registerFragmentLifecycleCallbacks(captor.capture(), anyBoolean());

        final FragmentManager.FragmentLifecycleCallbacks callbacks = captor.getValue();

        final Map<FragmentEvent, TriggerEvent> triggers = triggers();

        final List<Lifebus.Action> actions = new ArrayList<>();

        final Fragment different = mock(Fragment.class);

        for (FragmentEvent event : FragmentEvent.values()) {
            final Lifebus.Action action = mock(Lifebus.Action.class);
            actions.add(action);
            lifebus.on(event, action);
            triggers.get(event).trigger(callbacks, different);
        }

        assertEquals(FragmentEvent.values().length, actions.size());

        for (Lifebus.Action action : actions) {
            verify(action, never()).apply();
        }
    }

    @Test
    public void multiple_actions() {

        final ArgumentCaptor<FragmentManager.FragmentLifecycleCallbacks> captor =
                ArgumentCaptor.forClass(FragmentManager.FragmentLifecycleCallbacks.class);

        verify(manager, times(1))
                .registerFragmentLifecycleCallbacks(captor.capture(), anyBoolean());

        final FragmentManager.FragmentLifecycleCallbacks callbacks = captor.getValue();

        final Lifebus.Action action1 = mock(Lifebus.Action.class);
        final Lifebus.Action action2 = mock(Lifebus.Action.class);

        lifebus
                .on(FragmentEvent.CREATE, action1)
                .on(FragmentEvent.CREATE, action2);

        callbacks.onFragmentCreated(manager, fragment, null);

        verify(action1, times(1)).apply();
        verify(action2, times(1)).apply();
    }

    private interface TriggerEvent {
        void trigger(@NonNull FragmentManager.FragmentLifecycleCallbacks callbacks, @NonNull Fragment fragment);
    }

    @NonNull
    private static Map<FragmentEvent, TriggerEvent> triggers() {

        final Map<FragmentEvent, TriggerEvent> map =
                new EnumMap<FragmentEvent, TriggerEvent>(FragmentEvent.class);

        map.put(FragmentEvent.ATTACH, new TriggerEvent() {
            @Override
            public void trigger(@NonNull FragmentManager.FragmentLifecycleCallbacks callbacks, @NonNull Fragment fragment) {
                callbacks.onFragmentAttached(fragment.getFragmentManager(), fragment, null);
            }
        });

        map.put(FragmentEvent.CREATE, new TriggerEvent() {
            @Override
            public void trigger(@NonNull FragmentManager.FragmentLifecycleCallbacks callbacks, @NonNull Fragment fragment) {
                callbacks.onFragmentCreated(fragment.getFragmentManager(), fragment, null);
            }
        });

        map.put(FragmentEvent.VIEW_CREATED, new TriggerEvent() {
            @Override
            public void trigger(@NonNull FragmentManager.FragmentLifecycleCallbacks callbacks, @NonNull Fragment fragment) {
                callbacks.onFragmentViewCreated(fragment.getFragmentManager(), fragment, null, null);
            }
        });

        map.put(FragmentEvent.START, new TriggerEvent() {
            @Override
            public void trigger(@NonNull FragmentManager.FragmentLifecycleCallbacks callbacks, @NonNull Fragment fragment) {
                callbacks.onFragmentStarted(fragment.getFragmentManager(), fragment);
            }
        });

        map.put(FragmentEvent.RESUME, new TriggerEvent() {
            @Override
            public void trigger(@NonNull FragmentManager.FragmentLifecycleCallbacks callbacks, @NonNull Fragment fragment) {
                callbacks.onFragmentResumed(fragment.getFragmentManager(), fragment);
            }
        });

        map.put(FragmentEvent.PAUSE, new TriggerEvent() {
            @Override
            public void trigger(@NonNull FragmentManager.FragmentLifecycleCallbacks callbacks, @NonNull Fragment fragment) {
                callbacks.onFragmentPaused(fragment.getFragmentManager(), fragment);
            }
        });

        map.put(FragmentEvent.STOP, new TriggerEvent() {
            @Override
            public void trigger(@NonNull FragmentManager.FragmentLifecycleCallbacks callbacks, @NonNull Fragment fragment) {
                callbacks.onFragmentStopped(fragment.getFragmentManager(), fragment);
            }
        });

        map.put(FragmentEvent.SAVE_INSTANCE_STATE, new TriggerEvent() {
            @Override
            public void trigger(@NonNull FragmentManager.FragmentLifecycleCallbacks callbacks, @NonNull Fragment fragment) {
                callbacks.onFragmentSaveInstanceState(fragment.getFragmentManager(), fragment, null);
            }
        });

        map.put(FragmentEvent.VIEW_DESTROYED, new TriggerEvent() {
            @Override
            public void trigger(@NonNull FragmentManager.FragmentLifecycleCallbacks callbacks, @NonNull Fragment fragment) {
                callbacks.onFragmentViewDestroyed(fragment.getFragmentManager(), fragment);
            }
        });

        map.put(FragmentEvent.DESTROY, new TriggerEvent() {
            @Override
            public void trigger(@NonNull FragmentManager.FragmentLifecycleCallbacks callbacks, @NonNull Fragment fragment) {
                callbacks.onFragmentDestroyed(fragment.getFragmentManager(), fragment);
            }
        });

        map.put(FragmentEvent.DETACH, new TriggerEvent() {
            @Override
            public void trigger(@NonNull FragmentManager.FragmentLifecycleCallbacks callbacks, @NonNull Fragment fragment) {
                callbacks.onFragmentDetached(fragment.getFragmentManager(), fragment);
            }
        });

        assertEquals(FragmentEvent.values().length, map.size());

        return map;
    }
}