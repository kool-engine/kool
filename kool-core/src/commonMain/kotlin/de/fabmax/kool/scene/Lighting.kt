package de.fabmax.kool.scene

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.Color

/**
 * @author fabmax
 */
class Lighting {
    val lights = mutableListOf<Light>()

    init {
        singleDirectionalLight {
            setup(Vec3f(-0.8f, -1.2f, -1f)).setColor(Color.WHITE, 1f)
        }
    }

    fun addLight(light: Light) {
        light.lightIndex = lights.size
        lights += light
    }

    fun removeLight(light: Light) {
        lights -= light
        light.lightIndex = -1
        lights.forEachIndexed { i, it ->
            it.lightIndex = i
        }
    }

    fun clear() {
        lights.forEach { it.lightIndex = -1 }
        lights.clear()
    }

    fun singleDirectionalLight(block: Light.Directional.() -> Unit) {
        lights.clear()
        addLight(Light.Directional().apply(block))
    }

    fun singlePointLight(block: Light.Point.() -> Unit) {
        lights.clear()
        addLight(Light.Point().apply(block))
    }

    fun singleSpotLight(block: Light.Spot.() -> Unit) {
        lights.clear()
        addLight(Light.Spot().apply(block))
    }
}
