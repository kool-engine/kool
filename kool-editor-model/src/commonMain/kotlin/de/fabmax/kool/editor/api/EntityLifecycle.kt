package de.fabmax.kool.editor.api

import de.fabmax.kool.editor.components.GameEntityComponent

enum class EntityLifecycle(val step: Int) {
    /**
     * Initial state: Entity and components are instantiated but not yet fully set up.
     */
    CREATED(0) {
        override val allowedNextStates: List<EntityLifecycle> by lazy { listOf(PREPARED, DESTROYED) }
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
    PREPARED(1) {
        override val allowedNextStates: List<EntityLifecycle> by lazy { listOf(RUNNING, DESTROYED) }
    },

    /**
     * The main play state. All systems active.
     */
    RUNNING(2) {
        override val allowedNextStates: List<EntityLifecycle> by lazy { listOf(DESTROYED) }
    },

    /**
     * All resources are disposed and the entity can be discarded. This is the final state and entities in
     * this state can not be used anymore.
     */
    DESTROYED(3) {
        override val allowedNextStates: List<EntityLifecycle> = emptyList()
    };

    abstract val allowedNextStates: List<EntityLifecycle>

    fun isAllowedAsNext(lifecycle: EntityLifecycle) = lifecycle in allowedNextStates
}

val GameEntity.isCreated: Boolean get() = lifecycle == EntityLifecycle.CREATED
val GameEntity.isPrepared: Boolean get() = lifecycle == EntityLifecycle.PREPARED
val GameEntity.isRunning: Boolean get() = lifecycle == EntityLifecycle.RUNNING
val GameEntity.isDestroyed: Boolean get() = lifecycle == EntityLifecycle.DESTROYED
val GameEntity.isPreparedOrRunning: Boolean
    get() = lifecycle == EntityLifecycle.PREPARED || lifecycle == EntityLifecycle.RUNNING


val GameEntityComponent.isCreated: Boolean get() = lifecycle == EntityLifecycle.CREATED
val GameEntityComponent.isPrepared: Boolean get() = lifecycle == EntityLifecycle.PREPARED
val GameEntityComponent.isRunning: Boolean get() = lifecycle == EntityLifecycle.RUNNING
val GameEntityComponent.isDestroyed: Boolean get() = lifecycle == EntityLifecycle.DESTROYED
val GameEntityComponent.isPreparedOrRunning: Boolean
    get() = lifecycle == EntityLifecycle.PREPARED || lifecycle == EntityLifecycle.RUNNING


val EditorScene.isCreated: Boolean get() = lifecycle == EntityLifecycle.CREATED
val EditorScene.isPrepared: Boolean get() = lifecycle == EntityLifecycle.PREPARED
val EditorScene.isRunning: Boolean get() = lifecycle == EntityLifecycle.RUNNING
val EditorScene.isDestroyed: Boolean get() = lifecycle == EntityLifecycle.DESTROYED
val EditorScene.isPreparedOrRunning: Boolean
    get() = lifecycle == EntityLifecycle.PREPARED || lifecycle == EntityLifecycle.RUNNING