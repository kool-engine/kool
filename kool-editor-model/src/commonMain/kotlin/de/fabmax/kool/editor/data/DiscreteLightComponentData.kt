package de.fabmax.kool.editor.data

import de.fabmax.kool.math.deg
import de.fabmax.kool.scene.Light
import de.fabmax.kool.util.Color
import kotlinx.serialization.Serializable

@Serializable
data class DiscreteLightComponentData(val light: LightTypeData) : ComponentData

@Serializable
sealed class LightTypeData {
    abstract val name: String
    abstract val color: ColorData
    abstract val intensity: Float

    abstract fun createLight(): Light

    abstract fun updateLight(existingLight: Light): Boolean

    @Serializable
    data class Directional(
        override val color: ColorData = ColorData(Color.WHITE),
        override val intensity: Float = 1.5f,
    ) : LightTypeData() {
        override val name: String get() = "Directional-Light"

        override fun createLight(): Light.Directional = Light.Directional().also { updateLight(it) }

        override fun updateLight(existingLight: Light): Boolean {
            val light = existingLight as? Light.Directional ?: return false
            light.name = name
            light.setColor(color.toColorLinear(), intensity)
            return true
        }
    }

    @Serializable
    data class Point(
        override val color: ColorData = ColorData(Color.WHITE),
        override val intensity: Float = 1000f,
    ) : LightTypeData() {
        override val name: String get() = "Point-Light"

        override fun createLight(): Light.Point = Light.Point().also { updateLight(it) }

        override fun updateLight(existingLight: Light): Boolean {
            val light = existingLight as? Light.Point ?: return false
            light.name = name
            light.setColor(color.toColorLinear(), intensity)
            return true
        }
    }

    @Serializable
    data class Spot(
        override val color: ColorData = ColorData(Color.WHITE),
        override val intensity: Float = 1000f,
        val spotAngle: Float = 60f,
        val coreRatio: Float = 0.5f,
    ) : LightTypeData() {
        override val name: String get() = "Spot-Light"

        override fun createLight(): Light.Spot = Light.Spot().also { updateLight(it) }

        override fun updateLight(existingLight: Light): Boolean {
            val light = existingLight as? Light.Spot ?: return false
            light.name = name
            light.spotAngle = spotAngle.deg
            light.coreRatio = coreRatio
            light.setColor(color.toColorLinear(), intensity)
            return true
        }
    }
}