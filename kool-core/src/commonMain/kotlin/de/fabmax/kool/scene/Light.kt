package de.fabmax.kool.scene

import de.fabmax.kool.math.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MutableColor
import kotlin.math.abs
import kotlin.math.cos

sealed class Light : Node() {
    val color = MutableColor(Color.WHITE)

    val encodedPosition = MutableVec4f()
    val encodedDirection = MutableVec4f()
    val encodedColor = MutableVec4f()

    abstract fun updateEncodedValues()

    open fun setColor(color: Color, intensity: Float): Light {
        this.color.set(color)
        this.color.a = intensity
        return this
    }

    protected fun encodeColor() {
        encodedColor.set(
            color.r * color.a,
            color.g * color.a,
            color.b * color.a,
            1f
        )
    }

    protected fun setTransformByDirectionAndPos(direction: Vec3f = Vec3f.X_AXIS, pos: Vec3d = Vec3d.ZERO) {
        val dir = direction.toMutableVec3d().norm()
        val v = if (abs(dir.dot(Vec3d.Y_AXIS)) > 0.9f) {
            Vec3d.X_AXIS
        } else {
            Vec3d.Y_AXIS
        }

        val b = dir.cross(v, MutableVec3d())
        val c = dir.cross(b, MutableVec3d())
        transform.matrix.setCol(0, dir, 0.0)
        transform.matrix.setCol(1, b, 0.0)
        transform.matrix.setCol(2, c, 0.0)
        transform.setPosition(pos)
    }

    class Directional : Light() {
        private val _direction = MutableVec3f()
        val direction: Vec3f
            get() = _direction

        fun setup(dir: Vec3f): Directional {
            setTransformByDirectionAndPos(dir)
            updateEncodedValues()
            return this
        }

        override fun updateEncodedValues() {
            encodeColor()
            if (parent == null) {
                transform.transform(_direction.set(Vec3f.X_AXIS), 0f)
            } else {
                toGlobalCoords(_direction.set(Vec3f.X_AXIS), 0f)
            }

            // directional light direction is intentionally stored in position instead of direction, for
            // easier / faster decoding in the shader
            encodedPosition.set(_direction, ENCODING)
        }

        override fun setColor(color: Color, intensity: Float): Directional {
            super.setColor(color, intensity)
            return this
        }

        companion object {
            const val ENCODING = 0f
        }
    }

    class Point : Light() {
        private val _position = MutableVec3f()
        val position: Vec3f
            get() = _position

        fun setup(pos: Vec3f): Point {
            setTransformByDirectionAndPos(pos = pos.toVec3d())
            updateEncodedValues()
            return this
        }

        override fun updateEncodedValues() {
            encodeColor()
            if (parent == null) {
                transform.transform(_position.set(Vec3f.ZERO))
            } else {
                toGlobalCoords(_position.set(Vec3f.ZERO))
            }

            encodedPosition.set(_position, ENCODING)
        }

        override fun setColor(color: Color, intensity: Float): Point {
            super.setColor(color, intensity)
            return this
        }

        companion object {
            const val ENCODING = 1f
        }
    }

    class Spot : Light() {
        private val _position = MutableVec3f()
        private val _direction = MutableVec3f()
        val position: Vec3f
            get() = _position
        val direction: Vec3f
            get() = _direction

        var spotAngle = 60f
        var coreRatio = 0.5f

        fun setup(pos: Vec3f, dir: Vec3f, angle: Float = spotAngle, ratio: Float = coreRatio): Spot {
            setTransformByDirectionAndPos(direction = dir, pos = pos.toVec3d())
            spotAngle = angle
            coreRatio = ratio
            updateEncodedValues()
            return this
        }

        override fun updateEncodedValues() {
            encodeColor()
            if (parent == null) {
                transform.transform(_position.set(Vec3f.ZERO))
                transform.transform(_direction.set(Vec3f.X_AXIS), 0f)
            } else {
                toGlobalCoords(_position.set(Vec3f.ZERO))
                toGlobalCoords(_direction.set(Vec3f.X_AXIS), 0f)
            }

            encodedPosition.set(_position, ENCODING)
            encodedDirection.set(direction, cos((spotAngle / 2).toRad()))
            encodedColor.w = coreRatio
        }

        override fun setColor(color: Color, intensity: Float): Spot {
            super.setColor(color, intensity)
            return this
        }

        companion object {
            const val ENCODING = 2f
        }
    }
}