package de.fabmax.kool.util

import de.fabmax.kool.KogleException
import de.fabmax.kool.platform.Float32Buffer
import de.fabmax.kool.platform.Platform

/**
 * @author fabmax
 */

open class Mat4f {

    var matrix = FloatArray(16)
        protected set
    var offset = 0
        protected set

    init {
        setIdentity()
    }

    fun translate(tx: Float, ty: Float, tz: Float): Mat4f {
        MatrixMath.translateM(matrix, offset, tx, ty, tz)
        return this
    }

    fun translate(result: Mat4f, tx: Float, ty: Float, tz: Float): Mat4f {
        MatrixMath.translateM(result.matrix, result.offset, matrix, offset, tx, ty, tz)
        return result
    }

    fun rotate(angleDeg: Float, axis: Vec3f) = rotate(angleDeg, axis.x, axis.y, axis.z)

    fun rotate(angleDeg: Float, axX: Float, axY: Float, axZ: Float): Mat4f {
        MatrixMath.rotateM(matrix, offset, angleDeg, axX, axY, axZ)
        return this
    }

    fun rotate(result: Mat4f, angleDeg: Float, axis: Vec3f) = rotate(result, angleDeg, axis.x, axis.y, axis.z)

    fun rotate(result: Mat4f, angleDeg: Float, axX: Float, axY: Float, axZ: Float): Mat4f {
        MatrixMath.rotateM(result.matrix, result.offset, matrix, offset, angleDeg, axX, axY, axZ)
        return result
    }

    fun rotateEuler(xDeg: Float, yDeg: Float, zDeg: Float): Mat4f {
        MatrixMath.rotateEulerM(matrix, offset, xDeg, yDeg, zDeg)
        return this
    }

    fun rotateEuler(result: Mat4f, xDeg: Float, yDeg: Float, zDeg: Float): Mat4f {
        MatrixMath.rotateEulerM(result.matrix, result.offset, matrix, offset, xDeg, yDeg, zDeg)
        return result
    }

    fun scale(sx: Float, sy: Float, sz: Float): Mat4f {
        MatrixMath.scaleM(matrix, offset, sx, sy, sz)
        return this
    }

    fun scale(result: Mat4f, sx: Float, sy: Float, sz: Float): Mat4f {
        MatrixMath.scaleM(result.matrix, result.offset, matrix, offset, sx, sy, sz)
        return result
    }

    fun transpose(): Mat4f {
        MatrixMath.transposeM(matrix, offset)
        return this
    }

    fun transpose(result: Mat4f): Mat4f {
        MatrixMath.transposeM(result.matrix, result.offset, matrix, offset)
        return result
    }

    fun invert(): Boolean {
        return MatrixMath.invertM(matrix, offset)
    }

    fun invert(result: Mat4f): Boolean {
        return MatrixMath.invertM(result.matrix, result.offset, matrix, offset)
    }

    fun transform(vec: MutableVec3f, w: Float = 1f): MutableVec3f {
        val x = vec.x * this[0, 0] + vec.y * this[1, 0] + vec.z * this[2, 0] + w * this[3, 0]
        val y = vec.x * this[0, 1] + vec.y * this[1, 1] + vec.z * this[2, 1] + w * this[3, 1]
        val z = vec.x * this[0, 2] + vec.y * this[1, 2] + vec.z * this[2, 2] + w * this[3, 2]
        return vec.set(x, y, z)
    }

    fun transform(result: MutableVec3f, vec: Vec3f, w: Float = 1f): MutableVec3f {
        result.x = vec.x * this[0, 0] + vec.y * this[1, 0] + vec.z * this[2, 0] + w * this[3, 0]
        result.y = vec.x * this[0, 1] + vec.y * this[1, 1] + vec.z * this[2, 1] + w * this[3, 1]
        result.z = vec.x * this[0, 2] + vec.y * this[1, 2] + vec.z * this[2, 2] + w * this[3, 2]
        return result
    }

    fun mul(other: Mat4f): Mat4f {
        MatrixMath.multiplyMM(matrix, offset, other.matrix, other.offset)
        return this
    }

    fun mul(result: Mat4f, other: Mat4f): Mat4f {
        MatrixMath.multiplyMM(result.matrix, result.offset, matrix, offset, other.matrix, other.offset)
        return result
    }

    fun set(other: Mat4f): Mat4f {
        for (i in 0..15) {
            matrix[offset + i] = other.matrix[other.offset + i]
        }
        return this
    }

    fun setIdentity(): Mat4f {
        MatrixMath.setIdentityM(matrix, offset)
        return this
    }

    fun setLookAt(position: Vec3f, lookAt: Vec3f, up: Vec3f): Mat4f {
        MatrixMath.setLookAtM(matrix, offset, position.x, position.y, position.z, lookAt.x, lookAt.y, lookAt.z,
                up.x, up.y, up.z)
        return this
    }

    fun setPerspective(fovy: Float, aspect: Float, near: Float, far: Float): Mat4f {
        MatrixMath.setPerspectiveM(matrix, offset, fovy, aspect, near, far)
        return this
    }

    operator fun get(i: Int): Float {
        return matrix[offset + i]
    }

    operator fun get(col: Int, row: Int): Float {
        return matrix[offset + col * 4 + row]
    }

    operator fun set(i: Int, value: Float) {
        matrix[offset + i] = value
    }

    operator fun set(col: Int, row: Int, value: Float) {
        matrix[offset + col * 4 + row] = value
    }

    fun toBuffer(buffer: Float32Buffer): Float32Buffer {
        buffer.put(matrix, offset, 16)
        buffer.flip()
        return buffer
    }
}

class Mat4fStack(val stackSize: Int = Mat4fStack.DEFAULT_STACK_SIZE) : Mat4f() {
    companion object {
        val DEFAULT_STACK_SIZE = 32
    }

    var stackIndex = 0
        private set(value) {
            field = value
            offset = value * 16
        }

    init {
        matrix = FloatArray(16 * stackSize)
        setIdentity()
    }

    fun push() {
        if (stackIndex >= stackSize) {
            throw KogleException("Matrix stack overflow")
        }
        for (i in 0 .. 15) {
            matrix[offset + 16 + i] = matrix[offset + i]
        }
        stackIndex++
    }

    fun pop() {
        if (stackIndex <= 0) {
            throw KogleException("Matrix stack underflow")
        }
        stackIndex--
    }

    fun reset(): Mat4fStack {
        stackIndex = 0
        setIdentity()
        return this
    }
}