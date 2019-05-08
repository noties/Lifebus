package io.noties.lifebus.activity;

public enum ActivityEvent {

    CREATE,
    START,
    RESUME,
    PAUSE,
    STOP,

    /**
     * @since 1.1.0
     */
    SAVE_INSTANCE_STATE,

    DESTROY,
}
