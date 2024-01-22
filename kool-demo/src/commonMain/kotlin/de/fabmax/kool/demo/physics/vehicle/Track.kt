package de.fabmax.kool.demo.physics.vehicle

import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.KdTree
import de.fabmax.kool.math.spatial.NearestTraverser
import de.fabmax.kool.math.spatial.pointKdTree
import de.fabmax.kool.physics.RigidStatic
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.deferred.deferredKslPbrShader
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.scene.geometry.multiShape
import de.fabmax.kool.scene.geometry.simpleShape
import de.fabmax.kool.util.*
import kotlin.math.*
import kotlin.random.Random

class Track(val world: VehicleWorld) : Node() {

    private val spline = SimpleSpline3f()
    private val numSamples = mutableListOf<Int>()

    private val trackPoints = mutableListOf<TrackPoint>()
    private val trackPointsMap = TreeMap<Float, TrackPoint>()
    private var trackPointTree: KdTree<Vec3f>? = null
    private val nearestTrav = NearestTraverser<Vec3f>()

    private val guardRails = mutableListOf<GuardRailSection>()

    var subdivs = 1
    var columnDist = 64.5f
    var curbLen = 2.511f
    val trackMesh = Mesh(Attribute.POSITIONS, Attribute.NORMALS, Attribute.TEXTURE_COORDS, Attribute.TANGENTS)
    val trackSupportMesh = Mesh(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, ATTRIBUTE_ROUGHNESS)

    lateinit var trackActor: RigidStatic
        private set
    val guardRail = GuardRail()

    init {
        isFrustumChecked = false
        addNode(trackMesh)
        addNode(trackSupportMesh)
        addNode(guardRail.guardRailMesh)
    }

    fun addControlPoint(ctrlPt: SimpleSpline3f.CtrlPoint, numSamples: Int = 20) {
        if (spline.ctrlPoints.isNotEmpty()) {
            this.numSamples += numSamples
        }
        spline.ctrlPoints += ctrlPt
    }

    fun addGuardRailSection(from: Float, to: Float, isLeft: Boolean) {
        guardRails += GuardRailSection(from, to, isLeft)
    }

    fun distanceToTrack(point: Vec3f): Float {
        val tree = trackPointTree ?: return -1f
        nearestTrav.setup(point).traverse(tree)
        return sqrt(nearestTrav.sqrDist).toFloat()
    }

    fun cleanUp() {
        guardRail.cleanUp()
    }

    private fun computeFrameAt(pos: Float, result: MutableMat4f) {
        val prevKey = trackPointsMap.lowerKey(pos) ?: trackPointsMap.firstKey()
        val nextKey = trackPointsMap.higherKey(pos) ?: trackPointsMap.lastKey()
        val prev = trackPointsMap[prevKey]!!
        val next = trackPointsMap[nextKey]!!

        val wPrev = if (nextKey == prevKey) {
            1f
        } else {
            (nextKey - pos) / (nextKey - prevKey)
        }
        val wNext = 1f - wPrev

        val vec = MutableVec3f(prev).mul(wPrev).add(MutableVec3f(next).mul(wNext))
        val up = MutableVec3f(prev.up).mul(wPrev).add(MutableVec3f(next.up).mul(wNext))
        val z = MutableVec3f(prev.dir).mul(wPrev).add(MutableVec3f(next.dir).mul(wNext))
        val x = MutableVec3f(z).apply { y = 0f }
        x.rotate(90f.deg, up).norm()
        val y = z.cross(x, MutableVec3f()).norm()

        result.setColumn(0, x, 0f)
        result.setColumn(1, y, 0f)
        result.setColumn(2, z, 0f)
        result.setColumn(3, vec, 1f)
    }

    private fun sampleTrack() {
        trackPoints.clear()
        var pos = 0f
        for (i in 0 .. (spline.ctrlPoints.size - 2)) {
            val samples = numSamples[i] * subdivs
            for (j in 0 until samples) {
                val pt = spline.evaluate(i + j / samples.toFloat(), MutableVec3f())
                if (trackPoints.isNotEmpty()) {
                    pos += pt.distance(trackPoints.last())
                }
                val trackPt = TrackPoint(pt, pos, trackPoints.size)
                trackPoints += trackPt
                trackPointsMap[trackPt.pos] = trackPt
            }
        }
        for (i in trackPoints.indices) {
            val prev = if (i == 0) trackPoints.last() else trackPoints[i-1]
            val pt = trackPoints[i % trackPoints.size]
            val next = trackPoints[(i + 1) % trackPoints.size]
            next.subtract(prev, pt.dir).norm()
        }
        trackPointTree = pointKdTree(trackPoints)
    }

    private fun build() {
        sampleTrack()

        val columnPts = mutableListOf<Vec4f>()
        var columnL = columnDist

        trackMesh.generate {
            var texU = 0f
            vertexModFun = {
                val texScale = 25f
                texCoord.x = texU
                texCoord.mul(1f / texScale)
            }

            color = MdColor.ORANGE toneLin 100
            profile {
                multiShape {
                    simpleShape(false) {
                        xy(7.5f, 0f)
                        xy(2.5f, 0f)
                        xy(-2.5f, 0f)
                        xy(-7.5f, 0f)
                        uv(0f, 7.5f)
                        uv(0f, 2.5f)
                        uv(0f, -2.5f)
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
                        pt.computeFrame(transform)

                        val adv = pt.distance(prev)
                        texU += adv
                        sample()

                        columnL -= adv
                        if (columnL < 0 && pt.y > 5f) {
                            val ang = atan2(pt.dir.x, pt.dir.z).toDeg()
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
            color = VehicleDemo.color(400)
            columnPts.forEach { pt ->
                val base = MutableVec3f(pt.x, 0f, pt.z)
                generateColumn(base, pt.y - 0.7f, pt.w)
            }
            geometry.generateNormals()
        }

        val collisionMesh = IndexedVertexList(Attribute.POSITIONS)
        collisionMesh.addGeometry(trackMesh.geometry)
        collisionMesh.addGeometry(trackSupportMesh.geometry)
        trackActor = world.addStaticCollisionBody(collisionMesh)

        buildGuardRails()
    }

    private fun buildGuardRails() {
        guardRails.forEach { guardRail ->
            sampleGuardRailTrack(guardRail)
        }
    }

    private fun sampleGuardRailTrack(guardRailSec: GuardRailSection) {
        val steps = ((guardRailSec.to - guardRailSec.from) / 3f).roundToInt()
        val step = (guardRailSec.to - guardRailSec.from) / steps
        val frame = MutableMat4f()
        for (i in 0 .. steps) {
            computeFrameAt(guardRailSec.from + step * i, frame)
            val leftRightSign = if (guardRailSec.isLeft) { -1f } else { 1f }
            frame.translate(-7.65f * leftRightSign, 0f, 0f)
                .translate(0f, 1.5f, 0f)
                .rotate((90f * leftRightSign).deg, Vec3f.Y_AXIS)

            guardRail.signs += GuardRail.SignInstance(guardRail.signs.size, guardRailSec.isLeft, frame, this, world)
        }
    }

    private fun MeshBuilder.generateCurbs() {
        val curbColors = listOf(VehicleDemo.color(150), VehicleDemo.color(600))
        color = curbColors[0]

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

                var pos = 0f
                var iColor = 0
                while (pos < trackPoints.last().pos) {
                    iColor = (iColor + 1) % curbColors.size
                    color = curbColors[iColor]

                    computeFrameAt(pos, transform)
                    sample(connect = false)
                    pos += curbLen
                    computeFrameAt(pos, transform)
                    sample()
                }
                computeFrameAt(0f, transform)
                sample()
            }
        }
    }

    private fun MeshBuilder.generateColumn(center: Vec3f, height: Float, dir: Float) {
        withTransform {
            translate(center)
            rotate(dir.deg, Vec3f.Y_AXIS)
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

        makeTrackShader()
        makeSupportMeshShader()

        return this
    }

    private fun makeTrackShader() {
        val texProps = TextureProps(
            generateMipMaps = false,
            defaultSamplerSettings = SamplerSettings().nearest()
        )
        val rand = Random(1337)
        val gradient = ColorGradient(VehicleDemo.color(50, false), VehicleDemo.color(300, false))
        val sz = 128
        val colorData = Uint8Buffer(sz * sz * 4)
        val roughnessData = Uint8Buffer(sz * sz)

        var c = gradient.getColor(rand.randomF())
        var len = rand.randomI(2, 5)
        for (i in 0 until sz * sz) {
            if (--len == 0) {
                c = gradient.getColor(rand.randomF())
                len = rand.randomI(2, 5)
            }
            colorData[i * 4 + 0] = (c.r * 255f).toInt().toUByte()
            colorData[i * 4 + 1] = (c.g * 255f).toInt().toUByte()
            colorData[i * 4 + 2] = (c.b * 255f).toInt().toUByte()
            colorData[i * 4 + 3] = (c.a * 255f).toInt().toUByte()
            roughnessData[i] = ((1f - c.brightness + 0.2f).clamp(0f, 1f) * 255f).toInt().toUByte()
        }

        val albedoMap = Texture2d(texProps, "track-color") {
            TextureData2d(colorData, sz, sz, TexFormat.RGBA)
        }
        val roughnessMap = Texture2d(texProps, "track-roughness") {
            TextureData2d(roughnessData, sz, sz, TexFormat.R)
        }
        albedoMap.releaseWith(trackMesh)
        roughnessMap.releaseWith(trackMesh)

        trackMesh.shader = deferredKslPbrShader {
            color { textureColor(albedoMap) }
            roughness { textureProperty(roughnessMap) }
        }
    }

    private fun makeSupportMeshShader() {
        trackSupportMesh.shader = deferredKslPbrShader {
            color { vertexColor() }
            roughness { vertexProperty(ATTRIBUTE_ROUGHNESS) }
        }
    }

    private class GuardRailSection(val from: Float, val to: Float, val isLeft: Boolean)

    private class TrackPoint(pt: Vec3f, val pos: Float, val index: Int) : Vec3f(pt) {
        val dir = MutableVec3f()
        val up = MutableVec3f(0f, 1f, 0f)

        fun computeFrame(result: MutableMat4f) {
            val z = dir
            val x = MutableVec3f(z).apply { y = 0f }
            x.rotate(90f.deg, up).norm()
            val y = z.cross(x, MutableVec3f()).norm()

            result.setColumn(0, x, 0f)
            result.setColumn(1, y, 0f)
            result.setColumn(2, z, 0f)
            result.setColumn(3, this, 1f)
        }
    }

    companion object {
        val ATTRIBUTE_ROUGHNESS = Attribute("aRoughness", GpuType.FLOAT1)
    }
}