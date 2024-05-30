package de.fabmax.kool.editor

import de.fabmax.kool.editor.components.BehaviorComponent
import de.fabmax.kool.editor.components.EditorModelComponent
import de.fabmax.kool.editor.model.SceneModel
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.math.*
import kotlin.reflect.KType

class BehaviorProperty(
    val name: String,
    val type: BehaviorPropertyType,
    val kType: KType,
    val label: String,
    val min: Double = Double.NEGATIVE_INFINITY,
    val max: Double = Double.POSITIVE_INFINITY
) {
    val isRanged: Boolean
        get() = min > Float.NEGATIVE_INFINITY && max < Float.POSITIVE_INFINITY

    fun get(behaviorComponent: BehaviorComponent): Any? {
        return behaviorComponent.getProperty(name)
    }

    fun set(behaviorComponent: BehaviorComponent, value: Any?): Boolean {
        return behaviorComponent.setProperty(name, value)
    }
}

enum class BehaviorPropertyType {
    STD,
    NODE_MODEL,
    COMPONENT
}

fun BehaviorProperty.getDouble(behaviorComponent: BehaviorComponent): Double = get(behaviorComponent) as Double? ?: 0.0
fun BehaviorProperty.getVec2d(behaviorComponent: BehaviorComponent): Vec2d = get(behaviorComponent) as Vec2d? ?: Vec2d.ZERO
fun BehaviorProperty.getVec3d(behaviorComponent: BehaviorComponent): Vec3d = get(behaviorComponent) as Vec3d? ?: Vec3d.ZERO
fun BehaviorProperty.getVec4d(behaviorComponent: BehaviorComponent): Vec4d = get(behaviorComponent) as Vec4d? ?: Vec4d.ZERO

fun BehaviorProperty.getFloat(behaviorComponent: BehaviorComponent): Float = get(behaviorComponent) as Float? ?: 0f
fun BehaviorProperty.getVec2f(behaviorComponent: BehaviorComponent): Vec2f = get(behaviorComponent) as Vec2f? ?: Vec2f.ZERO
fun BehaviorProperty.getVec3f(behaviorComponent: BehaviorComponent): Vec3f = get(behaviorComponent) as Vec3f? ?: Vec3f.ZERO
fun BehaviorProperty.getVec4f(behaviorComponent: BehaviorComponent): Vec4f = get(behaviorComponent) as Vec4f? ?: Vec4f.ZERO

fun BehaviorProperty.getInt(behaviorComponent: BehaviorComponent): Int = get(behaviorComponent) as Int? ?: 0
fun BehaviorProperty.getVec2i(behaviorComponent: BehaviorComponent): Vec2i = get(behaviorComponent) as Vec2i? ?: Vec2i.ZERO
fun BehaviorProperty.getVec3i(behaviorComponent: BehaviorComponent): Vec3i = get(behaviorComponent) as Vec3i? ?: Vec3i.ZERO
fun BehaviorProperty.getVec4i(behaviorComponent: BehaviorComponent): Vec4i = get(behaviorComponent) as Vec4i? ?: Vec4i.ZERO

fun BehaviorProperty.getBoolean(behaviorComponent: BehaviorComponent): Boolean = get(behaviorComponent) as Boolean? ?: false

fun BehaviorProperty.getComponent(behaviorComponent: BehaviorComponent): EditorModelComponent? = get(behaviorComponent) as EditorModelComponent?
fun BehaviorProperty.getScene(behaviorComponent: BehaviorComponent): SceneModel? = get(behaviorComponent) as SceneModel?
fun BehaviorProperty.getSceneNode(behaviorComponent: BehaviorComponent): SceneNodeModel? = get(behaviorComponent) as SceneNodeModel?
