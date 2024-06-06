package de.fabmax.kool.editor.data

import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.components.GameEntityComponent
import kotlinx.serialization.Serializable

@Serializable
data class BehaviorComponentData(
    val behaviorClassName: String,
    val propertyValues: Map<String, PropertyValue> = mapOf()
) : ComponentData

@Serializable
data class PropertyValue(
    val d1: Double? = null,
    val d2: Vec2Data? = null,
    val d3: Vec3Data? = null,
    val d4: Vec4Data? = null,

    val f1: Float? = null,
    val f2: Vec2Data? = null,
    val f3: Vec3Data? = null,
    val f4: Vec4Data? = null,

    val i1: Int? = null,
    val i2: Vec2Data? = null,
    val i3: Vec3Data? = null,
    val i4: Vec4Data? = null,

    val bool: Boolean? = null,
    val color: ColorData? = null,
    val transform: TransformData? = null,
    val str: String? = null,
    val nodeRef: EntityId? = null,
    val componentRef: ComponentRef? = null,
) {
    fun get(): Any {
        return when {
            d1 != null -> d1
            d2 != null -> d2.toVec2d()
            d3 != null -> d3.toVec3d()
            d4 != null -> d4.toVec4d()

            f1 != null -> f1
            f2 != null -> f2.toVec2f()
            f3 != null -> f3.toVec3f()
            f4 != null -> f4.toVec4f()

            i1 != null -> i1
            i2 != null -> i2.toVec2i()
            i3 != null -> i3.toVec3i()
            i4 != null -> i4.toVec4i()

            bool != null -> bool
            color != null -> color.toColorLinear()
            transform != null -> transform.toMat4d()
            str != null -> str
            nodeRef != null -> nodeRef
            componentRef != null -> componentRef
            else -> error("PropertyValue has no non-null value")
        }
    }
}

@Serializable
data class ComponentRef(
    val entityId: EntityId,
    val componentClassName: String
)

fun ComponentRef(component: GameEntityComponent?): ComponentRef {
    // qualified class name would be much more robust but is not supported on JS -> use simple class name instead
    return if (component != null) {
        ComponentRef(component.gameEntity.id, component::class.simpleName!!)
    } else {
        ComponentRef(EntityId(-1L), "<null>")
    }
}

fun ComponentRef.matchesComponent(component: GameEntityComponent): Boolean {
    // qualified class name would be much more robust but is not supported on JS -> use simple class name instead
    return entityId == component.gameEntity.id && component::class.simpleName == componentClassName
}

fun GameEntity.getComponent(ref: ComponentRef): GameEntityComponent? {
    return components.find { ref.matchesComponent(it) }
}
