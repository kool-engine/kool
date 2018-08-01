package de.fabmax.kool.math

import de.fabmax.kool.KoolException
import de.fabmax.kool.util.Float32Buffer
import kotlin.math.*

open class Mat4d {

    var matrix = DoubleArray(16)
        protected set
    var offset = 0
        protected set

    init {
        setIdentity()
    }

    fun translate(tx: Double, ty: Double, tz: Double): Mat4d {
        for (i in 0..3) {
            val mi = offset + i
            matrix[12 + mi] += matrix[mi] * tx + matrix[4 + mi] * ty + matrix[8 + mi] * tz
        }
        return this
    }

    fun translate(t: Vec3d): Mat4d = translate(t.x, t.y, t.z)

    fun translate(tx: Double, ty: Double, tz: Double, result: Mat4d): Mat4d {
        for (i in 0..11) {
            result.matrix[result.offset + i] = matrix[offset + i]
        }
        for (i in 0..3) {
            val mi = offset + i
            result.matrix[result.offset + 12 + i] =
                    matrix[mi] * tx + matrix[4 + mi] * ty + matrix[8 + mi] * tz + matrix[12 + mi]
        }
        return result
    }

    fun rotate(angleDeg: Double, axX: Double, axY: Double, axZ: Double): Mat4d {
        synchronized(tmpMatLock) {
            tmpMatA.setRotate(angleDeg, axX, axY, axZ)
            set(mul(tmpMatA, tmpMatB))
        }
        return this
    }

    fun rotate(angleDeg: Double, axis: Vec3d) = rotate(angleDeg, axis.x, axis.y, axis.z)

    fun rotate(angleDeg: Double, axX: Double, axY: Double, axZ: Double, result: Mat4d): Mat4d {
        synchronized(tmpMatLock) {
            tmpMatA.setRotate(angleDeg, axX, axY, axZ)
            mul(tmpMatA, result)
        }
        return result
    }

    fun rotate(angleDeg: Double, axis: Vec3d, result: Mat4d) = rotate(angleDeg, axis.x, axis.y, axis.z, result)

    fun scale(sx: Double, sy: Double, sz: Double): Mat4d {
        for (i in 0..3) {
            val mi = offset + i
            matrix[mi] *= sx
            matrix[4 + mi] *= sy
            matrix[8 + mi] *= sz
        }
        return this
    }

    fun scale(scale: Vec3d): Mat4d = scale(scale.x, scale.y, scale.z)

    fun scale(sx: Double, sy: Double, sz: Double, result: Mat4d): Mat4d {
        for (i in 0..3) {
            val smi = result.offset + i
            val mi = offset + i
            result.matrix[smi] = matrix[mi] * sx
            result.matrix[4 + smi] = matrix[4 + mi] * sy
            result.matrix[8 + smi] = matrix[8 + mi] * sz
            result.matrix[12 + smi] = matrix[12 + mi]
        }
        return result
    }

    fun transpose(): Mat4d {
        synchronized(tmpMatLock) {
            set(transpose(tmpMatA))
        }
        return this
    }

    fun transpose(result: Mat4d): Mat4d {
        for (i in 0..3) {
            val mBase = i * 4 + offset
            result.matrix[i + result.offset] = matrix[mBase]
            result.matrix[i + 4 + result.offset] = matrix[mBase + 1]
            result.matrix[i + 8 + result.offset] = matrix[mBase + 2]
            result.matrix[i + 12 + result.offset] = matrix[mBase + 3]
        }
        return result
    }

    fun invert(): Boolean {
        var success = false
        synchronized(tmpMatLock) {
            success = invert(tmpMatA)
            if (success) {
                set(tmpMatA)
            }
        }
        return success
    }

    fun invert(result: Mat4d): Boolean {
        // Invert a 4 x 4 matrix using Cramer's Rule

        // transpose matrix
        val src0 = matrix[offset + 0]
        val src4 = matrix[offset + 1]
        val src8 = matrix[offset + 2]
        val src12 = matrix[offset + 3]

        val src1 = matrix[offset + 4]
        val src5 = matrix[offset + 5]
        val src9 = matrix[offset + 6]
        val src13 = matrix[offset + 7]

        val src2 = matrix[offset + 8]
        val src6 = matrix[offset + 9]
        val src10 = matrix[offset + 10]
        val src14 = matrix[offset + 11]

        val src3 = matrix[offset + 12]
        val src7 = matrix[offset + 13]
        val src11 = matrix[offset + 14]
        val src15 = matrix[offset + 15]

        // calculate pairs for first 8 elements (cofactors)
        val atmp0 = src10 * src15
        val atmp1 = src11 * src14
        val atmp2 = src9 * src15
        val atmp3 = src11 * src13
        val atmp4 = src9 * src14
        val atmp5 = src10 * src13
        val atmp6 = src8 * src15
        val atmp7 = src11 * src12
        val atmp8 = src8 * src14
        val atmp9 = src10 * src12
        val atmp10 = src8 * src13
        val atmp11 = src9 * src12

        // calculate first 8 elements (cofactors)
        val dst0 = atmp0 * src5 + atmp3 * src6 + atmp4 * src7 - (atmp1 * src5 + atmp2 * src6 + atmp5 * src7)
        val dst1 = atmp1 * src4 + atmp6 * src6 + atmp9 * src7 - (atmp0 * src4 + atmp7 * src6 + atmp8 * src7)
        val dst2 = atmp2 * src4 + atmp7 * src5 + atmp10 * src7 - (atmp3 * src4 + atmp6 * src5 + atmp11 * src7)
        val dst3 = atmp5 * src4 + atmp8 * src5 + atmp11 * src6 - (atmp4 * src4 + atmp9 * src5 + atmp10 * src6)
        val dst4 = atmp1 * src1 + atmp2 * src2 + atmp5 * src3 - (atmp0 * src1 + atmp3 * src2 + atmp4 * src3)
        val dst5 = atmp0 * src0 + atmp7 * src2 + atmp8 * src3 - (atmp1 * src0 + atmp6 * src2 + atmp9 * src3)
        val dst6 = atmp3 * src0 + atmp6 * src1 + atmp11 * src3 - (atmp2 * src0 + atmp7 * src1 + atmp10 * src3)
        val dst7 = atmp4 * src0 + atmp9 * src1 + atmp10 * src2 - (atmp5 * src0 + atmp8 * src1 + atmp11 * src2)

        // calculate pairs for second 8 elements (cofactors)
        val btmp0 = src2 * src7
        val btmp1 = src3 * src6
        val btmp2 = src1 * src7
        val btmp3 = src3 * src5
        val btmp4 = src1 * src6
        val btmp5 = src2 * src5
        val btmp6 = src0 * src7
        val btmp7 = src3 * src4
        val btmp8 = src0 * src6
        val btmp9 = src2 * src4
        val btmp10 = src0 * src5
        val btmp11 = src1 * src4

        // calculate second 8 elements (cofactors)
        val dst8 = btmp0 * src13 + btmp3 * src14 + btmp4 * src15 - (btmp1 * src13 + btmp2 * src14 + btmp5 * src15)
        val dst9 = btmp1 * src12 + btmp6 * src14 + btmp9 * src15 - (btmp0 * src12 + btmp7 * src14 + btmp8 * src15)
        val dst10 = btmp2 * src12 + btmp7 * src13 + btmp10 * src15 - (btmp3 * src12 + btmp6 * src13 + btmp11 * src15)
        val dst11 = btmp5 * src12 + btmp8 * src13 + btmp11 * src14 - (btmp4 * src12 + btmp9 * src13 + btmp10 * src14)
        val dst12 = btmp2 * src10 + btmp5 * src11 + btmp1 * src9 - (btmp4 * src11 + btmp0 * src9 + btmp3 * src10)
        val dst13 = btmp8 * src11 + btmp0 * src8 + btmp7 * src10 - (btmp6 * src10 + btmp9 * src11 + btmp1 * src8)
        val dst14 = btmp6 * src9 + btmp11 * src11 + btmp3 * src8 - (btmp10 * src11 + btmp2 * src8 + btmp7 * src9)
        val dst15 = btmp10 * src10 + btmp4 * src8 + btmp9 * src9 - (btmp8 * src9 + btmp11 * src10 + btmp5 * src8)

        // calculate determinant
        val det = src0 * dst0 + src1 * dst1 + src2 * dst2 + src3 * dst3

        if (det == 0.0) {
            return false
        }

        // calculate matrix inverse
        val invdet = 1.0 / det
        result.matrix[result.offset] = dst0 * invdet
        result.matrix[1 + result.offset] = dst1 * invdet
        result.matrix[2 + result.offset] = dst2 * invdet
        result.matrix[3 + result.offset] = dst3 * invdet

        result.matrix[4 + result.offset] = dst4 * invdet
        result.matrix[5 + result.offset] = dst5 * invdet
        result.matrix[6 + result.offset] = dst6 * invdet
        result.matrix[7 + result.offset] = dst7 * invdet

        result.matrix[8 + result.offset] = dst8 * invdet
        result.matrix[9 + result.offset] = dst9 * invdet
        result.matrix[10 + result.offset] = dst10 * invdet
        result.matrix[11 + result.offset] = dst11 * invdet

        result.matrix[12 + result.offset] = dst12 * invdet
        result.matrix[13 + result.offset] = dst13 * invdet
        result.matrix[14 + result.offset] = dst14 * invdet
        result.matrix[15 + result.offset] = dst15 * invdet

        return true
    }

    fun transform(vec: MutableVec3f, w: Float = 1.0f): MutableVec3f {
        val x = vec.x * this[0, 0] + vec.y * this[0, 1] + vec.z * this[0, 2] + w * this[0, 3]
        val y = vec.x * this[1, 0] + vec.y * this[1, 1] + vec.z * this[1, 2] + w * this[1, 3]
        val z = vec.x * this[2, 0] + vec.y * this[2, 1] + vec.z * this[2, 2] + w * this[2, 3]
        return vec.set(x.toFloat(), y.toFloat(), z.toFloat())
    }

    fun transform(vec: Vec3f, w: Float = 1.0f, result: MutableVec3f): MutableVec3f {
        result.x = (vec.x * this[0, 0] + vec.y * this[0, 1] + vec.z * this[0, 2] + w * this[0, 3]).toFloat()
        result.y = (vec.x * this[1, 0] + vec.y * this[1, 1] + vec.z * this[1, 2] + w * this[1, 3]).toFloat()
        result.z = (vec.x * this[2, 0] + vec.y * this[2, 1] + vec.z * this[2, 2] + w * this[2, 3]).toFloat()
        return result
    }

    fun transform(vec: MutableVec4f): MutableVec4f {
        val x = vec.x * this[0, 0] + vec.y * this[0, 1] + vec.z * this[0, 2] + vec.w * this[0, 3]
        val y = vec.x * this[1, 0] + vec.y * this[1, 1] + vec.z * this[1, 2] + vec.w * this[1, 3]
        val z = vec.x * this[2, 0] + vec.y * this[2, 1] + vec.z * this[2, 2] + vec.w * this[2, 3]
        val w = vec.x * this[3, 0] + vec.y * this[3, 1] + vec.z * this[3, 2] + vec.w * this[3, 3]
        return vec.set(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat())
    }

    fun transform(vec: Vec4f, result: MutableVec4f): MutableVec4f {
        result.x = (vec.x * this[0, 0] + vec.y * this[0, 1] + vec.z * this[0, 2] + vec.w * this[0, 3]).toFloat()
        result.y = (vec.x * this[1, 0] + vec.y * this[1, 1] + vec.z * this[1, 2] + vec.w * this[1, 3]).toFloat()
        result.z = (vec.x * this[2, 0] + vec.y * this[2, 1] + vec.z * this[2, 2] + vec.w * this[2, 3]).toFloat()
        result.w = (vec.x * this[3, 0] + vec.y * this[3, 1] + vec.z * this[3, 2] + vec.w * this[3, 3]).toFloat()
        return result
    }

    fun transform(vec: MutableVec3d, w: Double = 1.0): MutableVec3d {
        val x = vec.x * this[0, 0] + vec.y * this[0, 1] + vec.z * this[0, 2] + w * this[0, 3]
        val y = vec.x * this[1, 0] + vec.y * this[1, 1] + vec.z * this[1, 2] + w * this[1, 3]
        val z = vec.x * this[2, 0] + vec.y * this[2, 1] + vec.z * this[2, 2] + w * this[2, 3]
        return vec.set(x, y, z)
    }

    fun transform(vec: Vec3d, w: Double = 1.0, result: MutableVec3d): MutableVec3d {
        result.x = vec.x * this[0, 0] + vec.y * this[0, 1] + vec.z * this[0, 2] + w * this[0, 3]
        result.y = vec.x * this[1, 0] + vec.y * this[1, 1] + vec.z * this[1, 2] + w * this[1, 3]
        result.z = vec.x * this[2, 0] + vec.y * this[2, 1] + vec.z * this[2, 2] + w * this[2, 3]
        return result
    }

    fun transform(vec: MutableVec4d): MutableVec4d {
        val x = vec.x * this[0, 0] + vec.y * this[0, 1] + vec.z * this[0, 2] + vec.w * this[0, 3]
        val y = vec.x * this[1, 0] + vec.y * this[1, 1] + vec.z * this[1, 2] + vec.w * this[1, 3]
        val z = vec.x * this[2, 0] + vec.y * this[2, 1] + vec.z * this[2, 2] + vec.w * this[2, 3]
        val w = vec.x * this[3, 0] + vec.y * this[3, 1] + vec.z * this[3, 2] + vec.w * this[3, 3]
        return vec.set(x, y, z, w)
    }

    fun transform(vec: Vec4d, result: MutableVec4d): MutableVec4d {
        result.x = vec.x * this[0, 0] + vec.y * this[0, 1] + vec.z * this[0, 2] + vec.w * this[0, 3]
        result.y = vec.x * this[1, 0] + vec.y * this[1, 1] + vec.z * this[1, 2] + vec.w * this[1, 3]
        result.z = vec.x * this[2, 0] + vec.y * this[2, 1] + vec.z * this[2, 2] + vec.w * this[2, 3]
        result.w = vec.x * this[3, 0] + vec.y * this[3, 1] + vec.z * this[3, 2] + vec.w * this[3, 3]
        return result
    }

    fun add(other: Mat4d): Mat4d {
        for (i in 0..15) {
            matrix[offset + i] += other.matrix[other.offset + i]
        }
        return this
    }

    fun mul(other: Mat4d): Mat4d {
        synchronized(tmpMatLock) {
            mul(other, tmpMatA)
            set(tmpMatA)
        }
        return this
    }

    fun mul(other: Mat4d, result: Mat4d): Mat4d {
        for (i in 0..3) {
            for (j in 0..3) {
                var x = 0.0
                for (k in 0..3) {
                    x += matrix[offset + j + k * 4] * other.matrix[other.offset + i * 4 + k]
                }
                result.matrix[result.offset + i * 4 + j] = x
            }
        }
        return result
    }

    fun set(other: Mat4d): Mat4d {
        for (i in 0..15) {
            matrix[offset + i] = other.matrix[other.offset + i]
        }
        return this
    }

    fun set(other: Mat4f): Mat4d {
        for (i in 0..15) {
            matrix[offset + i] = other.matrix[other.offset + i].toDouble()
        }
        return this
    }

    fun set(doubles: List<Double>): Mat4d {
        for (i in 0..15) {
            matrix[offset + i] = doubles[i]
        }
        return this
    }

    fun setZero(): Mat4d {
        for (i in 0..15) {
            matrix[offset + i] = 0.0
        }
        return this
    }

    fun setIdentity(): Mat4d {
        for (i in 1..15) {
            matrix[offset + i] = 0.0
        }
        matrix[offset] = 1.0
        matrix[offset + 5] = 1.0
        matrix[offset + 10] = 1.0
        matrix[offset + 15] = 1.0
        return this
    }

    fun setRotate(rotA: Double, axX: Double, axY: Double, axZ: Double): Mat4d {
        val a = rotA.toRad()
        var x = axX
        var y = axY
        var z = axZ
        matrix[offset + 3] = 0.0
        matrix[offset + 7] = 0.0
        matrix[offset + 11] = 0.0
        matrix[offset + 12] = 0.0
        matrix[offset + 13] = 0.0
        matrix[offset + 14] = 0.0
        matrix[offset + 15] = 1.0
        val s = sin(a)
        val c = cos(a)
        if (1.0 == x && 0.0 == y && 0.0 == z) {
            matrix[offset + 5] = c
            matrix[offset + 10] = c
            matrix[offset + 6] = s
            matrix[offset + 9] = -s
            matrix[offset + 1] = 0.0
            matrix[offset + 2] = 0.0
            matrix[offset + 4] = 0.0
            matrix[offset + 8] = 0.0
            matrix[offset + 0] = 1.0
        } else if (0.0 == x && 1.0 == y && 0.0 == z) {
            matrix[offset + 0] = c
            matrix[offset + 10] = c
            matrix[offset + 8] = s
            matrix[offset + 2] = -s
            matrix[offset + 1] = 0.0
            matrix[offset + 4] = 0.0
            matrix[offset + 6] = 0.0
            matrix[offset + 9] = 0.0
            matrix[offset + 5] = 1.0
        } else if (0.0 == x && 0.0 == y && 1.0 == z) {
            matrix[offset + 0] = c
            matrix[offset + 5] = c
            matrix[offset + 1] = s
            matrix[offset + 4] = -s
            matrix[offset + 2] = 0.0
            matrix[offset + 6] = 0.0
            matrix[offset + 8] = 0.0
            matrix[offset + 9] = 0.0
            matrix[offset + 10] = 1.0
        } else {
            val len = sqrt(x*x + y*y + z*z)
            if (!isFuzzyEqual(len, 1.0)) {
                val recipLen = 1.0f / len
                x *= recipLen
                y *= recipLen
                z *= recipLen
            }
            val nc = 1.0 - c
            val xy = x * y
            val yz = y * z
            val zx = z * x
            val xs = x * s
            val ys = y * s
            val zs = z * s
            matrix[offset + 0] = x * x * nc + c
            matrix[offset + 4] = xy * nc - zs
            matrix[offset + 8] = zx * nc + ys
            matrix[offset + 1] = xy * nc + zs
            matrix[offset + 5] = y * y * nc + c
            matrix[offset + 9] = yz * nc - xs
            matrix[offset + 2] = zx * nc - ys
            matrix[offset + 6] = yz * nc + xs
            matrix[offset + 10] = z * z * nc + c
        }
        return this
    }

    fun setRotate(quaternion: Vec4d): Mat4d {
        val r = quaternion.w
        val i = quaternion.x
        val j = quaternion.y
        val k = quaternion.z

        var s = sqrt(r*r + i*i + j*j + k*k)
        s = 1.0 / (s * s)

        this[0, 0] = 1.0 - 2*s*(j*j + k*k)
        this[0, 1] = 2.0*s*(i*j - k*r)
        this[0, 2] = 2.0*s*(i*k + j*r)
        this[0, 3] = 0.0

        this[1, 0] = 2.0*s*(i*j + k*r)
        this[1, 1] = 1.0 - 2*s*(i*i + k*k)
        this[1, 2] = 2.0*s*(j*k - i*r)
        this[1, 3] = 0.0

        this[2, 0] = 2.0*s*(i*k - j*r)
        this[2, 1] = 2.0*s*(j*k + i*r)
        this[2, 2] = 1.0 - 2*s*(i*i + j*j)
        this[2, 3] = 0.0

        this[3, 0] = 0.0
        this[3, 1] = 0.0
        this[3, 2] = 0.0
        this[3, 3] = 1.0

        return this
    }

    fun setLookAt(position: Vec3d, lookAt: Vec3d, up: Vec3d): Mat4d {
        // See the OpenGL GLUT documentation for gluLookAt for a description
        // of the algorithm. We implement it in a straightforward way:
        var fx = lookAt.x - position.x
        var fy = lookAt.y - position.y
        var fz = lookAt.z - position.z

        // Normalize f
        val rlf = 1.0 / sqrt(fx*fx + fy*fy + fz*fz)
        fx *= rlf
        fy *= rlf
        fz *= rlf

        // compute s = f x up (x means "cross product")
        var sx = fy * up.z - fz * up.y
        var sy = fz * up.x - fx * up.z
        var sz = fx * up.y - fy * up.x

        // and normalize s
        val rls = 1.0 / sqrt(sx*sx + sy*sy + sz*sz)
        sx *= rls
        sy *= rls
        sz *= rls

        // compute u = s x f
        val ux = sy * fz - sz * fy
        val uy = sz * fx - sx * fz
        val uz = sx * fy - sy * fx

        matrix[offset + 0] = sx
        matrix[offset + 1] = ux
        matrix[offset + 2] = -fx
        matrix[offset + 3] = 0.0

        matrix[offset + 4] = sy
        matrix[offset + 5] = uy
        matrix[offset + 6] = -fy
        matrix[offset + 7] = 0.0

        matrix[offset + 8] = sz
        matrix[offset + 9] = uz
        matrix[offset + 10] = -fz
        matrix[offset + 11] = 0.0

        matrix[offset + 12] = 0.0
        matrix[offset + 13] = 0.0
        matrix[offset + 14] = 0.0
        matrix[offset + 15] = 1.0

        return translate(-position.x, -position.y, -position.z)
    }

    fun setOrthographic(left: Double, right: Double, bottom: Double, top: Double, near: Double, far: Double): Mat4d {
        if (left == right) {
            throw IllegalArgumentException("left == right")
        }
        if (bottom == top) {
            throw IllegalArgumentException("bottom == top")
        }
        if (near == far) {
            throw IllegalArgumentException("near == far")
        }

        val width = 1.0 / (right - left)
        val height = 1.0 / (top - bottom)
        val depth = 1.0 / (far - near)
        val x = 2.0 * width
        val y = 2.0 * height
        val z = -2.0 * depth
        val tx = -(right + left) * width
        val ty = -(top + bottom) * height
        val tz = -(far + near) * depth
        matrix[offset + 0] = x
        matrix[offset + 5] = y
        matrix[offset + 10] = z
        matrix[offset + 12] = tx
        matrix[offset + 13] = ty
        matrix[offset + 14] = tz
        matrix[offset + 15] = 1.0
        matrix[offset + 1] = 0.0
        matrix[offset + 2] = 0.0
        matrix[offset + 3] = 0.0
        matrix[offset + 4] = 0.0
        matrix[offset + 6] = 0.0
        matrix[offset + 7] = 0.0
        matrix[offset + 8] = 0.0
        matrix[offset + 9] = 0.0
        matrix[offset + 11] = 0.0

        return this
    }

    fun setPerspective(fovy: Double, aspect: Double, near: Double, far: Double): Mat4d {
        val f = 1.0 / tan(fovy * (PI / 360.0))
        val rangeReciprocal = 1.0 / (near - far)

        matrix[offset + 0] = f / aspect
        matrix[offset + 1] = 0.0
        matrix[offset + 2] = 0.0
        matrix[offset + 3] = 0.0

        matrix[offset + 4] = 0.0
        matrix[offset + 5] = f
        matrix[offset + 6] = 0.0
        matrix[offset + 7] = 0.0

        matrix[offset + 8] = 0.0
        matrix[offset + 9] = 0.0
        matrix[offset + 10] = (far + near) * rangeReciprocal
        matrix[offset + 11] = -1.0

        matrix[offset + 12] = 0.0
        matrix[offset + 13] = 0.0
        matrix[offset + 14] = 2.0 * far * near * rangeReciprocal
        matrix[offset + 15] = 0.0

        return this
    }

    operator fun get(i: Int): Double = matrix[offset + i]

    operator fun get(row: Int, col: Int): Double = matrix[offset + col * 4 + row]

    operator fun set(i: Int, value: Double) {
        matrix[offset + i] = value
    }

    operator fun set(row: Int, col: Int, value: Double) {
        matrix[offset + col * 4 + row] = value
    }

    fun setColVec(col: Int, vec: Vec3d, w: Double) {
        this[0, col] = vec.x
        this[1, col] = vec.y
        this[2, col] = vec.z
        this[3, col] = w
    }

    fun setColVec(col: Int, value: Vec4d) {
        this[0, col] = value.x
        this[1, col] = value.y
        this[2, col] = value.z
        this[3, col] = value.w
    }

    fun getColVec(col: Int, result: MutableVec4d): MutableVec4d {
        result.y = this[1, col]
        result.x = this[0, col]
        result.z = this[2, col]
        result.w = this[3, col]
        return result
    }

    fun getOrigin(result: MutableVec3d): MutableVec3d {
        result.x = this[0, 3]
        result.y = this[1, 3]
        result.z = this[2, 3]
        return result
    }

//    fun getOrientation(result: Mat3f): Mat3f {
//        result[0, 0] = this[0, 0]
//        result[0, 1] = this[0, 1]
//        result[0, 2] = this[0, 2]
//
//        result[1, 0] = this[1, 0]
//        result[1, 1] = this[1, 1]
//        result[1, 2] = this[1, 2]
//
//        result[2, 0] = this[2, 0]
//        result[2, 1] = this[2, 1]
//        result[2, 2] = this[2, 2]
//
//        return result
//    }
//
//    fun getOrientationTransposed(result: Mat3f): Mat3f {
//        result[0, 0] = this[0, 0]
//        result[0, 1] = this[1, 0]
//        result[0, 2] = this[2, 0]
//
//        result[1, 0] = this[0, 1]
//        result[1, 1] = this[1, 1]
//        result[1, 2] = this[2, 1]
//
//        result[2, 0] = this[0, 2]
//        result[2, 1] = this[1, 2]
//        result[2, 2] = this[2, 2]
//
//        return result
//    }

    fun getRotation(result: MutableVec4d): MutableVec4d {
        val trace = this[0, 0] + this[1, 1] + this[2, 2]

        if (trace > 0f) {
            var s = sqrt(trace + 1f)
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

            var s = sqrt(this[i, i] - this[j, j] - this[k, k] + 1f)
            result[i] = s * 0.5f
            s = 0.5f / s

            result.w = (this[k, j] - this[j, k]) * s
            result[j] = (this[j, i] + this[i, j]) * s
            result[k] = (this[k, i] + this[i, k]) * s
        }
        return result
    }

    fun toBuffer(buffer: Float32Buffer): Float32Buffer {
        for (i in 0 until 16) {
            buffer.put(matrix[offset + i].toFloat())
        }
        buffer.flip()
        return buffer
    }

    companion object {
        private val tmpMatLock = Any()
        private val tmpMatA = Mat4d()
        private val tmpMatB = Mat4d()
    }
}

class Mat4dStack(val stackSize: Int = DEFAULT_STACK_SIZE) : Mat4d() {
    companion object {
        const val DEFAULT_STACK_SIZE = 32
    }

    var stackIndex = 0
        private set(value) {
            field = value
            offset = value * 16
        }

    init {
        matrix = DoubleArray(16 * stackSize)
        setIdentity()
    }

    fun push(): Mat4dStack {
        if (stackIndex >= stackSize) {
            throw KoolException("Matrix stack overflow")
        }
        for (i in 0 .. 15) {
            matrix[offset + 16 + i] = matrix[offset + i]
        }
        stackIndex++
        return this
    }

    fun pop(): Mat4dStack {
        if (stackIndex <= 0) {
            throw KoolException("Matrix stack underflow")
        }
        stackIndex--
        return this
    }

    fun reset(): Mat4dStack {
        stackIndex = 0
        setIdentity()
        return this
    }
}