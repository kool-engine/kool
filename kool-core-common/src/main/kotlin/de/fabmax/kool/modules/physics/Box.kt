package de.fabmax.kool.modules.physics

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import kotlin.math.abs

class Box(sizeX: Float, sizeY: Float, sizeZ: Float) {
    val halfExtents = Vec3f(sizeX * 0.5f, sizeY * 0.5f, sizeZ * 0.5f)
    val eX: Float get() = halfExtents.x
    val eY: Float get() = halfExtents.y
    val eZ: Float get() = halfExtents.z

    val transform = Mat4f()
    val bX: Vec3f = ColVecView(0)
    val bY: Vec3f = ColVecView(1)
    val bZ: Vec3f = ColVecView(2)
    val center: MutableVec3f = ColVecView(3)

    private val tmpD = MutableVec3f()

    /**
     * Tests this and another oriented bounding box for intersection. This function performs a separating axis test
     * (SAT). Implementation is based on https://www.geometrictools.com/Documentation/DynamicCollisionDetection.pdf
     * Thanks David Eberly!
     */
    fun isIntersecting(other: Box): Boolean {
        other.center.subtract(center, tmpD)

        val rX = halfExtents.x + other.halfExtents.x
        val rY = halfExtents.y + other.halfExtents.y
        val rZ = halfExtents.z + other.halfExtents.z
        if (tmpD.sqrLength() > rX*rX + rY*rY + rZ*rZ) {
            // bounding spheres do not intersect
            return false
        }

        val c00 = bX * other.bX
        val c10 = bY * other.bX
        val c20 = bZ * other.bX

        val c01 = bX * other.bY
        val c11 = bY * other.bY
        val c21 = bZ * other.bY

        val c02 = bX * other.bZ
        val c12 = bY * other.bZ
        val c22 = bZ * other.bZ


        // L = bX
        var r0 = halfExtents.x
        var r1 = other.halfExtents.x * abs(c00) + other.halfExtents.y * abs(c01) + other.halfExtents.z * abs(c02)
        var r = abs(bX * tmpD)
        if (r > r0 + r1) { return false }

        // L = bY
        r0 = halfExtents.y
        r1 = other.halfExtents.x * abs(c10) + other.halfExtents.y * abs(c11) + other.halfExtents.z * abs(c12)
        r = abs(bY * tmpD)
        if (r > r0 + r1) { return false }

        // L = bZ
        r0 = halfExtents.z
        r1 = other.halfExtents.x * abs(c20) + other.halfExtents.y * abs(c21) + other.halfExtents.z * abs(c22)
        r = abs(bZ * tmpD)
        if (r > r0 + r1) { return false }


        // L = other.bX
        r0 = halfExtents.x * abs(c00) + halfExtents.y * abs(c10) + halfExtents.z * abs(c20)
        r1 = other.halfExtents.x
        r = abs(other.bX * tmpD)
        if (r > r0 + r1) { return false }

        // L = other.bY
        r0 = halfExtents.x * abs(c01) + halfExtents.y * abs(c11) + halfExtents.z * abs(c21)
        r1 = other.halfExtents.y
        r = abs(other.bY * tmpD)
        if (r > r0 + r1) { return false }

        // L = other.bZ
        r0 = halfExtents.x * abs(c02) + halfExtents.y * abs(c12) + halfExtents.z * abs(c22)
        r1 = other.halfExtents.z
        r = abs(other.bZ * tmpD)
        if (r > r0 + r1) { return false }


        // L = bX x other.bX
        r0 = halfExtents.y * abs(c20) + halfExtents.z * abs(c10)
        r1 = other.halfExtents.y * abs(c02) + other.halfExtents.z * abs(c01)
        r = abs(c10 * (bZ * tmpD) - c20 * (bY * tmpD))
        if (r > r0 + r1) { return false }

        // L = bX x other.bY
        r0 = halfExtents.y * abs(c21) + halfExtents.z * abs(c11)
        r1 = other.halfExtents.x * abs(c02) + other.halfExtents.z * abs(c00)
        r = abs(c11 * (bZ * tmpD) - c21 * (bY * tmpD))
        if (r > r0 + r1) { return false }

        // L = bX x other.bZ
        r0 = halfExtents.y * abs(c22) + halfExtents.z * abs(c12)
        r1 = other.halfExtents.x * abs(c01) + other.halfExtents.y * abs(c00)
        r = abs(c12 * (bZ * tmpD) - c22 * (bY * tmpD))
        if (r > r0 + r1) { return false }


        // L = bY x other.bX
        r0 = halfExtents.x * abs(c20) + halfExtents.z * abs(c00)
        r1 = other.halfExtents.y * abs(c12) + other.halfExtents.z * abs(c11)
        r = abs(c20 * (bX * tmpD) - c00 * (bZ * tmpD))
        if (r > r0 + r1) { return false }

        // L = bY x other.bY
        r0 = halfExtents.x * abs(c21) + halfExtents.z * abs(c01)
        r1 = other.halfExtents.x * abs(c12) + other.halfExtents.z * abs(c10)
        r = abs(c21 * (bX * tmpD) - c01 * (bZ * tmpD))
        if (r > r0 + r1) { return false }

        // L = bY x other.bZ
        r0 = halfExtents.x * abs(c22) + halfExtents.z * abs(c02)
        r1 = other.halfExtents.x * abs(c11) + other.halfExtents.y * abs(c10)
        r = abs(c22 * (bX * tmpD) - c02 * (bZ * tmpD))
        if (r > r0 + r1) { return false }


        // L = bZ x other.bX
        r0 = halfExtents.x * abs(c10) + halfExtents.y * abs(c00)
        r1 = other.halfExtents.y * abs(c22) + other.halfExtents.z * abs(c21)
        r = abs(c00 * (bY * tmpD) - c10 * (bX * tmpD))
        if (r > r0 + r1) { return false }

        // L = bZ x other.bY
        r0 = halfExtents.x * abs(c11) + halfExtents.y * abs(c01)
        r1 = other.halfExtents.x * abs(c22) + other.halfExtents.z * abs(c20)
        r = abs(c01 * (bY * tmpD) - c11 * (bX * tmpD))
        if (r > r0 + r1) { return false }

        // L = bZ x other.bZ
        r0 = halfExtents.x * abs(c12) + halfExtents.y * abs(c02)
        r1 = other.halfExtents.x * abs(c21) + other.halfExtents.y * abs(c20)
        r = abs(c02 * (bY * tmpD) - c12 * (bX * tmpD))
        if (r > r0 + r1) { return false }

        return true
    }

    private inner class ColVecView(col: Int) : MutableVec3f() {
        private val iX = col * 4
        private val iY = col * 4 + 1
        private val iZ = col * 4 + 2

        override var x: Float
            get() = transform.matrix[iX]
            set(value) { transform.matrix[iX] = value }
        override var y: Float
            get() = transform.matrix[iY]
            set(value) { transform.matrix[iY] = value }
        override var z: Float
            get() = transform.matrix[iZ]
            set(value) { transform.matrix[iZ] = value }
    }
}
