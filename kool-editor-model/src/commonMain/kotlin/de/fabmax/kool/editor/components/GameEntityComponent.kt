package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.api.*
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.util.logW
import kotlin.reflect.KClass

val GameEntityComponent.project: EditorProject get() = gameEntity.scene.project
val GameEntityComponent.scene: EditorScene get() = gameEntity.scene
val GameEntityComponent.sceneEntity: GameEntity get() = gameEntity.scene.sceneEntity
val GameEntityComponent.sceneComponent: SceneComponent get() = sceneEntity.requireComponent()

abstract class GameEntityComponent(val gameEntity: GameEntity) {

    open val componentType: String
        get() = this::class.simpleName!!

    private val _dependencies: MutableList<ComponentDependency> = mutableListOf()
    val dependencies: List<ComponentDependency>
        get() = _dependencies

    val requiredAssets = mutableSetOf<AssetReference>()

    var lifecycle = EntityLifecycle.CREATED
        protected set(value) {
            check(field.isAllowedAsNext(value)) {
                "GameEntityComponent $componentType (entity: ${gameEntity.name}): Transitioning from lifecycle state $field to $value is not allowed"
            }
            field = value
        }

    var componentOrder = COMPONENT_ORDER_DEFAULT
        protected set

    protected fun checkDependencies() {
        check(areDependenciesMetBy(gameEntity.components)) {
            "Unable to create component ${this::class.simpleName} in node ${gameEntity.name}: There are unmet component dependencies"
        }
    }

    open suspend fun applyComponent() {
        checkDependencies()
        lifecycle = EntityLifecycle.PREPARED
    }

    open fun destroyComponent() {
        if (isDestroyed) {
            logW { "Component $componentType destroyed multiple times (entity: ${gameEntity.name})" }
        }
        lifecycle = EntityLifecycle.DESTROYED
    }

    open fun onStart() {
        lifecycle = EntityLifecycle.RUNNING
    }

    open fun onUpdate(ev: RenderPass.UpdateEvent) { }

    open fun onPhysicsUpdate(timeStep: Float) { }

    protected fun dependsOn(componentType: KClass<*>, isOptional: Boolean = false) {
        _dependencies += ComponentDependency(componentType, isOptional)
    }

    fun areDependenciesMetBy(components: List<GameEntityComponent>): Boolean {
        return dependencies.all { dep -> dep.isOptional || components.any { s -> dep.type.isInstance(s) } }
    }

    data class ComponentDependency(val type: KClass<*>, val isOptional: Boolean)

    companion object {
        const val COMPONENT_ORDER_EARLY = -100
        const val COMPONENT_ORDER_DEFAULT = 0
        const val COMPONENT_ORDER_LATE = 100
    }
}

fun MutableList<GameEntityComponent>.sortByDependencies() {
    val sorted = mutableListOf<GameEntityComponent>()

    // pre sort components: early order first
    sortBy { it.componentOrder }

    while (isNotEmpty()) {
        val iter = iterator()
        var lastAdded: GameEntityComponent? = null
        while (iter.hasNext()) {
            val candidate = iter.next()

            if (lastAdded != null && lastAdded.componentOrder < candidate.componentOrder) {
                // do not add different componentOrder components in the same loop iteration
                // we might have skipped other components with earlier order because of missing dependencies
                break
            }

            // check whether candidate component has optional dependencies to yet unprocessed components
            // if so, areDependenciesMetBy() would return true (because unmet dependencies are optional) and
            // candidate would be added early, although we could fulfill the optional dependencies as well
            val unsortedOptionalDeps = candidate.dependencies
                .filter { it.isOptional }
                .any { optDep -> any { optDep.type.isInstance(it) } }

            if (!unsortedOptionalDeps && candidate.areDependenciesMetBy(sorted)) {
                sorted += candidate
                iter.remove()
                lastAdded = candidate
            }
        }
        if (lastAdded == null) {
            throw IllegalStateException("Unable to sort EditModelComponents (missing or cyclic component dependencies)")
        }
    }
    addAll(sorted)
}