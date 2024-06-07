package de.fabmax.kool.editor.api

enum class EntityLifecycle {
    /**
     * Initial state: Entity and components are instantiated but not yet fully set up.
     */
    CREATED {
        override val allowedNextStates: List<EntityLifecycle> by lazy { listOf(PREPARED) }
    },

    /**
     * Entity and components are fully initialized:
     *  - All resources loaded
     *  - All component references are in place
     *
     * Most dynamic stuff is still disabled: Physics is not yet running and [KoolBehavior] update callbacks
     * are not yet called.
     * This is the state, where the editor edit mode operates.
     */
    PREPARED {
        override val allowedNextStates: List<EntityLifecycle> by lazy { listOf(RUNNING, DETACHED) }
    },

    /**
     * The main play state. All systems active.
     */
    RUNNING {
        override val allowedNextStates: List<EntityLifecycle> by lazy { listOf(DESTROYED, DETACHED) }
    },

    /**
     * All resources are disposed and the entity can be discarded. This is the final state and entities in
     * this state can not be used anymore.
     */
    DESTROYED {
        override val allowedNextStates: List<EntityLifecycle> = emptyList()
    },

    /**
     * todo: yes or no?
     * Somewhat special state for entities, that are not yet destroyed but also not attached to an active scene.
     */
    DETACHED {
        override val allowedNextStates: List<EntityLifecycle> by lazy { listOf(PREPARED, RUNNING) }
    };

    abstract val allowedNextStates: List<EntityLifecycle>

    fun isAllowedAsNext(lifecycle: EntityLifecycle) = lifecycle in allowedNextStates
}