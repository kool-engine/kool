package de.fabmax.kool.editor.data

import de.fabmax.kool.util.Color
import kotlinx.serialization.Serializable

@Serializable
class DiscreteLightComponentData(var light: LightTypeData) : ComponentData

@Serializable
sealed class LightTypeData {
    abstract val name: String
    abstract val color: ColorData
    abstract val intensity: Float

    @Serializable
    class Directional(
        override val color: ColorData = ColorData(Color.WHITE),
        override val intensity: Float = 1.5f,
    ) : LightTypeData() {
        override val name: String get() = "Directional-Light"
    }

    @Serializable
    class Point(
        override val color: ColorData = ColorData(Color.WHITE),
        override val intensity: Float = 1000f,
    ) : LightTypeData() {
        override val name: String get() = "Point-Light"
    }

    @Serializable
    class Spot(
        override val color: ColorData = ColorData(Color.WHITE),
        override val intensity: Float = 1000f,
        val spotAngle: Float = 60f,
        val coreRatio: Float = 0.5f,
    ) : LightTypeData() {
        override val name: String get() = "Spot-Light"
    }
}