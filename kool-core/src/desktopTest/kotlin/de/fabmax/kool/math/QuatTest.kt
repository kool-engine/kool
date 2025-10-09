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
}