package ru.noties.lifebus;

import android.support.annotation.NonNull;

import ru.noties.subscription.Subscription;

class LifebusImpl<E extends Enum<E>> extends Lifebus<E> {


    private final LifebusSource<E> source;


    LifebusImpl(@NonNull LifebusSource<E> source) {
        this.source = source;
    }

    @NonNull
    @Override
    public Lifebus<E> on(@NonNull E e, @NonNull Action action) {
        ListenerImpl.register(source, e, action);
        return this;
    }


    private static class ListenerImpl<E extends Enum<E>> implements LifebusSource.Listener<E> {

        static <E extends Enum<E>> void register(@NonNull LifebusSource<E> source, @NonNull E e, @NonNull Action action) {
            new ListenerImpl<>(source, e, action);
        }

        private final Subscription subscription;
        private final E e;
        private final Action action;

        private ListenerImpl(@NonNull LifebusSource<E> source, @NonNull E e, @NonNull Action action) {
            this.e = e;
            this.action = action;
            this.subscription = source.registerListener(this);
        }

        @Override
        public void onEvent(@NonNull E e) {
            if (this.e == e) {

                action.apply();

                // unregister
                subscription.unsubscribe();
            }
        }
    }
}
