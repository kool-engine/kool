package de.fabmax.kool.demo.procedural

import de.fabmax.kool.math.*
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.colorMesh
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ColorGradient
import de.fabmax.kool.util.MeshBuilder
import de.fabmax.kool.util.deferred.deferredPbrShader
import kotlin.math.cos
import kotlin.math.sin

class Roses : Group() {

    init {
        makeRose(6415168)
        makeRose(2541685)
        makeRose(25)
        makeRose(523947)
        makeRose(1234)
    }

    fun makeRose(seed: Int) {
        val rose = GeneratedRose(seed)
        +rose.shaftMesh
        +rose.leafMesh
        +rose.blossomMesh
    }

    private class GeneratedRose(seed: Int) {
        val shaftGrad = ColorGradient(0f to Color.MD_BROWN_900.toLinear(), 0.4f to Color.MD_BROWN.toLinear(), 1f to Color.MD_LIGHT_GREEN_600.toLinear())
        val blossomLeafGrad = ColorGradient(Color.MD_LIGHT_GREEN_600.toLinear(), Color.MD_LIGHT_GREEN.mix(Color.MD_YELLOW_200, 0.5f).toLinear())

        val shaftMesh: Mesh
        val leafMesh: Mesh
        val blossomMesh: Mesh

        val rand = Random(seed)
        val shaftTopTransform = Mat4f()

        init {
            shaftMesh = colorMesh {
                generate {
                    makeShaftGeometry()
                    geometry.removeDegeneratedTriangles()
                    geometry.generateNormals()
                }
                shader = deferredPbrShader {
                    roughness = 0.3f
                }
            }
            leafMesh = colorMesh {
                generate {
                    makeLeafGeometry()
                    geometry.removeDegeneratedTriangles()
                    geometry.generateNormals()
                }
                shader = deferredPbrShader {
                    roughness = 0.5f
                }
            }
            blossomMesh = colorMesh {
                generate {
                    makeBlossomGeometry()
                    geometry.removeDegeneratedTriangles()
                    geometry.generateNormals()
                }
                shader = deferredPbrShader {
                    roughness = 0.8f
                }
            }
        }

        private fun MeshBuilder.makeShaftGeometry(origin: Vec3f = Vec3f(-7.5f, -2.5f, 10f)) {
            withTransform {
                rotate(90f, Vec3f.NEG_X_AXIS)
                translate(origin)

                // shaft
                profile {
                    circleShape(0.2f, steps = 8)

                    val steps = 6
                    for (i in 0 until steps) {
                        val ax = MutableVec3f(rand.randomF(-1f, 1f), rand.randomF(-1f, 1f), 0f).norm()
                        rotate(rand.randomF(0f, 15f), ax)
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

        private fun MeshBuilder.makeLeafGeometry() {
            withTransform {
                transform.mul(shaftTopTransform)

                for (l in 0..5) {
                    withTransform {
                        scale(0.8f, 0.8f, 0.8f)
                        rotate(60f * l, Vec3f.Z_AXIS)
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

                            rotate(90f, Vec3f.Y_AXIS)
                            val scales = listOf(0.5f, 0.7f, 0.8f, 0.93f, 1f, 0.95f, 0.8f, 0.7f, 0.6f, 0.35f, 0.2f, 0.1f, 0f)
                            scales.forEachIndexed { i, s ->
                                color = blossomLeafGrad.getColor(i.toFloat() / scales.lastIndex)
                                translate(0f, 0f, 0.3f)
                                rotate(100f / scales.size + rand.randomF(-8f, 8f), Vec3f.NEG_Y_AXIS)
                                rotate(rand.randomF(-8f, 8f), Vec3f.X_AXIS)
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

                        rotate(97f * l, Vec3f.Z_AXIS)
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

                            rotate(60f, Vec3f.Y_AXIS)
                            val scales = listOf(0.5f, 0.9f, 1f, 0.93f, 0.8f, 0.7f, 0.72f, 0.8f, 0.95f, 1.05f, 1f, 0.95f, 0.85f, 0.5f)
                            scales.forEachIndexed { i, s ->
                                val js = s * rand.randomF(0.95f, 1.05f)
                                color = Color.RED.toLinear()
                                translate(0f, 0f, 0.2f)
                                val r = (0.5f - (i.toFloat() / scales.size)) * 20f
                                rotate(r + rand.randomF(-5f, 5f), Vec3f.NEG_Y_AXIS)
                                rotate(rand.randomF(-5f, 5f), Vec3f.X_AXIS)
                                withTransform {
                                    scale(js, js, 1f)
                                    translate((1f - js) * 0.7f, (1f - js) * 0.7f, 0f)
                                    sample()
                                }
                            }
                        }
                    }
                }
            }
        }
    }


}