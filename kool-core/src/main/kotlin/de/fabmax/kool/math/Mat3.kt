package de.fabmax.kool.math

import de.fabmax.kool.util.Float32Buffer
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class Mat3f {

    val matrix = FloatArray(9)

    init {
        setIdentity()
    }

    fun rotate(angleDeg: Float, axX: Float, axY: Float, axZ: Float): Mat3f {
        var aX = axX
        var aY = axY
        var aZ = axZ
        val len = sqrt(aX * aX + aY * aY + aZ * aZ)
        if (!(1.0 - len).isFuzzyZero()) {
            val recipLen = 1f / len
            aX *= recipLen
            aY *= recipLen
            aZ *= recipLen
        }

        val ang = angleDeg * (PI.toFloat() / 180f)
        val s = sin(ang)
        val c = cos(ang)

        val nc = 1f - c
        val xy = aX * aY
        val yz = aY * aZ
        val zx = aZ * aX
        val xs = aX * s
        val ys = aY * s
        val zs = aZ * s

        this[0] = aX * aX * nc + c
        this[3] = xy * nc - zs
        this[6] = zx * nc + ys
        this[1] = xy * nc + zs
        this[4] = aY * aY * nc + c
        this[7] = yz * nc - xs
        this[2] = zx * nc - ys
        this[5] = yz * nc + xs
        this[8] = aZ * aZ * nc + c

        return this
    }

    fun rotate(angleDeg: Float, axis: Vec3f) = rotate(angleDeg, axis.x, axis.y, axis.z)

    fun rotate(angleDeg: Float, axX: Float, axY: Float, axZ: Float, result: Mat3f): Mat3f {
        result.set(this)
        result.rotate(angleDeg, axX, axY, axZ)
        return result
    }

    fun rotate(angleDeg: Float, axis: Vec3f, result: Mat3f) = rotate(angleDeg, axis.x, axis.y, axis.z, result)

    fun transpose(): Mat3f {
        var d = this[1]
        this[1] = this[3]
        this[3] = d
        d = this[2]
        this[2] = this[6]
        this[6] = d
        d = this[5]
        this[5] = this[7]
        this[7] = d
        return this
    }

    fun transpose(result: Mat4f): Mat4f {
        result[0] = this[0]
        result[1] = this[3]
        result[2] = this[6]

        result[3] = this[1]
        result[4] = this[4]
        result[5] = this[7]

        result[6] = this[2]
        result[7] = this[5]
        result[8] = this[8]

        return result
    }

    fun invert(): Boolean {
        var success = false
        synchronized(tmpMatLock) {
            success = invert(tmpMat)
            if (success) {
                set(tmpMat)
            }
        }
        return success
    }

    fun invert(result: Mat3f): Boolean {
        var det = 0f
        for (i in 0..2) {
            det += (this[i] * (this[3+(i+1)%3] * this[6+(i+2)%3] - this[3+(i+2)%3] * this[6+(i+1)%3]))
        }

        return if (det > 0f) {
            det = 1f / det
            for (j in 0..2) {
                for (i in 0..2) {
                    result[j * 3 + i] = ((this[((i+1)%3)*3 + (j+1)%3] * this[((i+2)%3)*3 + (j+2)%3]) -
                            (this[((i+1)%3)*3 + (j+2)%3] * this[((i+2)%3)*3 + (j+1)%3])) * det
                }
            }
            true
        } else {
            false
        }
    }

    fun transform(vec: MutableVec3f): MutableVec3f {
        val x = vec.x * this[0, 0] + vec.y * this[0, 1] + vec.z * this[0, 2]
        val y = vec.x * this[1, 0] + vec.y * this[1, 1] + vec.z * this[1, 2]
        val z = vec.x * this[2, 0] + vec.y * this[2, 1] + vec.z * this[2, 2]
        return vec.set(x, y, z)
    }

    fun transform(vec: Vec3f, result: MutableVec3f): MutableVec3f {
        result.x = vec.x * this[0, 0] + vec.y * this[0, 1] + vec.z * this[0, 2]
        result.y = vec.x * this[1, 0] + vec.y * this[1, 1] + vec.z * this[1, 2]
        result.z = vec.x * this[2, 0] + vec.y * this[2, 1] + vec.z * this[2, 2]
        return result
    }

    fun mul(other: Mat3f): Mat3f {
        synchronized(tmpMatLock) {
            mul(other, tmpMat)
            set(tmpMat)
        }
        return this
    }

    fun mul(other: Mat3f, result: Mat3f): Mat3f {
        for (i in 0..2) {
            for (j in 0..2) {
                var x = 0f
                for (k in 0..2) {
                    x += this[j + k * 3] * other[i * 3 + k]
                }
                result[i * 3 + j] = x
            }
        }
        return result
    }

    fun scale(sx: Float, sy: Float, sz: Float): Mat3f {
        for (i in 0..2) {
            matrix[i] *= sx
            matrix[3 + i] *= sy
            matrix[6 + i] *= sz
        }
        return this
    }

    fun scale(scale: Vec3f): Mat3f = scale(scale.x, scale.y, scale.z)

    fun scale(sx: Float, sy: Float, sz: Float, result: Mat3f): Mat3f {
        for (i in 0..2) {
            result.matrix[i] = matrix[i] * sx
            result.matrix[3 + i] = matrix[3 + i] * sy
            result.matrix[6 + i] = matrix[6 + i] * sz
        }
        return result
    }

    fun set(other: Mat3f): Mat3f {
        for (i in 0..8) {
            this[i] = other[i]
        }
        return this
    }

    fun set(floats: List<Float>) {
        for (i in 0..8) {
            this[i] = floats[i]
        }
    }

    fun setIdentity(): Mat3f {
        for (i in 1..8) {
            this[i] = 0f
        }
        for (i in 0..8 step 4) {
            this[i] = 1f
        }
        return this
    }

    fun setRotate(rotA: Float, axX: Float, axY: Float, axZ: Float): Mat3f {
        setIdentity()
        rotate(rotA, axX, axY, axZ)
        return this
    }

    fun setRotate(quaternion: Vec4f): Mat3f {
        val r = quaternion.w
        val i = quaternion.x
        val j = quaternion.y
        val k = quaternion.z

        var s = sqrt(r*r + i*i + j*j + k*k)
        s = 1f / (s * s)

        this[0, 0] = 1 - 2*s*(j*j + k*k)
        this[0, 1] = 2*s*(i*j - k*r)
        this[0, 2] = 2*s*(i*k + j*r)

        this[1, 0] = 2*s*(i*j + k*r)
        this[1, 1] = 1 - 2*s*(i*i + k*k)
        this[1, 2] = 2*s*(j*k - i*r)

        this[2, 0] = 2*s*(i*k - j*r)
        this[2, 1] = 2*s*(j*k + i*r)
        this[2, 2] = 1 - 2*s*(i*i + j*j)

        return this
    }

    fun getRotation(result: MutableVec4f): MutableVec4f {
        val trace = this[0, 0] + this[1, 1] + this[2, 2]

        if (trace > 0f) {
            var s = sqrt(trace + 1f)
            result.w = s * 0.5f
            s = 0.5f / s

            result.x = (this[2, 1] - this[1, 2]) * s
            result.y = (this[0, 2] - this[2, 0]) * s
            result.z = (this[1, 0] - this[0, 1]) * s

        } else {
            val i = if (this[0, 0] < this[1, 1]) {
                if (this[1, 1] < this[2, 2]) { 2 } else { 1 }
            } else {
                if (this[0, 0] < this[2, 2]) { 2 } else { 0 }
            }
            val j = (i + 1) % 3
            val k = (i + 2) % 3

            var s = sqrt(this[i, i] - this[j, j] - this[k, k] + 1f)
            result[i] = s * 0.5f
            s = 0.5f / s

            result.w = (this[k, j] - this[j, k]) * s
            result[j] = (this[j, i] + this[i, j]) * s
            result[k] = (this[k, i] + this[i, k]) * s
        }
        return result
    }

    operator fun get(i: Int): Float = matrix[i]

    operator fun get(row: Int, col: Int): Float = matrix[col * 3 + row]

    operator fun set(i: Int, value: Float) {
        matrix[i] = value
    }

    operator fun set(row: Int, col: Int, value: Float) {
        matrix[col * 3 + row] = value
    }

    fun setColVec(col: Int, vec: Vec3f) {
        this[0, col] = vec.x
        this[1, col] = vec.y
        this[2, col] = vec.z
    }

    fun getColVec(col: Int, result: MutableVec3f): MutableVec3f {
        result.x = this[0, col]
        result.y = this[1, col]
        result.z = this[2, col]
        return result
    }

    fun toBuffer(buffer: Float32Buffer): Float32Buffer {
        buffer.put(matrix, 0, 9)
        buffer.flip()
        return buffer
    }

    companion object {
        private val tmpMatLock = Any()
        private val tmpMat = Mat3f()
    }
}