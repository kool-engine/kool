package de.fabmax.kool.demo.physics.vehicle

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.SimpleSpline3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.multiShape
import de.fabmax.kool.util.simpleShape

class Track : Group() {

    private val spline = SimpleSpline3f()
    private val numSamples = mutableListOf<Int>()

    var subdivs = 1
    val trackMesh = mesh(listOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.TEXTURE_COORDS, Attribute.TANGENTS)) {  }

    init {
        +trackMesh
    }

    fun addControlPoint(ctrlPt: SimpleSpline3f.CtrlPoint, numSamples: Int = 20) {
        if (spline.ctrlPoints.isNotEmpty()) {
            this.numSamples += numSamples
        }
        spline.ctrlPoints += ctrlPt
    }

    private fun build() {
        val points = mutableListOf<Vec3f>()
        for (i in 0 .. (spline.ctrlPoints.size - 2)) {
            val samples = numSamples[i] * subdivs
            for (j in 0 until samples) {
                points += spline.evaluate(i + j / samples.toFloat(), MutableVec3f())
            }
        }

        trackMesh.generate {
            vertexModFun = {
                val texScaleX = 25f
                val texScaleY = 25f
                texCoord.set(position.x / texScaleX, position.z / texScaleY)
            }

            color = Color.MD_ORANGE_100.toLinear()
            profile {
                multiShape {
                    simpleShape(false) {
                        xy(7.5f, 0f)
                        xy(-7.5f, 0f)
                    }
                    simpleShape(false) {
                        xy(-7.5f, 0f)
                        xy(-7.5f, -1f)
                    }
                    simpleShape(false) {
                        xy(-7.5f, -1f)
                        xy(7.5f, -1f)
                    }
                    simpleShape(false) {
                        xy(7.5f, -1f)
                        xy(7.5f, 0f)
                    }
                }

                withTransform {
                    for (i in 0 .. points.size) {
                        val prev = if (i == 0) points.last() else points[i-1]
                        val pt = points[i % points.size]
                        val next = points[(i + 1) % points.size]

                        val z = MutableVec3f(next).subtract(pt).norm().add(MutableVec3f(pt).subtract(prev).norm()).norm()
                        val x = MutableVec3f(z).apply { y = 0f }
                        x.rotate(90f, Vec3f.Y_AXIS).norm()
                        val y = z.cross(x, MutableVec3f())

                        transform.setCol(0, x, 0f)
                        transform.setCol(1, y, 0f)
                        transform.setCol(2, z, 0f)
                        transform.setCol(3, pt, 1f)

                        sample()
                    }
                }
                geometry.generateNormals()
                geometry.generateTangents()
            }
        }
    }

    fun generate(block: Track.() -> Unit): Track {
        block()
        build()
        return this
    }
}