package de.fabmax.kool.scene

import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.BlendMode
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.pipeline.GlslType
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.VertexView
import de.fabmax.kool.util.Color

fun BetterLineMesh(block: BetterLineMesh.() -> Unit): BetterLineMesh {
    return BetterLineMesh().apply(block)
}

class BetterLineMesh(geometry: IndexedVertexList, name: String? = null) : Mesh(geometry, name) {

    constructor(name: String? = null) : this(IndexedVertexList(lineMeshAttribs), name)

    private val lineBuffer = mutableListOf<LineVertex>()

    private val lineAttribAccessor: MutableVec2f
    private val prevDirAccessor: MutableVec3f
    private val nextDirAccessor: MutableVec3f

    var color = Color.RED
    var width = 1f

    init {
        isCastingShadow = false

        lineAttribAccessor = geometry.vertexIt.getVec2fAttribute(ATTRIB_LINE_ATTRIBS)
            ?: throw IllegalStateException("Mesh geometry misses required vertex attribute: $ATTRIB_LINE_ATTRIBS")
        prevDirAccessor = geometry.vertexIt.getVec3fAttribute(ATTRIB_PREV_DIR)
            ?: throw IllegalStateException("Mesh geometry misses required vertex attribute: $ATTRIB_PREV_DIR")
        nextDirAccessor = geometry.vertexIt.getVec3fAttribute(ATTRIB_NEXT_DIR)
            ?: throw IllegalStateException("Mesh geometry misses required vertex attribute: $ATTRIB_NEXT_DIR")

        shader = LineShader()
    }

    fun moveTo(x: Float, y: Float, z: Float) = moveTo(Vec3f(x, y, z))

    fun moveTo(
        position: Vec3f,
        color: Color = this.color,
        width: Float = this.width,
        vertexMod: (VertexView.() -> Unit)? = null
    ): BetterLineMesh {
        if (lineBuffer.isNotEmpty()) {
            stroke()
        }
        lineBuffer.add(LineVertex(Vec3f(position), color, width, vertexMod))
        return this
    }

    fun lineTo(x: Float, y: Float, z: Float) = lineTo(Vec3f(x, y, z))

    fun lineTo(
        position: Vec3f,
        color: Color = this.color,
        width: Float = this.width,
        vertexMod: (VertexView.() -> Unit)? = null
    ): BetterLineMesh {
        lineBuffer.add(LineVertex(Vec3f(position), color, width, vertexMod))
        return this
    }

    fun line(from: Vec3f, to: Vec3f, color: Color = this.color, width: Float = this.width): BetterLineMesh {
        moveTo(from, color, width)
        lineTo(to, color, width)
        return stroke()
    }

    fun stroke(): BetterLineMesh {
        if (lineBuffer.size > 1) {
            val startPos = MutableVec3f(lineBuffer.first().position).scale(2f).subtract(lineBuffer[1].position)
            val endPos = MutableVec3f(lineBuffer.last().position).scale(2f).subtract(lineBuffer[lineBuffer.lastIndex-1].position)
            for (i in 0 until lineBuffer.size) {
                val v = lineBuffer[i]
                val p = if (i == 0) startPos else lineBuffer[i-1].position
                val n = if (i == lineBuffer.lastIndex) endPos else lineBuffer[i+1].position
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

    private fun IndexedVertexList.addLineVertex(vertex: LineVertex, u: Float, prevDir: Vec3f, nextDir: Vec3f): Int {
        return addVertex {
            set(vertex.position)
            color.set(vertex.color)
            lineAttribAccessor.set(u, vertex.width)
            prevDirAccessor.set(prevDir)
            nextDirAccessor.set(nextDir)
            vertex.vertexMod?.invoke(this)
        }
    }

    fun clear() {
        lineBuffer.clear()
        geometry.clear()
    }

    class LineVertex(val position: Vec3f, val color: Color, val width: Float, val vertexMod: (VertexView.() -> Unit)?)

    open class LineShader(cfg: LineShaderConfig = defaultCfg, model: LineModel = LineModel(cfg)) : KslUnlitShader(cfg, model) {

        constructor(block: LineShaderConfig.() -> Unit) : this(LineShaderConfig().apply(block))

        companion object {
            private val defaultCfg = LineShaderConfig().apply {
                pipeline { cullMethod = CullMethod.NO_CULLING }
                color { vertexColor() }
                colorSpaceConversion = ColorSpaceConversion.AS_IS
            }
        }

        class LineShaderConfig : UnlitShaderConfig() {
            var depthFactor = 1f
        }

        class LineModel(cfg: LineShaderConfig) : KslProgram("Line Shader") {
            init {
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

                        val vPrevPos = vertexAttribFloat3(ATTRIB_PREV_DIR.name)
                        val vNextPos = vertexAttribFloat3(ATTRIB_NEXT_DIR.name)
                        val vAttribs = vertexAttribFloat2(ATTRIB_LINE_ATTRIBS.name)
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
                        projPos.xy += (x * lineWidthPort.output / camData.viewport.w) * projPos.w
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
                            val near = 0f.const
                            val far = 1f.const
                            outDepth set (((far - near) * clipDepth) + near + far) / 2f.const
                        }
                    }
                }
                cfg.modelCustomizer?.invoke(this)
            }
        }
    }

    companion object {
        val ATTRIB_LINE_ATTRIBS = Attribute("aLineAttribs", GlslType.VEC_2F)
        val ATTRIB_PREV_DIR = Attribute("aPrevDir", GlslType.VEC_3F)
        val ATTRIB_NEXT_DIR = Attribute("aNextDir", GlslType.VEC_3F)

        val lineMeshAttribs = listOf(Attribute.COLORS, ATTRIB_LINE_ATTRIBS, Attribute.POSITIONS, ATTRIB_PREV_DIR, ATTRIB_NEXT_DIR)
    }
}