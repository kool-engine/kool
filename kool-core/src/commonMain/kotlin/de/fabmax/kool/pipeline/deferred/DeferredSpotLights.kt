package de.fabmax.kool.pipeline.deferred

import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.scene.Light
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.scene.geometry.MeshBuilder
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MutableColor
import kotlin.math.*


class DeferredSpotLights(val maxSpotAngle: AngleF) {
    val lightInstances = mutableListOf<SpotLight>()
    var isDynamic = true

    private val lightInstanceData = MeshInstanceList(listOf(
        Attribute.INSTANCE_MODEL_MAT,
        DeferredLightShader.LIGHT_POS,
        DeferredLightShader.LIGHT_DIR,
        Attribute.COLORS
    ), 10000)

    private val modelMat = MutableMat4f()
    private val encodedLightData = FloatArray(12)
    private val tmpLightDir = MutableVec3f()

    val lightShader = DeferredLightShader(Light.Spot.ENCODING)

    val mesh = Mesh(
        Attribute.POSITIONS,
        instances = lightInstanceData,
        name = "DeferredSpotLights"
    ).apply {
        isFrustumChecked = false

        generate {
            makeHalfSphereCone(maxSpotAngle, 1.17f)
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
                encodeLight(lightInstances[i])
                modelMat.putTo(buf)
                buf.put(encodedLightData)
            }
        }
    }

    private fun encodeLight(light: SpotLight) {
        modelMat
            .setIdentity()
            .translate(light.position)
            .rotate(light.rotation)
        modelMat.transform(tmpLightDir.set(Vec3f.X_AXIS), 0f)
        modelMat.scale(light.radius)

        encodedLightData[0] = light.position.x
        encodedLightData[1] = light.position.y
        encodedLightData[2] = light.position.z
        encodedLightData[3] = Light.Spot.ENCODING

        encodedLightData[4] = tmpLightDir.x
        encodedLightData[5] = tmpLightDir.y
        encodedLightData[6] = tmpLightDir.z
        encodedLightData[7] = cos(min(maxSpotAngle.rad, light.spotAngle.rad) / 2f)

        encodedLightData[8] = light.color.r * light.intensity
        encodedLightData[9] = light.color.g * light.intensity
        encodedLightData[10] = light.color.b * light.intensity
        encodedLightData[11] = light.coreRatio
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
        val rotation = MutableQuatF()
        var spotAngle = 60f.deg
        var coreRatio = 0.5f
        val color = MutableColor(Color.WHITE)
        var radius = 1f
        var intensity = 1f

        fun setDirection(direction: Vec3f) {
            val v = if (abs(direction.dot(Vec3f.Y_AXIS)) > 0.9f) {
                Vec3f.X_AXIS
            } else {
                Vec3f.Y_AXIS
            }

            val b = direction.cross(v, MutableVec3f())
            val c = direction.cross(b, MutableVec3f())
            Mat3f(direction, b, c).getRotation(rotation)
        }
    }

    private fun MeshBuilder.makeHalfSphereCone(angle: AngleF, radius: Float) {
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
}