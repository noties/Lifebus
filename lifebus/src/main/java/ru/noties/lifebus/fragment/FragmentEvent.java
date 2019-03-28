package ru.noties.lifebus.fragment;

public enum FragmentEvent {

    ATTACH,
    CREATE,

    /**
     * @since 1.1.0 renamed from `CREATE_VIEW`
     */
    VIEW_CREATED,

    START,
    RESUME,
    PAUSE,
    STOP,

    /**
     * @since 1.1.0
     */
    SAVE_INSTANCE_STATE,

    /**
     * @since 1.1.0 renamed from `DESTROY_VIEW`
     */
    VIEW_DESTROYED,

    DESTROY,

    DETACH,
}
