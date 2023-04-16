package de.fabmax.kool.editor.model

import de.fabmax.kool.math.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MutableColor
import kotlinx.serialization.Serializable

@Serializable
data class MTransform(
    val position: MVec3,
    val rotation: MVec4,
    val scale: MVec3
) {
    companion object {
        val IDENTITY = MTransform(
            position = MVec3(Vec3d.ZERO),
            rotation = MVec4(Vec4d.W_AXIS),
            scale = MVec3(Vec3d(1.0, 1.0, 1.0))
        )
    }
}

@Serializable
data class MVec3(val x: Double, val y: Double, val z: Double) {
    constructor(vec: Vec3d): this(vec.x, vec.y, vec.z)
    constructor(vec: Vec3f): this(vec.x.toDouble(), vec.y.toDouble(), vec.z.toDouble())

    fun toVec3f(result: MutableVec3f = MutableVec3f()) {
        result.set(x.toFloat(), y.toFloat(), z.toFloat())
    }

    fun toVec3d(result: MutableVec3d = MutableVec3d()) {
        result.set(x, y, z)
    }
}

@Serializable
data class MVec4(val x: Double, val y: Double, val z: Double, val w: Double) {
    constructor(vec: Vec4d): this(vec.x, vec.y, vec.z, vec.w)
    constructor(vec: Vec4f): this(vec.x.toDouble(), vec.y.toDouble(), vec.z.toDouble(), vec.w.toDouble())

    fun toVec4f(result: MutableVec4f = MutableVec4f()) {
        result.set(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat())
    }

    fun toVec4d(result: MutableVec4d = MutableVec4d()) {
        result.set(x, y, z, w)
    }
}

@Serializable
data class MColor(val r: Float, val g: Float, val b: Float, val a: Float) {
    constructor(color: Color): this(color.r, color.g, color.b, color.a)

    fun toColor(result: MutableColor = MutableColor()): MutableColor {
        return result.set(r, g, b, a)
    }
}