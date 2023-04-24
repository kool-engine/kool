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
    companion object {
        val IDENTITY = MTransform(
            position = MVec3(Vec3d.ZERO),
            rotation = MVec4(Vec4d.W_AXIS),
            scale = MVec3(Vec3d(1.0, 1.0, 1.0))
        )
    }

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

    fun set(matrix: Mat4d) {
        position.set(matrix.getOrigin(MutableVec3d()))
        rotation.set(matrix.getRotation(MutableVec4d()))
        scale.set(matrix.getScale(MutableVec3d()))
    }
}

@Serializable
data class MVec2(var x: Double, var y: Double) {
    constructor(vec: Vec2d): this(vec.x, vec.y)
    constructor(vec: Vec2f): this(vec.x.toDouble(), vec.y.toDouble())

    fun toVec2f(result: MutableVec2f = MutableVec2f()): MutableVec2f {
        return result.set(x.toFloat(), y.toFloat())
    }

    fun toVec2d(result: MutableVec2d = MutableVec2d()): MutableVec2d {
        return result.set(x, y)
    }

    fun set(vec: Vec2f) {
        x = vec.x.toDouble()
        y = vec.y.toDouble()
    }

    fun set(vec: Vec2d) {
        x = vec.x
        y = vec.y
    }
}

@Serializable
data class MVec3(var x: Double, var y: Double, var z: Double) {
    constructor(vec: Vec3d): this(vec.x, vec.y, vec.z)
    constructor(vec: Vec3f): this(vec.x.toDouble(), vec.y.toDouble(), vec.z.toDouble())

    fun toVec3f(result: MutableVec3f = MutableVec3f()): MutableVec3f {
        return result.set(x.toFloat(), y.toFloat(), z.toFloat())
    }

    fun toVec3d(result: MutableVec3d = MutableVec3d()): MutableVec3d {
        return result.set(x, y, z)
    }

    fun set(vec: Vec3f) {
        x = vec.x.toDouble()
        y = vec.y.toDouble()
        z = vec.z.toDouble()
    }

    fun set(vec: Vec3d) {
        x = vec.x
        y = vec.y
        z = vec.z
    }
}

@Serializable
data class MVec4(var x: Double, var y: Double, var z: Double, var w: Double) {
    constructor(vec: Vec4d): this(vec.x, vec.y, vec.z, vec.w)
    constructor(vec: Vec4f): this(vec.x.toDouble(), vec.y.toDouble(), vec.z.toDouble(), vec.w.toDouble())

    fun toVec4f(result: MutableVec4f = MutableVec4f()): MutableVec4f {
        return result.set(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat())
    }

    fun toVec4d(result: MutableVec4d = MutableVec4d()): MutableVec4d {
        return result.set(x, y, z, w)
    }

    fun set(vec: Vec4f) {
        x = vec.x.toDouble()
        y = vec.y.toDouble()
        z = vec.z.toDouble()
        w = vec.w.toDouble()
    }

    fun set(vec: Vec4d) {
        x = vec.x
        y = vec.y
        z = vec.z
        w = vec.w
    }
}

@Serializable
data class MColor(var r: Float, var g: Float, var b: Float, var a: Float) {
    constructor(color: Color): this(color.r, color.g, color.b, color.a)

    fun toColor(result: MutableColor = MutableColor()): MutableColor {
        return result.set(r, g, b, a)
    }

    fun set(vec: Vec4f) {
        r = vec.x
        g = vec.y
        b = vec.z
        a = vec.w
    }
}