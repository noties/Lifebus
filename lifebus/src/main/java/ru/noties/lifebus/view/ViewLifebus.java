package ru.noties.lifebus.view;

import android.support.annotation.NonNull;
import android.view.View;

import ru.noties.lifebus.Lifebus;

/**
 * @since 1.0.2
 */
public abstract class ViewLifebus extends Lifebus<ViewEvent> {

    /**
     * Factory method to create an instance of {@link ViewLifebus}
     *
     * @param view to listen to {@link ViewEvent}
     * @return {@link ViewLifebus}
     */
    @NonNull
    public static ViewLifebus create(@NonNull View view) {
        return new Impl(Lifebus.create(ViewLifebusSource.create(view)));
    }

    static class Impl extends ViewLifebus {

        private final Lifebus<ViewEvent> lifebus;

        Impl(@NonNull Lifebus<ViewEvent> lifebus) {
            this.lifebus = lifebus;
        }

        @NonNull
        @Override
        public Lifebus<ViewEvent> on(@NonNull ViewEvent viewEvent, @NonNull Action action) {
            lifebus.on(viewEvent, action);
            return this;
        }
    }
}
