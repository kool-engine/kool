package de.fabmax.kool.util.serialization

import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MutableColor
import kotlinx.serialization.Optional
import kotlinx.serialization.SerialId
import kotlinx.serialization.Serializable

@Serializable
data class MaterialData(
        @SerialId(1) @Optional val name: String = "",

        @SerialId(2) @Optional val ambientColor: List<Float> = emptyList(),

        @SerialId(3) @Optional val diffuseColor: List<Float> = emptyList(),

        @SerialId(4) @Optional val specularColor: List<Float> = emptyList(),

        @SerialId(5) @Optional val emissiveColor: List<Float> = emptyList(),

        @SerialId(6) @Optional val shininess: Float = 10f,

        @SerialId(7) @Optional val reflectivity: Float = 0f
) {

    fun getAmbientColor(result: MutableColor = MutableColor()): MutableColor = ambientColor.getColor(result)
    fun getDiffuseColor(result: MutableColor = MutableColor()): MutableColor = diffuseColor.getColor(result)
    fun getSpecularColor(result: MutableColor = MutableColor()): MutableColor = specularColor.getColor(result)
    fun getEmissiveColor(result: MutableColor = MutableColor()): MutableColor = emissiveColor.getColor(result)

    private fun List<Float>.getColor(result: MutableColor): MutableColor {
        when {
            isEmpty() -> result.set(Color.GRAY)
            size == 3 -> result.set(this[0], this[1], this[2], 1f)
            size == 4 -> result.set(this[0], this[1], this[2], this[3])
        }
        return result
    }
}