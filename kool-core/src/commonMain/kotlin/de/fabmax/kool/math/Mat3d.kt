package de.fabmax.kool.math

import de.fabmax.kool.util.Float32Buffer
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Mat3d {

    val matrix = DoubleArray(9)

    init {
        setIdentity()
    }

    fun rotate(angleDeg: Double, axX: Double, axY: Double, axZ: Double): Mat3d {
        return synchronized(Mat3d) {
            tmpMatA.setRotate(angleDeg, axX, axY, axZ)
            set(mul(tmpMatA, tmpMatB))
        }
    }

    fun rotate(angleDeg: Double, axis: Vec3d) = rotate(angleDeg, axis.x, axis.y, axis.z)

    fun rotate(eulerX: Double, eulerY: Double, eulerZ: Double): Mat3d {
        return synchronized(Mat3d) {
            tmpMatA.setRotate(eulerX, eulerY, eulerZ)
            set(mul(tmpMatA, tmpMatB))
        }
    }

    fun rotate(angleDeg: Double, axX: Double, axY: Double, axZ: Double, result: Mat3d): Mat3d {
        result.set(this)
        result.rotate(angleDeg, axX, axY, axZ)
        return result
    }

    fun rotate(angleDeg: Double, axis: Vec3d, result: Mat3d) = rotate(angleDeg, axis.x, axis.y, axis.z, result)

    fun rotate(eulerX: Double, eulerY: Double, eulerZ: Double, result: Mat3d): Mat3d {
        result.set(this)
        result.rotate(eulerX, eulerY, eulerZ)
        return result
    }

    fun transpose(): Mat3d {
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

    fun transpose(result: Mat3d): Mat3d {
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
        return synchronized(Mat3d) { invert(tmpMatA).also { if (it) set(tmpMatA) } }
    }

    fun invert(result: Mat3d): Boolean {
        var det = 0.0
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

    fun transform(vec: MutableVec3d): MutableVec3d {
        val x = vec.x * this[0, 0] + vec.y * this[0, 1] + vec.z * this[0, 2]
        val y = vec.x * this[1, 0] + vec.y * this[1, 1] + vec.z * this[1, 2]
        val z = vec.x * this[2, 0] + vec.y * this[2, 1] + vec.z * this[2, 2]
        return vec.set(x, y, z)
    }

    fun transform(vec: Vec3d, result: MutableVec3d): MutableVec3d {
        result.x = vec.x * this[0, 0] + vec.y * this[0, 1] + vec.z * this[0, 2]
        result.y = vec.x * this[1, 0] + vec.y * this[1, 1] + vec.z * this[1, 2]
        result.z = vec.x * this[2, 0] + vec.y * this[2, 1] + vec.z * this[2, 2]
        return result
    }

    fun mul(other: Mat3d): Mat3d {
        return synchronized(Mat3d) {
            mul(other, tmpMatA)
            set(tmpMatA)
        }
    }

    fun mul(other: Mat3d, result: Mat3d): Mat3d {
        for (i in 0..2) {
            for (j in 0..2) {
                var x = 0.0
                for (k in 0..2) {
                    x += this[j + k * 3] * other[i * 3 + k]
                }
                result[i * 3 + j] = x
            }
        }
        return result
    }

    fun scale(sx: Double, sy: Double, sz: Double): Mat3d {
        for (i in 0..2) {
            matrix[i] *= sx
            matrix[3 + i] *= sy
            matrix[6 + i] *= sz
        }
        return this
    }

    fun scale(scale: Vec3d): Mat3d = scale(scale.x, scale.y, scale.z)

    fun scale(sx: Double, sy: Double, sz: Double, result: Mat3d): Mat3d {
        for (i in 0..2) {
            result.matrix[i] = matrix[i] * sx
            result.matrix[3 + i] = matrix[3 + i] * sy
            result.matrix[6 + i] = matrix[6 + i] * sz
        }
        return result
    }

    fun set(other: Mat3d): Mat3d {
        for (i in 0..8) {
            this[i] = other[i]
        }
        return this
    }

    fun set(other: Mat3f): Mat3d {
        for (i in 0..8) {
            this[i] = other[i].toDouble()
        }
        return this
    }

    fun set(Doubles: List<Double>) {
        for (i in 0..8) {
            this[i] = Doubles[i]
        }
    }

    fun setRow(row: Int, vec: Vec3d) {
        this[row, 0] = vec.x
        this[row, 1] = vec.y
        this[row, 2] = vec.z
    }

    fun getRow(row: Int, result: MutableVec3d): MutableVec3d {
        result.x = this[row, 0]
        result.y = this[row, 1]
        result.z = this[row, 2]
        return result
    }

    fun setCol(col: Int, value: Vec3d) {
        this[0, col] = value.x
        this[1, col] = value.y
        this[2, col] = value.z
    }

    fun getCol(col: Int, result: MutableVec3d): MutableVec3d {
        result.x = this[0, col]
        result.y = this[1, col]
        result.z = this[2, col]
        return result
    }

    fun setIdentity(): Mat3d {
        for (i in 1..8) {
            this[i] = 0.0
        }
        for (i in 0..8 step 4) {
            this[i] = 1.0
        }
        return this
    }

    fun setRotate(eulerX: Double, eulerY: Double, eulerZ: Double): Mat3d {
        val a = eulerX.toRad()
        val b = eulerY.toRad()
        val c = eulerZ.toRad()

        val ci = cos(a)
        val cj = cos(b)
        val ch = cos(c)
        val si = sin(a)
        val sj = sin(b)
        val sh = sin(c)
        val cc = ci * ch
        val cs = ci * sh
        val sc = si * ch
        val ss = si * sh

        matrix[0] = cj * ch
        matrix[3] = sj * sc - cs
        matrix[6] = sj * cc + ss

        matrix[1] = cj * sh
        matrix[4] = sj * ss + cc
        matrix[7] = sj * cs - sc

        matrix[2] = -sj
        matrix[5] = cj * si
        matrix[8] = cj * ci

        return this
    }

    fun setRotate(angleDeg: Double, axX: Double, axY: Double, axZ: Double): Mat3d {
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

        val ang = angleDeg.toRad()
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

    fun setRotate(quaternion: Vec4d): Mat3d = setRotate(QuatD(quaternion.x, quaternion.y, quaternion.z, quaternion.w))

    fun setRotate(quaternion: QuatD): Mat3d {
        val r = quaternion.w
        val i = quaternion.x
        val j = quaternion.y
        val k = quaternion.z

        var s = sqrt(r*r + i*i + j*j + k*k)
        s = 1.0 / (s * s)

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

    fun getRotation(result: MutableQuatD): MutableQuatD {
        val trace = this[0, 0] + this[1, 1] + this[2, 2]

        if (trace > 0f) {
            var s = sqrt(trace + 1.0)
            result.w = s * 0.5
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

            var s = sqrt(this[i, i] - this[j, j] - this[k, k] + 1.0)
            result[i] = s * 0.5
            s = 0.5 / s

            result.w = (this[k, j] - this[j, k]) * s
            result[j] = (this[j, i] + this[i, j]) * s
            result[k] = (this[k, i] + this[i, k]) * s
        }
        return result
    }

    private operator fun MutableQuatD.set(i: Int, value: Double) {
        when(i) {
            0 -> x = value
            1 -> y = value
            2 -> z = value
            else -> w = value
        }
    }

    fun getEulerAngles(result: MutableVec3d): MutableVec3d {
        val sy = sqrt(this[0, 0] * this[0, 0] + this[1, 0] * this[1, 0])
        val isSingular = sy.isFuzzyZero()

        if (!isSingular) {
            result.x = atan2(this[2, 1], this[2, 2]).toDeg()
            result.y = atan2(-this[2, 0], sy).toDeg()
            result.z = atan2(this[1, 0], this[0, 0]).toDeg()
        } else {
            result.x = atan2(-this[1, 2], this[1, 1]).toDeg()
            result.y = atan2(-this[2, 0], sy).toDeg()
            result.z = 0.0
        }
        return result
    }

    operator fun get(i: Int): Double = matrix[i]

    operator fun get(row: Int, col: Int): Double = matrix[col * 3 + row]

    operator fun set(i: Int, value: Double) {
        matrix[i] = value
    }

    operator fun set(row: Int, col: Int, value: Double) {
        matrix[col * 3 + row] = value
    }

    fun setColVec(col: Int, vec: Vec3d) {
        this[0, col] = vec.x
        this[1, col] = vec.y
        this[2, col] = vec.z
    }

    fun getColVec(col: Int, result: MutableVec3d): MutableVec3d {
        result.x = this[0, col]
        result.y = this[1, col]
        result.z = this[2, col]
        return result
    }

    fun toBuffer(buffer: Float32Buffer): Float32Buffer {
        for (i in 0..8) {
            buffer.put(matrix[i].toFloat())
        }
        buffer.flip()
        return buffer
    }

    fun dump() {
        for (r in 0..2) {
            for (c in 0..2) {
                print("${this[r, c]} ")
            }
            println()
        }
    }

    companion object : SynchronizedObject() {
        private val tmpMatA = Mat3d()
        private val tmpMatB = Mat3d()
    }
}