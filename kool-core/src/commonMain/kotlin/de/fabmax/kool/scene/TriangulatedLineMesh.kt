package de.fabmax.kool.scene

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.spatial.BoundingBoxF
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ksl.blocks.cameraData
import de.fabmax.kool.modules.ksl.blocks.convertColorSpace
import de.fabmax.kool.modules.ksl.blocks.fragmentColorBlock
import de.fabmax.kool.modules.ksl.blocks.mvpMatrix
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.BlendMode
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.pipeline.asAttribute
import de.fabmax.kool.pipeline.backend.DepthRange
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.PrimitiveType
import de.fabmax.kool.util.*
import kotlin.math.max
import kotlin.math.min

fun Node.addTriangulatedLineMesh(
    name: String = makeChildName("TriangulatedLineMesh"),
    block: TriangulatedLineMesh.() -> Unit
): TriangulatedLineMesh {
    val mesh = TriangulatedLineMesh(name).apply(block)
    addNode(mesh)
    return mesh
}

fun TriangulatedLineMesh(name: String = Node.makeNodeName("TriangulatedLineMesh")): TriangulatedLineMesh {
    return TriangulatedLineMesh(IndexedVertexList(CustomTriangulatedLineMesh.LineVertexLayout), name)
}

typealias TriangulatedLineMesh = CustomTriangulatedLineMesh<CustomTriangulatedLineMesh.LineVertexLayout>

class CustomTriangulatedLineMesh<Layout: Struct>(
    geometry: IndexedVertexList<Layout>,
    name: String = makeNodeName("TriangulatedLineMesh")
) : Mesh<Layout>(geometry, name = name) {
    private val lineBuffer = mutableListOf<LineVertex>()

    private val lineAttr = requireNotNull(geometry.layout.getFloat2(LineVertexLayout.lineAttribs.name)) {
        "Mesh geometry misses required vertex attribute: $ATTRIB_LINE_ATTRIBS"
    }
    private val prevAttr = requireNotNull(geometry.layout.getFloat3(LineVertexLayout.prevDir.name)) {
        "Mesh geometry misses required vertex attribute: $ATTRIB_PREV_DIR"
    }
    private val nextAttr = requireNotNull(geometry.layout.getFloat3(LineVertexLayout.nextDir.name)) {
        "Mesh geometry misses required vertex attribute: $ATTRIB_NEXT_DIR"
    }

    var color = Color.RED
    var width = 1f

    init {
        isCastingShadow = false
        shader = Shader()
    }

    fun clear() {
        lineBuffer.clear()
        geometry.clear()
    }

    fun addLine(from: Vec3f, to: Vec3f, color: Color = this.color, width: Float = this.width): CustomTriangulatedLineMesh<Layout> {
        return addLine(from, color, width, to, color, width)
    }

    fun addLine(
        from: Vec3f,
        fromColor: Color,
        fromWidth: Float,
        to: Vec3f,
        toColor: Color,
        toWidth: Float
    ): CustomTriangulatedLineMesh<Layout> {
        moveTo(from, fromColor, fromWidth)
        lineTo(to, toColor, toWidth)
        return stroke()
    }

    fun addLine(vararg points: Vec3f): CustomTriangulatedLineMesh<Layout> {
        return addLine(color, width, *points)
    }

    fun addLine(color: Color, width: Float, vararg points: Vec3f): CustomTriangulatedLineMesh<Layout> {
        for (i in 0 until points.lastIndex) {
            addLine(points[i], color, width, points[i+1], color, width)
        }
        return this
    }

    fun addLineString(lineString: LineString<*>, color: Color = this.color, width: Float = this.width): CustomTriangulatedLineMesh<Layout> {
        for (i in 0 until lineString.lastIndex) {
            addLine(lineString[i], color, width, lineString[i+1], color, width)
        }
        return this
    }

    fun moveTo(x: Float, y: Float, z: Float) = moveTo(Vec3f(x, y, z))

    fun moveTo(
        position: Vec3f,
        color: Color = this.color,
        width: Float = this.width,
        vertexMod: (MutableStructBufferView<Layout>.(Layout) -> Unit)? = null
    ): CustomTriangulatedLineMesh<Layout> {
        if (lineBuffer.isNotEmpty()) {
            stroke()
        }
        lineBuffer.add(LineVertex(position, color, width, vertexMod))
        return this
    }

    fun lineTo(x: Float, y: Float, z: Float) = lineTo(Vec3f(x, y, z))

    fun lineTo(
        position: Vec3f,
        color: Color = this.color,
        width: Float = this.width,
        vertexMod: (MutableStructBufferView<Layout>.(Layout) -> Unit)? = null
    ): CustomTriangulatedLineMesh<Layout> {
        lineBuffer.add(LineVertex(position, color, width, vertexMod))
        return this
    }

    fun stroke(): CustomTriangulatedLineMesh<Layout> {
        if (lineBuffer.size > 1) {
            val startPos = MutableVec3f(lineBuffer.first()).mul(2f).subtract(lineBuffer[1])
            val endPos = MutableVec3f(lineBuffer.last()).mul(2f)
                .subtract(lineBuffer[lineBuffer.lastIndex - 1])
            for (i in 0 until lineBuffer.size) {
                val v = lineBuffer[i]
                val p = if (i == 0) startPos else lineBuffer[i - 1]
                val n = if (i == lineBuffer.lastIndex) endPos else lineBuffer[i + 1]
                val ia = geometry.addLineVertex(v, -1f, p, n)
                val ib = geometry.addLineVertex(v, 1f, p, n)

                if (i > 0) {
                    geometry.addTriIndices(ia, ia - 2, ia - 1)
                    geometry.addTriIndices(ia, ia - 1, ib)
                }
            }
        }
        lineBuffer.clear()
        return this
    }

    private fun IndexedVertexList<Layout>.addLineVertex(vertex: LineVertex, u: Float, prevDir: Vec3f, nextDir: Vec3f): Int {
        return addVertex {
            positionAttr?.set(vertex)
            colorAttr?.set(vertex.color)
            lineAttr.set(u, vertex.width)
            prevAttr.set(prevDir)
            nextAttr.set(nextDir)
            vertex.vertexMod?.invoke(this, it)
        }
    }

    fun addWireframe(triMesh: IndexedVertexList<*>, lineColor: Color? = null, width: Float = this.width) {
        if (triMesh.primitiveType != PrimitiveType.TRIANGLES) {
            throw IllegalArgumentException("Supplied mesh is not a triangle mesh: ${triMesh.primitiveType}")
        }

        val addedEdges = mutableSetOf<Long>()
        for (i in 0 until triMesh.numIndices step 3) {
            val i1 = triMesh.indices[i]
            val i2 = triMesh.indices[i + 1]
            val i3 = triMesh.indices[i + 2]

            val e1 = min(i1, i2).toLong() shl 32 or max(i1, i2).toLong()
            val e2 = min(i2, i3).toLong() shl 32 or max(i2, i3).toLong()
            val e3 = min(i3, i1).toLong() shl 32 or max(i3, i1).toLong()

            val v1 = triMesh[i1]
            val v2 = triMesh[i2]
            val v3 = triMesh[i3]

            if (e1 !in addedEdges) {
                addLine(v1, lineColor ?: v1.color, width, v2, lineColor ?: v2.color, width)
                addedEdges += e1
            }
            if (e2 !in addedEdges) {
                addLine(v2, lineColor ?: v2.color, width, v3, lineColor ?: v3.color, width)
                addedEdges += e2
            }
            if (e3 !in addedEdges) {
                addLine(v3, lineColor ?: v3.color, width, v1, lineColor ?: v1.color, width)
                addedEdges += e3
            }
        }
    }

    fun addNormals(geometry: IndexedVertexList<*>, lineColor: Color? = null, len: Float = 1f, width: Float = this.width) {
        val tmpN = MutableVec3f()
        geometry.forEach {
            tmpN.set(it.normal).norm().mul(len).add(it.position)
            val color = lineColor ?: it.color
            addLine(it.position, color,width, tmpN, color, width)
        }
    }

    fun addBoundingBox(aabb: BoundingBoxF, color: Color = this.color, width: Float = this.width) {
        val p0 = Vec3f(aabb.min.x, aabb.min.y, aabb.min.z)
        val p1 = Vec3f(aabb.min.x, aabb.min.y, aabb.max.z)
        val p2 = Vec3f(aabb.min.x, aabb.max.y, aabb.max.z)
        val p3 = Vec3f(aabb.min.x, aabb.max.y, aabb.min.z)
        val p4 = Vec3f(aabb.max.x, aabb.min.y, aabb.min.z)
        val p5 = Vec3f(aabb.max.x, aabb.min.y, aabb.max.z)
        val p6 = Vec3f(aabb.max.x, aabb.max.y, aabb.max.z)
        val p7 = Vec3f(aabb.max.x, aabb.max.y, aabb.min.z)

        addLine(p0, p1, color, width)
        addLine(p1, p2, color, width)
        addLine(p2, p3, color, width)
        addLine(p3, p0, color, width)

        addLine(p4, p5, color, width)
        addLine(p5, p6, color, width)
        addLine(p6, p7, color, width)
        addLine(p7, p4, color, width)

        addLine(p0, p4, color, width)
        addLine(p1, p5, color, width)
        addLine(p2, p6, color, width)
        addLine(p3, p7, color, width)
    }

    inner class LineVertex(position: Vec3f, val color: Color, val width: Float, val vertexMod: (MutableStructBufferView<Layout>.(Layout) -> Unit)?): Vec3f(position)

    open class Shader(cfg: Config = defaultCfg) : KslShader("Triangulated Line Shader") {

        constructor(block: Config.Builder.() -> Unit) : this(Config.Builder().apply(block).build())

        init {
            pipelineConfig = cfg.pipelineCfg
            program.makeProgram(cfg)
            cfg.modelCustomizer?.invoke(program)
        }

        private fun KslProgram.makeProgram(cfg: Config) {
            val clipPos = interStageFloat4()

            vertexStage {
                val cross2 = functionFloat1("cross2") {
                    val v1 = paramFloat2("v1")
                    val v2 = paramFloat2("v2")
                    body {
                        v1.x * v2.y - v1.y * v2.x
                    }
                }

                val rotate90 = functionFloat2("rotate90") {
                    val v = paramFloat2("v")
                    val d = paramFloat1("d")
                    body {
                        float2Value(v.y * d, -v.x * d)
                    }
                }

                main {
                    val mvp = mvpMatrix().matrix
                    val camData = cameraData()
                    val ar = camData.viewport.z / camData.viewport.w

                    val vPrevPos = vertexAttrib(LineVertexLayout.prevDir)
                    val vNextPos = vertexAttrib(LineVertexLayout.nextDir)
                    val vAttribs = vertexAttrib(LineVertexLayout.lineAttribs)
                    val pos = vertexAttribFloat3(Attribute.POSITIONS.name)
                    val shiftDir = vAttribs.x
                    val lineWidthPort = float1Port("lineWidth", vAttribs.y)

                    // project positions and compute 2d directions between prev, current and next points
                    val projPos = float4Var(mvp * float4Value(pos, 1f))
                    val projPrv = float4Var(mvp * float4Value(vPrevPos, 1f))
                    val projNxt = float4Var(mvp * float4Value(vNextPos, 1f))

                    val s = float2Var(projNxt.xy / projNxt.w - projPos.xy / projPos.w)
                    val r = float2Var(projPos.xy / projPos.w - projPrv.xy / projPrv.w)
                    s set normalize(s * float2Value(ar, 1f.const) * sign(projPos.w * projNxt.w))
                    r set normalize(r * float2Value(ar, 1f.const) * sign(projPos.w * projPrv.w))

                    // compute prev / next edge end points: rotate directions by 90°
                    val p = float2Var(rotate90(r, shiftDir))
                    val q = float2Var(rotate90(s, shiftDir))

                    // compute intersection points of prev and next edge
                    val x = float2Var((p + q) * 0.5f.const)
                    val rCrossS = float1Var(cross2(r, s))
                    `if`(abs(rCrossS) gt 0.001f.const) {
                        // lines are neither collinear nor parallel
                        val t = float1Var(clamp(cross2(q - p, s) / rCrossS, (-5f).const, 5f.const))
                        x set p + t * r
                    }

                    x.x *= 1f.const / ar
                    projPos.xy += (x * lineWidthPort / camData.viewport.w) * projPos.w
                    clipPos.input set projPos
                    outPosition set projPos
                }
            }
            fragmentStage {
                main {
                    val colorBlock = fragmentColorBlock(cfg.colorCfg)
                    val baseColor = float4Port("baseColor", colorBlock.outColor)
                    val outRgb = float3Var(baseColor.rgb)
                    outRgb set convertColorSpace(outRgb, cfg.colorSpaceConversion)
                    if (cfg.pipelineCfg.blendMode == BlendMode.BLEND_PREMULTIPLIED_ALPHA) {
                        outRgb set outRgb * baseColor.a
                    }
                    colorOutput(outRgb, baseColor.a)

                    if (cfg.depthFactor != 1f) {
                        val clipDepth = float1Var(clipPos.output.z / clipPos.output.w) * cfg.depthFactor.const

                        val isZeroToOne = KoolSystem.getContextOrNull()?.backend?.depthRange == DepthRange.ZERO_TO_ONE
                        if (isZeroToOne) {
                            outDepth set clipDepth
                        } else {
                            outDepth set (clipDepth + 1f.const) * 0.5f.const
                        }
                    }
                }
            }
        }

        companion object {
            private val defaultCfg = Config.Builder().apply {
                pipeline { cullMethod = CullMethod.NO_CULLING }
                color { vertexColor() }
            }.build()
        }

        class Config(builder: Builder) : KslUnlitShader.UnlitShaderConfig(builder) {
            val depthFactor = builder.depthFactor

            class Builder : KslUnlitShader.UnlitShaderConfig.Builder() {
                var depthFactor = 1f

                override fun build() = Config(this)
            }
        }
    }

    object LineVertexLayout : Struct("LineVertexLayout", MemoryLayout.TightlyPacked) {
        val color = float4(Attribute.COLORS.name)
        val lineAttribs = float2("aLineAttribs")
        val position = float3(Attribute.POSITIONS.name)
        val prevDir = float3("aPrevDir")
        val nextDir = float3("aNextDir")
    }

    companion object {
        val ATTRIB_LINE_ATTRIBS = LineVertexLayout.lineAttribs.asAttribute()
        val ATTRIB_PREV_DIR = LineVertexLayout.prevDir.asAttribute()
        val ATTRIB_NEXT_DIR = LineVertexLayout.nextDir.asAttribute()
    }
}