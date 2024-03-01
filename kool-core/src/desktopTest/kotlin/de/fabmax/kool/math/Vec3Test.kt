package de.fabmax.kool.math

import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Vec3Test {
    @Test
    fun vec3Consts() {
        assertTrue(Vec3f.X_AXIS.x == 1f && Vec3f.X_AXIS.y == 0f && Vec3f.X_AXIS.z == 0f, "x != (1, 0, 0)")
        assertTrue(Vec3f.Y_AXIS.x == 0f && Vec3f.Y_AXIS.y == 1f && Vec3f.Y_AXIS.z == 0f, "y != (0, 1, 0)")
        assertTrue(Vec3f.Z_AXIS.x == 0f && Vec3f.Z_AXIS.y == 0f && Vec3f.Z_AXIS.z == 1f, "z != (0, 0, 1)")
        assertTrue(Vec3f.ZERO.x == 0f && Vec3f.ZERO.y == 0f && Vec3f.ZERO.z == 0f, "0 != (0, 0, 0)")
    }

    @Test
    fun vec3Len() {
        val t = Vec3f(2f, 3f, 4f)
        assertTrue(isFuzzyEqual(t.sqrLength(), 29f), "sqrLen failed")
        assertTrue(isFuzzyEqual(t.length(), sqrt(29f)), "length failed")
        assertTrue(isFuzzyEqual(t.normed().length(), 1f), "norm failed")
    }

    @Test
    fun vec3Add() = assertTrue(MutableVec3f(1f, 2f, 3f).add(Vec3f(2f, 3f, 4f)).isFuzzyEqual(Vec3f(3f, 5f, 7f)))

    @Test
    fun vec3Sub() = assertTrue(MutableVec3f(2f, 3f, 4f).subtract(Vec3f(1f, 2f, 3f)).isFuzzyEqual(Vec3f(1f, 1f, 1f)))

    @Test
    fun vec3Scale() {
        assertTrue(MutableVec3f(1f, 2f, 3f).mul(2f).isFuzzyEqual(Vec3f(2f, 4f, 6f)))
    }

    @Test
    fun vec3Dist() {
        assertTrue(isFuzzyEqual(Vec3f(1f, 2f, 3f).distance(Vec3f.ZERO), Vec3f(1f, 2f, 3f).length()))
    }

    @Test
    fun vec3Dot() {
        // dot prod
        assertEquals(Vec3f.X_AXIS.dot(Vec3f.X_AXIS), 1f, "x * x != 1")
        assertEquals(Vec3f.Y_AXIS.dot(Vec3f.Y_AXIS), 1f, "y * y != 1")
        assertEquals(Vec3f.Z_AXIS.dot(Vec3f.Z_AXIS), 1f, "z * z != 1")
        assertEquals(Vec3f.X_AXIS.dot(Vec3f.Y_AXIS), 0f, "x * y != 0")
        assertEquals(Vec3f.X_AXIS.dot(Vec3f.Z_AXIS), 0f, "x * z != 0")
        assertEquals(Vec3f.Y_AXIS.dot(Vec3f.Z_AXIS), 0f, "y * z != 0")
    }

    @Test
    fun vec3Cross() {
        // dot prod
        assertTrue(Vec3f.X_AXIS.cross(Vec3f.Y_AXIS, MutableVec3f()).isFuzzyEqual(Vec3f.Z_AXIS), "x * y != z")
        assertTrue(Vec3f.Z_AXIS.cross(Vec3f.X_AXIS, MutableVec3f()).isFuzzyEqual(Vec3f.Y_AXIS), "z * x != y")
        assertTrue(Vec3f.Y_AXIS.cross(Vec3f.Z_AXIS, MutableVec3f()).isFuzzyEqual(Vec3f.X_AXIS), "y * z != x")
    }

    @Test
    fun vec3Rotate() {
        // dot prod
        assertTrue(Vec3f.X_AXIS.rotate(90f.deg, Vec3f.Z_AXIS, MutableVec3f()).isFuzzyEqual(Vec3f.Y_AXIS), "x.rot(90, z) != y")
        assertTrue(Vec3f.Y_AXIS.rotate(90f.deg, Vec3f.X_AXIS, MutableVec3f()).isFuzzyEqual(Vec3f.Z_AXIS), "y.rot(90, z) != z")
        assertTrue(Vec3f.Z_AXIS.rotate(90f.deg, Vec3f.Y_AXIS, MutableVec3f()).isFuzzyEqual(Vec3f.X_AXIS), "z.rot(90, y) != x")
    }
}
