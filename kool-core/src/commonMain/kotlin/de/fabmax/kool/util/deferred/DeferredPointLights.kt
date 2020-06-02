package de.fabmax.kool.util.deferred

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.Light
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MeshInstanceList
import kotlin.math.min
import kotlin.math.sqrt


class DeferredPointLights(mrtPass: DeferredMrtPass) {
    val lightInstances = mutableListOf<PointLight>()
    var isDynamic = true

    private val lightInstanceData = MeshInstanceList(listOf(MeshInstanceList.MODEL_MAT, DeferredLightShader.LIGHT_POS, Attribute.COLORS), 10000)

    private val modelMat = Mat4f()
    private val encodedColor = FloatArray(4)
    private val encodedPos = FloatArray(4)

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
            sceneCamera = mrtPass.camera
            positionAo = mrtPass.positionAo
            normalRoughness = mrtPass.normalRoughness
            albedoMetal = mrtPass.albedoMetal
        }
        pipelineLoader = DeferredLightShader(lightCfg)

        onUpdate += { _, _ ->
            if (isDynamic) {
                updateLightData()
            }
        }
    }

    fun updateLightData() {
        lightInstanceData.clear()
        for (i in 0 until min(lightInstances.size, lightInstanceData.maxInstances)) {
            lightInstanceData.addInstance {
                encodeLight(lightInstances[i])
                put(modelMat.matrix)
                put(encodedPos)
                put(encodedColor)
            }
        }
    }

    private fun encodeLight(light: PointLight) {
        modelMat.setIdentity()
        modelMat.translate(light.position)
        val soi = sqrt(light.intensity)
        modelMat.scale(soi, soi, soi)

        encodedColor[0] = light.color.r
        encodedColor[1] = light.color.g
        encodedColor[2] = light.color.b
        encodedColor[3] = light.intensity

        encodedPos[0] = light.position.x
        encodedPos[1] = light.position.y
        encodedPos[2] = light.position.z
        encodedPos[3] = Light.Type.POINT.encoded
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
        var color = Color.MD_PINK
        var intensity = 1f
    }
}