package de.fabmax.kool.util.serialization

import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MutableColor
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoId

@Serializable
data class MaterialData(
        @ProtoId(1) val name: String = "",
        @ProtoId(2) val ambientColor: List<Float> = emptyList(),
        @ProtoId(3) val diffuseColor: List<Float> = emptyList(),
        @ProtoId(4) val specularColor: List<Float> = emptyList(),
        @ProtoId(5) val emissiveColor: List<Float> = emptyList(),
        @ProtoId(6) val shininess: Float = 10f,
        @ProtoId(7) val reflectivity: Float = 0f
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