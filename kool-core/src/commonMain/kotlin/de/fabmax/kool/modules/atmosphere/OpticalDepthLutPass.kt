package de.fabmax.kool.modules.atmosphere

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.addMesh
import de.fabmax.kool.util.logI

class OpticalDepthLutPass :
    OffscreenPass2d(
        drawNode = Node(),
        attachmentConfig = AttachmentConfig.singleColorNoDepth(TexFormat.RG_F16),
        initialSize = Vec2i(LUT_SIZE),
        name = "optical-depth-lut"
    )
{

    private val lutShader = OpticalDepthLutShader()
    var atmosphereRadius by lutShader::atmosphereRadius
    var surfaceRadius by lutShader::surfaceRadius
    var densityFalloff by lutShader::densityFalloff

    init {
        drawNode.apply {
            addMesh(Attribute.POSITIONS, Attribute.TEXTURE_COORDS) {
                generateFullscreenQuad()
                shader = lutShader

                onUpdate += {
                    logI("OpticalDepthLutPass") { "Updating atmosphere depth LUT: atmosphere radius = $atmosphereRadius, surface radius: $surfaceRadius, falloff: $densityFalloff" }
                }
            }
        }

        // this pass only needs to be rendered once, remove it immediately after first render
        onAfterPass {
            isEnabled = false
        }
    }

    fun update() {
        isEnabled = true
    }

    private fun opticalDepthLutProg() = KslProgram("Optical Depth LUT").apply {
        val uv = interStageFloat2("uv")

        fullscreenQuadVertexStage(uv)

        fragmentStage {
            val uAtmosphereRadius = uniformFloat1("uAtmosphereRadius")
            val uSurfaceRadius = uniformFloat1("uSurfaceRadius")
            val uDensityFalloff = uniformFloat1("uDensityFalloff")

            val funRayLength = functionFloat1("rayLength") {
                val rayOrigin = paramFloat3("rayOrigin")
                val rayDir = paramFloat3("rayDir")

                body {
                    val a = float1Var(dot(rayDir, rayDir))
                    val b = float1Var(2f.const * dot(rayDir, rayOrigin))
                    val c = float1Var(dot(rayOrigin, rayOrigin) - uAtmosphereRadius * uAtmosphereRadius)

                    val discriminant = float1Var(b * b - 4f.const * a * c)
                    val result = float1Var((-1f).const)
                    `if`(discriminant ge 0f.const) {
                        val q = float1Var((-0.5f).const * (b + sign(b) * sqrt(discriminant)))
                        val t1 = float1Var(q / a)
                        val t2 = float1Var(c / q)
                        result set max(t1, t2)
                    }
                    result
                }
            }

            val funDensityAtAltitude = functionFloat1("densityAtAltitude") {
                val altitude = paramFloat1("altitude")

                body {
                    val normalizedHeight = float1Var(clamp(altitude / (uAtmosphereRadius - uSurfaceRadius), 0f.const, 1f.const))
                    val x = float1Var(normalizedHeight * 10f.const)
                    val f = float1Var(0.3f.const * (2f.const + 3f.const * x * exp(-x)) * (10f.const - x) / 10f.const)
                    val h = float1Var(1f.const - f)

                    exp(-clamp(h, 0f.const, 1f.const) * uDensityFalloff) * (1f.const - smoothStep(0f.const, 1f.const, normalizedHeight))
                }
            }

            val funOpticalDepth = functionFloat1("opticalDepth") {
                val origin = paramFloat3("rayOrigin")
                val dir = paramFloat3("rayDir")
                val rayLength = paramFloat1("rayLength")

                body {
                    val numDepthSamples = 100.const
                    val samplePt = float3Var(origin)
                    val stepSize = float1Var(rayLength / (numDepthSamples.toFloat1() + 1f.const))
                    val opticalDepth = float1Var(0f.const)
                    fori(0.const, numDepthSamples) {
                        samplePt += dir * stepSize
                        opticalDepth += funDensityAtAltitude(length(samplePt) - uSurfaceRadius) * stepSize
                    }
                    opticalDepth
                }
            }

            main {
                val cosAngle = float1Var(uv.output.x * 2f.const - 1f.const)
                val altitude = float1Var(uv.output.y * (uAtmosphereRadius - uSurfaceRadius))

                val origin = float3Var(float3Value(uSurfaceRadius + altitude, 0f.const, 0f.const))
                val direction = float3Var(float3Value(cosAngle, sin(acos(cosAngle)), 0f.const))

                val shift = uAtmosphereRadius * 2f.const
                val rayLen = funRayLength(origin - direction * shift, direction) - shift
                val opticalDepth = funOpticalDepth(origin, direction, rayLen)

                colorOutput(float4Value(opticalDepth, funDensityAtAltitude(altitude), 0f.const, 1f.const))
            }
        }
    }

    private inner class OpticalDepthLutShader
        : KslShader(opticalDepthLutProg(), FullscreenShaderUtil.fullscreenShaderPipelineCfg) {
        var atmosphereRadius by uniform1f("uAtmosphereRadius", 65f)
        var surfaceRadius by uniform1f("uSurfaceRadius", 60f)
        var densityFalloff by uniform1f("uDensityFalloff", 9f)
    }

    companion object {
        const val LUT_SIZE = 512
    }
}