package io.noties.lifebus;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * @since 1.1.0
 */
public abstract class BaseLifebus<T, E extends Enum<E>> extends Lifebus<E> {

    private T owner;
    private final E disposeEvent;

    private final Map<E, List<Action>> map;

    private boolean isDisposed;

    protected BaseLifebus(@NonNull T owner, @NonNull Class<E> event, @NonNull E disposeEvent) {
        this.owner = owner;
        this.disposeEvent = disposeEvent;
        this.map = new EnumMap<E, List<Action>>(event);
    }

    private void dispose() {
        map.clear();
        owner = null;
        isDisposed = true;
    }

    @NonNull
    @Override
    public final Lifebus<E> on(@NonNull E e, @NonNull Action action) {
        if (isDisposed) {
            throw new IllegalStateException("Already disposed, event: " + e);
        }
        ensureEventActions(e).add(action);
        onEventAdded(e);
        return this;
    }

    /**
     * @since 1.2.0-SNAPSHOT
     */
    @NonNull
    protected T requireOwner() {
        T owner = this.owner;
        if (owner == null) {
            throw new IllegalStateException("Lifebus instance is disposed, this: " + this);
        }
        return owner;
    }

    /**
     * @since 1.2.0-SNAPSHOT
     */
    protected void onEventAdded(@NonNull E e) {

    }

    @NonNull
    private List<Action> ensureEventActions(@NonNull E e) {
        List<Action> actions = map.get(e);
        if (actions == null) {
            actions = new ArrayList<>(2);
            map.put(e, actions);
        }
        return actions;
    }

    /**
     * @return flag indicating if event was dispatched to owner (argument `t` equals owner)
     */
    protected boolean triggerEventNotification(@NonNull T t, @NonNull E e) {

        if (isDisposed) {
            throw new IllegalStateException("Already disposed, event: " + e);
        }

        final boolean result = owner == t;
        if (result) {

            final List<Action> actions = map.remove(e);
            if (actions != null) {
                for (Action action : actions) {
                    action.apply();
                }
            }

            if (disposeEvent == e) {
                dispose();
            }
        }

        return result;
    }
}
