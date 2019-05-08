package io.noties.lifebus.activity;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import io.noties.lifebus.Lifebus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ActivityLifebusTest {

    private Activity activity;
    private Application application;
    private Lifebus<ActivityEvent> lifebus;

    @Before
    public void before() {
        activity = mock(Activity.class);
        application = mock(Application.class);
        when(activity.getApplication()).thenReturn(application);
        lifebus = ActivityLifebus.create(activity);
    }

    @Test
    public void create_activity_no_application() {
        // test factory create method for with a single argument when activity has no application

        final Activity activity = mock(Activity.class);
        when(activity.getApplication()).thenReturn(null);

        try {
            ActivityLifebus.create(activity);
            fail();
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage(), e.getMessage().startsWith("Provided activity "));
        }
    }

    @Test
    public void create_registers_activity_lifecycle_callbacks() {
        // when created lifebus immediately registers callbacks

        verify(application, times(1))
                .registerActivityLifecycleCallbacks(any(Application.ActivityLifecycleCallbacks.class));
    }

    @Test
    public void create_with_application_registers_activity_lifecycle_callbacks() {
        // when created lifebus immediately registers callbacks

        final Activity activity = mock(Activity.class);
        final Application application = mock(Application.class);

        ActivityLifebus.create(application, activity);

        verify(application, times(1))
                .registerActivityLifecycleCallbacks(any(Application.ActivityLifecycleCallbacks.class));
    }

    @Test
    public void events_triggered() {

        final ArgumentCaptor<Application.ActivityLifecycleCallbacks> captor =
                ArgumentCaptor.forClass(Application.ActivityLifecycleCallbacks.class);

        verify(application, times(1))
                .registerActivityLifecycleCallbacks(captor.capture());

        final Application.ActivityLifecycleCallbacks callbacks = captor.getValue();

        final Map<ActivityEvent, TriggerEvent> triggers = triggers();

        final List<Lifebus.Action> actions = new ArrayList<>();

        for (ActivityEvent event : ActivityEvent.values()) {
            final Lifebus.Action action = mock(Lifebus.Action.class);
            actions.add(action);
            lifebus.on(event, action);
            triggers.get(event).trigger(callbacks, activity);
        }

        assertEquals(ActivityEvent.values().length, actions.size());

        for (Lifebus.Action action : actions) {
            verify(action, times(1)).apply();
        }
    }

    @Test
    public void events_triggered_immediate() {

        final ArgumentCaptor<Application.ActivityLifecycleCallbacks> captor =
                ArgumentCaptor.forClass(Application.ActivityLifecycleCallbacks.class);

        verify(application, times(1))
                .registerActivityLifecycleCallbacks(captor.capture());

        final Application.ActivityLifecycleCallbacks callbacks = captor.getValue();

        final Map<ActivityEvent, TriggerEvent> triggers = triggers();

        for (ActivityEvent event : ActivityEvent.values()) {
            final Lifebus.Action action = mock(Lifebus.Action.class);
            lifebus.on(event, action);
            triggers.get(event).trigger(callbacks, activity);
            verify(action, times(1)).apply();
        }
    }

    @Test
    public void disposed_on_destroy() {
        // application will have callbacks unregistered
        // further calls to `on` will throw

        final ArgumentCaptor<Application.ActivityLifecycleCallbacks> captor =
                ArgumentCaptor.forClass(Application.ActivityLifecycleCallbacks.class);

        verify(application, times(1))
                .registerActivityLifecycleCallbacks(captor.capture());

        final Lifebus.Action action = mock(Lifebus.Action.class);
        lifebus.on(ActivityEvent.DESTROY, action);

        final Application.ActivityLifecycleCallbacks callbacks = captor.getValue();

        callbacks.onActivityDestroyed(activity);

        // action must be triggered
        verify(action, times(1)).apply();

        verify(application, times(1))
                .unregisterActivityLifecycleCallbacks(eq(callbacks));

        try {
            lifebus.on(ActivityEvent.CREATE, mock(Lifebus.Action.class));
            fail();
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage(), e.getMessage().startsWith("Already disposed"));
        }
    }

    @Test
    public void destroy_for_different_activity_does_not_dispose() {

        final ArgumentCaptor<Application.ActivityLifecycleCallbacks> captor =
                ArgumentCaptor.forClass(Application.ActivityLifecycleCallbacks.class);

        verify(application, times(1))
                .registerActivityLifecycleCallbacks(captor.capture());

        final Application.ActivityLifecycleCallbacks callbacks = captor.getValue();

        callbacks.onActivityDestroyed(mock(Activity.class));

        // must not throw
        lifebus.on(ActivityEvent.CREATE, mock(Lifebus.Action.class));
    }

    @Test
    public void event_different_activity() {
        // will not trigger events for different activities

        final ArgumentCaptor<Application.ActivityLifecycleCallbacks> captor =
                ArgumentCaptor.forClass(Application.ActivityLifecycleCallbacks.class);

        verify(application, times(1))
                .registerActivityLifecycleCallbacks(captor.capture());

        final Application.ActivityLifecycleCallbacks callbacks = captor.getValue();

        final Map<ActivityEvent, TriggerEvent> triggers = triggers();

        final List<Lifebus.Action> actions = new ArrayList<>();

        final Activity different = mock(Activity.class);

        for (ActivityEvent event : ActivityEvent.values()) {
            final Lifebus.Action action = mock(Lifebus.Action.class);
            actions.add(action);
            lifebus.on(event, action);
            triggers.get(event).trigger(callbacks, different);
        }

        assertEquals(ActivityEvent.values().length, actions.size());

        for (Lifebus.Action action : actions) {
            verify(action, never()).apply();
        }
    }

    @Test
    public void multiple_actions() {

        final ArgumentCaptor<Application.ActivityLifecycleCallbacks> captor =
                ArgumentCaptor.forClass(Application.ActivityLifecycleCallbacks.class);

        verify(application, times(1))
                .registerActivityLifecycleCallbacks(captor.capture());

        final Application.ActivityLifecycleCallbacks callbacks = captor.getValue();

        final Lifebus.Action action1 = mock(Lifebus.Action.class);
        final Lifebus.Action action2 = mock(Lifebus.Action.class);

        lifebus
                .on(ActivityEvent.CREATE, action1)
                .on(ActivityEvent.CREATE, action2);

        callbacks.onActivityCreated(activity, null);

        verify(action1, times(1)).apply();
        verify(action2, times(1)).apply();
    }

    @NonNull
    private static Map<ActivityEvent, TriggerEvent> triggers() {

        final Map<ActivityEvent, TriggerEvent> map =
                new EnumMap<ActivityEvent, TriggerEvent>(ActivityEvent.class);

        map.put(ActivityEvent.CREATE, new TriggerEvent() {
            @Override
            public void trigger(@NonNull Application.ActivityLifecycleCallbacks callbacks, @NonNull Activity activity) {
                callbacks.onActivityCreated(activity, null);
            }
        });

        map.put(ActivityEvent.START, new TriggerEvent() {
            @Override
            public void trigger(@NonNull Application.ActivityLifecycleCallbacks callbacks, @NonNull Activity activity) {
                callbacks.onActivityStarted(activity);
            }
        });

        map.put(ActivityEvent.RESUME, new TriggerEvent() {
            @Override
            public void trigger(@NonNull Application.ActivityLifecycleCallbacks callbacks, @NonNull Activity activity) {
                callbacks.onActivityResumed(activity);
            }
        });

        map.put(ActivityEvent.PAUSE, new TriggerEvent() {
            @Override
            public void trigger(@NonNull Application.ActivityLifecycleCallbacks callbacks, @NonNull Activity activity) {
                callbacks.onActivityPaused(activity);
            }
        });

        map.put(ActivityEvent.STOP, new TriggerEvent() {
            @Override
            public void trigger(@NonNull Application.ActivityLifecycleCallbacks callbacks, @NonNull Activity activity) {
                callbacks.onActivityStopped(activity);
            }
        });

        map.put(ActivityEvent.DESTROY, new TriggerEvent() {
            @Override
            public void trigger(@NonNull Application.ActivityLifecycleCallbacks callbacks, @NonNull Activity activity) {
                callbacks.onActivityDestroyed(activity);
            }
        });

        map.put(ActivityEvent.SAVE_INSTANCE_STATE, new TriggerEvent() {
            @Override
            public void trigger(@NonNull Application.ActivityLifecycleCallbacks callbacks, @NonNull Activity activity) {
                callbacks.onActivitySaveInstanceState(activity, null);
            }
        });

        assertEquals(ActivityEvent.values().length, map.size());

        return map;
    }

    private interface TriggerEvent {
        void trigger(@NonNull Application.ActivityLifecycleCallbacks callbacks, @NonNull Activity activity);
    }
}