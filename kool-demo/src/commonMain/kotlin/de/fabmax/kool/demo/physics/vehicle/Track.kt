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
import de.fabmax.kool.util.spatial.KdTree
import de.fabmax.kool.util.spatial.NearestTraverser
import de.fabmax.kool.util.spatial.pointKdTree
import kotlin.math.sqrt

class Track : Group() {

    private val spline = SimpleSpline3f()
    private val numSamples = mutableListOf<Int>()

    private val trackPoints = mutableListOf<Vec3f>()
    private var trackPointTree: KdTree<Vec3f>? = null
    private val nearestTrav = NearestTraverser<Vec3f>()

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

    fun distanceToTrack(point: Vec3f): Float {
        val tree = trackPointTree ?: return -1f
        nearestTrav.setup(point).traverse(tree)
        return sqrt(nearestTrav.sqrDist)
    }

    private fun build() {
        trackPoints.clear()
        for (i in 0 .. (spline.ctrlPoints.size - 2)) {
            val samples = numSamples[i] * subdivs
            for (j in 0 until samples) {
                trackPoints += spline.evaluate(i + j / samples.toFloat(), MutableVec3f())
            }
        }
        trackPointTree = pointKdTree(trackPoints)

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
                    for (i in 0 .. trackPoints.size) {
                        val prev = if (i == 0) trackPoints.last() else trackPoints[i-1]
                        val pt = trackPoints[i % trackPoints.size]
                        val next = trackPoints[(i + 1) % trackPoints.size]

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