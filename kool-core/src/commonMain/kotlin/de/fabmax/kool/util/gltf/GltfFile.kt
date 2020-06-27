package de.fabmax.kool.util.gltf

import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.math.Mat4dStack
import de.fabmax.kool.math.Vec4d
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.Model
import de.fabmax.kool.scene.TransformGroup
import de.fabmax.kool.scene.animation.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.logW
import kotlinx.serialization.Serializable
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.parse
import kotlin.math.min

/**
 * The root object for a glTF asset.
 *
 * @param extensionsUsed     Names of glTF extensions used somewhere in this asset.
 * @param extensionsRequired Names of glTF extensions required to properly load this asset.
 * @param accessors          An array of accessors.
 * @param animations         An array of keyframe animations.
 * @param asset              Metadata about the glTF asset.
 * @param buffers            An array of buffers.
 * @param bufferViews        An array of bufferViews.
 * @param images             An array of images.
 * @param materials          An array of materials.
 * @param meshes             An array of meshes.
 * @param nodes              An array of nodes.
 * @param scene              The index of the default scene
 * @param scenes             An array of scenes.
 * @param textures           An array of textures.
 */
@Serializable
data class GltfFile(
        val extensionsUsed: List<String> = emptyList(),
        val extensionsRequired: List<String> = emptyList(),
        val accessors: List<Accessor> = emptyList(),
        val animations: List<Animation> = emptyList(),
        val asset: Asset,
        val buffers: List<Buffer> = emptyList(),
        val bufferViews: List<BufferView> = emptyList(),
        //val cameras List<Camera> = emptyList(),
        val images: List<Image> = emptyList(),
        val materials: List<Material> = emptyList(),
        val meshes: List<Mesh> = emptyList(),
        val nodes: List<Node> = emptyList(),
        //val samplers: List<Sampler> = emptyList(),
        val scene: Int = 0,
        val scenes: List<Scene> = emptyList(),
        //val skins: List<Skin> = emptyList(),
        val textures: List<Texture> = emptyList()
) {

    fun makeModel(modelCfg: ModelGenerateConfig = ModelGenerateConfig(), scene: Int = this.scene): Model {
        return ModelGenerator(modelCfg).makeModel(scenes[scene])
    }

    internal fun updateReferences() {
        accessors.forEach { it.bufferViewRef = bufferViews[it.bufferView] }
        animations.forEach { anim ->
            anim.samplers.forEach {
                it.inputAccessorRef = accessors[it.input]
                it.outputAccessorRef = accessors[it.output]
            }
            anim.channels.forEach {
                it.samplerRef = anim.samplers[it.sampler]
                if (it.target.node >= 0) {
                    it.target.nodeRef = nodes[it.target.node]
                }
            }
        }
        bufferViews.forEach { it.bufferRef = buffers[it.buffer] }
        images.filter { it.bufferView >= 0 }.forEach { it.bufferViewRef = bufferViews[it.bufferView] }
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
        nodes.forEach {
            it.childRefs = it.children.map { iNd -> nodes[iNd] }
            if (it.mesh >= 0) {
                it.meshRef = meshes[it.mesh]
            }
        }
        scenes.forEach { it.nodeRefs = it.nodes.map { iNd -> nodes[iNd] } }
        textures.forEach { it.imageRef = images[it.source] }
    }

    class ModelGenerateConfig(
            val generateNormals: Boolean = false,
            val loadAnimations: Boolean = true,
            val applyTransforms: Boolean = false,
            val mergeMeshesByMaterial: Boolean = false,
            val sortNodesByAlpha: Boolean = true,
            val applyMaterials: Boolean = true,
            val pbrBlock: (PbrShader.PbrConfig.(MeshPrimitive) -> Unit)? = null
    )

    private inner class ModelGenerator(val cfg: ModelGenerateConfig) {
        val modelNodes = mutableMapOf<Node, TransformGroup>()
        val meshesByMaterial = mutableMapOf<Int, MutableSet<de.fabmax.kool.scene.Mesh>>()
        val meshMaterials = mutableMapOf<de.fabmax.kool.scene.Mesh, Material?>()

        fun makeModel(scene: Scene): Model {
            val model = Model(scene.name)
            scene.nodeRefs.forEach { nd ->
                model += nd.makeNode(model, cfg)
            }
            if (cfg.loadAnimations) {
                makeAnimations(model)
            }
            if (cfg.applyTransforms && model.animations.isEmpty()) {
                applyTransforms(model)
            }
            if (cfg.mergeMeshesByMaterial) {
                mergeMeshesByMaterial(model)
            }
            if (cfg.sortNodesByAlpha) {
                model.sortNodesByAlpha()
            }
            return model
        }

        private fun makeAnimations(model: Model) {
            animations.forEach { anim ->
                val modelAnim = de.fabmax.kool.scene.animation.Animation(anim.name)
                model.animations += modelAnim
                anim.channels.forEach { channel ->
                    when (channel.target.path) {
                        AnimationTarget.PATH_TRANSLATION -> makeTranslationAnimation(channel, modelAnim)
                        AnimationTarget.PATH_ROTATION -> makeRotationAnimation(channel, modelAnim)
                        AnimationTarget.PATH_SCALE -> makeScaleAnimation(channel, modelAnim)
                        AnimationTarget.PATH_WEIGHTS -> logW { "Unsupported animation: weights" }
                    }
                }
                modelAnim.prepareAnimation()
            }
        }

        private fun makeTranslationAnimation(animCh: AnimationChannel, modelAnim: de.fabmax.kool.scene.animation.Animation) {
            val node = animCh.target.nodeRef ?: return
            val nodeGrp = modelNodes[node] ?: return
            val inputAcc = animCh.samplerRef.inputAccessorRef
            val outputAcc = animCh.samplerRef.outputAccessorRef

            if (inputAcc.type != Accessor.TYPE_SCALAR || inputAcc.componentType != Accessor.COMP_TYPE_FLOAT) {
                logW { "Unsupported translation animation input accessor: type = ${inputAcc.type}, component type = ${inputAcc.componentType}, should be SCALAR and 5126 (float)" }
                return
            }
            if (outputAcc.type != Accessor.TYPE_VEC3 || outputAcc.componentType != Accessor.COMP_TYPE_FLOAT) {
                logW { "Unsupported translation animation output accessor: type = ${inputAcc.type}, component type = ${inputAcc.componentType}, should be VEC3 and 5126 (float)" }
                return
            }

            val transChannel = TranslationAnimationChannel("${modelAnim.name}_translation", AnimatedTransformGroup(nodeGrp))
            val interpolation = when (animCh.samplerRef.interpolation) {
                AnimationSampler.INTERPOLATION_STEP -> AnimationKey.Interpolation.STEP
                AnimationSampler.INTERPOLATION_CUBICSPLINE -> AnimationKey.Interpolation.CUBICSPLINE
                else -> AnimationKey.Interpolation.LINEAR
            }
            modelAnim.channels += transChannel

            val inTimes = FloatAccessor(inputAcc)
            val outTranslations = Vec3fAccessor(outputAcc)
            for (i in 0 until min(inputAcc.count, outputAcc.count)) {
                val t = inTimes.next()
                val transKey = if (interpolation == AnimationKey.Interpolation.CUBICSPLINE) {
                    val startTan = outTranslations.nextD()
                    val point = outTranslations.nextD()
                    val endTan = outTranslations.nextD()
                    CubicTranslationKey(t, point, startTan, endTan)
                } else {
                    TranslationKey(t, outTranslations.nextD())
                }
                transKey.interpolation = interpolation
                transChannel.keys[t] = transKey
            }
        }

        private fun makeRotationAnimation(animCh: AnimationChannel, modelAnim: de.fabmax.kool.scene.animation.Animation) {
            val node = animCh.target.nodeRef ?: return
            val nodeGrp = modelNodes[node] ?: return
            val inputAcc = animCh.samplerRef.inputAccessorRef
            val outputAcc = animCh.samplerRef.outputAccessorRef

            if (inputAcc.type != Accessor.TYPE_SCALAR || inputAcc.componentType != Accessor.COMP_TYPE_FLOAT) {
                logW { "Unsupported rotation animation input accessor: type = ${inputAcc.type}, component type = ${inputAcc.componentType}, should be SCALAR and 5126 (float)" }
                return
            }
            if (outputAcc.type != Accessor.TYPE_VEC4 || outputAcc.componentType != Accessor.COMP_TYPE_FLOAT) {
                logW { "Unsupported rotation animation output accessor: type = ${inputAcc.type}, component type = ${inputAcc.componentType}, should be VEC4 and 5126 (float)" }
                return
            }

            val rotChannel = RotationAnimationChannel("${modelAnim.name}_rotation", AnimatedTransformGroup(nodeGrp))
            val interpolation = when (animCh.samplerRef.interpolation) {
                AnimationSampler.INTERPOLATION_STEP -> AnimationKey.Interpolation.STEP
                AnimationSampler.INTERPOLATION_CUBICSPLINE -> AnimationKey.Interpolation.CUBICSPLINE
                else -> AnimationKey.Interpolation.LINEAR
            }
            modelAnim.channels += rotChannel

            val inTimes = FloatAccessor(inputAcc)
            val outRotations = Vec4fAccessor(outputAcc)
            for (i in 0 until min(inputAcc.count, outputAcc.count)) {
                val t = inTimes.next()
                val rotKey = if (interpolation == AnimationKey.Interpolation.CUBICSPLINE) {
                    val startTan = outRotations.nextD()
                    val point = outRotations.nextD()
                    val endTan = outRotations.nextD()
                    CubicRotationKey(t, point, startTan, endTan)
                } else {
                    RotationKey(t, outRotations.nextD())
                }
                rotKey.interpolation = interpolation
                rotChannel.keys[t] = rotKey
            }
        }

        private fun makeScaleAnimation(animCh: AnimationChannel, modelAnim: de.fabmax.kool.scene.animation.Animation) {
            val node = animCh.target.nodeRef ?: return
            val nodeGrp = modelNodes[node] ?: return
            val inputAcc = animCh.samplerRef.inputAccessorRef
            val outputAcc = animCh.samplerRef.outputAccessorRef

            if (inputAcc.type != Accessor.TYPE_SCALAR || inputAcc.componentType != Accessor.COMP_TYPE_FLOAT) {
                logW { "Unsupported scale animation input accessor: type = ${inputAcc.type}, component type = ${inputAcc.componentType}, should be SCALAR and 5126 (float)" }
                return
            }
            if (outputAcc.type != Accessor.TYPE_VEC3 || outputAcc.componentType != Accessor.COMP_TYPE_FLOAT) {
                logW { "Unsupported scale animation output accessor: type = ${inputAcc.type}, component type = ${inputAcc.componentType}, should be VEC3 and 5126 (float)" }
                return
            }

            val scaleChannel = ScaleAnimationChannel("${modelAnim.name}_scale", AnimatedTransformGroup(nodeGrp))
            val interpolation = when (animCh.samplerRef.interpolation) {
                AnimationSampler.INTERPOLATION_STEP -> AnimationKey.Interpolation.STEP
                AnimationSampler.INTERPOLATION_CUBICSPLINE -> AnimationKey.Interpolation.CUBICSPLINE
                else -> AnimationKey.Interpolation.LINEAR
            }
            modelAnim.channels += scaleChannel

            val inTimes = FloatAccessor(inputAcc)
            val outScale = Vec3fAccessor(outputAcc)
            for (i in 0 until min(inputAcc.count, outputAcc.count)) {
                val t = inTimes.next()
                val scaleKey = if (interpolation == AnimationKey.Interpolation.CUBICSPLINE) {
                    val startTan = outScale.nextD()
                    val point = outScale.nextD()
                    val endTan = outScale.nextD()
                    CubicScaleKey(t, point, startTan, endTan)
                } else {
                    ScaleKey(t, outScale.nextD())
                }
                scaleKey.interpolation = interpolation
                scaleChannel.keys[t] = scaleKey
            }
        }

        private fun TransformGroup.sortNodesByAlpha() {
            children.filterIsInstance<TransformGroup>().forEach { it.sortNodesByAlpha() }
            sortChildrenBy {
                var a = 1.1f
                if (it is de.fabmax.kool.scene.Mesh) {
                    val mat = meshMaterials[it]
                    if (mat != null) {
                        a = when (mat.alphaMode) {
                            Material.ALPHA_MODE_BLEND -> min(0.999f, mat.pbrMetallicRoughness.baseColorFactor[3])
                            else -> 1f
                        }
                    }
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
            modelNodes[this] = nodeGrp
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
                val geometry = p.toGeometry(cfg.generateNormals)
                if (!geometry.isEmpty()) {
                    val mesh = de.fabmax.kool.scene.Mesh(geometry, name)
                    nodeGrp += mesh

                    meshesByMaterial.getOrPut(p.material) { mutableSetOf() } += mesh
                    meshMaterials[mesh] = p.materialRef

                    if (cfg.applyMaterials) {
                        val useVertexColor = p.attributes.containsKey(MeshPrimitive.ATTRIBUTE_COLOR_0)
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
            }

            return nodeGrp
        }
    }

    companion object {
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

