package de.fabmax.kool.editor

import de.fabmax.kool.editor.components.BehaviorComponent
import de.fabmax.kool.math.*
import kotlin.reflect.KClass

class BehaviorProperty(
    val name: String,
    val type: KClass<*>,
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

    fun getDouble(behaviorComponent: BehaviorComponent) = get(behaviorComponent) as Double
    fun getVec2d(behaviorComponent: BehaviorComponent) = get(behaviorComponent) as Vec2d
    fun getVec3d(behaviorComponent: BehaviorComponent) = get(behaviorComponent) as Vec3d
    fun getVec4d(behaviorComponent: BehaviorComponent) = get(behaviorComponent) as Vec4d

    fun getFloat(behaviorComponent: BehaviorComponent) = get(behaviorComponent) as Float
    fun getVec2f(behaviorComponent: BehaviorComponent) = get(behaviorComponent) as Vec2f
    fun getVec3f(behaviorComponent: BehaviorComponent) = get(behaviorComponent) as Vec3f
    fun getVec4f(behaviorComponent: BehaviorComponent) = get(behaviorComponent) as Vec4f

    fun getInt(behaviorComponent: BehaviorComponent) = get(behaviorComponent) as Int
    fun getVec2i(behaviorComponent: BehaviorComponent) = get(behaviorComponent) as Vec2i
    fun getVec3i(behaviorComponent: BehaviorComponent) = get(behaviorComponent) as Vec3i
    fun getVec4i(behaviorComponent: BehaviorComponent) = get(behaviorComponent) as Vec4i
}
