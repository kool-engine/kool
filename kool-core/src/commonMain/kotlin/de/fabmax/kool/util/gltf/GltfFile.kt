package de.fabmax.kool.util.gltf

import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.math.Mat4dStack
import de.fabmax.kool.math.Vec4d
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.Model
import de.fabmax.kool.scene.TransformGroup
import de.fabmax.kool.util.Color
import kotlinx.serialization.Serializable
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.parse

@Serializable
data class GltfFile(
        val asset: Asset,
        val scene: Int = 0,
        val scenes: List<Scene> = emptyList(),
        val nodes: List<Node> = emptyList(),
        val meshes: List<Mesh> = emptyList(),
        val materials: List<Material> = emptyList(),
        val textures: List<Texture> = emptyList(),
        val images: List<Image> = emptyList(),
        val buffers: List<Buffer> = emptyList(),
        val bufferViews: List<BufferView> = emptyList(),
        val accessors: List<Accessor> = emptyList(),
        val extensionsUsed: List<String> = emptyList(),
        val extensionsRequired: List<String> = emptyList()
) {

    fun makeModel(modelCfg: ModelGenerateConfig = ModelGenerateConfig(), scene: Int = this.scene): Model {
        return ModelGenerator(modelCfg).makeModel(scenes[scene])
    }

    internal fun updateReferences() {
        accessors.forEach { it.bufferViewRef = bufferViews[it.bufferView] }
        bufferViews.forEach { it.bufferRef = buffers[it.buffer] }
        meshes.forEach { mesh ->
            mesh.primitives.forEach {
                if (it.material >= 0) {
                    it.materialRef = materials[it.material]
                }
                if (it.indices >= 0) {
                    it.indexAccessorRef = accessors[it.indices]
                }
                it.attributes.forEach { (attrib, iAcc) ->
                    it.attribAccessorRefs[attrib] = accessors[iAcc]
                }
            }
        }
        scenes.forEach { it.nodeRefs = it.nodes.map { iNd -> nodes[iNd] } }
        nodes.forEach {
            it.childRefs = it.children.map { iNd -> nodes[iNd] }
            if (it.mesh >= 0) {
                it.meshRef = meshes[it.mesh]
            }
        }
        textures.forEach { it.imageRef = images[it.source] }
        images.filter { it.bufferView >= 0 }.forEach { it.bufferViewRef = bufferViews[it.bufferView] }
    }

    class ModelGenerateConfig(
            val generateNormals: Boolean = false,
            val applyTransforms: Boolean = false,
            val mergeMeshesByMaterial: Boolean = false,
            val applyMaterials: Boolean = true,
            val pbrBlock: (PbrShader.PbrConfig.(MeshPrimitive) -> Unit)? = null
    )

    private inner class ModelGenerator(val cfg: ModelGenerateConfig) {
        val meshesByMaterial = mutableMapOf<Int, MutableSet<de.fabmax.kool.scene.Mesh>>()

        fun makeModel(scene: Scene): Model {
            val model = Model(scene.name)
            scene.nodeRefs.forEach { nd ->
                model += nd.makeNode(model, cfg)
            }
            if (cfg.applyTransforms) {
                applyTransforms(model)
            }
            if (cfg.mergeMeshesByMaterial) {
                mergeMeshesByMaterial(model)
            }
            model.sortByAlpha()
            return model
        }

        private fun TransformGroup.sortByAlpha() {
            children.filterIsInstance<TransformGroup>().forEach { it.sortByAlpha() }
            sortChildrenBy {
                var a = 1.1f
                if (it is de.fabmax.kool.scene.Mesh) {
                    a = (it.pipelineLoader as? PbrShader)?.albedo?.a ?: 0f
                }
                -a
            }
        }

            private fun mergeMeshesByMaterial(model: Model) {
            model.mergeMeshesByMaterial()
        }

        private fun TransformGroup.mergeMeshesByMaterial() {
            children.filterIsInstance<TransformGroup>().forEach { it.mergeMeshesByMaterial() }

            meshesByMaterial.values.forEach { sameMatMeshes ->
                val mergeMeshes = children.filter { it in sameMatMeshes }.map { it as de.fabmax.kool.scene.Mesh }
                if (mergeMeshes.size > 1) {
                    val r = mergeMeshes[0]
                    for (i in 1 until mergeMeshes.size) {
                        val m = mergeMeshes[i]
                        r.geometry.addGeometry(m.geometry)
                        removeNode(m)
                    }
                }
            }
        }

        private fun applyTransforms(model: Model) {
            val transform = Mat4dStack()
            transform.setIdentity()
            model.applyTransforms(transform, model)
        }

        private fun TransformGroup.applyTransforms(transform: Mat4dStack, rootGroup: TransformGroup) {
            transform.push()
            transform.mul(this.transform)

            children.filterIsInstance<de.fabmax.kool.scene.Mesh>().forEach {
                it.geometry.batchUpdate(true) {
                    forEach { v ->
                        transform.transform(v.position, 1f)
                        transform.transform(v.normal, 0f)
                        transform.transform(v.tangent, 0f)
                    }
                }
                if (rootGroup != this) {
                    rootGroup += it
                }
            }

            val childGroups = children.filterIsInstance<TransformGroup>()
            childGroups.forEach {
                it.applyTransforms(transform, rootGroup)
                removeNode(it)
            }

            transform.pop()
        }

        private fun Node.makeNode(model: Model, cfg: ModelGenerateConfig): TransformGroup {
            val nodeGrp = TransformGroup(name)
            model.nodes[name] = nodeGrp

            if (matrix != null) {
                nodeGrp.transform.set(matrix.map { it.toDouble() })
            } else {
                if (translation != null) {
                    nodeGrp.translate(translation[0], translation[1], translation[2])
                }
                if (rotation != null) {
                    val rotMat = Mat4d().setRotate(Vec4d(rotation[0].toDouble(), rotation[1].toDouble(), rotation[2].toDouble(), rotation[3].toDouble()))
                    nodeGrp.transform.mul(rotMat)
                }
                if (scale != null) {
                    nodeGrp.scale(scale[0], scale[1], scale[2])
                }
            }

            childRefs.forEach {
                nodeGrp += it.makeNode(model, cfg)
            }

            meshRef?.primitives?.forEachIndexed { index, p ->
                val name = "${meshRef?.name ?: name}_$index"
                val mesh = de.fabmax.kool.scene.Mesh(p.toGeometry(cfg.generateNormals), name)
                nodeGrp += mesh

                meshesByMaterial.getOrPut(p.material) { mutableSetOf() } += mesh

                if (cfg.applyMaterials) {
                    val useVertexColor = p.attributes.containsKey(MESH_ATTRIBUTE_COLOR_0)
                    mesh.pipelineLoader = pbrShader {
                        val material = p.materialRef
                        if (material != null) {
                            material.applyTo(this, useVertexColor, this@GltfFile)
                        } else {
                            albedo = Color.GRAY
                            albedoSource = Albedo.STATIC_ALBEDO
                        }
                        cfg.pbrBlock?.invoke(this, p)

                        albedoMap?.let { model.textures[it.name ?: "tex_${model.textures.size}"] = it }
                        normalMap?.let { model.textures[it.name ?: "tex_${model.textures.size}"] = it }
                        roughnessMap?.let { model.textures[it.name ?: "tex_${model.textures.size}"] = it }
                        metallicMap?.let { model.textures[it.name ?: "tex_${model.textures.size}"] = it }
                        ambientOcclusionMap?.let { model.textures[it.name ?: "tex_${model.textures.size}"] = it }
                        displacementMap?.let { model.textures[it.name ?: "tex_${model.textures.size}"] = it }
                    }
                }
                model.meshes[name] = mesh
            }

            return nodeGrp
        }
    }

    companion object {
        const val ACCESSOR_TYPE_SCALAR = "SCALAR"
        const val ACCESSOR_TYPE_VEC2 = "VEC2"
        const val ACCESSOR_TYPE_VEC3 = "VEC3"
        const val ACCESSOR_TYPE_VEC4 = "VEC4"

        const val COMP_TYPE_BYTE = 5120
        const val COMP_TYPE_UNSIGNED_BYTE = 5121
        const val COMP_TYPE_SHORT = 5122
        const val COMP_TYPE_UNSIGNED_SHORT = 5123
        const val COMP_TYPE_INT = 5124
        const val COMP_TYPE_UNSIGNED_INT = 5125
        const val COMP_TYPE_FLOAT = 5126

        const val MODE_POINTS = 0
        const val MODE_LINES = 1
        const val MODE_LINE_LOOP = 2
        const val MODE_LINE_STRIP = 3
        const val MODE_TRIANGLES = 4
        const val MODE_TRIANGLE_STRIP = 5
        const val MODE_TRIANGLE_FAN = 6
        const val MODE_QUADS = 7
        const val MODE_QUAD_STRIP = 8
        const val MODE_POLYGON = 9

        const val MESH_ATTRIBUTE_POSITION = "POSITION"
        const val MESH_ATTRIBUTE_NORMAL = "NORMAL"
        const val MESH_ATTRIBUTE_TANGENT = "TANGENT"
        const val MESH_ATTRIBUTE_TEXCOORD_0 = "TEXCOORD_0"
        const val MESH_ATTRIBUTE_TEXCOORD_1 = "TEXCOORD_1"
        const val MESH_ATTRIBUTE_COLOR_0 = "COLOR_0"
        const val MESH_ATTRIBUTE_JOINTS_0 = "JOINTS_0"
        const val MESH_ATTRIBUTE_WEIGHTS_0 = "WEIGHTS_0"

        const val GLB_FILE_MAGIC = 0x46546c67
        const val GLB_CHUNK_MAGIC_JSON = 0x4e4f534a
        const val GLB_CHUNK_MAGIC_BIN = 0x004e4942

        @OptIn(UnstableDefault::class)
        fun fromJson(json: String): GltfFile {
            return Json(JsonConfiguration(
                    isLenient = true,
                    ignoreUnknownKeys = true,
                    serializeSpecialFloatingPointValues = true,
                    useArrayPolymorphism = true
            )).parse(json)
        }
    }
}

