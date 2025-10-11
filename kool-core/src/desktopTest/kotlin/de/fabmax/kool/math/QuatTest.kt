package de.fabmax.kool.math

import kotlin.math.cos
import kotlin.math.sin
import kotlin.test.Test

class QuatTest {

    @Test
    fun fuzzyEqualRotation() {
        val q = QuatD(1.0, 2.0, 3.0, 4.0).normed()
        val minusQ = q * -1.0

        assert(q.isFuzzyEqualRotation(minusQ))

        val m1 = Mat3d.rotation(q)
        val m2 = Mat3d.rotation(minusQ)

        assert(m1.isFuzzyEqual(m2))
    }

    @Test
    fun exponentAndLogarithmAreInvertible() {
        for(log in testVectors) {
            val q = QuatD.exponent(log)
            val restoredLog = q.log()
            assert(log.isFuzzyEqual(restoredLog, 1e-15))
        }
    }

    @Test
    fun exponentAndNaiveExponentAreTheSame() {
        for(log in testVectors) {
            val qD = QuatD.exponent(log)
            val qF = QuatF.exponent(log.toVec3f())
            val qNaive = naiveExponent(log)
            assert(qD.isFuzzyEqual(qNaive, 1e-15))
            assert(qF.isFuzzyEqual(qNaive.toQuatF(), 1e-7f))
        }
    }

    @Test
    fun quaternionPow() {
        for(log in testVectors) {
            val q = QuatD.exponent(log)

            assert(q.pow(-1.0).isFuzzyEqual(q.inverted(), 1e-15))
            assert(q.pow(0.0).isFuzzyEqual(QuatD.IDENTITY, 1e-15))
            assert(q.pow(1.0).isFuzzyEqual(q, 1e-15))
            assert(q.pow(2.0).isFuzzyEqual(q * q, 1e-14))
            assert(q.pow(3.0).isFuzzyEqual(q * q * q, 1e-14))
        }
    }

    @Test
    fun exponentAndRotation() {
        val a = QuatD.rotation(AngleD(1.0), Vec3d.X_AXIS)
        val b = QuatD.exponent(Vec3d.X_AXIS, 0.5)
        assert(a.isFuzzyEqual(b, 1e-15))
    }

    @Test
    fun rotationEqualsToSetRotation() {
        val q = QuatD(1.0, 2.0, 3.0, 4.0).normed()
        val rotatedQ = MutableQuatD(q).rotate(AngleD(1.0), Vec3d.X_AXIS)
        val rotatedByMult = MutableQuatD(q) * QuatD.rotation(AngleD(1.0), Vec3d.X_AXIS)

        assert(rotatedQ.isFuzzyEqual(rotatedByMult, 1e-15))
    }

    @Test
    fun testRotationX() {
        val angle = 90.0.deg
        val a = QuatD.rotation(angle, Vec3d.X_AXIS)
        val b = MutableQuatD().rotate(angle, Vec3d.X_AXIS)

        assert(a.isFuzzyEqual(b, 1e-15))

        assert(a.w == cos(angle.rad * 0.5))
        assert(a.x == sin(angle.rad * 0.5))
        assert(a.y == 0.0)
        assert(a.z == 0.0)
    }

    @Test
    fun exponentAndMultiplier() {
        for(log in testVectors) {
            val q = QuatD.exponent(log)

            val q2 = QuatD.exponent(log, 2.0)
            val q2v2 = QuatD.exponent(log * 2.0, 1.0)

            assert(q2.isFuzzyEqual(q * q, 1e-15))
            assert(q2.isFuzzyEqual(q2v2, 1e-16))
        }
    }

    private fun naiveExponent(v: Vec3d): QuatD {
        val q = QuatD(v.x, v.y, v.z, 0.0)
        val seriesSum = MutableQuatD().setIdentity()

        val qN = MutableQuatD().setIdentity()
        var factorial = 1.0

        for(i in 1 .. 100) {
            qN *= q
            factorial *= i

            val sumElement = qN * (1.0 / factorial)

            if (sumElement.length() < 1e-15) {
                break
            }

            seriesSum += sumElement
        }

        assert(isFuzzyEqual(seriesSum.length(), 1.0, 1e-15)) { "seriesSum.length() = ${seriesSum.length()}"}
        return seriesSum
    }

    companion object {
        private val testVectors = listOf(
            Vec3d(0.0, 0.0, 0.0),
            Vec3d(0.1, 0.0, 0.0),
            Vec3d(0.0, 1.0, 0.0),
            Vec3d(0.0, 0.0, 0.1),
            Vec3d(0.1, 0.1, 0.1),
            Vec3d(0.1, 0.2, 0.3),
            Vec3d(1e-6, 0.0, 0.0),
            Vec3d(1e-12, 0.0, 0.0),
            Vec3d(1e-50, 0.0, 0.0),
        )
    }
}