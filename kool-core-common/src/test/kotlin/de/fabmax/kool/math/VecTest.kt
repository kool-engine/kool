package de.fabmax.kool.math

import de.fabmax.kool.testAssert
import kotlin.math.sqrt
import kotlin.test.Test

/**
 * Tests for Vec class
 */
class VecTest {
    @Test
    fun vec3Consts() {
        testAssert(Vec3f.X_AXIS.x == 1f && Vec3f.X_AXIS.y == 0f && Vec3f.X_AXIS.z == 0f, "x != (1, 0, 0)")
        testAssert(Vec3f.Y_AXIS.x == 0f && Vec3f.Y_AXIS.y == 1f && Vec3f.Y_AXIS.z == 0f, "y != (0, 1, 0)")
        testAssert(Vec3f.Z_AXIS.x == 0f && Vec3f.Z_AXIS.y == 0f && Vec3f.Z_AXIS.z == 1f, "z != (0, 0, 1)")
        testAssert(Vec3f.ZERO.x == 0f && Vec3f.ZERO.y == 0f && Vec3f.ZERO.z == 0f, "0 != (0, 0, 0)")
    }

    @Test
    fun vec3Len() {
        val t = Vec3f(2f, 3f, 4f)
        testAssert(isFuzzyEqual(t.sqrLength(), 29f), "sqrLen failed")
        testAssert(isFuzzyEqual(t.length(), sqrt(29f)), "length failed")
        testAssert(isFuzzyEqual(t.norm(MutableVec3f()).length(), 1f), "norm failed")
    }

    @Test
    fun vec3Add() = testAssert(add(Vec3f(1f, 2f, 3f), Vec3f(2f, 3f, 4f)).isFuzzyEqual(Vec3f(3f, 5f, 7f)))

    @Test
    fun vec3Sub() = testAssert(subtract(Vec3f(2f, 3f, 4f), Vec3f(1f, 2f, 3f)).isFuzzyEqual(Vec3f(1f, 1f, 1f)))

    @Test
    fun vec3Scale() {
        testAssert(scale(Vec3f(1f, 2f, 3f), 2f).isFuzzyEqual(Vec3f(2f, 4f, 6f)))
    }

    @Test
    fun vec3Dist() {
        testAssert(isFuzzyEqual(Vec3f(1f, 2f, 3f).distance(Vec3f.ZERO), Vec3f(1f, 2f, 3f).length()))
    }

    @Test
    fun vec3Dot() {
        // dot prod
        testAssert(Vec3f.X_AXIS * Vec3f.X_AXIS == 1f, "x * x != 1")
        testAssert(Vec3f.Y_AXIS * Vec3f.Y_AXIS == 1f, "y * y != 1")
        testAssert(Vec3f.Z_AXIS * Vec3f.Z_AXIS == 1f, "z * z != 1")
        testAssert(Vec3f.X_AXIS * Vec3f.Y_AXIS == 0f, "x * y != 0")
        testAssert(Vec3f.X_AXIS * Vec3f.Z_AXIS == 0f, "x * z != 0")
        testAssert(Vec3f.Y_AXIS * Vec3f.Z_AXIS == 0f, "y * z != 0")
    }

    @Test
    fun vec3Cross() {
        // dot prod
        testAssert(cross(Vec3f.X_AXIS, Vec3f.Y_AXIS).isFuzzyEqual(Vec3f.Z_AXIS), "x * y != z")
        testAssert(cross(Vec3f.Z_AXIS, Vec3f.X_AXIS).isFuzzyEqual(Vec3f.Y_AXIS), "z * x != y")
        testAssert(cross(Vec3f.Y_AXIS, Vec3f.Z_AXIS).isFuzzyEqual(Vec3f.X_AXIS), "y * z != x")
    }

    @Test
    fun vec3Rotate() {
        // dot prod
        testAssert(Vec3f.X_AXIS.rotate(90f, Vec3f.Z_AXIS, MutableVec3f()).isFuzzyEqual(Vec3f.Y_AXIS),
                "x.rot(90, z) != y")
        testAssert(Vec3f.Y_AXIS.rotate(90f, Vec3f.X_AXIS, MutableVec3f()).isFuzzyEqual(Vec3f.Z_AXIS),
                "y.rot(90, z) != z")
        testAssert(Vec3f.Z_AXIS.rotate(90f, Vec3f.Y_AXIS, MutableVec3f()).isFuzzyEqual(Vec3f.X_AXIS),
                "z.rot(90, y) != x")
    }
}
