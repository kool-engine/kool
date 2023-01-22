package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
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

class UiPrimitiveMesh : Mesh(IndexedVertexList(ATTRIB_OUTER_WEIGHTS, ATTRIB_INNER_WEIGHTS)) {

    private val primitives = MeshInstanceList(listOf(
        ATTRIB_OUTER_DIMENS,
        ATTRIB_INNER_DIMENS,
        Ui2Shader.ATTRIB_CLIP,
        ATTRIB_COLOR_A,
        ATTRIB_COLOR_B,
        ATTRIB_GRADIENT_DIR,
        ATTRIB_CENTER)
    )

    init {
        instances = primitives
        isFrustumChecked = false
        isCastingShadow = false

        generate {
            val weights = MutableVec4f()
            val outerWeights = MutableVec4f()
            val innerWeights = MutableVec4f()
            vertexModFun = {
                getVec4fAttribute(ATTRIB_OUTER_WEIGHTS)!!.set(outerWeights)
                getVec4fAttribute(ATTRIB_INNER_WEIGHTS)!!.set(innerWeights)
            }

            fun addOuterInnerVerts(weights: Vec4f) {
                vertex {
                    outerWeights.set(weights)
                    innerWeights.set(Vec4f.ZERO)
                }
                vertex {
                    outerWeights.set(Vec4f.ZERO)
                    innerWeights.set(weights)
                }
            }

            addOuterInnerVerts(weights.set(-0.5f, -0.5f, 0f, -1f))
            for (i in 0..7) {
                val a = PI.toFloat() / 2f * i / 7f
                addOuterInnerVerts(weights.set(0.5f, -0.5f, sin(a), -cos(a)))
            }
            for (i in 0..7) {
                val a = PI.toFloat() / 2f * i / 7f
                addOuterInnerVerts(weights.set(0.5f, 0.5f, cos(a), sin(a)))
            }
            for (i in 0..7) {
                val a = PI.toFloat() / 2f * i / 7f
                addOuterInnerVerts(weights.set(-0.5f, 0.5f, -sin(a), cos(a)))
            }
            for (i in 0..7) {
                val a = PI.toFloat() / 2f * i / 7f
                addOuterInnerVerts(weights.set(-0.5f, -0.5f, -cos(a), -sin(a)))
            }

            for (i in 3 until geometry.numVertices step 2) {
                addTriIndices(i-3, i-2, i)
                addTriIndices(i-3, i, i-1)
            }
        }
        shader = PrimitiveShader()
    }

    fun rect(x: Float, y: Float, width: Float, height: Float, clip: Vec4f,
             colorA: Color, colorB: Color = colorA, gradientX: Float = 1f, gradientY: Float = 0f) {
        addPrimitive(
            x, y, width, height, 0f, 0f,
            0f, 0f, 0f, 0f, clip,
            colorA, colorB, gradientX, gradientY
        )
    }

    fun roundRect(x: Float, y: Float, width: Float, height: Float, radius: Float, clip: Vec4f,
                  colorA: Color, colorB: Color = colorA, gradientX: Float = 1f, gradientY: Float = 0f) {
        addPrimitive(
            x, y, width, height, radius, radius,
            0f, 0f, 0f, 0f, clip,
            colorA, colorB, gradientX, gradientY
        )
    }

    fun circle(x: Float, y: Float, radius: Float, clip: Vec4f,
               colorA: Color, colorB: Color = colorA, gradientX: Float = 1f, gradientY: Float = 0f) {
        addPrimitive(
            x - radius, y - radius, radius * 2f, radius * 2f, radius, radius,
            0f, 0f, 0f, 0f, clip,
            colorA, colorB, gradientX, gradientY
        )
    }

    fun oval(x: Float, y: Float, xRadius: Float, yRadius: Float, clip: Vec4f,
             colorA: Color, colorB: Color = colorA, gradientX: Float = 1f, gradientY: Float = 0f) {
        addPrimitive(
            x - xRadius, y - yRadius, xRadius * 2f, yRadius * 2f, xRadius, yRadius,
            0f, 0f, 0f, 0f, clip,
            colorA, colorB, gradientX, gradientY
        )
    }

    fun rectBorder(x: Float, y: Float, width: Float, height: Float,
                   borderWidth: Float, clip: Vec4f,
                   colorA: Color, colorB: Color = colorA, gradientX: Float = 1f, gradientY: Float = 0f) {
        addPrimitive(
            x, y,
            width, height, 0f, 0f,
            width - borderWidth * 2, height - borderWidth * 2, 0f, 0f,
            clip, colorA, colorB, gradientX, gradientY
        )
    }

    fun roundRectBorder(x: Float, y: Float, width: Float, height: Float, radius: Float,
                        borderWidth: Float, clip: Vec4f,
                        colorA: Color, colorB: Color = colorA, gradientX: Float = 1f, gradientY: Float = 0f) {
        addPrimitive(
            x, y,
            width, height, radius, radius,
            (width - borderWidth * 2f), (height - borderWidth * 2f), radius - borderWidth, radius - borderWidth,
            clip, colorA, colorB, gradientX, gradientY
        )
    }

    fun circleBorder(x: Float, y: Float, radius: Float,
                     borderWidth: Float, clip: Vec4f,
                     colorA: Color, colorB: Color = colorA, gradientX: Float = 1f, gradientY: Float = 0f) {
        addPrimitive(
            x - radius, y - radius,
            radius * 2f, radius * 2f, radius, radius,
            (radius - borderWidth) * 2f, (radius - borderWidth) * 2f, radius - borderWidth, radius - borderWidth,
            clip, colorA, colorB, gradientX, gradientY
        )
    }

    fun ovalBorder(x: Float, y: Float, xRadius: Float, yRadius: Float,
                   borderWidth: Float, clip: Vec4f,
                   colorA: Color, colorB: Color = colorA, gradientX: Float = 1f, gradientY: Float = 0f) {
        addPrimitive(
            x - xRadius, y - yRadius,
            xRadius * 2f, yRadius * 2f, xRadius, yRadius,
            (xRadius - borderWidth) * 2f, (yRadius - borderWidth) * 2f, xRadius - borderWidth, yRadius - borderWidth,
            clip, colorA, colorB, gradientX, gradientY
        )
    }

    private fun addPrimitive(
        x: Float, y: Float,
        outerW: Float, outerH: Float, outerRx: Float, outerRy: Float,
        innerW: Float, innerH: Float, innerRx: Float, innerRy: Float,
        clip: Vec4f,
        colorA: Color, colorB: Color, gradientX: Float, gradientY: Float
    ) {
        val vec4 = MutableVec4f()
        primitives.addInstance {
            put(vec4.set(
                max(outerW - outerRx * 2f, 0f),
                max(outerH - outerRy * 2f, 0f),
                min(outerRx, outerW * 0.5f),
                min(outerRy, outerH * 0.5f)
            ).array)

            put(vec4.set(
                max(innerW - innerRx * 2f, 0f),
                max(innerH - innerRy * 2f, 0f),
                min(innerRx, innerW * 0.5f),
                min(innerRy, innerH * 0.5f)
            ).array)

            put(vec4.set(clip).array)

            put(vec4.set(colorA).array)
            put(vec4.set(colorB).array)
            put(gradientX)
            put(gradientY)

            // center position
            put(x + outerW * 0.5f)
            put(y + outerH * 0.5f)
        }
    }

    companion object {
        val ATTRIB_CENTER = Attribute("aCenter", GlslType.VEC_2F)
        val ATTRIB_OUTER_DIMENS = Attribute("aOuterDimens", GlslType.VEC_4F)
        val ATTRIB_INNER_DIMENS = Attribute("aInnerDimens", GlslType.VEC_4F)
        val ATTRIB_OUTER_WEIGHTS = Attribute("aOuterW", GlslType.VEC_4F)
        val ATTRIB_INNER_WEIGHTS = Attribute("aInnerW", GlslType.VEC_4F)
        val ATTRIB_COLOR_A = Attribute("aColorA", GlslType.VEC_4F)
        val ATTRIB_COLOR_B = Attribute("aColorB", GlslType.VEC_4F)
        val ATTRIB_GRADIENT_DIR = Attribute("aGradientDir", GlslType.VEC_2F)
    }

    private class PrimitiveShader : KslShader(Model(), pipelineConfig) {
        private class Model : KslProgram("UI2 primitive shape shader") {
            init {
                val screenPos = interStageFloat2()
                val color = interStageFloat4()
                val clipBounds = interStageFloat4(interpolation = KslInterStageInterpolation.Flat)

                vertexStage {
                    main {
                        clipBounds.input set instanceAttribFloat4(Ui2Shader.ATTRIB_CLIP.name)

                        val center = float2Var(instanceAttribFloat2(ATTRIB_CENTER.name))
                        val outerDimens = float4Var(instanceAttribFloat4(ATTRIB_OUTER_DIMENS.name))
                        val innerDimens = float4Var(instanceAttribFloat4(ATTRIB_INNER_DIMENS.name))
                        val outerPosWeights = float4Var(vertexAttribFloat4(ATTRIB_OUTER_WEIGHTS.name))
                        val innerPosWeights = float4Var(vertexAttribFloat4(ATTRIB_INNER_WEIGHTS.name))
                        val pos = float3Var(Vec3f.ZERO.const)

                        pos.xy set center + outerPosWeights.xy * outerDimens.xy + outerPosWeights.zw * outerDimens.zw
                        pos.xy += innerPosWeights.xy * innerDimens.xy + innerPosWeights.zw * innerDimens.zw

                        val colorA = instanceAttribFloat4(ATTRIB_COLOR_A.name)
                        val colorB = instanceAttribFloat4(ATTRIB_COLOR_B.name)
                        val gradientDir = instanceAttribFloat2(ATTRIB_GRADIENT_DIR.name)

                        val widthHeight = float2Var(outerDimens.xy + outerDimens.zw)
                        val normPos = float2Var((pos.xy - center) / widthHeight * 2f.const)
                        val wB = clamp(dot(normPos, gradientDir) * 0.5f.const + 0.5f.const, 0f.const, 1f.const)
                        val wA = 1f.const - wB

                        color.input set colorA * wA + colorB * wB
                        color.input.rgb set color.input.rgb * color.input.a

                        screenPos.input set pos.xy
                        outPosition set mvpMatrix().matrix * float4Value(pos, 1f.const)
                    }
                }
                fragmentStage {
                    main {
                        `if` (any(screenPos.output lt clipBounds.output.xy) or
                                any(screenPos.output gt clipBounds.output.zw)) {
                            discard()
                        }.`else` {
                            colorOutput(color.output)
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