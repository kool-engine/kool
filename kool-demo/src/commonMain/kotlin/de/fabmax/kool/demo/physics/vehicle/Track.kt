package de.fabmax.kool.demo.physics.vehicle

import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.GlslType
import de.fabmax.kool.pipeline.Shader
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.shadermodel.PbrMaterialNode
import de.fabmax.kool.pipeline.shadermodel.StageInterfaceNode
import de.fabmax.kool.pipeline.shadermodel.fragmentStage
import de.fabmax.kool.pipeline.shadermodel.vertexStage
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.PbrMaterialConfig
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.util.*
import de.fabmax.kool.util.ibl.EnvironmentMaps
import de.fabmax.kool.util.spatial.KdTree
import de.fabmax.kool.util.spatial.NearestTraverser
import de.fabmax.kool.util.spatial.pointKdTree
import kotlin.math.*

class Track : Group() {

    private val spline = SimpleSpline3f()
    private val numSamples = mutableListOf<Int>()

    private val trackPoints = mutableListOf<Vec3f>()
    private var trackPointTree: KdTree<Vec3f>? = null
    private val nearestTrav = NearestTraverser<Vec3f>()

    var subdivs = 1
    var columnDist = 64.5f
    var curbLen = 2.511f
    val trackMesh = mesh(listOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.TEXTURE_COORDS, Attribute.TANGENTS)) {  }
    val trackSupportMesh = mesh(listOf(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, ATTRIBUTE_ROUGHNESS)) {  }

    init {
        +trackMesh
        +trackSupportMesh
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

    private fun MeshBuilder.applyTransform(prev: Vec3f, pt: Vec3f, next: Vec3f) {
        val z = MutableVec3f(next).subtract(pt).norm().add(MutableVec3f(pt).subtract(prev).norm()).norm()
        val x = MutableVec3f(z).apply { y = 0f }
        x.rotate(90f, Vec3f.Y_AXIS).norm()
        val y = z.cross(x, MutableVec3f()).norm()

        transform.setCol(0, x, 0f)
        transform.setCol(1, y, 0f)
        transform.setCol(2, z, 0f)
        transform.setCol(3, pt, 1f)
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

        val columnPts = mutableListOf<Vec4f>()
        var columnL = columnDist

        trackMesh.generate {
            var texU = 0f
            vertexModFun = {
                val texScale = 25f
                texCoord.x = texU
                texCoord.scale(1f / texScale)
            }

            color = Color.MD_ORANGE_100.toLinear()
            profile {
                multiShape {
                    simpleShape(false) {
                        xy(7.5f, 0f)
                        xy(-7.5f, 0f)
                        uv(0f, 7.5f)
                        uv(0f, -7.5f)
                    }
                    simpleShape(false) {
                        xy(-7.5f, 0f)
                        xy(-7.5f, -1f)
                        uv(0f, 0f)
                        uv(0f, -1f)
                    }
                    simpleShape(false) {
                        xy(-7.5f, -1f)
                        xy(7.5f, -1f)
                        uv(0f, -7.5f)
                        uv(0f, 7.5f)
                    }
                    simpleShape(false) {
                        xy(7.5f, -1f)
                        xy(7.5f, 0f)
                        uv(0f, -1f)
                        uv(0f, 0f)
                    }
                }

                withTransform {
                    for (i in 0 .. trackPoints.size) {
                        val prev = if (i == 0) trackPoints.last() else trackPoints[i-1]
                        val pt = trackPoints[i % trackPoints.size]
                        val next = trackPoints[(i + 1) % trackPoints.size]
                        applyTransform(prev, pt, next)

                        val adv = pt.distance(prev)
                        texU += adv
                        sample()

                        columnL -= adv
                        if (columnL < 0 && pt.y > 5f) {
                            val head = MutableVec3f(next).subtract(pt).norm()
                            val ang = atan2(head.x, head.z).toDeg()
                            columnPts += Vec4f(pt, ang)
                            columnL = columnDist
                        }
                    }
                }
                geometry.generateNormals()
                geometry.generateTangents()
            }
        }

        trackSupportMesh.generate {
            var roughness = 0.3f
            vertexModFun = {
                getFloatAttribute(ATTRIBUTE_ROUGHNESS)?.f = roughness
            }
            generateCurbs()

            roughness = 0.8f
            color = VehicleDemo.color(400f)
            columnPts.forEach { pt ->
                val base = MutableVec3f(pt.x, 0f, pt.z)
                generateColumn(base, pt.y - 0.7f, pt.w)
            }
            geometry.generateNormals()
        }
    }

    private fun MeshBuilder.generateCurbs() {
        val curbColors = listOf(VehicleDemo.color(150f), VehicleDemo.color(600f))
        var iCurb = 0

        color = curbColors[iCurb]
        withTransform {
            profile {
                multiShape {
                    simpleShape(false) {
                        xy(6.7f, -1.0f)
                        xy(6.75f, -1.05f)
                        xy(7.6f, -1.05f)
                        xy(7.65f, -1.0f)
                        xy(7.65f, 0.0f)
                        xy(7.6f, 0.05f)
                        xy(6.75f, 0.05f)
                        xy(6.7f, 0.0f)
                    }
                    simpleShape(false) {
                        xy(-6.7f, 0.0f)
                        xy(-6.75f, 0.05f)
                        xy(-7.6f, 0.05f)
                        xy(-7.65f, 0.0f)
                        xy(-7.65f, -1.0f)
                        xy(-7.6f, -1.05f)
                        xy(-6.75f, -1.05f)
                        xy(-6.7f, -1.0f)
                    }
                }

                var p = curbLen
                var prev = trackPoints.last()
                var i = 0
                while (i <= trackPoints.size) {
                    var pt = trackPoints[i % trackPoints.size]
                    var next = trackPoints[(i + 1) % trackPoints.size]

                    val adv = prev.distance(pt)
                    if (adv >= p) {
                        val samplePt = MutableVec3f(pt).subtract(prev).norm().scale(p).add(prev)
                        next = pt
                        pt = samplePt
                        applyTransform(prev, pt, next)
                        sample()

                        p = curbLen
                        iCurb = (iCurb + 1) % curbColors.size
                        color = curbColors[iCurb]
                        sample(connect = false)

                    } else {
                        p -= adv
                        i++
                        applyTransform(prev, pt, next)
                        sample()
                    }
                    prev = pt
                }
            }
        }
    }

    private fun MeshBuilder.generateColumn(center: Vec3f, height: Float, dir: Float) {
        withTransform {
            translate(center)
            rotate(dir, Vec3f.Y_AXIS)
            profile {
                multiShape {
                    simpleShape(true) {
                        for (i in 0 until 16) {
                            val a = i / 16f * 2f * PI.toFloat()
                            xz(sin(a) - 4, cos(a))
                        }
                    }
                    simpleShape(true) {
                        for (i in 0 until 16) {
                            val a = i / 16f * 2f * PI.toFloat()
                            xz(sin(a) + 4, cos(a))
                        }
                    }
                }
                for (i in 0..10) {
                    val s = abs((i - 5f) / 5f).pow(2) * 0.4f + 0.6f
                    scale(s, 1f, 1f)
                    sample()
                    scale(1f / s, 1f, 1f)
                    translate(0f, height / 10f, 0f)
                }
            }
        }
    }

    fun generate(block: Track.() -> Unit): Track {
        apply(block)
        build()
        return this
    }

    fun makeSupportMeshShader(shadows: List<ShadowMap>, ibl: EnvironmentMaps, aoMap: Texture2d): Shader {
        val cfg = PbrMaterialConfig().apply {
            albedoSource = Albedo.VERTEX_ALBEDO
            shadowMaps += shadows
            useImageBasedLighting(ibl)
            useScreenSpaceAmbientOcclusion(aoMap)
            roughness = 0.3f
        }
        val model = PbrShader.defaultPbrModel(cfg).apply {
            val ifRoughness: StageInterfaceNode
            vertexStage {
                ifRoughness = stageInterfaceNode("ifRoughness", attributeNode(ATTRIBUTE_ROUGHNESS).output)
            }
            fragmentStage {
                findNodeByType<PbrMaterialNode>()!!.inRoughness = ifRoughness.output
            }
        }
        return PbrShader(cfg, model)
    }

    companion object {
        val ATTRIBUTE_ROUGHNESS = Attribute("aRoughness", GlslType.FLOAT)
    }
}