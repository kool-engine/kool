package de.fabmax.kool.editor.model

import de.fabmax.kool.math.*
import de.fabmax.kool.scene.Transform
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MutableColor
import kotlinx.serialization.Serializable

@Serializable
data class MTransform(
    val position: MVec3,
    val rotation: MVec4,
    val scale: MVec3
) {
    constructor(matrix: Mat4d): this(
        position = MVec3(matrix.getOrigin(MutableVec3d())),
        rotation = MVec4(matrix.getRotation(MutableVec4d())),
        scale = MVec3(matrix.getScale(MutableVec3d()))
    )

    fun toTransform(result: Transform): Transform {
        toMat4d(result.matrix)
        result.markDirty()
        return result
    }

    fun toMat4d(result: Mat4d = Mat4d()): Mat4d {
        return result
            .setRotate(rotation.toVec4d())
            .scale(scale.x, scale.y, scale.z)
            .setOrigin(position.toVec3d())
    }

    companion object {
        val IDENTITY = MTransform(
            position = MVec3(Vec3d.ZERO),
            rotation = MVec4(Vec4d.W_AXIS),
            scale = MVec3(Vec3d(1.0, 1.0, 1.0))
        )
    }
}

@Serializable
data class MVec2(val x: Double, val y: Double) {
    constructor(vec: Vec2d): this(vec.x, vec.y)
    constructor(vec: Vec2f): this(vec.x.toDouble(), vec.y.toDouble())

    fun toVec2f(result: MutableVec2f = MutableVec2f()): MutableVec2f {
        return result.set(x.toFloat(), y.toFloat())
    }

    fun toVec2d(result: MutableVec2d = MutableVec2d()): MutableVec2d {
        return result.set(x, y)
    }
}

@Serializable
data class MVec3(val x: Double, val y: Double, val z: Double) {
    constructor(vec: Vec3d): this(vec.x, vec.y, vec.z)
    constructor(vec: Vec3f): this(vec.x.toDouble(), vec.y.toDouble(), vec.z.toDouble())

    fun toVec3f(result: MutableVec3f = MutableVec3f()): MutableVec3f {
        return result.set(x.toFloat(), y.toFloat(), z.toFloat())
    }

    fun toVec3d(result: MutableVec3d = MutableVec3d()): MutableVec3d {
        return result.set(x, y, z)
    }
}

@Serializable
data class MVec4(val x: Double, val y: Double, val z: Double, val w: Double) {
    constructor(vec: Vec4d): this(vec.x, vec.y, vec.z, vec.w)
    constructor(vec: Vec4f): this(vec.x.toDouble(), vec.y.toDouble(), vec.z.toDouble(), vec.w.toDouble())

    fun toVec4f(result: MutableVec4f = MutableVec4f()): MutableVec4f {
        return result.set(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat())
    }

    fun toVec4d(result: MutableVec4d = MutableVec4d()): MutableVec4d {
        return result.set(x, y, z, w)
    }
}

@Serializable
data class MColor(val r: Float, val g: Float, val b: Float, val a: Float) {
    constructor(color: Color): this(color.r, color.g, color.b, color.a)

    fun toColor(result: MutableColor = MutableColor()): MutableColor {
        return result.set(r, g, b, a)
    }
}