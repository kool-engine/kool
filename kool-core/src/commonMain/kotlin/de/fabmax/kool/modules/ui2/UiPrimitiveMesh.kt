package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.Vec2f
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
import de.fabmax.kool.util.MemoryLayout
import de.fabmax.kool.util.Struct
import de.fabmax.kool.util.set
import kotlin.math.*

class UiPrimitiveMesh(name: String) :
    Mesh<UiPrimitiveMesh.UiPrimVertexLayout>(
        geometry = IndexedVertexList(UiPrimVertexLayout),
        instances = MeshInstanceList(InstanceLayout, 1024),
        name = name
    )
{
    @Suppress("UNCHECKED_CAST")
    private val primitives: MeshInstanceList<InstanceLayout> get() = instances as MeshInstanceList<InstanceLayout>

    init {
        isFrustumChecked = false
        isCastingShadow = false

        generate(updateBounds = false) {
            fun addOuterInnerVerts(weights: Vec4f) {
                vertex {
                    it.outerWeights.set(weights)
                    it.innerWeights.set(Vec4f.ZERO)
                }
                vertex {
                    it.outerWeights.set(Vec4f.ZERO)
                    it.innerWeights.set(weights)
                }
            }

            addOuterInnerVerts(Vec4f(-0.5f, -0.5f, 0f, -1f))
            for (i in 0..7) {
                val a = PI.toFloat() / 2f * i / 7f
                addOuterInnerVerts(Vec4f(0.5f, -0.5f, sin(a), -cos(a)))
            }
            for (i in 0..7) {
                val a = PI.toFloat() / 2f * i / 7f
                addOuterInnerVerts(Vec4f(0.5f, 0.5f, cos(a), sin(a)))
            }
            for (i in 0..7) {
                val a = PI.toFloat() / 2f * i / 7f
                addOuterInnerVerts(Vec4f(-0.5f, 0.5f, -sin(a), cos(a)))
            }
            for (i in 0..7) {
                val a = PI.toFloat() / 2f * i / 7f
                addOuterInnerVerts(Vec4f(-0.5f, -0.5f, -cos(a), -sin(a)))
            }

            for (i in 3 until geometry.numVertices step 2) {
                addTriIndices(i-3, i-2, i)
                addTriIndices(i-3, i, i-1)
            }
        }
        shader = PrimitiveShader()
    }

    fun rect(
        x: Float, y: Float, width: Float, height: Float, clip: Vec4f,
        colorA: Color, colorB: Color = colorA,
        gradientCx: Float = x, gradientCy: Float = x, gradientRx: Float = 1f, gradientRy: Float = 0f
    ) {
        addPrimitive(
            x, y, width, height, 0f, 0f,
            0f, 0f, 0f, 0f, clip,
            colorA, colorB, gradientCx, gradientCy, gradientRx, gradientRy
        )
    }

    fun roundRect(
        x: Float, y: Float, width: Float, height: Float, radius: Float, clip: Vec4f,
        colorA: Color, colorB: Color = colorA,
        gradientCx: Float = x, gradientCy: Float = x, gradientRx: Float = 1f, gradientRy: Float = 0f
    ) {
        addPrimitive(
            x, y, width, height, radius, radius,
            0f, 0f, 0f, 0f, clip,
            colorA, colorB, gradientCx, gradientCy, gradientRx, gradientRy
        )
    }

    fun circle(
        x: Float, y: Float, radius: Float, clip: Vec4f,
        colorA: Color, colorB: Color = colorA,
        gradientCx: Float = x, gradientCy: Float = x, gradientRx: Float = 1f, gradientRy: Float = 0f
    ) {
        addPrimitive(
            x - radius, y - radius, radius * 2f, radius * 2f, radius, radius,
            0f, 0f, 0f, 0f, clip,
            colorA, colorB, gradientCx, gradientCy, gradientRx, gradientRy
        )
    }

    fun oval(
        x: Float, y: Float, xRadius: Float, yRadius: Float, clip: Vec4f,
        colorA: Color, colorB: Color = colorA,
        gradientCx: Float = x, gradientCy: Float = x, gradientRx: Float = 1f, gradientRy: Float = 0f
    ) {
        addPrimitive(
            x - xRadius, y - yRadius, xRadius * 2f, yRadius * 2f, xRadius, yRadius,
            0f, 0f, 0f, 0f, clip,
            colorA, colorB, gradientCx, gradientCy, gradientRx, gradientRy
        )
    }

    fun rectBorder(
        x: Float, y: Float, width: Float, height: Float,
        borderWidth: Float, clip: Vec4f,
        colorA: Color, colorB: Color = colorA,
        gradientCx: Float = x, gradientCy: Float = x, gradientRx: Float = 1f, gradientRy: Float = 0f
    ) {
        addPrimitive(
            x, y,
            width, height, 0f, 0f,
            width - borderWidth * 2, height - borderWidth * 2, 0f, 0f,
            clip, colorA, colorB, gradientCx, gradientCy, gradientRx, gradientRy
        )
    }

    fun roundRectBorder(
        x: Float, y: Float, width: Float, height: Float, radius: Float,
        borderWidth: Float, clip: Vec4f,
        colorA: Color, colorB: Color = colorA,
        gradientCx: Float = x, gradientCy: Float = x, gradientRx: Float = 1f, gradientRy: Float = 0f
    ) {
        addPrimitive(
            x, y,
            width, height, radius, radius,
            (width - borderWidth * 2f), (height - borderWidth * 2f), radius - borderWidth, radius - borderWidth,
            clip, colorA, colorB, gradientCx, gradientCy, gradientRx, gradientRy
        )
    }

    fun circleBorder(
        x: Float, y: Float, radius: Float,
        borderWidth: Float, clip: Vec4f,
        colorA: Color, colorB: Color = colorA,
        gradientCx: Float = x, gradientCy: Float = x, gradientRx: Float = 1f, gradientRy: Float = 0f
    ) {
        addPrimitive(
            x - radius, y - radius,
            radius * 2f, radius * 2f, radius, radius,
            (radius - borderWidth) * 2f, (radius - borderWidth) * 2f, radius - borderWidth, radius - borderWidth,
            clip, colorA, colorB, gradientCx, gradientCy, gradientRx, gradientRy
        )
    }

    fun ovalBorder(
        x: Float, y: Float, xRadius: Float, yRadius: Float,
        borderWidth: Float, clip: Vec4f,
        colorA: Color, colorB: Color = colorA,
        gradientCx: Float = x, gradientCy: Float = x, gradientRx: Float = 1f, gradientRy: Float = 0f
    ) {
        addPrimitive(
            x - xRadius, y - yRadius,
            xRadius * 2f, yRadius * 2f, xRadius, yRadius,
            (xRadius - borderWidth) * 2f, (yRadius - borderWidth) * 2f, xRadius - borderWidth, yRadius - borderWidth,
            clip, colorA, colorB, gradientCx, gradientCy, gradientRx, gradientRy
        )
    }

    private fun addPrimitive(
        x: Float, y: Float,
        outerW: Float, outerH: Float, outerRx: Float, outerRy: Float,
        innerW: Float, innerH: Float, innerRx: Float, innerRy: Float,
        clip: Vec4f,
        colorA: Color, colorB: Color, gradientCx: Float, gradientCy: Float, gradientRx: Float, gradientRy: Float
    ) {
        primitives.addInstance {
            set(it.clip, clip)
            set(it.outerDimens,
                max(outerW - outerRx * 2f, 0f),
                max(outerH - outerRy * 2f, 0f),
                min(outerRx, outerW * 0.5f),
                min(outerRy, outerH * 0.5f),
            )
            set(it.innerDimens,
                max(innerW - innerRx * 2f, 0f),
                max(innerH - innerRy * 2f, 0f),
                min(innerRx, innerW * 0.5f),
                min(innerRy, innerH * 0.5f),
            )
            set(it.colorA, colorA)
            set(it.colorB, colorB)
            set(it.gradientCfg, gradientCx, gradientCy, gradientRx, gradientRy)
            set(it.center, x + outerW * 0.5f, y + outerH * 0.5f)
        }
    }

    object InstanceLayout : Struct("InstanceAttribs", MemoryLayout.TightlyPacked) {
        val clip = float4("aClip")
        val center = float2("aCenter")
        val outerDimens = float4("aOuterDimens")
        val innerDimens = float4("aInnerDimens")
        val colorA = float4("aColorA")
        val colorB = float4("aColorB")
        val gradientCfg = float4("aGradientCfg")

    }

    object UiPrimVertexLayout : Struct("UiPrimitiveAttribs", MemoryLayout.TightlyPacked) {
        val outerWeights = float4("aOuterW")
        val innerWeights = float4("aInnerW")
    }

    companion object {
        private val attrOuterWeights = UiPrimVertexLayout.outerWeights.asAttribute()
        private val attrInnerWeights = UiPrimVertexLayout.innerWeights.asAttribute()
    }

    private class PrimitiveShader : KslShader(Model(), pipelineConfig) {
        private class Model : KslProgram("UI2 primitive shape shader") {
            init {
                val screenPos = interStageFloat2()
                val clipBounds = interStageFloat4(interpolation = KslInterStageInterpolation.Flat)
                val gradientCfg = interStageFloat4(interpolation = KslInterStageInterpolation.Flat)
                val colorA = interStageFloat4()
                val colorB = interStageFloat4()

                vertexStage {
                    main {
                        clipBounds.input set instanceAttribFloat4(Ui2Shader.ATTRIB_CLIP.name)

                        val center = float2Var(instanceAttrib(InstanceLayout.center))
                        val outerDimens = float4Var(instanceAttrib(InstanceLayout.outerDimens))
                        val innerDimens = float4Var(instanceAttrib(InstanceLayout.innerDimens))
                        val outerPosWeights = float4Var(vertexAttrib(UiPrimVertexLayout.outerWeights))
                        val innerPosWeights = float4Var(vertexAttrib(UiPrimVertexLayout.innerWeights))
                        val pos = float3Var(Vec3f.ZERO.const)

                        pos.xy set center + outerPosWeights.xy * outerDimens.xy + outerPosWeights.zw * outerDimens.zw
                        pos.xy += innerPosWeights.xy * innerDimens.xy + innerPosWeights.zw * innerDimens.zw

                        colorA.input set instanceAttrib(InstanceLayout.colorA)
                        colorB.input set instanceAttrib(InstanceLayout.colorB)
                        gradientCfg.input set instanceAttrib(InstanceLayout.gradientCfg)

                        screenPos.input set pos.xy
                        outPosition set mvpMatrix().matrix * float4Value(pos, 1f.const)
                    }
                }
                fragmentStage {
                    main {
                        `if`(any(screenPos.output lt clipBounds.output.xy) or
                                any(screenPos.output gt clipBounds.output.zw)) {
                            discard()
                        }.`else` {
                            `if`(any(gradientCfg.output.zw ne Vec2f.ZERO.const)) {
                                val gradPos = float2Var(screenPos.output - gradientCfg.output.xy)
                                `if`(gradientCfg.output.z ne 0f.const) {
                                    gradPos.x /= gradientCfg.output.z
                                }.`else` {
                                    gradPos.x set 0f.const
                                }
                                `if`(gradientCfg.output.w ne 0f.const) {
                                    gradPos.y /= gradientCfg.output.w
                                }.`else` {
                                    gradPos.y set 0f.const
                                }

                                val gradW = float1Var(smoothStep(0f.const, 1f.const, length(gradPos)))
                                val gradColor = float4Var(mix(colorA.output, colorB.output, gradW))
                                colorOutput(gradColor.rgb * gradColor.a, gradColor.a)

                            }.`else` {
                                colorOutput(colorA.output.rgb * colorA.output.a, colorA.output.a)
                            }
                        }
                    }
                }
            }
        }

        companion object {
            val pipelineConfig = PipelineConfig(
                blendMode = BlendMode.BLEND_PREMULTIPLIED_ALPHA,
                cullMethod = CullMethod.NO_CULLING,
                depthTest = DepthCompareOp.ALWAYS
            )
        }
    }
}