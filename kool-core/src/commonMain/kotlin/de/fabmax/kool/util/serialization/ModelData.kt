package de.fabmax.kool.util.serialization

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.TransformGroup
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logW
import kotlinx.serialization.Serializable
import kotlinx.serialization.load
import kotlinx.serialization.protobuf.ProtoBuf
import kotlinx.serialization.protobuf.ProtoId

@Serializable
data class ModelData(
        /**
         * Version indicator should always be equal to [ModelData.VERSION]
         */
        @ProtoId(1) val version: Int,

        /**
         * Optional model name. Empty if not set.
         */
        @ProtoId(5) val name: String = "",

        /**
         * List of meshes in this model. Meshes are referenced by index from [ModelNodeData].
         */
        @ProtoId(2) val meshes: List<ModelMeshData>,

        /**
         * Root nodes of the model hierarchy. Multiple root nodes correspond to different levels of detail.
         */
        @ProtoId(3) val lodRootNodes: List<ModelNodeData>,

        /**
         * List of materials in this model. Materials are references by index from [ModelMeshData]
         */
        @ProtoId(4) val materials: List<MaterialData> = emptyList()
) {

    fun toModel(lod: Int = 0): TransformGroup = toModel(lod, { TransformGroup(it.name).set(it.getTransformMatrix()) }, { it.toMesh(this) })

    fun <G: Group, M: Node> toModel(lod: Int, nodeMapper: (ModelNodeData) -> G, meshMapper: (ModelMeshData) -> M?): G {
        return lodRootNodes[lod].toGroup(nodeMapper, meshMapper)
    }

    private fun <G: Group, M: Node> ModelNodeData.toGroup(nodeMapper: (ModelNodeData) -> G, meshMapper: (ModelMeshData) -> M?): G {
        val group = nodeMapper(this)
        for (child in children) {
            group += child.toGroup(nodeMapper, meshMapper)
        }
        for (meshIdx in meshes) {
            if (meshIdx in this@ModelData.meshes.indices) {
                val mesh = meshMapper(this@ModelData.meshes[meshIdx])
                if (mesh != null) {
                    group += mesh
                }
            } else {
                logE { "Invalid mesh index: $meshIdx" }
            }
        }

        // add tags
        if (tags.isNotEmpty()) {
            tags.forEach {
                group.tags += it
            }
        }
        return group
    }

    companion object {
        const val VERSION = 2

        fun load(data: Uint8Buffer): ModelData {
            val model = ProtoBuf.load<ModelData>(data.toArray())
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
        @ProtoId(1) val name: String,

        /**
         * 4x4 transform matrix
         */
        @ProtoId(2) val transform: List<Float>,

        /**
         * Child nodes
         */
        @ProtoId(3) val children: List<ModelNodeData> = emptyList(),

        /**
         * Index list of included meshes
         */
        @ProtoId(4) val meshes: List<Int> = emptyList(),

        /**
         * Optional list of arbitrary tags.
         */
        @ProtoId(5) val tags: List<String> = emptyList()
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