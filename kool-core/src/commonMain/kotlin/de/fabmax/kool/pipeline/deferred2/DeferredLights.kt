package de.fabmax.kool.pipeline.deferred2

import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.swapPipelineData
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.*
import kotlin.math.*

class DeferredLights(
    pipeline: Deferred2Pipeline,
    val isDynamic: Boolean = true,
    name: String = "deferred-lights",
) : Node(name) {
    val pointLights: List<DynamicPointLight>
        field = mutableListOf<DynamicPointLight>()
    val spotLights: Map<AngleF, List<DynamicSpotLight>>
        field = mutableMapOf<AngleF, MutableList<DynamicSpotLight>>()

    private val pointLightInstances = MeshInstanceList(DeferredLightInstanceLayout)
    private val modelMat = MutableMat4f()

    val lightShader = DeferredLightShader()

    val spotLightMeshes = mutableMapOf<AngleF, SpotLightMesh>()
    val pointLightMesh = Mesh(
        layout = VertexLayouts.Position,
        instances = pointLightInstances,
        name = "DeferredPointLights"
    ).apply {
        generate { makePointLightMesh() }
        shader = lightShader
    }

    init {
        pipeline.onSwap { swapPipelineData(pipeline) }
        addNode(pointLightMesh)
        if (isDynamic) {
            onUpdate { updateLightInstanceData() }
        }
    }

    fun clear() {
        pointLights.clear()
        pointLightInstances.clear()
        spotLights.clear()
        spotLightMeshes.values.forEach {
            it.lights.clear()
            it.instances.clear()
        }
    }

    fun updateLightInstanceData() {
        pointLightInstances.clear()
        pointLightInstances.addInstances(pointLights.size) { buf ->
            for (i in 0 until pointLights.size) {
                buf.put { encodePoint(pointLights[i]) }
            }
        }
        if (spotLightMeshes.isNotEmpty()) {
            for (lights in spotLightMeshes.values) {
                lights.instances.clear()
                lights.instances.addInstances(lights.lights.size) { buf ->
                    for (i in 0 until lights.lights.size) {
                        buf.put { encodeSpot(lights.lights[i]) }
                    }
                }
            }
        }
    }

    private fun MutableStructBufferView<DeferredLightInstanceLayout>.encodePoint(light: DynamicPointLight) {
        modelMat
            .setIdentity()
            .translate(light.position)
            .scale(light.radius)
        set(DeferredLightInstanceLayout.modelMat, modelMat)
        set(DeferredLightInstanceLayout.lightType, Light.Point.ENCODING)
        set(DeferredLightInstanceLayout.encodedSpotAngle, -1.01f)
        set(DeferredLightInstanceLayout.lightColor,
            light.color.r * light.intensity,
            light.color.g * light.intensity,
            light.color.b * light.intensity,
            1f,
        )
    }

    private fun MutableStructBufferView<DeferredLightInstanceLayout>.encodeSpot(light: DynamicSpotLight) {
        modelMat
            .setIdentity()
            .translate(light.position)
            .rotate(light.rotation)
        modelMat.scale(light.radius)
        set(DeferredLightInstanceLayout.modelMat, modelMat)

        set(DeferredLightInstanceLayout.lightType, Light.Spot.ENCODING)
        set(DeferredLightInstanceLayout.encodedSpotAngle, cos(min(light.maxSpotAngle.rad, light.spotAngle.rad) / 2f))
        set(DeferredLightInstanceLayout.lightColor,
            light.color.r * light.intensity,
            light.color.g * light.intensity,
            light.color.b * light.intensity,
            light.coreRatio,
        )
    }

    fun addPointLight(pointLight: DynamicPointLight) {
        pointLights += pointLight
    }

    inline fun addPointLight(block: DynamicPointLight.() -> Unit): DynamicPointLight {
        val light = DynamicPointLight()
        light.block()
        addPointLight(light)
        return light
    }

    fun removePointLight(light: DynamicPointLight) {
        // fixme this is very slow for many lights
        pointLights -= light
    }

    fun addSpotLight(spotLight: DynamicSpotLight) {
        spotLights.getOrPut(spotLight.maxSpotAngle) { mutableListOf() } += spotLight
        val mesh = spotLightMeshes.getOrPut(spotLight.maxSpotAngle) {
            SpotLightMesh(spotLight.maxSpotAngle).also { addNode(it.mesh) }
        }
        mesh.lights += spotLight
    }

    inline fun addSpotLight(maxAngle: AngleF = 60f.deg, block: DynamicSpotLight.() -> Unit): DynamicSpotLight {
        val light = DynamicSpotLight(maxAngle)
        light.block()
        addSpotLight(light)
        return light
    }

    fun removeSpotLight(light: DynamicSpotLight) {
        // fixme this is very slow for many lights
        spotLights[light.maxSpotAngle]?.remove(light)
        spotLightMeshes[light.maxSpotAngle]?.lights?.remove(light)
    }

    fun swapPipelineData(pipeline: Deferred2Pipeline) {
        val gbuffer = pipeline.gbuffers.newVal
        lightShader.swapPipelineData(gbuffer) {
            depth = gbuffer.depth
            normals = gbuffer.normals
            albedoEmission = gbuffer.albedoEmission
            metalRoughAo = gbuffer.metalRoughnessAo
            camData = pipeline.camData
        }
    }

    inner class SpotLightMesh(angle: AngleF) {
        val lights = mutableListOf<DynamicSpotLight>()
        val instances = MeshInstanceList(DeferredLightInstanceLayout)
        val mesh = Mesh(
            layout = VertexLayouts.Position,
            instances = instances,
            name = "DeferredPointLights"
        ).apply {
            generate { makeSpotLightMesh(angle) }
            shader = lightShader
        }
    }
}

class DynamicPointLight(
    val position: MutableVec3f = MutableVec3f(),
    val color: MutableColor = MutableColor(Color.WHITE),
    var radius: Float = 1f,
    var intensity: Float = 1f,
) {
    fun strengthByRadius(radius: Float) {
        this.radius = radius
        intensity = radius * radius
    }

    fun strengthByIntensity(intensity: Float) {
        this.intensity = intensity
        radius = sqrt(intensity)
    }
}

class DynamicSpotLight(val maxSpotAngle: AngleF) {
    val position = MutableVec3f()
    val rotation = MutableQuatF()
    var spotAngle = maxSpotAngle
    var coreRatio = 0.5f
    val color = MutableColor(Color.WHITE)
    var radius = 1f
    var intensity = 1f

    private val tmpDir = MutableVec3f()

    fun setDirection(direction: Vec3f) {
        direction.normed(tmpDir)
        val v = if (abs(tmpDir.dot(Vec3f.Y_AXIS)) > 0.9f) Vec3f.X_AXIS else Vec3f.Y_AXIS
        val b = tmpDir.cross(v, MutableVec3f())
        val c = tmpDir.cross(b, MutableVec3f())
        Mat3f(tmpDir, b, c).getRotation(rotation)
    }

    fun strengthByRadius(radius: Float) {
        this.radius = radius
        intensity = radius * radius
    }

    fun strengthByIntensity(intensity: Float) {
        this.intensity = intensity
        radius = sqrt(intensity)
    }
}

object DeferredLightInstanceLayout : Struct("PointLightInstanceLayout", MemoryLayout.TightlyPacked) {
    val modelMat = include(InstanceLayouts.ModelMat.modelMat)
    val lightColor = float4("lightColor")
    val encodedSpotAngle = float1("encodedSpotAngle")
    val lightType = float1("lightType")
}

private fun MeshBuilder<*>.makePointLightMesh() {
    icoSphere {
        steps = 0
        radius = 1.176f     // required radius to fully include unit sphere at 0 subdivisions
    }
}

private fun MeshBuilder<*>.makeSpotLightMesh(angle: AngleF) {
    val radius = 1.17f
    val cAng = angle.deg.clamp(0f, 360f) / 2f
    val steps = 8
    val belts = (steps / 2 * cAng / 180).toInt()

    // far cap
    val iCenter = vertex(Vec3f(radius, 0f, 0f), Vec3f.X_AXIS)
    for (i in 0 until steps) {
        val a = min((360f / steps).toRad(), cAng.toRad())
        val x = cos(a) * radius
        val r = sin(a) * radius
        val y = sin(2 * PI * i / steps).toFloat() * r
        val z = cos(2 * PI * i / steps).toFloat() * r

        val vp = Vec3f(x, y, z)
        val iv = vertex(vp, vp)
        if (i > 0) {
            geometry.addTriIndices(iCenter, iv, iv - 1)
        }
        if (i == steps - 1) {
            geometry.addTriIndices(iCenter, iv - steps + 1, iv)
        }
    }

    // belts
    for (b in 0 until belts) {
        val a = if (b < belts-1) { (360f / steps * (b + 2)).toRad() } else { cAng.toRad() }
        for (i in 0 until steps) {
            val x = cos(a) * radius
            val r = sin(a) * radius
            val y = sin(2 * PI * i / steps).toFloat() * r
            val z = cos(2 * PI * i / steps).toFloat() * r

            val vp = Vec3f(x, y, z)
            val iv = vertex(vp, vp)
            if (i > 0) {
                geometry.addTriIndices(iv, iv - 1, iv - steps)
                geometry.addTriIndices(iv - 1, iv - steps - 1, iv - steps)
            }
            if (i == steps-1) {
                geometry.addTriIndices(iv, iv - steps, iv - steps * 2 + 1)
                geometry.addTriIndices(iv - steps + 1, iv, iv - steps * 2 + 1)
            }
        }
    }

    // cone
    val iOri = vertex(Vec3f(-0.1f, 0f, 0f), Vec3f.NEG_X_AXIS)
    for (i in 0 until steps) {
        if (i < steps - 1) {
            geometry.addTriIndices(iOri, iOri - steps + i, iOri - steps + i + 1)
        } else {
            geometry.addTriIndices(iOri, iOri - 1, iOri - steps)
        }
    }

}
