package de.fabmax.kool.modules.atmosphere

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.blocks.noise31
import de.fabmax.kool.modules.ksl.blocks.raySphereIntersection
import de.fabmax.kool.modules.ksl.lang.*

fun KslProgram.atmosphereData(): AtmosphereData {
    return (dataBlocks.find { it is AtmosphereData } as? AtmosphereData) ?: AtmosphereData(this)
}

class AtmosphereData(program: KslProgram) : KslDataBlock {
    override val name = NAME

    val uDirToSun = program.uniformFloat3("uDirToSun")
    val uPlanetCenter = program.uniformFloat3("uPlanetCenter")
    val uSurfaceRadius = program.uniformFloat1("uSurfaceRadius")
    val uAtmosphereRadius = program.uniformFloat1("uAtmosphereRadius")
    val uScatteringCoeffs = program.uniformFloat3("uScatteringCoeffs")
    val uMieG = program.uniformFloat1("uMieG")
    val uMieColor = program.uniformFloat4("uMieColor")
    val uRayleighColor = program.uniformFloat4("uRayleighColor")
    val uSunColor = program.uniformFloat4("uSunColor")
    val uRandomOffset = program.uniformFloat2("uAtmoRandOffset")

    companion object {
        const val NAME = "AtmosphereData"
    }
}

fun KslScopeBuilder.atmosphereBlock(
    atmosphereData: AtmosphereData,
    opticalDepthLut: KslExpression<KslColorSampler2d>,
    numScatterSamples: Int = 16,
    randomizeStartOffsets: Boolean = true,
    sunShadowTex: KslExpression<KslDepthSampler2d>? = null,
    sunShadowProj: KslExprMat4? = null
): AtmosphereBlock {
    val atmosphereBlock = AtmosphereBlock(
        this,
        atmosphereData,
        opticalDepthLut,
        numScatterSamples,
        randomizeStartOffsets,
        sunShadowTex,
        sunShadowProj
    )
    ops += atmosphereBlock
    return atmosphereBlock
}

class AtmosphereBlock(
    parentScope: KslScopeBuilder,
    atmosphereData: AtmosphereData,
    opticalDepthLut: KslExpression<KslColorSampler2d>,
    private val numScatterSamples: Int,
    private val randomizeStartOffsets: Boolean,
    private val sunShadowTex: KslExpression<KslDepthSampler2d>?,
    private val sunShadowProj: KslExprMat4?
) : KslBlock("atmosphereBlock", parentScope) {

    val inSceneColor = inFloat4("inSceneColor", KslValueFloat4(0f, 0f, 0f, 1f))
    val inSkyColor = inFloat4("inSkyColor", KslValueFloat4(0f, 0f, 0f, 1f))
    val inSceneDepth = inFloat1("inSceneDepth", KslValueFloat1(0f))
    val inViewDepth = inFloat1("inViewDepth", KslValueFloat1(0f))
    val inScenePos = inFloat3("inScenePos", KslValueFloat3(0f, 0f, 0f))
    val inCamPos = inFloat3("inCamPos", KslValueFloat3(0f, 0f, 0f))
    val inLookDir = inFloat3("inLookDir", KslValueFloat3(0f, 0f, 1f))

    val outColor = outFloat4("outColor")

    private val funPhaseFunRayleigh = parentScope.parentStage.functionFloat3("phaseFunRayleigh") {
        val cosTheta = paramFloat1("cosTheta")

        body {
            val phase = float1Var(1.5f.const + cosTheta * 0.5f.const)
            atmosphereData.uRayleighColor.rgb * atmosphereData.uRayleighColor.a * phase
        }
    }

    private val funPhaseFunMie = parentScope.parentStage.functionFloat3("phaseFunMie") {
        val cosTheta = paramFloat1("cosTheta")
        val g = paramFloat1("g")

        body {
            val g2 = float1Var(g * g)
            val f1 = float1Var((3f.const * (1f.const - g2)) / (2f.const * (2f.const + g2)))
            val f2 = float1Var((1f.const + cosTheta * cosTheta) / pow(1f.const + g2 - 2f.const * g * cosTheta, 1.5f.const))
            atmosphereData.uMieColor.rgb * atmosphereData.uMieColor.a * f1 * f2
        }
    }

    private val funOpticalDepth = parentScope.parentStage.functionFloat2("opticalDepth") {
        val altitude = paramFloat1("altitude")
        val cosTheta = paramFloat1("cosTheta")

        body {
            sampleTexture(opticalDepthLut, float2Value(cosTheta * 0.5f.const + 0.5f.const, altitude), 0f.const).rg
        }
    }

    private val funOpticalDepthLen = parentScope.parentStage.functionFloat1("opticalDepthLen") {
        val origin = paramFloat3("origin")
        val dir = paramFloat3("dir")
        val len = paramFloat1("len")

        body {
            val viewDir = float3Var(dir)
            val atmosphereThickness = float1Var(atmosphereData.uAtmosphereRadius - atmosphereData.uSurfaceRadius)
            val p1 = float3Var(origin)
            val p2 = float3Var(origin + viewDir * len)
            val altitude1 = float1Var((length(p1) - atmosphereData.uSurfaceRadius) / atmosphereThickness)
            val altitude2 = float1Var((length(p2) - atmosphereData.uSurfaceRadius) / atmosphereThickness)

            `if`(altitude1 gt altitude2) {
                // swap points and direction if ray is pointing downwards
                p1 set p2
                p2 set origin

                val swapAlt = float1Var(altitude1)
                altitude1 set altitude2
                altitude2 set swapAlt
                viewDir set -dir
            }

            val depth1 = float1Var(funOpticalDepth(altitude1, dot(viewDir, normalize(p1))).x)
            val depth2 = float1Var(funOpticalDepth(altitude2, dot(viewDir, normalize(p2))).x)
            depth1 - depth2
        }
    }

    private val funScatterLight = parentScope.parentStage.functionFloat3("scatterLight") {
        val startOffset = paramFloat1("startOffset")
        val origin = paramFloat3("origin")
        val dir = paramFloat3("dir")
        val rayLength = paramFloat1("rayLength")
        val dirToSun = paramFloat3("dirToSun")

        body {
            val atmosphereThickness = float1Var(atmosphereData.uAtmosphereRadius - atmosphereData.uSurfaceRadius)
            val stepSize = float1Var(rayLength / (numScatterSamples + 1f).const)
            val inScatterPt = float3Var(origin)
            val inScatteredLight = float3Var(Vec3f.ZERO.const)

            inScatterPt += dir * stepSize * startOffset

            fori(0.const, numScatterSamples.const) { i ->
                inScatterPt += dir * stepSize

                val shadowFac = if (sunShadowProj != null && sunShadowTex != null) {
                    val projPos = float4Var(sunShadowProj * float4Value(inScatterPt, 1f.const))
                    val shadowCoord = float3Var(projPos.xyz / projPos.w)
                    float1Var(sampleDepthTexture(sunShadowTex, shadowCoord.xy, shadowCoord.z))
                } else {
                    1f.const
                }

                val planetHit = raySphereIntersection(inScatterPt, dirToSun, Vec3f.ZERO.const, atmosphereData.uSurfaceRadius)
                `if`(planetHit.z eq 0f.const) {
                    // sun is not blocked by planet
                    val verticalDir = float3Var(normalize(inScatterPt))
                    val cosTheta = float1Var(dot(dirToSun, verticalDir))
                    val altitude = float1Var((length(inScatterPt) - atmosphereData.uSurfaceRadius) / atmosphereThickness)

                    val viewRayOpticalDepth = float1Var(funOpticalDepthLen(origin, dir, stepSize * i.toFloat1()))
                    val opticalDepthToSun = funOpticalDepth(altitude, cosTheta)
                    val sunRayOpticalDepth = opticalDepthToSun.x
                    val localDensity = opticalDepthToSun.y
                    val transmittance = float3Var(exp(atmosphereData.uScatteringCoeffs * -(sunRayOpticalDepth + viewRayOpticalDepth)))

                    inScatteredLight += transmittance * localDensity * atmosphereData.uScatteringCoeffs * stepSize * shadowFac
                }
            }

            val sunAngle = float1Var(dot(dir, dirToSun))
            val rayleigh = float3Var(funPhaseFunRayleigh(sunAngle))
            val mie = float3Var(funPhaseFunMie(sunAngle, atmosphereData.uMieG))
            (rayleigh + mie) * inScatteredLight * atmosphereData.uSunColor.rgb * atmosphereData.uSunColor.a
        }
    }

    init {
        body.apply {
            val camOri = float3Var(inCamPos - atmosphereData.uPlanetCenter)
            val nrmLookDir = float3Var(normalize(inLookDir))
            val hitAtmo = raySphereIntersection(camOri, nrmLookDir, Vec3f.ZERO.const, atmosphereData.uAtmosphereRadius)
            val isHit = bool1Var(hitAtmo.z gt 0f.const)

            outColor set inSceneColor
            `if`(inSceneDepth gt 0f.const) {
                outColor set inSkyColor
            }
            `if`(isHit) {
                // inbound hit distance is negative if camera is inside atmosphere
                val dToAtmo = float1Var(max(0f.const, hitAtmo.x))
                val distThroughAtmo = float1Var(hitAtmo.y - dToAtmo)
                `if`(inViewDepth lt 0f.const) {
                    val sceneDepth = float1Var(length(inCamPos - inScenePos))
                    distThroughAtmo set min(distThroughAtmo, sceneDepth - dToAtmo)
                }

                val startOffset = float1Var(noise31(inLookDir))
                val atmoHitPt = float3Var(camOri + nrmLookDir * dToAtmo)
                outColor.rgb += funScatterLight(startOffset, atmoHitPt, nrmLookDir, distThroughAtmo, atmosphereData.uDirToSun)
            }
        }
    }
}