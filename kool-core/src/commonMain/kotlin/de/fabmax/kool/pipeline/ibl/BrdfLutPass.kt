package de.fabmax.kool.pipeline.ibl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.util.logD
import kotlin.math.PI


class BrdfLutPass(parentScene: Scene) :
        OffscreenRenderPass2d(Group(), renderPassConfig {
            name = "BrdfLutPass"
            setSize(512, 512)
            addColorTexture(TexFormat.RG_F16)
            clearDepthTexture()
        }) {

    var isAutoRemove = true

    init {
        clearColor = null
        (drawNode as Group).apply {
            +mesh(listOf(Attribute.POSITIONS, Attribute.TEXTURE_COORDS)) {
                generate {
                    rect {
                        origin.set(-1f, -1f, 0f)
                        size.set(2f, 2f)
                        mirrorTexCoordsY()
                    }
                }
                shader = brdfLutShader()
            }
        }

        // this pass only needs to be rendered once, remove it immediately after first render
        onAfterDraw += { ctx ->
            logD { "Generated BRDF look-up table" }
            if (isAutoRemove) {
                parentScene.removeOffscreenPass(this)
                ctx.runDelayed(1) { dispose(ctx) }
            } else {
                isEnabled = false
            }
        }
    }

    override fun dispose(ctx: KoolContext) {
        drawNode.dispose(ctx)
        super.dispose(ctx)
    }

    private fun brdfLutShader(): KslShader {
        val prog = KslProgram("BRDF LUT").apply {
            val uv = interStageFloat2("uv")

            vertexStage {
                main {
                    uv.input set vertexAttribFloat2(Attribute.TEXTURE_COORDS.name)
                    outPosition set float4Value(vertexAttribFloat3(Attribute.POSITIONS.name), 1f)
                }
            }

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

                val hammersley = functionFloat2("hammersley") {
                    val i = paramInt1("i")
                    val n = paramInt1("n")

                    body {
                        val bits = uint1Var(i.toUint1())
                        bits set ((bits shl 16u.const) or (bits shr 16u.const))
                        bits set (((bits and 0x55555555u.const) shl 1u.const) or ((bits and 0xaaaaaaaau.const) shr 1u.const))
                        bits set (((bits and 0x33333333u.const) shl 2u.const) or ((bits and 0xccccccccu.const) shr 2u.const))
                        bits set (((bits and 0x0f0f0f0fu.const) shl 4u.const) or ((bits and 0xf0f0f0f0u.const) shr 4u.const))
                        bits set (((bits and 0x00ff00ffu.const) shl 8u.const) or ((bits and 0xff00ff00u.const) shr 8u.const))
                        val radicalInverse = float1Var(bits.uToFloat1() * (1f / 0x100000000).const)
                        return@body float2Value(i.toFloat1() / n.toFloat1(), radicalInverse)
                    }
                }

                val importanceSampleGgx = functionFloat3("importanceSampleGgx") {
                    val xi = paramFloat2("xi")
                    val n = paramFloat3("n")
                    val roughness = paramFloat1("roughness")

                    body {
                        val a = float1Var(roughness * roughness)
                        val phi = float1Var(2f.const * PI.const * xi.x)
                        val cosTheta = float1Var(sqrt((1f.const - xi.y) / (1f.const + (a * a - 1f.const) * xi.y)))
                        val sinTheta = float1Var(sqrt(1f.const - cosTheta * cosTheta))

                        // from spherical coordinates to cartesian coordinates
                        val h = float3Var(
                            float3Value(
                                cos(phi) * sinTheta,
                                sin(phi) * sinTheta,
                                cosTheta
                            )
                        )

                        // from tangent-space vector to world-space sample vector
                        val up = float3Var(Vec3f.X_AXIS.const)
                        `if`(abs(n.z) lt 0.9999f.const) {
                            up set Vec3f.Z_AXIS.const
                        }
                        val tangent = float3Var(normalize(cross(up, n)))
                        val bitangent = cross(n, tangent)

                        // sample vector
                        return@body normalize(tangent * h.x + bitangent * h.y + n * h.z)
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

        return KslShader(prog, KslShader.PipelineConfig().apply {
            blendMode = BlendMode.DISABLED
            cullMethod = CullMethod.NO_CULLING
            depthTest = DepthCompareOp.DISABLED
            isWriteDepth = false
        })
    }
}