package de.fabmax.kool.math

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RayDTest {
    @Test
    fun testSphere() {
        val ray = RayD(Vec3d(-0.7955355, 0.8786867, 2.7643886), Vec3d(0.4377568, 0.053849265, -0.89747936))
        val center = Vec3d(0.0, 6.31413, 0.0)
        val radius = 90.76581

        val hit = MutableVec3d()
        assertTrue(ray.sphereIntersection(center, radius, hit))
        assertEquals(ray.origin, hit)
    }
}