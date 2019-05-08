package io.noties.lifebus.sample.custom;

import android.support.annotation.NonNull;
import android.view.View;

import io.noties.lifebus.BaseLifebus;
import io.noties.lifebus.Lifebus;

public abstract class ViewLifebus extends BaseLifebus<View, ViewEvent> {

    @NonNull
    public static Lifebus<ViewEvent> create(@NonNull View view) {
        return new Impl(view);
    }

    ViewLifebus(@NonNull View owner, @NonNull Class<ViewEvent> event, @NonNull ViewEvent disposeEvent) {
        super(owner, event, disposeEvent);
    }

    static class Impl extends ViewLifebus implements View.OnAttachStateChangeListener {

        Impl(@NonNull View owner) {
            super(owner, ViewEvent.class, ViewEvent.DETACHED);

            owner.addOnAttachStateChangeListener(this);
        }

        @Override
        protected void onEventAdded(@NonNull ViewEvent viewEvent) {
            // if we have any special events we can handle them here
            // for example, add on-pre-draw-listener

            // we can obtain owner for additional configuration
//            final View view = requireOwner();
        }

        @Override
        public void onViewAttachedToWindow(View v) {
            triggerEventNotification(v, ViewEvent.ATTACHED);
        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            if (triggerEventNotification(v, ViewEvent.DETACHED)) {
                v.removeOnAttachStateChangeListener(this);
            }
        }
    }
}
