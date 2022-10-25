package de.fabmax.kool.scene

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.BlendMode
import de.fabmax.kool.pipeline.GlslType
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.util.Color

class BetterLineMesh(name: String? = null) : Mesh(IndexedVertexList(lineMeshAttribs), name) {

    private val lineBuffer = mutableListOf<LineVertex>()

    var color = Color.RED
    var width = 1f

    init {
        shader = LineShader()
    }

    fun moveTo(x: Float, y: Float, z: Float) = moveTo(Vec3f(x, y, z))

    fun moveTo(position: Vec3f, color: Color = this.color, width: Float = this.width): BetterLineMesh {
        if (lineBuffer.isNotEmpty()) {
            stroke()
        }
        lineBuffer.add(LineVertex(Vec3f(position), color, width))
        return this
    }

    fun lineTo(x: Float, y: Float, z: Float) = lineTo(Vec3f(x, y, z))

    fun lineTo(position: Vec3f, color: Color = this.color, width: Float = this.width): BetterLineMesh {
        lineBuffer.add(LineVertex(Vec3f(position), color, width))
        return this
    }

    fun line(from: Vec3f, to: Vec3f, color: Color = this.color, width: Float = this.width): BetterLineMesh {
        moveTo(from, color, width)
        lineTo(to, color, width)
        return stroke()
    }

    fun stroke(): BetterLineMesh {
        if (lineBuffer.size > 1) {
            val prevDir = MutableVec3f(lineBuffer[1].position).subtract(lineBuffer[0].position)
            val nextDir = MutableVec3f()
            for (i in 0 until lineBuffer.size) {
                val v = lineBuffer[i]
                if (i < lineBuffer.lastIndex) {
                    nextDir.set(lineBuffer[i+1].position).subtract(v.position)
                } else {
                    nextDir.set(prevDir)
                }

                val ia = geometry.addLineVertex(v, -1f, prevDir, nextDir)
                val ib = geometry.addLineVertex(v, 1f, prevDir, nextDir)
                if (i > 0) {
                    geometry.addTriIndices(ia, ia - 2, ia - 1)
                    geometry.addTriIndices(ia, ia - 1, ib)
                }

                prevDir.set(nextDir)
            }
        }
        lineBuffer.clear()
        return this
    }

    private fun IndexedVertexList.addLineVertex(vertex: LineVertex, u: Float, prevDir: Vec3f, nextDir: Vec3f): Int {
        return addVertex {
            set(vertex.position)
            color.set(vertex.color)
            getVec2fAttribute(ATTRIB_LINE_ATTRIBS)?.set(u, vertex.width)
            getVec3fAttribute(ATTRIB_PREV_DIR)?.set(prevDir)
            getVec3fAttribute(ATTRIB_NEXT_DIR)?.set(nextDir)
        }
    }

    fun clear() {
        lineBuffer.clear()
        geometry.clear()
    }

    data class LineVertex(val position: Vec3f, val color: Color, val width: Float)

    class LineShader(cfg: LineShaderConfig = defaultCfg, model: LineModel = LineModel(cfg)) : KslUnlitShader(cfg, model) {

        constructor(block: LineShaderConfig.() -> Unit) : this(LineShaderConfig().apply(block))

        companion object {
            private val defaultCfg = LineShaderConfig().apply {
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

                    main {
                        val mvp = mvpMatrix().matrix
                        val camData = cameraData()
                        val ar = camData.viewport.z / camData.viewport.w

                        val vPrevDir = vertexAttribFloat3(ATTRIB_PREV_DIR.name)
                        val vNextDir = vertexAttribFloat3(ATTRIB_NEXT_DIR.name)
                        val vAttribs = vertexAttribFloat2(ATTRIB_LINE_ATTRIBS.name)
                        val pos = vertexAttribFloat3(Attribute.POSITIONS.name)
                        val shiftDir = vAttribs.x
                        val lineWidth = vAttribs.y / camData.viewport.w

                        val projPos = float4Var(mvp * float4Value(pos, 1f))
                        val projPrv = float4Var(mvp * float4Value(pos + normalize(vPrevDir) * 0.01f.const, 1f))
                        val projNxt = float4Var(mvp * float4Value(pos + normalize(vNextDir) * 0.01f.const, 1f))

                        // compute projected prev / next direction, scaled by aspect ratio
                        val s = float2Var(projNxt.xy / projNxt.w - projPos.xy / projPos.w)
                        val r = float2Var(projPrv.xy / projPrv.w - projPos.xy / projPos.w)
                        r.x *= ar
                        s.x *= ar
                        r set normalize(r)
                        s set normalize(s)

                        // compute prev / next edge end points: rotate directions by 90°
                        val p = float2Var(r)
                        val q = float2Var(s)
                        val swap = float1Var(p.x)
                        p.x set p.y * shiftDir
                        p.y set -swap * shiftDir
                        swap set q.x
                        q.x set q.y * shiftDir
                        q.y set -swap * shiftDir

                        // compute intersection points of prev and next edge
                        val x = float2Var((p + q) * 0.5f.const)
                        val rCrossS = float1Var(cross2(r, s))
                        `if`(rCrossS ne 0f.const) {
                            // lines are neither collinear nor parallel
                            val t = float1Var(clamp(cross2(q - p, s) / rCrossS, (-5f).const, 5f.const))
                            x set p + t * r
                        }

                        x.x *= 1f.const / ar
                        projPos.xy += (x * lineWidth) * projPos.w
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