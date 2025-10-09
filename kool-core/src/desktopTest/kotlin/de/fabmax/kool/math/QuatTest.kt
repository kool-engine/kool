package de.fabmax.kool.math

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
        val logs = listOf(
            Vec3d(0.0, 0.0, 0.0),
            Vec3d(0.1, 0.0, 0.0),
            Vec3d(0.0, 1.0, 0.0),
            Vec3d(0.0, 0.0, 0.1),
            Vec3d(0.1, 0.1, 0.1),
            Vec3d(1e-6, 0.0, 0.0),
            Vec3d(1e-12, 0.0, 0.0),
            Vec3d(1e-50, 0.0, 0.0),
        )

        for(log in logs) {
            val q = QuatD.exponent(log)
            val restoredLog = q.log()

            val distance = restoredLog.distance(log)

            println("log: $log, restored: $restoredLog, distance: $distance")
        }
    }
}