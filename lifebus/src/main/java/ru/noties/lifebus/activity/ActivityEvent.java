package ru.noties.lifebus.activity;

public enum ActivityEvent {

    CREATE,
    START,
    RESUME,
    PAUSE,
    STOP,

    /**
     * @since 1.1.0-SNAPSHOT
     */
    SAVE_INSTANCE_STATE,

    DESTROY,
}
