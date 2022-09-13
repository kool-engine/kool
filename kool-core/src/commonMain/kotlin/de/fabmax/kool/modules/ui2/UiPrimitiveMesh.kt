package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.mvpMatrix
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.util.Color
import kotlin.math.*

class UiPrimitiveMesh : Mesh(IndexedVertexList(ATTRIB_OUTER_WEIGHTS)) {

    private val primitives = MeshInstanceList(listOf(ATTRIB_OUTER_DIMENS, Attribute.COLORS, Ui2Shader.ATTRIB_CLIP, ATTRIB_CENTER))

    init {
        instances = primitives
        isFrustumChecked = false

        generate {
            // x: rect-x
            // y: rect-y
            // z: radius-x
            // w: radius-y
            val outerWeights = MutableVec4f()
            vertexModFun = {
                getVec4fAttribute(ATTRIB_OUTER_WEIGHTS)!!.set(outerWeights)
            }

            vertex { outerWeights.set(-0.5f, -0.5f, 0f, -1f) }
            for (i in 0..7) {
                val a = PI.toFloat() / 2f * i / 7f
                vertex { outerWeights.set(0.5f, -0.5f, sin(a), -cos(a)) }
            }
            for (i in 0..7) {
                val a = PI.toFloat() / 2f * i / 7f
                vertex { outerWeights.set(0.5f, 0.5f, cos(a), sin(a)) }
            }
            for (i in 0..7) {
                val a = PI.toFloat() / 2f * i / 7f
                vertex { outerWeights.set(-0.5f, 0.5f, -sin(a), cos(a)) }
            }
            for (i in 0..7) {
                val a = PI.toFloat() / 2f * i / 7f
                vertex { outerWeights.set(-0.5f, -0.5f, -cos(a), -sin(a)) }
            }

            for (i in 2 until geometry.numVertices) {
                addTriIndices(0, i-1, i)
            }
        }
        shader = PrimitiveShader()
    }

    fun addRect(x: Float, y: Float, width: Float, height: Float, color: Color, clip: Vec4f) {
        addPrimitive(x, y, width, height, 0f, 0f, color, clip)
    }

    fun addRoundRect(x: Float, y: Float, width: Float, height: Float, radius: Float, color: Color, clip: Vec4f) {
        addPrimitive(x, y, width, height, radius, radius, color, clip)
    }

    fun addCircle(x: Float, y: Float, radius: Float, color: Color, clip: Vec4f) {
        addPrimitive(x, y, 0f, 0f, radius, radius, color, clip)
    }

    fun addOval(x: Float, y: Float, xRadius: Float, yRadius: Float, color: Color, clip: Vec4f) {
        addPrimitive(x, y, 0f, 0f, xRadius, yRadius, color, clip)
    }

    private fun addPrimitive(
        x: Float, y: Float, width: Float, height: Float,
        xRadius: Float, yRadius: Float,
        color: Color, clip: Vec4f
    ) {
        primitives.addInstance {
            put(max(width - xRadius * 2f, 0f))
            put(max(height - yRadius * 2f, 0f))
            put(min(xRadius, width * 0.5f))
            put(min(yRadius, height * 0.5f))

            put(color.r)
            put(color.g)
            put(color.b)
            put(color.a)

            put(clip.x)
            put(clip.y)
            put(clip.z)
            put(clip.w)

            put(x + width * 0.5f)
            put(y + height * 0.5f)
        }
    }

    companion object {
        val ATTRIB_CENTER = Attribute("aCenter", GlslType.VEC_2F)
        val ATTRIB_OUTER_DIMENS = Attribute("aOuterDimens", GlslType.VEC_4F)
        val ATTRIB_OUTER_WEIGHTS = Attribute("aOuterW", GlslType.VEC_4F)
    }

    private class PrimitiveShader : KslShader(Model(), pipelineConfig) {
        private class Model : KslProgram("UI2 Shader") {
            init {
                val screenPos = interStageFloat2()
                val color = interStageFloat4()
                val clipBounds = interStageFloat4(interpolation = KslInterStageInterpolation.Flat)

                vertexStage {
                    main {
                        color.input set instanceAttribFloat4(Attribute.COLORS.name)
                        color.input.rgb set color.input.rgb * color.input.a
                        clipBounds.input set instanceAttribFloat4(Ui2Shader.ATTRIB_CLIP.name)

                        val center = float4Var(instanceAttribFloat4(ATTRIB_CENTER.name))
                        val outerDimens = float4Var(instanceAttribFloat4(ATTRIB_OUTER_DIMENS.name))
                        val outerPosWeights = float4Var(vertexAttribFloat4(ATTRIB_OUTER_WEIGHTS.name))
                        val pos = float3Var()
                        pos.x set center.x + outerPosWeights.x * outerDimens.x + outerPosWeights.z * outerDimens.z
                        pos.y set center.y + outerPosWeights.y * outerDimens.y + outerPosWeights.w * outerDimens.w
                        pos.z set 0f.const
                        screenPos.input set pos.xy
                        outPosition set mvpMatrix().matrix * float4Value(pos, 1f.const)
                    }
                }
                fragmentStage {
                    main {
                        `if` (all(screenPos.output gt clipBounds.output.xy) and
                                all(screenPos.output lt clipBounds.output.zw)) {
                            colorOutput(color.output)
                        }.`else` {
                            discard()
                        }
                    }
                }
            }
        }

        companion object {
            val pipelineConfig = PipelineConfig().apply {
                blendMode = BlendMode.BLEND_PREMULTIPLIED_ALPHA
                cullMethod = CullMethod.NO_CULLING
                depthTest = DepthCompareOp.DISABLED
            }
        }
    }
}