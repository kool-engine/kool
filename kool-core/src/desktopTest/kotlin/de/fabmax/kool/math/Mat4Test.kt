package de.fabmax.kool.math

import kotlin.random.Random
import kotlin.test.Test

class Mat4Test {

    @Test
    fun mulTest() {
        val t1 = Mat4f(
            1f, 2f, 3f, 4f,
            5f, 6f, 7f, 8f,
            9f, 10f, 11f, 12f,
            13f, 14f, 15f, 16f
        )
        val t2 = Mat4f(
            1f, 5f, 9f, 13f,
            2f, 6f, 10f, 14f,
            3f, 7f, 11f, 15f,
            4f, 8f, 12f, 16f
        )
        val expected = Mat4f(
            30f, 70f, 110f, 150f,
            70f, 174f, 278f, 382f,
            110f, 278f, 446f, 614f,
            150f, 382f, 614f, 846f
        )

        assert(t1 * t2 == expected)

        val mutT1 = MutableMat4f(t1)
        mutT1 *= t2
        assert(mutT1 == expected)
    }

    @Test
    fun translateTest() {
        val t = Vec3f(1f, 2f, 3f)
        val mat = MutableMat4f().translate(t)
        val r = mat * Vec4f(0f, 0f, 0f, 1f)
        assert(r.xyz.isFuzzyEqual(t)) { "Result: $r, expected: $t" }

        val mt = Mat4f.translation(4f, 5f, 6f)
        mat.rotate(30f.deg, 40f.deg, 50f.deg)

        val m2 = MutableMat4f(mat)
        mat.translate(4f, 5f, 6f)
        m2.mul(mt)

        assert(m2.isFuzzyEqual(mat))
    }

    @Test
    fun axisRotationTest() {
        val rotAxX = Mat4f.rotation(90f.deg, Vec3f.X_AXIS)
        val rX = rotAxX * Vec4f(Vec3f.Y_AXIS, 1f)
        assert(rX.xyz.isFuzzyEqual(Vec3f.Z_AXIS)) { "X-axis rotation: Result: ${rX.xyz}, expected: ${Vec3f.Z_AXIS}" }

        val rotAxY = Mat4f.rotation(90f.deg, Vec3f.Y_AXIS)
        val rY = rotAxY * Vec4f(Vec3f.Z_AXIS, 1f)
        assert(rY.xyz.isFuzzyEqual(Vec3f.X_AXIS)) { "Y-axis rotation: Result: ${rY.xyz}, expected: ${Vec3f.X_AXIS}" }

        val rotAxZ = Mat4f.rotation(90f.deg, Vec3f.Z_AXIS)
        val rZ = rotAxZ * Vec4f(Vec3f.X_AXIS, 1f)
        assert(rZ.xyz.isFuzzyEqual(Vec3f.Y_AXIS)) { "Z-axis rotation: Result: ${rZ.xyz}, expected: ${Vec3f.Y_AXIS}" }

        val rotGen = Mat4f.rotation(45f.deg, Vec3f.ONES)
        val rGen = rotGen * Vec4f(Vec3f.X_AXIS, 1f)
        assert(rGen.xyz.isFuzzyEqual(Vec3f(0.80473787f, 0.50587934f, -0.3106172f))) {
            "General case rotation: Result: ${rGen.xyz}, expected: (0.80473787, 0.50587934, -0.3106172)"
        }
    }

    @Test
    fun quaternionRotationTest() {
        val rotX = Mat4f.rotation(QuatF.rotation(90f.deg, Vec3f.X_AXIS))
        val rX = rotX * Vec4f(Vec3f.Y_AXIS, 1f)
        assert(rX.xyz.isFuzzyEqual(Vec3f.Z_AXIS)) { "X-axis quaternion rotation: Result: ${rX.xyz}, expected: ${Vec3f.Z_AXIS}" }

        val rotY = Mat4f.rotation(QuatF.rotation(90f.deg, Vec3f.Y_AXIS))
        val rY = rotY * Vec4f(Vec3f.Z_AXIS, 1f)
        assert(rY.xyz.isFuzzyEqual(Vec3f.X_AXIS)) { "Y-axis quaternion rotation: Result: ${rY.xyz}, expected: ${Vec3f.X_AXIS}" }

        val rotZ = Mat4f.rotation(QuatF.rotation(90f.deg, Vec3f.Z_AXIS))
        val rZ = rotZ * Vec4f(Vec3f.X_AXIS, 1f)
        assert(rZ.xyz.isFuzzyEqual(Vec3f.Y_AXIS)) { "Z-axis quaternion rotation: Result: ${rZ.xyz}, expected: ${Vec3f.Y_AXIS}" }

        val rotGen = Mat4f.rotation(QuatF.rotation(45f.deg, Vec3f.ONES))
        val rGen = rotGen * Vec4f(Vec3f.X_AXIS, 1f)
        assert(rGen.xyz.isFuzzyEqual(Vec3f(0.80473787f, 0.50587934f, -0.3106172f))) {
            "General case quaternion rotation: Result: ${rGen.xyz}, expected: (0.80473787, 0.50587934, -0.3106172)"
        }
    }

    @Test
    fun eulerRotationTest() {
        val rotX = Mat4f.rotation(90f.deg, 0f.deg, 0f.deg)
        val rX = rotX * Vec4f(Vec3f.Y_AXIS, 1f)
        assert(rX.xyz.isFuzzyEqual(Vec3f.Z_AXIS)) { "X-axis euler rotation: Result: ${rX.xyz}, expected: ${Vec3f.Z_AXIS}" }

        val rotY = Mat4f.rotation(0f.deg, 90f.deg, 0f.deg)
        val rY = rotY * Vec4f(Vec3f.Z_AXIS, 1f)
        assert(rY.xyz.isFuzzyEqual(Vec3f.X_AXIS)) { "Y-axis euler rotation: Result: ${rY.xyz}, expected: ${Vec3f.X_AXIS}" }

        val rotZ = Mat4f.rotation(0f.deg, 0f.deg, 90f.deg)
        val rZ = rotZ * Vec4f(Vec3f.X_AXIS, 1f)
        assert(rZ.xyz.isFuzzyEqual(Vec3f.Y_AXIS)) { "Z-axis euler rotation: Result: ${rZ.xyz}, expected: ${Vec3f.Y_AXIS}" }
    }

    @Test
    fun scaleTest() {
        val scaleUni = MutableMat4f().scale(1.23f)
        val sUni = scaleUni * Vec4f.ONES
        assert(sUni.xyz.isFuzzyEqual(Vec3f(1.23f, 1.23f, 1.23f))) { "Uniform scale: Result: ${sUni.xyz}, expected: (1.23, 1.23, 1.23)" }

        val scaleXyz = MutableMat4f().scale(Vec3f(2f, 3f, 4f))
        val sXyz = scaleXyz * Vec4f.ONES
        assert(sXyz.xyz.isFuzzyEqual(Vec3f(2f, 3f, 4f))) { "Xyz scale: Result: ${sUni.xyz}, expected: (2.0, 3.0, 4.0)" }
    }

    @Test
    fun composeTest() {
        val t = Vec3f(4f, 5f, 6f)
        val s = Vec3f(1.5f, 2.5f, 3.5f)
        val r = QuatF.rotation(45f.deg, Vec3f.ONES)

        val m = Mat4f.composition(t, r, s)
        val mt = MutableMat4f()
            .translate(t)
            .rotate(r)
            .scale(s)
        assert(m.isFuzzyEqual(mt)) { "Result: $m, expected: $mt" }

        val rt = MutableVec3f()
        val rr = MutableQuatF()
        val rs = MutableVec3f()
        m.decompose(rt, rr, rs)

        assert(rt.isFuzzyEqual(t)) { "Decomposed translation: Result: $rt, expected: $t" }
        assert(rr.isFuzzyEqual(r)) { "Decomposed rotation: Result: $rr, expected: $r" }
        assert(rs.isFuzzyEqual(s)) { "Decomposed scale: Result: $rs, expected: $s" }
    }

    @Test
    fun decomposeQuatTest() {
        val rand = Random(1337)
        for (i in 1..100) {
            val ax = rand.randomInUnitSphere()
            val q = QuatF.rotation(rand.randomF(-180f, 180f).deg, ax)
            val m = Mat4f.rotation(q)

            val rq = MutableQuatF()
            m.decompose(MutableVec3f(), rq, MutableVec3f())
            assert(q.isFuzzyEqual(rq)) { "$rq != $q (expected)" }
        }
    }

    @Test
    fun eulerTest() {
        val rand = Random(1337)
        val eps = 1e-3f

        for (test in 1..100) {
            val o = EulerOrder.entries.random(rand)
            val ex = rand.randomF(-90f, 90f).deg
            val ey = rand.randomF(-90f, 90f).deg
            val ez = rand.randomF(-90f, 90f).deg
            val m = Mat4f.rotation(ex, ey, ez, o)

            val r = m.getEulerAngles(MutableVec3f(), o)
            assert(isFuzzyEqual(ex.deg, r.x, eps)) { "${r.x} != ${ex.deg} (expected)" }
            assert(isFuzzyEqual(ey.deg, r.y, eps)) { "${r.y} != ${ey.deg} (expected)" }
            assert(isFuzzyEqual(ez.deg, r.z, eps)) { "${r.z} != ${ez.deg} (expected)" }
        }
    }

    @Test
    fun stackTest() {
        val stack = Mat4fStack()

        val m = Mat4f.rotation(37f.deg, Vec3f.ONES)

        assert(stack.isFuzzyEqual(Mat4f.IDENTITY))
        stack.push()
        assert(stack.isFuzzyEqual(Mat4f.IDENTITY))
        stack.mul(m)
        assert(stack.isFuzzyEqual(m))
        stack.pop()
        assert(stack.isFuzzyEqual(Mat4f.IDENTITY))
    }
}