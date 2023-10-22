package de.fabmax.kool.scene

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.logW

/**
 * @author fabmax
 */
class Lighting {
    private val _lights = mutableListOf<Light>()
    val lights: List<Light>
        get() = _lights

    var maxNumberOfLights = 4

    init {
        singleDirectionalLight {
            setup(Vec3f(-0.8f, -1.2f, -1f)).setColor(Color.WHITE, 1f)
        }
    }

    fun onUpdate(updateEvent: RenderPass.UpdateEvent) {
        // if lights are not attached to the scene graph, their update function is not called, do this here instead
        for (i in lights.indices) {
            if (lights[i].parent == null) {
                lights[i].update(updateEvent)
            }
        }
    }

    fun addLight(light: Light) {
        if (light in _lights) {
            logW { "light is already present in lights list" }
            return
        }
        if (_lights.size >= maxNumberOfLights) {
            logW { "Unable to add light: Maximum number of lights (${maxNumberOfLights}) reached. Consider increasing Scene.lighting.maxNumberOfLights" }
            return
        }
        light.lightIndex = lights.size
        _lights += light
    }

    inline fun addDirectionalLight(block: Light.Directional.() -> Unit): Light.Directional {
        val light = Light.Directional()
        light.block()
        addLight(light)
        return light
    }

    inline fun addSpotLight(block: Light.Spot.() -> Unit): Light.Spot {
        val light = Light.Spot()
        light.block()
        addLight(light)
        return light
    }

    inline fun addPointLight(block: Light.Point.() -> Unit): Light.Point {
        val light = Light.Point()
        light.block()
        addLight(light)
        return light
    }

    fun removeLight(light: Light) {
        _lights -= light
        light.lightIndex = -1
        lights.forEachIndexed { i, it ->
            it.lightIndex = i
        }
    }

    fun clear() {
        lights.forEach { it.lightIndex = -1 }
        _lights.clear()
    }

    fun singleDirectionalLight(block: Light.Directional.() -> Unit): Light {
        clear()
        addLight(Light.Directional().apply(block))
        return lights[0]
    }

    fun singlePointLight(block: Light.Point.() -> Unit): Light {
        clear()
        addLight(Light.Point().apply(block))
        return lights[0]
    }

    fun singleSpotLight(block: Light.Spot.() -> Unit): Light {
        clear()
        addLight(Light.Spot().apply(block))
        return lights[0]
    }
}
