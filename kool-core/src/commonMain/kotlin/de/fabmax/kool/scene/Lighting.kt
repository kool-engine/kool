package de.fabmax.kool.scene

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.Color

/**
 * @author fabmax
 */
class Lighting {
    val lights = mutableListOf<Light>(Light.Directional().setup(Vec3f(-0.8f, -1.2f, -1f)).setColor(Color.WHITE, 1f))

    fun clear() {
        lights.clear()
    }

    fun singleDirectionalLight(block: Light.Directional.() -> Unit) {
        lights.clear()
        lights += Light.Directional().apply(block)
    }

    fun singlePointLight(block: Light.Point.() -> Unit) {
        lights.clear()
        lights += Light.Point().apply(block)
    }

    fun singleSpotLight(block: Light.Spot.() -> Unit) {
        lights.clear()
        lights += Light.Spot().apply(block)
    }
}
