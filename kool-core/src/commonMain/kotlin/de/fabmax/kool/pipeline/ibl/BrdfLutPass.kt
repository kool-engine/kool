package de.fabmax.kool.pipeline.ibl

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.AttachmentConfig
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenShaderPipelineCfg
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.pipeline.OffscreenPass2d
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addMesh
import de.fabmax.kool.util.launchDelayed
import de.fabmax.kool.util.logT


class BrdfLutPass(parentScene: Scene) :
    OffscreenPass2d(
        drawNode = Node(),
        attachmentConfig = AttachmentConfig.singleColorNoDepth(TexFormat.RG_F16),
        initialSize = Vec2i(256),
        name = "brdf-lut"
    )
{

    var isAutoRemove = true

    init {
        drawNode.apply {
            addMesh(Attribute.POSITIONS, Attribute.TEXTURE_COORDS, name = "brdf-lut-mesh") {
                generateFullscreenQuad()
                shader = brdfLutShader()
            }
        }

        // this pass only needs to be rendered once, remove it immediately after first render
        onAfterPass {
            logT { "Generated BRDF look-up table" }
            if (isAutoRemove) {
                parentScene.removeOffscreenPass(this)
                launchDelayed(1) { release() }
            } else {
                isEnabled = false
            }
        }
    }

    private fun brdfLutShader(): KslShader {
        val prog = KslProgram("BRDF LUT").apply {
            val uv = interStageFloat2("uv")

            fullscreenQuadVertexStage(uv)

            fragmentStage {
                // Very similar to geometrySchlickGgx in PbrFunctions but uses slightly different parameters
                val geometrySchlickGgx = functionFloat1("geometrySchlickGgx") {
                    val nDotV = paramFloat1("nDotV")
                    val roughness = paramFloat1("roughness")

                    body {
                        val k = float1Var((roughness * roughness) / 2f.const)
                        val denom = nDotV * (1f.const - k) + k
                        return@body nDotV / denom
                    }
                }

                val geometrySmith = functionFloat1("geometrySmith") {
                    val n = paramFloat3("n")
                    val v = paramFloat3("v")
                    val l = paramFloat3("l")
                    val roughness = paramFloat1("roughness")

                    body {
                        val nDotV = float1Var(max(dot(n, v), 0f.const))
                        val nDotL = float1Var(max(dot(n, l), 0f.const))
                        val ggx1 = float1Var(geometrySchlickGgx(nDotL, roughness))
                        val ggx2 = float1Var(geometrySchlickGgx(nDotV, roughness))
                        return@body ggx1 * ggx2
                    }
                }

                val integrateBrdf = functionFloat2("integrateBrdf") {
                    val nDotV = paramFloat1("nDotV")
                    val roughness = paramFloat1("roughness")

                    body {
                        val v = float3Var(float3Value(sqrt(1f.const - nDotV * nDotV), 0f.const, nDotV), "v")
                        val a = float1Var(0f.const, "a")
                        val b = float1Var(0f.const, "b")
                        val n = float3Var(Vec3f.Z_AXIS.const, "n")

                        val sampleCount = 1024.const

                        fori(0.const, sampleCount) { i ->
                            val xi = float2Var(hammersley(i, sampleCount))
                            val h = float3Var(importanceSampleGgx(xi, n, roughness))
                            val l = float3Var(2f.const * dot(v, h) * h - v)

                            val nDotL = float1Var(max(l.z, 0f.const))
                            val nDotH = float1Var(max(h.z, 0f.const))
                            val vDotH = float1Var(max(dot(v, h), 0f.const))

                            `if`(nDotL gt 0f.const) {
                                val g = float1Var(geometrySmith(n, v, l, roughness))
                                val gVis = float1Var((g * vDotH) / (nDotH * nDotV))
                                val fc = float1Var(pow(1f.const - vDotH, 5f.const))

                                a += (1f.const - fc) * gVis
                                b += fc * gVis
                            }
                        }

                        return@body float2Value(a / sampleCount.toFloat1(), b / sampleCount.toFloat1())
                    }
                }

                main {
                    val integratedBrdf = float2Var(integrateBrdf(uv.output.x, uv.output.y))
                    colorOutput(float4Value(integratedBrdf.x, integratedBrdf.y, 0f.const, 1f.const))
                }
            }
        }

        return KslShader(prog, fullscreenShaderPipelineCfg)
    }
}