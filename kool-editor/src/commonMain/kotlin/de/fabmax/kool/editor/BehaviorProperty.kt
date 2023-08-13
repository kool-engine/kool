package de.fabmax.kool.editor

import de.fabmax.kool.editor.components.BehaviorComponent
import kotlin.reflect.KType

class BehaviorProperty(
    val name: String,
    val type: KType,
    val label: String,
    val min: Double,
    val max: Double
) {
    val isRanged: Boolean
        get() = min > Float.NEGATIVE_INFINITY && max < Float.POSITIVE_INFINITY

    fun get(behaviorComponent: BehaviorComponent): Any? {
        return behaviorComponent.getProperty(name)
    }

    fun set(behaviorComponent: BehaviorComponent, value: Any): Boolean {
        return behaviorComponent.setProperty(name, value)
    }
}
