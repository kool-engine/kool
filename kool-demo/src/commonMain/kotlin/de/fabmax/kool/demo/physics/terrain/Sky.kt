package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.math.*
import de.fabmax.kool.modules.atmosphere.OpticalDepthLutPass
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ksl.UnlitShaderConfig
import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.pipeline.ibl.SkyCubeIblSystem
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.*
import de.fabmax.kool.util.Color.Hsv
import kotlin.collections.set
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.roundToInt
import kotlin.random.Random

class Sky(mainScene: Scene, moonTex: Texture2d) {

    var timeOfDay = 0.25f
    var fullDayDuration = 180f

    val isDay: Boolean
        get() = timeOfDay > 0.25f && timeOfDay < 0.75f

    val skies = TreeMap<Float, EnvironmentMaps>()

    val sunDirection = MutableVec3f()
    val moonDirection = MutableVec3f()
    private val sunOrientation = MutableMat3f()
    private val nightOrientation = MutableMat3f()

    private val skybox = Skybox.Cube(texLod = 1f, isInfiniteDepth = mainScene.isInfiniteDepth)
    private val sunShader = SkyObjectShader(mainScene.isInfiniteDepth) {
        uniformColor(Color.WHITE.mix(MdColor.YELLOW, 0.15f).toLinear())
    }
    private val moonShader = SkyObjectShader(mainScene.isInfiniteDepth) {
        textureColor(moonTex)
    }
    private val starShader = StarShader(mainScene.isInfiniteDepth)

    private val sunMesh = ColorMesh().apply {
        isFrustumChecked = false
        generate {
            circle {
                center.set(0f, 0f, -1f)
                radius = 0.015f
            }
        }
        shader = sunShader
    }
    private val moonMesh = TextureMesh().apply {
        isFrustumChecked = false
        generate {
            rect {
                size.set(0.17f, 0.17f)
                origin.set(0f, 0f, -1f)
            }
        }
        shader = moonShader
    }
    private val starMesh = TriangulatedPointMesh(numVertices = 4).apply {
        isFrustumChecked = false
        val r = Random(1337)
        for (i in 0..10_000) {
            val p = MutableVec3f(1f, 1f, 1f)
            while (p.sqrLength() > 1f) {
                p.set(r.randomF(-1f, 1f), r.randomF(-1f, 1f), r.randomF(-1f, 1f))
            }
            p.y *= 0.6f
            p.z *= 0.75f
            p.norm()

            val sz = r.randomF(1f, 3f)
            addPoint(p, sz, Hsv(r.randomF(0f, 360f), r.randomF(0f, 0.25f), 1f).toSrgb(a = sz / 3f))
        }
        shader = starShader
    }

    val skyGroup = Node().apply {
        addNode(skybox)
        addNode(sunMesh)
        addNode(starMesh)
        addNode(moonMesh)
    }

    lateinit var weightedEnvs: WeightedEnvMaps

    private val sunColorGradient = ColorGradient(
        0.00f to MdColor.YELLOW.mix(Color.WHITE, 0.7f),
        0.72f to MdColor.YELLOW.mix(Color.WHITE, 0.4f),
        0.84f to MdColor.AMBER.mix(Color.WHITE, 0.3f),
        0.92f to MdColor.AMBER,
        1.00f to MdColor.ORANGE,
        toLinear = true
    )

    init {
        mainScene.onUpdate += {
            val timeInc = Time.deltaT / fullDayDuration
            timeOfDay = (timeOfDay + timeInc) % 1f

            var fKey = skies.floorKey(timeOfDay)
            var cKey = skies.ceilingKey(timeOfDay)
            if (fKey == null) fKey = cKey!!
            if (cKey == null) cKey = fKey

            weightedEnvs.envA = skies[cKey]!!
            weightedEnvs.envB = skies[fKey]!!
            if (fKey != cKey) {
                weightedEnvs.weightA = (timeOfDay - fKey) / (cKey - fKey)
                weightedEnvs.weightB = (1f - weightedEnvs.weightA)
            } else {
                weightedEnvs.weightA = 1f
                weightedEnvs.weightB = 0f
            }

            skybox.skyboxShader.setBlendSkies(
                weightedEnvs.envA.reflectionMap, weightedEnvs.weightA * 2f,
                weightedEnvs.envB.reflectionMap, weightedEnvs.weightB * 2f
            )
        }

        mainScene.onRelease {
            skies.values.forEach { it.release() }
        }
    }

    suspend fun generateSkyMaps(terrainDemo: TerrainDemo, parentScene: Scene) {
        val hours = listOf(4f, 5f, 5.5f, 6f, 6.5f, 7f, 8f, 9f, 10f, 11f, 12f, 13f, 14f, 15f, 16f, 17f, 17.5f, 18f, 18.5f, 19f, 20f, 21f)
        val skyLut = OpticalDepthLutPass()
        parentScene.addOffscreenPass(skyLut)

        val sky = SkyCubeIblSystem(parentScene, skyLut.colorTexture!!)
        sky.setupOffscreenPasses()

        hours.forEachIndexed { i, h ->
            terrainDemo.showLoadText("Creating sky (${(i * 100f / hours.lastIndex).roundToInt()}%)...", 0)

            val timeOfDay = h / 24f
            val sunDir = computeLightDirection(SUN_TILT, sunProgress(timeOfDay), MutableMat3f())
            sky.skyPass.elevation = 90f - acos(-sunDir.y).toDeg()
            sky.skyPass.azimuth = atan2(sunDir.x, -sunDir.z).toDeg()

            val skyIrradiance = sky.irradianceMapPass.copyColor()
            val skyReflection = sky.reflectionMapPass.copyColor()
            skies[timeOfDay] = EnvironmentMaps(skyIrradiance, skyReflection)

            delayFrames(1)

        }
        weightedEnvs = WeightedEnvMaps(skies.firstValue(), skies.firstValue())

        launchDelayed(1) {
            parentScene.removeOffscreenPass(skyLut)
            sky.releaseOffscreenPasses()
            skyLut.release()
        }
    }

    fun updateLight(sceneLight: Light.Directional) {
        computeLightDirection(SUN_TILT, sunProgress(timeOfDay), sunOrientation, sunDirection)
        computeLightDirection(MOON_TILT, moonProgress(timeOfDay), nightOrientation, moonDirection)

        sunShader.orientation = sunOrientation
        moonShader.orientation = nightOrientation
        starShader.orientation = nightOrientation
        starShader.alpha = 1f - smoothStep(0.23f, 0.28f, timeOfDay) + smoothStep(0.72f, 0.77f, timeOfDay)

        if (isDay) {
            // daytime -> light is the sun
            val sunProgress = sunProgress(timeOfDay)
            val sunColor = sunColorGradient.getColorInterpolated(abs(sunProgress - 0.5f) * 2f, MutableColor())
            val sunIntensity = smoothStep(0.0f, 0.06f, sunProgress) * (1f - smoothStep(0.94f, 1.0f, sunProgress))
            sceneLight.setColor(sunColor, sunIntensity * 1.5f)
            sceneLight.setup(sunDirection)

        } else {
            // nighttime -> light is the moon
            val moonProgress = moonProgress(timeOfDay)
            val moonIntensity = smoothStep(0.0f, 0.06f, moonProgress) * (1f - smoothStep(0.94f, 1.0f, moonProgress))
            sceneLight.setColor(moonColor, moonIntensity * 0.12f)
            sceneLight.setup(moonDirection)
        }

    }

    private fun computeLightDirection(
        tilt: Float,
        progress: Float,
        orientation: MutableMat3f,
        direction: MutableVec3f = MutableVec3f()
    ): Vec3f {
        orientation
            .setIdentity()
            .rotate(tilt.deg, Vec3f.Z_AXIS)
            .rotate((progress * 180f).deg, Vec3f.X_AXIS)
        return orientation.transform(direction.set(0f, 0f, 1f))
    }

    fun sunProgress(timeOfDay: Float): Float {
        return (timeOfDay - 0.26f) * 2.083f
    }

    fun moonProgress(timeOfDay: Float): Float {
        val nightTime = (timeOfDay + 0.5f) % 1f
        return (nightTime - 0.26f) * 2.083f
    }

    private class SkyObjectShader(
        isReverseDepth: Boolean,
        colorBlock: ColorBlockConfig.Builder.() -> Unit
    ) : KslUnlitShader(config(isReverseDepth, colorBlock)) {

        var orientation: Mat3f by uniformMat3f("uOrientation")
        var alpha: Float by uniform1f("uAlpha", 1f)

        companion object {
            fun config(isReverseDepth: Boolean, colorBlock: ColorBlockConfig.Builder.() -> Unit) = UnlitShaderConfig {
                color {
                    colorBlock()
                }
                pipeline {
                    cullMethod = CullMethod.NO_CULLING
                    isWriteDepth = false
                }
                colorSpaceConversion = ColorSpaceConversion.LinearToSrgb()
                modelCustomizer = {
                    vertexStage {
                        main {
                            val mvpMat = mvpMatrix().matrix
                            val localPos = vertexAttribFloat3(Attribute.POSITIONS.name)
                            val orientation = uniformMat3("uOrientation")
                            if (isReverseDepth) {
                                outPosition set (mvpMat * float4Value(orientation * localPos * 1e9f.const, 1f)).float4("xyzw")
                            } else {
                                outPosition set (mvpMat * float4Value(orientation * localPos, 0f)).float4("xyww")
                            }
                        }
                    }
                    fragmentStage {
                        main {
                            val baseColorPort = getFloat4Port("baseColor")
                            val alphaColor = float4Var(baseColorPort.input.input)
                            alphaColor.a *= uniformFloat1("uAlpha")
                            baseColorPort.input(alphaColor)
                        }
                    }
                }
            }
        }
    }

    private class StarShader(isReverseDepth: Boolean) : KslShader("triangulated-star-shader") {
        var orientation: Mat3f by uniformMat3f("uOrientation")
        var alpha: Float by uniform1f("uAlpha", 1f)

        init {
            program.apply {
                val color = interStageFloat4()
                vertexStage {
                    main {
                        val camData = cameraData()
                        val modelMat = modelMatrix()

                        val pointCfg = instanceAttribFloat4(TriangulatedPointMesh.ATTR_POINT_POS_SZ)
                        val pointPos = float3Var(pointCfg.xyz)
                        val pointSize = float1Var(pointCfg.w)
                        val pxSize = float2Var(float2Value(1f.const / camData.viewport.z, 1f.const / camData.viewport.w))

                        val mvpMat = mat4Var(camData.viewProjMat * modelMat.matrix)
                        val orientation = uniformMat3("uOrientation")
                        if (isReverseDepth) {
                            outPosition set (mvpMat * float4Value(orientation * pointPos * 1e9f.const, 1f)).float4("xyzw")
                        } else {
                            outPosition set (mvpMat * float4Value(orientation * pointPos, 0f)).float4("xyww")
                        }

                        color.input set instanceAttribFloat4(TriangulatedPointMesh.ATTR_POINT_COLOR)
                        outPosition.xy += vertexAttribFloat2(TriangulatedPointMesh.ATTR_POINT_VERTEX) * outPosition.w * pointSize * pxSize
                    }
                }
                fragmentStage {
                    main {
                        val starColor = float4Var(color.output)
                        starColor.a *= uniformFloat1("uAlpha")
                        colorOutput(starColor)
                    }
                }
            }
        }
    }

    companion object {
        private const val SUN_TILT = 30f
        private const val MOON_TILT = 45f
        private val moonColor = MdColor.BLUE toneLin 200
    }

    class WeightedEnvMaps(var envA: EnvironmentMaps, var envB: EnvironmentMaps) {
        var weightA = 1f
        var weightB = 0f
    }
}