package de.fabmax.kool.util.deferred

import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.Light
import de.fabmax.kool.scene.mesh
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MeshBuilder
import de.fabmax.kool.util.MeshInstanceList
import de.fabmax.kool.util.MutableColor
import kotlin.math.*


class DeferredSpotLights(val maxSpotAngle: Float, mrtPass: DeferredMrtPass) {
    val lightInstances = mutableListOf<SpotLight>()
    var isDynamic = true

    private val lightInstanceData = MeshInstanceList(listOf(MeshInstanceList.MODEL_MAT, DeferredLightShader.LIGHT_POS,
            DeferredLightShader.LIGHT_DIR, Attribute.COLORS), 10000)

    private val modelMat = Mat4f()
    private val encodedLightData = FloatArray(12)
    private val tmpLightDir = MutableVec3f()

    val mesh = mesh(listOf(Attribute.POSITIONS)) {
        isFrustumChecked = false
        instances = lightInstanceData

        generate {
            makeHalfSphereCone(maxSpotAngle, 1.17f)
        }

        val lightCfg = DeferredLightShader.Config().apply {
            lightType = Light.Type.SPOT
            sceneCamera = mrtPass.camera
            positionAo = mrtPass.positionAo
            normalRoughness = mrtPass.normalRoughness
            albedoMetal = mrtPass.albedoMetal
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
        val nInstances = min(lightInstances.size, lightInstanceData.maxInstances)
        lightInstanceData.addInstances(nInstances) { buf ->
            for (i in 0 until nInstances) {
                encodeLight(lightInstances[i])
                buf.put(modelMat.matrix)
                buf.put(encodedLightData)
            }
        }
    }

    private fun encodeLight(light: SpotLight) {
        modelMat.setIdentity()
        modelMat.translate(light.position)
        val soi = sqrt(light.intensity)
        modelMat.scale(soi, soi, soi)
        modelMat.rotate(light.orientation)

        tmpLightDir.set(Vec3f.X_AXIS)
        light.orientation.transform(tmpLightDir)

        encodedLightData[0] = light.position.x
        encodedLightData[1] = light.position.y
        encodedLightData[2] = light.position.z
        encodedLightData[3] = Light.Type.SPOT.encoded

        encodedLightData[4] = tmpLightDir.x
        encodedLightData[5] = tmpLightDir.y
        encodedLightData[6] = tmpLightDir.z
        encodedLightData[7] = cos((min(maxSpotAngle, light.spotAngle) / 2).toRad())

        encodedLightData[8] = light.color.r
        encodedLightData[9] = light.color.g
        encodedLightData[10] = light.color.b
        encodedLightData[11] = light.intensity
    }

    fun addSpotLight(spotLight: SpotLight) {
        lightInstances += spotLight
    }

    inline fun addSpotLight(block: SpotLight.() -> Unit): SpotLight {
        val light = SpotLight()
        light.block()
        addSpotLight(light)
        return light
    }

    fun removeSpotLight(light: SpotLight) {
        lightInstances -= light
    }

    class SpotLight {
        val position = MutableVec3f()
        val orientation = Mat3f()
        var spotAngle = 60f
        val color = MutableColor(Color.WHITE)
        var intensity = 1f

        fun setDirection(direction: Vec3f) {
            val v = if (abs(direction.dot(Vec3f.Y_AXIS)) > 0.9f) {
                Vec3f.X_AXIS
            } else {
                Vec3f.Y_AXIS
            }

            val b = direction.cross(v, MutableVec3f())
            val c = direction.cross(b, MutableVec3f())
            orientation.setColVec(0, direction)
            orientation.setColVec(1, b)
            orientation.setColVec(2, c)
        }
    }

    private fun MeshBuilder.makeHalfSphereCone(angle: Float, radius: Float) {
        val cAng = angle.clamp(0f, 360f) / 2f
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
                geometry.addTriIndices(iCenter, iv, iv-1)
            }
            if (i == steps-1) {
                geometry.addTriIndices(iCenter, iv-steps+1, iv)
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
}