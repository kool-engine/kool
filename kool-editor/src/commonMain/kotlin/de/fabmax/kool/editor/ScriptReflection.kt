package de.fabmax.kool.editor

import de.fabmax.kool.editor.model.ScriptComponent
import kotlin.reflect.KClass
import kotlin.reflect.KType

expect object ScriptReflection {
    val editableTypes: Set<KClass<*>>

    fun getEditableProperties(scriptClass: KClass<*>): List<ScriptProperty>
}

class ScriptProperty(
    val name: String,
    val type: KType,
    val label: String,
    val min: Float,
    val max: Float
) {
    val isRanged: Boolean
        get() = min > Float.NEGATIVE_INFINITY && max < Float.POSITIVE_INFINITY

    fun get(scriptComponent: ScriptComponent): Any? {
        return scriptComponent.getProperty(name)
    }

    fun set(scriptComponent: ScriptComponent, value: Any): Boolean {
        return scriptComponent.setProperty(name, value)
    }
}
