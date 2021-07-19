package de.fabmax.kool.util.deferred

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.Light
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MeshInstanceList
import de.fabmax.kool.util.MutableColor
import kotlin.math.sqrt


class DeferredPointLights(mrtPass: DeferredMrtPass) {
    val lightInstances = mutableListOf<PointLight>()
    var isDynamic = true

    private val lightInstanceData = MeshInstanceList(listOf(MeshInstanceList.MODEL_MAT, DeferredLightShader.LIGHT_POS,
        Attribute.COLORS, DeferredLightShader.LIGHT_DATA), 10000)

    private val modelMat = Mat4f()
    private val encodedLightData = FloatArray(12)

    val mesh = mesh(listOf(Attribute.POSITIONS)) {
        isFrustumChecked = false
        instances = lightInstanceData

        generate {
            icoSphere {
                steps = 0
                radius = 1.176f     // required radius to fully include unit sphere at 0 subdivisions
            }
        }

        val lightCfg = DeferredLightShader.Config().apply {
            lightType = Light.Type.POINT
            sceneCamera = mrtPass.camera
            positionAo = mrtPass.positionAo
            normalRoughness = mrtPass.normalRoughness
            albedoMetal = mrtPass.albedoMetal
            emissiveMat = mrtPass.emissive
        }
        shader = DeferredLightShader(lightCfg)

        onUpdate += {
            if (isDynamic) {
                updateLightData()
            }
        }
    }

    fun updateLightData() {
        lightInstanceData.clear()
        lightInstanceData.addInstances(lightInstances.size) { buf ->
            for (i in 0 until lightInstances.size) {
                encodeLight(lightInstances[i])
                buf.put(modelMat.matrix)
                buf.put(encodedLightData)
            }
        }
    }

    private fun encodeLight(light: PointLight) {
        modelMat.setIdentity()
        modelMat.translate(light.position)
        val soi = sqrt(light.power)
        modelMat.scale(soi, soi, soi)

        encodedLightData[0] = light.position.x
        encodedLightData[1] = light.position.y
        encodedLightData[2] = light.position.z
        encodedLightData[3] = Light.Type.POINT.encoded

        encodedLightData[4] = light.color.r
        encodedLightData[5] = light.color.g
        encodedLightData[6] = light.color.b
        encodedLightData[7] = light.power

        encodedLightData[8] = light.maxIntensity
    }

    fun addPointLight(pointLight: PointLight) {
        lightInstances += pointLight
    }

    inline fun addPointLight(block: PointLight.() -> Unit): PointLight {
        val light = PointLight()
        light.block()
        addPointLight(light)
        return light
    }

    fun removePointLight(light: PointLight) {
        lightInstances -= light
    }

    class PointLight {
        val position = MutableVec3f()
        val color = MutableColor(Color.WHITE)
        var power = 1f
        var maxIntensity = 100f
    }
}