package de.fabmax.kool.demo.procedural

import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.deferred.deferredKslPbrShader
import de.fabmax.kool.scene.ColorMesh
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.addGroup
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.geometry.simpleShape
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Color.Hsv
import de.fabmax.kool.util.ColorGradient
import de.fabmax.kool.util.MdColor
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

class Roses : Node() {

    init {
        makeRose(1234)
        makeRose(6415168)
        makeRose(2541685)
        makeRose(-336577773)
        makeRose(1339055691)

        transform.translate(-7.5f, 10.5f, 2.5f)
    }

    fun makeRose(seed: Int) {
        val rose = GeneratedRose(seed)

        addGroup {
            addNode(rose.shaftMesh)
            addNode(rose.shaftLeafMesh)
            addNode(rose.leafMesh)
            addNode(rose.blossomMesh)
        }
    }

    private class GeneratedRose(seed: Int) {
        val shaftGrad = ColorGradient(0f to (MdColor.BROWN toneLin 900), 0.4f to MdColor.BROWN.toLinear(), 1f to (MdColor.LIGHT_GREEN tone 600))
        val blossomLeafGrad = ColorGradient(MdColor.LIGHT_GREEN toneLin 600, MdColor.LIGHT_GREEN.mix(MdColor.YELLOW tone 200, 0.5f).toLinear())

        val shaftMesh: Mesh
        val leafMesh: Mesh
        val shaftLeafMesh: Mesh
        val blossomMesh: Mesh

        val rand = Random(seed)
        val shaftTopTransform = MutableMat4f()
        val shaftLeafTransform = MutableMat4f()

        init {
            shaftMesh = ColorMesh().apply {
                generate {
                    makeShaftGeometry()
                    geometry.removeDegeneratedTriangles()
                    geometry.generateNormals()
                }
                shader = deferredKslPbrShader {
                    color { vertexColor() }
                    roughness(0.3f)
                }
            }
            shaftLeafMesh = ColorMesh().apply {
                generate {
                    makeShaftLeafGeometry()
                    geometry.removeDegeneratedTriangles()
                    geometry.generateNormals()
                }
                shader = deferredKslPbrShader {
                    color { vertexColor() }
                    roughness(0.5f)
                }
            }
            leafMesh = ColorMesh().apply {
                generate {
                    makeLeafGeometry()
                    geometry.removeDegeneratedTriangles()
                    geometry.generateNormals()
                }
                shader = deferredKslPbrShader {
                    color { vertexColor() }
                    roughness(0.5f)
                }
            }
            blossomMesh = ColorMesh().apply {
                generate {
                    makeBlossomGeometry()
                    geometry.removeDegeneratedTriangles()
                    geometry.generateNormals()
                }
                shader = deferredKslPbrShader {
                    color { vertexColor() }
                    roughness(0.8f)
                }
            }

            applyTint(Hsv(rand.randomF(0f, 360f), 0.12f, rand.randomF(0.7f, 1f)).toLinearRgb())
        }

        fun applyTint(tint: Color) {
            shaftMesh.geometry.applyTint(tint)
            shaftLeafMesh.geometry.applyTint(tint)
            leafMesh.geometry.applyTint(tint)
            blossomMesh.geometry.applyTint(tint)
        }

        private fun IndexedVertexList.applyTint(tint: Color) {
            forEach {
                it.color.r *= tint.r
                it.color.g *= tint.g
                it.color.b *= tint.b
                it.color.a *= tint.a
            }
        }

        private fun MeshBuilder.makeShaftGeometry() {
            withTransform {
                rotate(90f.deg, Vec3f.NEG_X_AXIS)

                // shaft
                profile {
                    circleShape(0.2f, steps = 8)

                    val leafPos = rand.randomI(2, 3)

                    val steps = 6
                    for (i in 0 until steps) {
                        val ax = MutableVec3f(rand.randomF(-1f, 1f), rand.randomF(-1f, 1f), 0f).norm()
                        rotate(rand.randomF(0f, 15f).deg, ax)
                        val h = rand.randomF(2f, 4f)

                        val p = i.toFloat() / steps
                        val sub = 1f / steps

                        scale(0.8f, 0.8f, 1f)
                        translate(0f, 0f, h * 0.1f)
                        color = shaftGrad.getColor(p + sub * 0.15f).mix(Color.BLACK, 0.25f)
                        sample()
                        scale(0.8f, 0.8f, 1f)
                        translate(0f, 0f, h * 0.2f)
                        color = shaftGrad.getColor(p + sub * 0.3f)
                        sample()
                        translate(0f, 0f, h * 0.4f)
                        color = shaftGrad.getColor(p + sub * 0.55f)
                        sample()
                        scale(1 / 0.8f, 1 / 0.8f, 1f)
                        translate(0f, 0f, h * 0.2f)
                        color = shaftGrad.getColor(p + sub * 0.7f).mix(Color.BLACK, 0.25f)
                        sample()
                        scale(1 / 0.8f, 1 / 0.8f, 1f)
                        translate(0f, 0f, h * 0.1f)
                        color = shaftGrad.getColor(p + sub * 0.85f).mix(Color.BLACK, 0.55f)
                        sample()

                        if (i == leafPos) {
                            shaftLeafTransform.set(transform)
                        } else {
                            for (j in 0 until rand.randomI(0, 1)) {
                                makeThorn()
                            }
                        }
                    }

                    translate(0f, 0f, 0.2f)
                    withTransform {
                        scale(1.5f, 1.5f, 1f)
                        sample()
                    }
                    translate(0f, 0f, 0.15f)
                    withTransform {
                        scale(3f, 3f, 1f)
                        sample()
                    }
                    fillTop()
                }

                shaftTopTransform.set(transform)
            }
        }

        private fun MeshBuilder.makeThorn() {
            withTransform {
                val ax = MutableVec3f(rand.randomF(-1f, 1f), rand.randomF(-1f, 1f), 0f).norm()
                rotate(90f.deg, ax)
                val shaftUp = MutableVec3f(0f, 0f, 1f).rotate((-90f).deg, ax).mul(0.1f)
                translate(0f, 0f, 0.1f)

                profile {
                    circleShape(0.18f, steps = 8)

                    for (i in 0..4) {
                        val p = i / 4f
                        val s = (1 - p).pow(1.5f)
                        scale(s, s, 1f)
                        sample()
                        scale(1 / s, 1 / s, 1f)

                        translate(0f, 0f, (1 - p) * 0.1f)
                        translate(shaftUp)
                    }
                }
            }
        }

        private fun MeshBuilder.makeShaftLeafGeometry() {
            transform.mul(shaftLeafTransform)
            rotate(rand.randomF(0f, 360f).deg, Vec3f.Z_AXIS)

            val grad = ColorGradient(shaftGrad.getColor(0.7f), MdColor.LIGHT_GREEN toneLin 900)

            for (i in 0..1) {
                withTransform {
                    rotate(rand.randomF(30f, 45f).deg, Vec3f.X_AXIS)
                    color = grad.getColor(0f)
                    val leafBases = mutableListOf<Mat4f>()

                    val rotYOffset = rand.randomF(-4f, 4f)
                    val rotZOffset = rand.randomF(-4f, 4f)

                    withTransform {
                        profile {
                            circleShape(0.1f, steps = 8)

                            sample()
                            translate(0f, 0f, 0.4f)
                            rotate(rand.randomF(2f, 4f).deg, Vec3f.X_AXIS)
                            withXyScale(0.6f) { sample() }
                            translate(0f, 0f, 0.4f)
                            rotate(rand.randomF(2f, 4f).deg, Vec3f.X_AXIS)
                            withXyScale(0.4f) { sample() }

                            for (j in 0..10) {
                                color = grad.getColor(j / 10f)
                                translate(0f, 0f, 0.4f)
                                rotate(rand.randomF(2f, 4f).deg, Vec3f.X_AXIS)
                                rotate((rand.randomF(-5f, 5f) + rotYOffset).deg, Vec3f.Y_AXIS)
                                rotate((rand.randomF(-5f, 5f) + rotZOffset).deg, Vec3f.Z_AXIS)
                                withXyScale(0.4f * 0.89f.pow(i)) {
                                    sample()
                                    val s = MutableVec3f()
                                    transform.decompose(scale = s)
                                    leafBases += MutableMat4f().set(transform).scale(Vec3f(1f / s.x, 1f / s.y, 1f / s.z))
                                }
                            }
                            fillTop()
                        }
                    }

                    profile {
                        color = (MdColor.LIGHT_GREEN toneLin 900).mix(MdColor.BROWN toneLin 900, 0.4f)

                        val ref = mutableListOf<Vec3f>()
                        for (j in -6..6) {
                            val p = j / 6f
                            val q = (abs(p) - 0.5f).pow(2) - 0.25f
                            ref += Vec3f(-0.5f * q, j * 0.2f, abs(j) * 0.15f * (0.7f + 0.3f * p * p))
                        }

                        simpleShape(true) {
                            positions += MutableVec3f(ref.first()).also { v -> v.x += 0.01f }
                            ref.forEach { positions += MutableVec3f(it).also { v -> v.x += 0.01f } }
                            positions += MutableVec3f(ref.last()).also { v -> v.x += 0.01f }

                            positions += MutableVec3f(ref.last()).also { v -> v.x -= 0.01f }
                            ref.reversed().forEach { positions += MutableVec3f(it).also { v -> v.x -= 0.01f } }
                            positions += MutableVec3f(ref.first()).also { v -> v.x -= 0.01f }
                        }

                        val scales = mutableListOf(0.6f, 0.9f, 1.0f, 0.95f, 0.85f, 0.7f, 0.5f, 0.35f, 0.22f, 0.12f, 0.05f)
                        withTransform {
                            val zRot = rand.randomF(-60f, 60f)
                            leafBases.forEachIndexed { i, base ->
                                transform.set(base)
                                rotate(zRot.deg, Vec3f.Z_AXIS)
                                val scale = scales[i]
                                val nextScale = if (i < scales.lastIndex) scales[i+1] + 0.05f else 0f
                                val invScale = 1f / scale

                                var s = 1f
                                for (j in 0..4) {
                                    val p = j / 4f
                                    s = scale * (1-p) + nextScale * p

                                    translate(0f, 0f, 0.06f)
                                    rotate(rotZOffset.deg / 5, Vec3f.Z_AXIS)
                                    withTransform {
                                        scale(s, s, s)
                                        sample()
                                    }
                                }

                                val baseColor = color
                                scale(s * 0.8f, s, s)
                                translate(0f, 0f, 0.02f * invScale)
                                color = baseColor.mix(Color.BLACK, 0.4f)
                                sample()
                                translate(0f, 0f, 0.01f * invScale)
                                sample()
                                scale(1 / 0.8f, 1f, 1f)

                                color = baseColor
                                translate(0f, 0f, 0.02f * invScale)
                                sample()
                            }
                        }
                    }
                }
                rotate(rand.randomF(160f, 220f).deg, Vec3f.Z_AXIS)
            }
        }

        private inline fun MeshBuilder.withXyScale(s: Float, block: MeshBuilder.() -> Unit) {
            scale(s, s, 1f)
            block()
            scale(1/s, 1/s, 1f)
        }

        private fun MeshBuilder.makeLeafGeometry() {
            withTransform {
                transform.mul(shaftTopTransform)

                for (l in 0..5) {
                    withTransform {
                        scale(0.8f, 0.8f, 0.8f)
                        rotate(60f.deg * l, Vec3f.Z_AXIS)
                        profile {
                            val ref = mutableListOf<Vec2f>()
                            val jit = 0.03f
                            for (i in 0..10) {
                                val a = (i / 10f * 72 - 36).toRad()
                                val seam = if (i == 5) -0.05f else 0f
                                if (i == 5) {
                                    val aa = a - 2f.toRad()
                                    ref += Vec2f(cos(aa) - 0.7f + rand.randomF(-jit, jit), sin(aa) + rand.randomF(-jit, jit))
                                }
                                ref += Vec2f(cos(a) - 0.7f + rand.randomF(-jit, jit) + seam, sin(a) + rand.randomF(-jit, jit))
                                if (i == 5) {
                                    val aa = a + 2f.toRad()
                                    ref += Vec2f(cos(aa) - 0.7f + rand.randomF(-jit, jit), sin(aa) + rand.randomF(-jit, jit))
                                }
                            }

                            simpleShape(true) {
                                ref.forEach { xy(it.x * 1.05f, it.y * 1.05f) }
                                ref.reversed().forEach { xy(it.x, it.y) }
                            }

                            rotate(90f.deg, Vec3f.Y_AXIS)
                            val scales = listOf(0.5f, 0.7f, 0.8f, 0.93f, 1f, 0.95f, 0.8f, 0.7f, 0.6f, 0.35f, 0.2f, 0.1f, 0f)
                            scales.forEachIndexed { i, s ->
                                color = blossomLeafGrad.getColor(i.toFloat() / scales.lastIndex)
                                translate(0f, 0f, 0.3f)
                                rotate(100f.deg / scales.size + rand.randomF(-8f, 8f).deg, Vec3f.NEG_Y_AXIS)
                                rotate(rand.randomF(-8f, 8f).deg, Vec3f.X_AXIS)
                                withTransform {
                                    scale(s, s, 1f)
                                    sample()
                                }
                            }
                        }
                    }
                }
            }
        }

        private fun MeshBuilder.makeBlossomGeometry() {
            withTransform {
                transform.mul(shaftTopTransform)

                val nLeafs = 17
                for (l in 0..nLeafs) {
                    withTransform {
                        val ls = l.toFloat() / nLeafs * 0.8f + 0.2f
                        scale(ls, ls, 1.2f)

                        rotate(97f.deg * l, Vec3f.Z_AXIS)
                        profile {
                            val ref = mutableListOf<Vec2f>()
                            val jit = 0.03f
                            for (i in 0..10) {
                                val a = (i / 10f * 120 - 60).toRad()
                                ref += Vec2f(cos(a) - 0.9f + rand.randomF(-jit, jit), sin(a) + rand.randomF(-jit, jit))
                            }

                            simpleShape(true) {
                                ref.forEach { xy(it.x * 1.05f, it.y * 1.05f) }
                                ref.reversed().forEach { xy(it.x, it.y) }
                            }

                            rotate(60f.deg, Vec3f.Y_AXIS)
                            val scales = listOf(0.5f, 0.9f, 1f, 0.93f, 0.8f, 0.7f, 0.72f, 0.8f, 0.95f, 1.05f, 1f, 0.95f, 0.85f, 0.5f)
                            scales.forEachIndexed { i, s ->
                                val js = s * rand.randomF(0.95f, 1.05f)
                                color = Color(1f, 0.1f, 0.1f).toLinear()
                                translate(0f, 0f, 0.2f)
                                val r = (0.5f - (i.toFloat() / scales.size)) * 20f
                                rotate(r.deg + rand.randomF(-5f, 5f).deg, Vec3f.NEG_Y_AXIS)
                                rotate(rand.randomF(-5f, 5f).deg, Vec3f.X_AXIS)
                                withTransform {
                                    scale(js, js, 1f)
                                    translate((1f - js) * 0.7f, (1f - js) * 0.7f, 0f)
                                    sample()
                                }
                            }
                            fillTop()
                        }
                    }
                }
            }
        }
    }


}