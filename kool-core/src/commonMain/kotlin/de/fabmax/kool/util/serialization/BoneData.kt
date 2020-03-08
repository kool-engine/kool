package de.fabmax.kool.util.serialization

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoId

@Serializable
data class BoneData(
        @ProtoId(1) val name: String,

        @ProtoId(2) val parent: String,
        @ProtoId(3) val children: List<String> = emptyList(),

        @ProtoId(4) val offsetMatrix: List<Float>,
        @ProtoId(5) val vertexIds: List<Int>,
        @ProtoId(6) val vertexWeights: List<Float>
)