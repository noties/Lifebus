package ru.noties.lifebus.view;

import android.support.annotation.NonNull;
import android.view.View;

import ru.noties.lifebus.LifebusSource;
import ru.noties.listeners.Listeners;
import ru.noties.subscription.CompositeSubscription;
import ru.noties.subscription.Subscription;

/**
 * @since 1.0.2
 */
public class ViewLifebusSource implements LifebusSource<ViewEvent> {

    /**
     * Factory method to create an instance of {@link ViewLifebusSource}
     *
     * @param view to listen to {@link ViewEvent}
     * @return {@link ViewLifebusSource}
     */
    @NonNull
    public static ViewLifebusSource create(@NonNull View view) {
        return new ViewLifebusSource(view);
    }

    private View view;

    private final Listeners<Listener<ViewEvent>> listeners = Listeners.create(2);

    private final CompositeSubscription compositeSubscription = CompositeSubscription.create();

    private ViewLifebusSource(@NonNull View view) {
        this.view = view;
        this.view.addOnAttachStateChangeListener(new OnAttachStateChangeListenerImpl());
    }

    @NonNull
    @Override
    public Subscription registerListener(@NonNull final Listener<ViewEvent> listener) {

        final Subscription.UnsubscribeAction action = new Subscription.UnsubscribeAction() {
            @Override
            public void apply(@NonNull Subscription subscription) {
                listeners.remove(listener);
                compositeSubscription.remove(subscription);
            }
        };

        listeners.add(listener);

        return Subscription.create(action)
                .accept(compositeSubscription.add());
    }

    private class OnAttachStateChangeListenerImpl implements View.OnAttachStateChangeListener {

        @Override
        public void onViewAttachedToWindow(View v) {
            if (view == v) {
                dispatchViewEvent(ViewEvent.ATTACH);
            }
        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            if (view == v) {

                dispatchViewEvent(ViewEvent.DETACH);

                listeners.clear();
                compositeSubscription.unsubscribe();

                view = null;
            }
        }

        private void dispatchViewEvent(@NonNull ViewEvent event) {
            for (Listener<ViewEvent> listener : listeners.begin()) {
                listener.onEvent(event);
            }
        }
    }
}
