package de.fabmax.kool.util.serialization

import kotlinx.serialization.Optional
import kotlinx.serialization.SerialId
import kotlinx.serialization.Serializable

@Serializable
data class BoneData(
        @SerialId(1) val name: String,

        @SerialId(2) val parent: String,
        @SerialId(3) @Optional val children: List<String> = emptyList(),

        @SerialId(4) val offsetMatrix: List<Float>,
        @SerialId(5) val vertexIds: List<Int>,
        @SerialId(6) val vertexWeights: List<Float>
)