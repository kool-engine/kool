package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.pipeline.ibl.SkyCubeIblSystem
import de.fabmax.kool.scene.Light
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.Skybox
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ColorGradient
import de.fabmax.kool.util.MdColor
import de.fabmax.kool.util.TreeMap
import kotlin.math.*

class Sky(mainScene: Scene) {

    var timeOfDay = 0.25f
    var fullDayDuration = 180f

    val sunDirection = MutableVec3f()
    val skybox = Skybox.Cube(texLod = 1f)
    val skies = TreeMap<Float, SkyCubeIblSystem>()

    lateinit var weightedEnvs: WeightedEnvMaps

    private val sunOrientation = Mat3f()
    private val sunColorGradient = ColorGradient(
        0.00f to MdColor.YELLOW.mix(Color.WHITE, 0.07f),
        0.36f to MdColor.YELLOW.mix(Color.WHITE, 0.12f),
        0.42f to MdColor.AMBER.mix(Color.WHITE, 0.3f),
        0.46f to MdColor.AMBER,
        0.50f to MdColor.ORANGE,
        0.54f to Color.BLACK,
        1.00f to Color.BLACK,

        toLinear = true
    )

    init {
        mainScene.onUpdate += {
            // make the night pass faster
            val x = (timeOfDay - 0.5f) * 4f
            val timeAdvFac = ((x * x * 0.2f + cos(x * PI)) / 3f + 0.5f).toFloat()
            val timeInc = timeAdvFac * it.deltaT / fullDayDuration

            timeOfDay = (timeOfDay + timeInc) % 1f
            computeLightDirection(timeOfDay, sunDirection)

            val fKey = skies.floorKey(timeOfDay)!!
            val cKey = skies.ceilingKey(timeOfDay)!!

            weightedEnvs.envA = skies[cKey]!!.envMaps
            weightedEnvs.envB = skies[fKey]!!.envMaps

            if (fKey != cKey) {
                weightedEnvs.weightA = (timeOfDay - fKey) / (cKey - fKey)
                weightedEnvs.weightB = (1f - weightedEnvs.weightA)
            } else {
                weightedEnvs.weightA = 1f
                weightedEnvs.weightB = 0f
            }

            skybox.skyboxShader.setBlendSkies(
                weightedEnvs.envA.reflectionMap, weightedEnvs.weightA * 2f,
                weightedEnvs.envB.reflectionMap, weightedEnvs.weightB * 2f)
        }
    }

    suspend fun generateMaps(terrainDemo: TerrainDemo, parentScene: Scene, ctx: KoolContext) {
        for (h in 0..23) {
            terrainDemo.showLoadText("Creating sky ($h:00)...", 0)
            precomputeSky(h / 24f, parentScene, ctx)
        }

        // add a few extra steps at dusk and dawn
        precomputeSky(5.5f / 24f, parentScene, ctx)
        precomputeSky(6.5f / 24f, parentScene, ctx)
        precomputeSky(17.5f / 24f, parentScene, ctx)
        precomputeSky(18.5f / 24f, parentScene, ctx)

        skies[1f] = skies[0f]!!
        weightedEnvs = WeightedEnvMaps(skies[0.5f]!!.envMaps, skies[0.5f]!!.envMaps)
    }

    private suspend fun precomputeSky(timeOfDay: Float, parentScene: Scene, ctx: KoolContext) {
        val sunDir = computeLightDirection(timeOfDay, forceSun = true)
        val sky = SkyCubeIblSystem(parentScene)
        sky.skyPass.elevation = 90f - acos(-sunDir.y).toDeg()
        sky.skyPass.azimuth = atan2(sunDir.x, -sunDir.z).toDeg()
        sky.setupOffscreenPasses()
        skies[timeOfDay] = sky
        ctx.delayFrames(1)
    }

    fun updateSun(sunLight: Light) {
        if (timeOfDay > 0.23f && timeOfDay < 0.77f) {
            // daytime -> light is the sun
            val sunColor = sunColorGradient.getColor(abs(timeOfDay - 0.5f) * 2f)
            val sunIntensity = smoothStep(0.23f, 0.26f, timeOfDay) * (1f - smoothStep(0.74f, 0.77f, timeOfDay))
            sunLight.setColor(sunColor, sunIntensity * 1.5f)
        } else {
            // nighttime -> light is the moon
            val moonColor = Color.WHITE.mix(MdColor.LIGHT_BLUE, 0.3f).toLinear()
            val moonProgress = moonProgress(timeOfDay)
            val moonIntensity = smoothStep(0.27f, 0.3f, moonProgress) * (1f - smoothStep(0.7f, 0.73f, moonProgress))
            sunLight.setColor(moonColor, moonIntensity * 0.06f)
        }
        sunLight.setDirectional(sunDirection)
    }

    private fun computeLightDirection(timeOfDay: Float, result: MutableVec3f = MutableVec3f(), forceSun: Boolean = false): Vec3f {
        return if (forceSun || timeOfDay > 0.23f && timeOfDay < 0.77f) {
            // daytime -> light is the sun
            sunOrientation
                .setIdentity()
                .rotate(30f, Vec3f.Z_AXIS)
                .rotate((timeOfDay - 0.25f) * 360f, Vec3f.X_AXIS)
            sunOrientation.transform(result.set(0f, 0f, 1f))
        } else {
            // nighttime -> light is the moon
            sunOrientation
                .setIdentity()
                .rotate(45f, Vec3f.Z_AXIS)
                .rotate((moonProgress(timeOfDay) - 0.25f) * 360f, Vec3f.X_AXIS)
            sunOrientation.transform(result.set(0f, 0f, 1f))
        }
    }

    private fun moonProgress(timeOfDay: Float): Float {
        val nightTime = (timeOfDay + 0.5f) % 1f
        return ((nightTime - 0.5f) * 1.05f + 0.5f).clamp(0f, 1f)
    }

    class WeightedEnvMaps(var envA: EnvironmentMaps, var envB: EnvironmentMaps) {
        var weightA = 1f
        var weightB = 0f
    }
}