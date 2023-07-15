package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.model.EditorNodeModel
import kotlin.reflect.KClass

abstract class EditorModelComponent(open val nodeModel: EditorNodeModel) {

    private val _dependencies: MutableList<ComponentDependency> = mutableListOf()
    val dependencies: List<ComponentDependency>
        get() = _dependencies

    var isCreated: Boolean = false
        protected set

    var componentOrder = COMPONENT_ORDER_DEFAULT
        protected set

    open suspend fun createComponent() {
        isCreated = true
        require(areDependenciesMetBy(nodeModel.components)) {
            "Unable to create component ${this::class.simpleName} in node ${nodeModel.name}: There are unmet component dependencies"
        }
    }

    protected fun dependsOn(componentType: KClass<*>, isOptional: Boolean = false) {
        _dependencies += ComponentDependency(componentType, isOptional)
    }

    fun areDependenciesMetBy(components: List<EditorModelComponent>): Boolean {
        return dependencies.all { dep -> dep.isOptional || components.any { s -> dep.type.isInstance(s) } }
    }

    open fun onNodeRemoved() { }
    open fun onNodeAdded() { }

    data class ComponentDependency(val type: KClass<*>, val isOptional: Boolean)

    companion object {
        const val COMPONENT_ORDER_EARLY = -100
        const val COMPONENT_ORDER_DEFAULT = 0
        const val COMPONENT_ORDER_LATE = 100
    }
}

fun MutableList<EditorModelComponent>.sortByDependencies() {
    val sorted = mutableListOf<EditorModelComponent>()

    // pre sort components: early order first
    sortBy { it.componentOrder }

    while (isNotEmpty()) {
        val iter = iterator()
        var lastAdded: EditorModelComponent? = null
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