package de.fabmax.kool.editor.data

import de.fabmax.kool.math.deg
import de.fabmax.kool.scene.Light
import de.fabmax.kool.util.Color
import kotlinx.serialization.Serializable

@Serializable
class DiscreteLightComponentData(var light: LightTypeData) : ComponentData

@Serializable
sealed class LightTypeData {
    abstract val name: String
    abstract val color: ColorData
    abstract val intensity: Float

    abstract fun createLight(): Light

    open fun updateOrCreateLight(existingLight: Light): Light {
        existingLight.name = name
        existingLight.setColor(color.toColorLinear(), intensity)
        return existingLight
    }

    @Serializable
    data class Directional(
        override val color: ColorData = ColorData(Color.WHITE),
        override val intensity: Float = 1.5f,
    ) : LightTypeData() {
        override val name: String get() = "Directional-Light"

        override fun createLight(): Light.Directional = updateOrCreateLight(Light.Directional())

        override fun updateOrCreateLight(existingLight: Light): Light.Directional {
            val dirLight = if (existingLight is Light.Directional) existingLight else createLight()
            super.updateOrCreateLight(dirLight)
            return dirLight
        }
    }

    @Serializable
    data class Point(
        override val color: ColorData = ColorData(Color.WHITE),
        override val intensity: Float = 1000f,
    ) : LightTypeData() {
        override val name: String get() = "Point-Light"

        override fun createLight(): Light.Point = updateOrCreateLight(Light.Point())

        override fun updateOrCreateLight(existingLight: Light): Light.Point {
            val pointLight = if (existingLight is Light.Point) existingLight else createLight()
            super.updateOrCreateLight(pointLight)
            return pointLight
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

        override fun createLight(): Light.Spot = updateOrCreateLight(Light.Spot())

        override fun updateOrCreateLight(existingLight: Light): Light.Spot {
            val spotLight = if (existingLight is Light.Spot) existingLight else createLight()
            spotLight.spotAngle = spotAngle.deg
            spotLight.coreRatio = coreRatio
            super.updateOrCreateLight(spotLight)
            return spotLight
        }
    }
}