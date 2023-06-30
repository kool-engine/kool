package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.model.EditorNodeModel
import kotlin.reflect.KClass

abstract class EditorModelComponent {

    private val _dependencies: MutableList<ComponentDependency> = mutableListOf()
    val dependencies: List<ComponentDependency>
        get() = _dependencies

    var componentOrder = COMPONENT_ORDER_DEFAULT
        protected set

    abstract suspend fun createComponent(nodeModel: EditorNodeModel)
    abstract suspend fun initComponent(nodeModel: EditorNodeModel)

    protected fun dependsOn(componentType: KClass<*>, isOptional: Boolean = false) {
        _dependencies += ComponentDependency(componentType, isOptional)
    }

    fun areDependenciesMetBy(components: List<EditorModelComponent>): Boolean {
        return dependencies.all { dep -> dep.isOptional || components.any { s -> dep.type.isInstance(s) } }
    }

    open fun onNodeRemoved(nodeModel: EditorNodeModel) { }
    open fun onNodeAdded(nodeModel: EditorNodeModel) { }

    data class ComponentDependency(val type: KClass<*>, val isOptional: Boolean)

    companion object {
        const val COMPONENT_ORDER_EARLY = -100
        const val COMPONENT_ORDER_DEFAULT = 0
        const val COMPONENT_ORDER_LATE = 100
    }
}

fun MutableList<EditorModelComponent>.sortByDependencies() {
    val sorted = mutableListOf<EditorModelComponent>()

    sortBy { it.componentOrder }

    while (isNotEmpty()) {
        val iter = iterator()
        var anyAdded = false
        while (iter.hasNext()) {
            val candidate = iter.next()

            // check whether candidate component has optional dependencies to yet unprocessed components
            // if so, areDependenciesMetBy() would return true (because unmet dependencies are optional) and
            // candidate would be added early, although we could fulfill the optional dependencies as well
            val unsortedOptionalDeps = candidate.dependencies
                .filter { it.isOptional }
                .any { optDep -> any { optDep.type.isInstance(it) } }

            if (!unsortedOptionalDeps && candidate.areDependenciesMetBy(sorted)) {
                sorted += candidate
                iter.remove()
                anyAdded = true
            }
        }
        if (!anyAdded) {
            throw IllegalStateException("Unable to sort EditModelComponents (missing or cyclic component dependencies)")
        }
    }
    addAll(sorted)
}