package de.fabmax.kool.editor.data

import kotlinx.serialization.Serializable

@Serializable
class ScriptComponentData(
    var scriptClassName: String,
    var runInEditMode: Boolean = false,
    var propertyValues: MutableMap<String, PropertyValue> = mutableMapOf()
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

    val color: ColorData? = null,
    val transform: TransformData? = null,
    val str: String? = null
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

            color != null -> color.toColor()
            transform != null -> transform.toMat4d()
            str != null -> str
            else -> throw IllegalStateException("PropertyValue has no non-null value")
        }
    }
}