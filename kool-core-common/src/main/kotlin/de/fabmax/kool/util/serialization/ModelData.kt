package de.fabmax.kool.util.serialization

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.util.logW
import kotlinx.serialization.Optional
import kotlinx.serialization.SerialId
import kotlinx.serialization.Serializable

@Serializable
data class ModelData(
        /**
         * Version indicator should always be equal to [ModelData.VERSION]
         */
        @SerialId(1) val version: Int,

        /**
         * List of meshes in this model. Meshes are referenced by index from [ModelNodeData].
         */
        @SerialId(2) val meshes: List<MeshData>,

        /**
         * Root nodes of the model hierarchy. Multiple root nodes correspond to different levels of detail.
         */
        @SerialId(3) val lodRootNode: List<ModelNodeData>,

        /**
         * List of materials in this model. Materials are references by index from [MeshData]
         */
        @SerialId(4) val materials: List<MaterialData>
) {
    companion object {
        const val VERSION = 1

        fun load(data: ByteArray): ModelData {
            val model = ProtoBufPacked.load<ModelData>(data)
            if (model.version != VERSION) {
                logW { "Unsupported model version: ${model.version} (should be $VERSION)" }
            }
            return model
        }
    }
}

@Serializable
data class ModelNodeData(
        /**
         * Node name.
         */
        @SerialId(1) val name: String,

        /**
         * 4x4 transform matrix
         */
        @SerialId(2) val transform: List<Float>,

        /**
         * Child nodes
         */
        @SerialId(3) @Optional val children: List<ModelNodeData> = emptyList(),

        /**
         * Index list of included meshes
         */
        @SerialId(4) @Optional val meshes: List<Int> = emptyList(),

        /**
         * Optional list of arbitrary tags.
         */
        @SerialId(5) @Optional val tags: List<String> = emptyList()
) {
    fun getTransformMatrix(result: Mat4f = Mat4f()): Mat4f = result.set(transform)

    fun printNodeHierarchy(model: ModelData, indent: String = "") {
        println("$indent+$name")
        children.forEach { it.printNodeHierarchy(model, "$indent  ") }
        meshes.forEach { println("$indent  -${model.meshes[it].name}") }
    }

    fun countMeshesBelow(): Int {
        return meshes.size + children.sumBy { it.countMeshesBelow() }
    }
}