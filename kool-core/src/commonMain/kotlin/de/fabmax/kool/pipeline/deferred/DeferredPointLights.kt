package de.fabmax.kool.pipeline.deferred

import de.fabmax.kool.math.MutableMat4f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.Light
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.util.*


class DeferredPointLights(var isDynamic: Boolean) {
    val lightInstances = mutableListOf<PointLight>()

    private val lightInstanceData = MeshInstanceList(PointLightInstanceLayout, 10000)

    private val modelMat = MutableMat4f()

    val lightShader = DeferredLightShader(Light.Point.ENCODING)

    val mesh = Mesh(
        listOf(Attribute.POSITIONS),
        instances = lightInstanceData,
        name = "DeferredPointLights"
    ).apply {
        isFrustumChecked = false

        generate {
            icoSphere {
                steps = 0
                radius = 1.176f     // required radius to fully include unit sphere at 0 subdivisions
            }
        }

        shader = lightShader

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
                buf.put { encodeLight(lightInstances[i]) }
            }
        }
    }

    private fun MutableStructBufferView<PointLightInstanceLayout>.encodeLight(light: PointLight) {
        modelMat
            .setIdentity()
            .translate(light.position)
            .scale(light.radius)
        set(PointLightInstanceLayout.modelMat, modelMat)

        set(PointLightInstanceLayout.lightPos,
            light.position.x,
            light.position.y,
            light.position.z,
            Light.Point.ENCODING,
        )
        set(PointLightInstanceLayout.lightColor,
            light.color.r * light.intensity,
            light.color.g * light.intensity,
            light.color.b * light.intensity,
            1f,
        )
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
        var radius = 1f
        var intensity = 1f
    }

    object PointLightInstanceLayout : Struct("PointLightInstanceLayout", MemoryLayout.TightlyPacked) {
        val modelMat = mat4(Attribute.INSTANCE_MODEL_MAT.name)
        val lightPos = float4("lightPos")
        val lightColor = float4("lightColor")
    }
}