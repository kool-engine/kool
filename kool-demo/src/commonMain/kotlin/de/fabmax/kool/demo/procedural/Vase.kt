package de.fabmax.kool.demo.procedural

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.math.rad
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.deferred.deferredKslPbrShader
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.geometry.simpleShape
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ColorGradient
import de.fabmax.kool.util.MdColor
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Vase : Mesh(IndexedVertexList(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS)) {

    init {
        generate {
            makeGeometry()
            geometry.removeDegeneratedTriangles()
            geometry.generateNormals()
        }
        shader = deferredKslPbrShader {
            color { vertexColor() }
            roughness(0.3f)
        }
    }

    private fun MeshBuilder.makeGeometry() {
        rotate(90f.deg, Vec3f.NEG_X_AXIS)
        translate(-7.5f, -2.5f, 0f)
        scale(1.8f, 1.8f, 1.8f)
        translate(0f, 0f, 0.15f)

        val gridGrad = ColorGradient(MdColor.BROWN, MdColor.BLUE tone 300)

        val tubeColors = Array(10) { i -> gridGrad.getColor(i / 9f).mix(Color.BLACK, 0.3f) }
        val tubeGrad = ColorGradient(*tubeColors)

        makeGrid(gridGrad)
        makeTube(tubeGrad)
    }

    private fun MeshBuilder.makeGrid(gridGrad: ColorGradient) {
        profile {
            simpleShape(true) {
                xy(0.8f, 1f); xy(-0.8f, 1f)
                xy(-1f, 0.8f); xy(-1f, -0.8f)
                xy(-0.8f, -1f); xy(0.8f, -1f)
                xy(1f, -0.8f); xy(1f, 0.8f)
            }

            val n = 50
            val cols = 24

            for (c in 0 until cols) {
                val rad = 2f * PI.toFloat() * c / cols
                for (i in 0..n) {
                    withTransform {
                        val p = i.toFloat() / n
                        val rot = p * 180f * if (c % 2 == 0) 1 else -1

                        color = gridGrad.getColor(p).toLinear()

                        rotate(rot.deg, Vec3f.Z_AXIS)
                        val r = 1f + (p - 0.5f) * (p - 0.5f) * 4
                        translate(cos(rad) * r, sin(rad) * r, 0f)

                        translate(0f, 0f, p * 10f)
                        rotate(rad.rad, Vec3f.Z_AXIS)
                        scale(0.05f, 0.05f, 1f)

                        sample(i != 0)
                    }
                }
            }
        }
    }

    private fun MeshBuilder.makeTube(tubeGrad: ColorGradient) {
        profile {
            circleShape(2.2f, 60)

            withTransform {
                for (i in 0..1) {
                    val invert = i == 1

                    withTransform {
                        color = tubeGrad.getColor(0f).toLinear()

                        scale(0.97f, 0.97f, 1f)
                        sample(connect = false, inverseOrientation = invert)
                        scale(1 / 0.97f, 1 / 0.97f, 1f)
                        scale(0.96f, 0.96f, 1f)
                        sample(inverseOrientation = invert)
                        scale(1 / 0.96f, 1 / 0.96f, 1f)
                        scale(0.95f, 0.95f, 1f)
                        //sample(inverseOrientation = invert)


                        for (j in 0..20) {
                            withTransform {
                                val p = j / 20f
                                val s = 1 - sin(p * PI.toFloat()) * 0.6f
                                val t = 5f - cos(p * PI.toFloat()) * 5

                                color = tubeGrad.getColor(p).toLinear()

                                translate(0f, 0f, t)
                                scale(s, s, 1f)
                                sample(inverseOrientation = invert)
                            }
                            if (j == 0) {
                                // actually fills bottom, but with inverted face orientation
                                fillTop()
                            }
                        }

                        translate(0f, 0f, 10f)
                        scale(1 / 0.95f, 1 / 0.95f, 1f)
                        scale(0.96f, 0.96f, 1f)
                        sample(inverseOrientation = invert)
                        scale(1 / 0.96f, 1 / 0.96f, 1f)
                        scale(0.97f, 0.97f, 1f)
                        sample(inverseOrientation = invert)
                    }
                    translate(0f, 0f, -0.15f)
                    scale(1f, 1f, 10.3f / 10f)
                }
            }

            for (i in 0..1) {
                color = tubeGrad.getColor(i.toFloat()).toLinear()
                withTransform {
                    translate(0f, 0f, -0.15f + 10.15f * i)
                    scale(0.97f, 0.97f, 1f)
                    sample(connect = false)
                    scale(1f / 0.97f, 1f / 0.97f, 1f)
                    sample()
                    translate(0f, 0f, 0.03f)
                    scale(1.02f, 1.02f, 1f)
                    sample()
                    translate(0f, 0f, 0.09f)
                    sample()
                    translate(0f, 0f, 0.03f)
                    scale(1f / 1.02f, 1f / 1.02f, 1f)
                    sample()
                    scale(0.97f, 0.97f, 1f)
                    sample()
                }
            }
        }
    }

}