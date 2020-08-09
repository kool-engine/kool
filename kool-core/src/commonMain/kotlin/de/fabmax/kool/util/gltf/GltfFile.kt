package de.fabmax.kool.util.gltf

import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.math.Mat4dStack
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec4d
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.AlphaModeBlend
import de.fabmax.kool.pipeline.shading.PbrMaterialConfig
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.Model
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.TransformGroup
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ShadowMap
import de.fabmax.kool.util.animation.*
import de.fabmax.kool.util.deferred.DeferredPbrShader
import de.fabmax.kool.util.ibl.EnvironmentMaps
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
 * @param skins              An array of skins.
 * @param textures           An array of textures.
 */
@Serializable
data class GltfFile(
        val extensionsUsed: List<String> = emptyList(),
        val extensionsRequired: List<String> = emptyList(),
        val accessors: List<GltfAccessor> = emptyList(),
        val animations: List<GltfAnimation> = emptyList(),
        val asset: GltfAsset,
        val buffers: List<GltfBuffer> = emptyList(),
        val bufferViews: List<GltfBufferView> = emptyList(),
        //val cameras List<Camera> = emptyList(),
        val images: List<GltfImage> = emptyList(),
        val materials: List<GltfMaterial> = emptyList(),
        val meshes: List<GltfMesh> = emptyList(),
        val nodes: List<GltfNode> = emptyList(),
        //val samplers: List<Sampler> = emptyList(),
        val scene: Int = 0,
        val scenes: List<GltfScene> = emptyList(),
        val skins: List<GltfSkin> = emptyList(),
        val textures: List<GltfTexture> = emptyList()
) {

    fun makeModel(modelCfg: ModelGenerateConfig = ModelGenerateConfig(), scene: Int = this.scene): Model {
        return ModelGenerator(modelCfg).makeModel(scenes[scene])
    }

    internal fun updateReferences() {
        accessors.forEach {
            if (it.bufferView >= 0) {
                it.bufferViewRef = bufferViews[it.bufferView]
            }
            it.sparse?.let { sparse ->
                sparse.indices.bufferViewRef = bufferViews[sparse.indices.bufferView]
                sparse.values.bufferViewRef = bufferViews[sparse.values.bufferView]
            }
        }
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
            }
        }
        nodes.forEach {
            it.childRefs = it.children.map { iNd -> nodes[iNd] }
            if (it.mesh >= 0) {
                it.meshRef = meshes[it.mesh]
            }
            if (it.skin >= 0) {
                it.skinRef = skins[it.skin]
            }
        }
        scenes.forEach { it.nodeRefs = it.nodes.map { iNd -> nodes[iNd] } }
        skins.forEach {
            if (it.inverseBindMatrices >= 0) {
                it.inverseBindMatrixAccessorRef = accessors[it.inverseBindMatrices]
            }
            it.jointRefs = it.joints.map { iJt -> nodes[iJt] }
        }
        textures.forEach { it.imageRef = images[it.source] }
    }

    class ModelGenerateConfig(
            val generateNormals: Boolean = false,
            val applyMaterials: Boolean = true,
            val materialConfig: ModelMaterialConfig = ModelMaterialConfig(),
            val loadAnimations: Boolean = true,
            val applySkins: Boolean = true,
            val applyMorphTargets: Boolean = true,
            val applyTransforms: Boolean = false,
            val removeEmptyNodes: Boolean = true,
            val mergeMeshesByMaterial: Boolean = false,
            val sortNodesByAlpha: Boolean = true,
            val pbrBlock: (PbrMaterialConfig.(GltfMesh.Primitive) -> Unit)? = null
    )

    class ModelMaterialConfig(
            val shadowMaps: List<ShadowMap> = emptyList(),
            val scrSpcAmbientOcclusionMap: Texture? = null,
            val environmentMaps: EnvironmentMaps? = null,
            val isDeferredShading: Boolean = false
    )

    private inner class ModelGenerator(val cfg: ModelGenerateConfig) {
        val modelAnimations = mutableListOf<Animation>()
        val modelNodes = mutableMapOf<GltfNode, TransformGroup>()
        val meshesByMaterial = mutableMapOf<Int, MutableSet<Mesh>>()
        val meshMaterials = mutableMapOf<Mesh, GltfMaterial?>()

        fun makeModel(scene: GltfScene): Model {
            val model = Model(scene.name ?: "model_scene")
            scene.nodeRefs.forEach { nd -> model += nd.makeNode(model, cfg) }

            if (cfg.loadAnimations) { makeTrsAnimations() }
            if (cfg.loadAnimations) { makeSkins(model) }
            modelNodes.forEach { (node, grp) -> node.createMeshes(model, grp, cfg) }
            if (cfg.loadAnimations) { makeMorphAnimations() }
            modelAnimations.filter { it.channels.isNotEmpty() }.forEach { modelAnim ->
                modelAnim.prepareAnimation()
                model.animations += modelAnim
            }
            model.disableAllAnimations()

            if (cfg.applyTransforms && model.animations.isEmpty()) { applyTransforms(model) }
            if (cfg.mergeMeshesByMaterial) { mergeMeshesByMaterial(model) }
            if (cfg.sortNodesByAlpha) { model.sortNodesByAlpha() }
            if (cfg.removeEmptyNodes) { model.removeEmpty() }

            return model
        }

        private fun TransformGroup.removeEmpty() {
            val tgChildren = children.filterIsInstance<TransformGroup>()
            tgChildren.forEach {
                it.removeEmpty()
                if (it.children.isEmpty()) {
                    removeNode(it)
                }
            }
        }

        private fun makeTrsAnimations() {
            animations.forEach { anim ->
                val modelAnim = Animation(anim.name)
                modelAnimations += modelAnim

                val animNodes = mutableMapOf<TransformGroup, AnimationNode>()
                anim.channels.forEach { channel ->
                    val nodeGrp = modelNodes[channel.target.nodeRef]
                    if (nodeGrp != null) {
                        val animationNd = animNodes.getOrPut(nodeGrp) { AnimatedTransformGroup(nodeGrp) }
                        when (channel.target.path) {
                            GltfAnimation.Target.PATH_TRANSLATION -> makeTranslationAnimation(channel, animationNd, modelAnim)
                            GltfAnimation.Target.PATH_ROTATION -> makeRotationAnimation(channel, animationNd, modelAnim)
                            GltfAnimation.Target.PATH_SCALE -> makeScaleAnimation(channel, animationNd, modelAnim)
                        }
                    }
                }
            }
        }

        private fun makeTranslationAnimation(animCh: GltfAnimation.Channel, animNd: AnimationNode, modelAnim:Animation) {
            val inputAcc = animCh.samplerRef.inputAccessorRef
            val outputAcc = animCh.samplerRef.outputAccessorRef

            if (inputAcc.type != GltfAccessor.TYPE_SCALAR || inputAcc.componentType != GltfAccessor.COMP_TYPE_FLOAT) {
                logW { "Unsupported translation animation input accessor: type = ${inputAcc.type}, component type = ${inputAcc.componentType}, should be SCALAR and 5126 (float)" }
                return
            }
            if (outputAcc.type != GltfAccessor.TYPE_VEC3 || outputAcc.componentType != GltfAccessor.COMP_TYPE_FLOAT) {
                logW { "Unsupported translation animation output accessor: type = ${outputAcc.type}, component type = ${outputAcc.componentType}, should be VEC3 and 5126 (float)" }
                return
            }

            val transChannel = TranslationAnimationChannel("${modelAnim.name}_translation", animNd)
            val interpolation = when (animCh.samplerRef.interpolation) {
                GltfAnimation.Sampler.INTERPOLATION_STEP -> AnimationKey.Interpolation.STEP
                GltfAnimation.Sampler.INTERPOLATION_CUBICSPLINE -> AnimationKey.Interpolation.CUBICSPLINE
                else -> AnimationKey.Interpolation.LINEAR
            }
            modelAnim.channels += transChannel

            val inTime = FloatAccessor(inputAcc)
            val outTranslation = Vec3fAccessor(outputAcc)
            for (i in 0 until min(inputAcc.count, outputAcc.count)) {
                val t = inTime.next()
                val transKey = if (interpolation == AnimationKey.Interpolation.CUBICSPLINE) {
                    val startTan = outTranslation.nextD()
                    val point = outTranslation.nextD()
                    val endTan = outTranslation.nextD()
                    CubicTranslationKey(t, point, startTan, endTan)
                } else {
                    TranslationKey(t, outTranslation.nextD())
                }
                transKey.interpolation = interpolation
                transChannel.keys[t] = transKey
            }
        }

        private fun makeRotationAnimation(animCh: GltfAnimation.Channel, animNd: AnimationNode, modelAnim: Animation) {
            val inputAcc = animCh.samplerRef.inputAccessorRef
            val outputAcc = animCh.samplerRef.outputAccessorRef

            if (inputAcc.type != GltfAccessor.TYPE_SCALAR || inputAcc.componentType != GltfAccessor.COMP_TYPE_FLOAT) {
                logW { "Unsupported rotation animation input accessor: type = ${inputAcc.type}, component type = ${inputAcc.componentType}, should be SCALAR and 5126 (float)" }
                return
            }
            if (outputAcc.type != GltfAccessor.TYPE_VEC4 || outputAcc.componentType != GltfAccessor.COMP_TYPE_FLOAT) {
                logW { "Unsupported rotation animation output accessor: type = ${outputAcc.type}, component type = ${outputAcc.componentType}, should be VEC4 and 5126 (float)" }
                return
            }

            val rotChannel = RotationAnimationChannel("${modelAnim.name}_rotation", animNd)
            val interpolation = when (animCh.samplerRef.interpolation) {
                GltfAnimation.Sampler.INTERPOLATION_STEP -> AnimationKey.Interpolation.STEP
                GltfAnimation.Sampler.INTERPOLATION_CUBICSPLINE -> AnimationKey.Interpolation.CUBICSPLINE
                else -> AnimationKey.Interpolation.LINEAR
            }
            modelAnim.channels += rotChannel

            val inTime = FloatAccessor(inputAcc)
            val outRotation = Vec4fAccessor(outputAcc)
            for (i in 0 until min(inputAcc.count, outputAcc.count)) {
                val t = inTime.next()
                val rotKey = if (interpolation == AnimationKey.Interpolation.CUBICSPLINE) {
                    val startTan = outRotation.nextD()
                    val point = outRotation.nextD()
                    val endTan = outRotation.nextD()
                    CubicRotationKey(t, point, startTan, endTan)
                } else {
                    RotationKey(t, outRotation.nextD())
                }
                rotKey.interpolation = interpolation
                rotChannel.keys[t] = rotKey
            }
        }

        private fun makeScaleAnimation(animCh: GltfAnimation.Channel, animNd: AnimationNode, modelAnim: Animation) {
            val inputAcc = animCh.samplerRef.inputAccessorRef
            val outputAcc = animCh.samplerRef.outputAccessorRef

            if (inputAcc.type != GltfAccessor.TYPE_SCALAR || inputAcc.componentType != GltfAccessor.COMP_TYPE_FLOAT) {
                logW { "Unsupported scale animation input accessor: type = ${inputAcc.type}, component type = ${inputAcc.componentType}, should be SCALAR and 5126 (float)" }
                return
            }
            if (outputAcc.type != GltfAccessor.TYPE_VEC3 || outputAcc.componentType != GltfAccessor.COMP_TYPE_FLOAT) {
                logW { "Unsupported scale animation output accessor: type = ${outputAcc.type}, component type = ${outputAcc.componentType}, should be VEC3 and 5126 (float)" }
                return
            }

            val scaleChannel = ScaleAnimationChannel("${modelAnim.name}_scale", animNd)
            val interpolation = when (animCh.samplerRef.interpolation) {
                GltfAnimation.Sampler.INTERPOLATION_STEP -> AnimationKey.Interpolation.STEP
                GltfAnimation.Sampler.INTERPOLATION_CUBICSPLINE -> AnimationKey.Interpolation.CUBICSPLINE
                else -> AnimationKey.Interpolation.LINEAR
            }
            modelAnim.channels += scaleChannel

            val inTime = FloatAccessor(inputAcc)
            val outScale = Vec3fAccessor(outputAcc)
            for (i in 0 until min(inputAcc.count, outputAcc.count)) {
                val t = inTime.next()
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

        private fun makeMorphAnimations() {
            animations.forEachIndexed { iAnim, anim ->
                anim.channels.forEach { channel ->
                    if (channel.target.path == GltfAnimation.Target.PATH_WEIGHTS) {
                        val modelAnim = modelAnimations[iAnim]
                        val gltfMesh = channel.target.nodeRef?.meshRef
                        val nodeGrp = modelNodes[channel.target.nodeRef]
                        nodeGrp?.children?.filterIsInstance<Mesh>()?.forEach {
                            makeWeightAnimation(gltfMesh!!, channel, MorphAnimatedMesh(it), modelAnim)
                        }
                    }
                }
            }
        }

        private fun makeWeightAnimation(gltfMesh: GltfMesh, animCh: GltfAnimation.Channel, animNd: MorphAnimatedMesh, modelAnim: Animation) {
            val inputAcc = animCh.samplerRef.inputAccessorRef
            val outputAcc = animCh.samplerRef.outputAccessorRef

            if (inputAcc.type != GltfAccessor.TYPE_SCALAR || inputAcc.componentType != GltfAccessor.COMP_TYPE_FLOAT) {
                logW { "Unsupported weight animation input accessor: type = ${inputAcc.type}, component type = ${inputAcc.componentType}, should be SCALAR and 5126 (float)" }
                return
            }
            if (outputAcc.type != GltfAccessor.TYPE_SCALAR || outputAcc.componentType != GltfAccessor.COMP_TYPE_FLOAT) {
                logW { "Unsupported weight animation output accessor: type = ${outputAcc.type}, component type = ${inputAcc.componentType}, should be VEC3 and 5126 (float)" }
                return
            }

            val weightChannel = WeightAnimationChannel("${modelAnim.name}_weight", animNd)
            val interpolation = when (animCh.samplerRef.interpolation) {
                GltfAnimation.Sampler.INTERPOLATION_STEP -> AnimationKey.Interpolation.STEP
                GltfAnimation.Sampler.INTERPOLATION_CUBICSPLINE -> AnimationKey.Interpolation.CUBICSPLINE
                else -> AnimationKey.Interpolation.LINEAR
            }
            modelAnim.channels += weightChannel

            val morphTargets = gltfMesh.primitives[0].targets
            val nAttribs = gltfMesh.primitives[0].targets.sumBy { it.size }
            val inTimes = FloatAccessor(inputAcc)
            val outWeight = FloatAccessor(outputAcc)
            for (i in 0 until min(inputAcc.count, outputAcc.count)) {
                val t = inTimes.next()
                val weightKey = if (interpolation == AnimationKey.Interpolation.CUBICSPLINE) {
                    val startTan = FloatArray(nAttribs)
                    val point = FloatArray(nAttribs)
                    val endTan = FloatArray(nAttribs)

                    var iAttrib = 0
                    for (m in morphTargets.indices) {
                        val w = outWeight.next()
                        for (j in 0 until morphTargets[m].size) { startTan[iAttrib++] = w }
                    }
                    iAttrib = 0
                    for (m in morphTargets.indices) {
                        val w = outWeight.next()
                        for (j in 0 until morphTargets[m].size) { point[iAttrib++] = w }
                    }
                    iAttrib = 0
                    for (m in morphTargets.indices) {
                        val w = outWeight.next()
                        for (j in 0 until morphTargets[m].size) { endTan[iAttrib++] = w }
                    }
                    CubicWeightKey(t, point, startTan, endTan)

                } else {
                    val attribWeights = FloatArray(nAttribs)
                    var iAttrib = 0
                    for (m in morphTargets.indices) {
                        val w = outWeight.next()
                        for (j in 0 until morphTargets[m].size) {
                            attribWeights[iAttrib++] = w
                        }
                    }
                    WeightKey(t, attribWeights)
                }

                weightKey.interpolation = interpolation
                weightChannel.keys[t] = weightKey
            }
        }

        private fun makeSkins(model: Model) {
            skins.forEach { skin ->
                val modelSkin = Skin()
                val invBinMats = skin.inverseBindMatrixAccessorRef?.let { Mat4fAccessor(it) }
                if (invBinMats != null) {
                    // 1st pass: make SkinNodes for specified nodes / TransformGroups
                    val skinNodes = mutableMapOf<GltfNode, Skin.SkinNode>()
                    skin.jointRefs.forEach { joint ->
                        val jointGrp = modelNodes[joint]!!
                        val invBindMat = invBinMats.next()
                        val skinNode = Skin.SkinNode(jointGrp, invBindMat)
                        modelSkin.nodes += skinNode
                        skinNodes[joint] = skinNode
                    }
                    // 2nd pass: build SkinNode hierarchy
                    skin.jointRefs.forEach { joint ->
                        val skinNode = skinNodes[joint]
                        if (skinNode != null) {
                            joint.childRefs.forEach { child ->
                                val childNode = skinNodes[child]
                                childNode?.let { skinNode.addChild(it) }
                            }
                        }
                    }
                    model.skins += modelSkin
                }
            }
        }

        private fun TransformGroup.sortNodesByAlpha(): Float {
            val childAlphas = mutableMapOf<Node, Float>()
            var avgAlpha = 0f
            for (child in children) {
                var a = 1f
                if (child is Mesh && !child.isOpaque) {
                    a = 0f
                } else if (child is TransformGroup) {
                    a = child.sortNodesByAlpha()
                }
                childAlphas[child] = a
                avgAlpha += a
            }
            sortChildrenBy { -(childAlphas[it] ?: 1f) }
            if (children.isNotEmpty()) {
                avgAlpha /= children.size
            }
            return avgAlpha
        }

        private fun mergeMeshesByMaterial(model: Model) {
            model.mergeMeshesByMaterial()
        }

        private fun TransformGroup.mergeMeshesByMaterial() {
            children.filterIsInstance<TransformGroup>().forEach { it.mergeMeshesByMaterial() }

            meshesByMaterial.values.forEach { sameMatMeshes ->
                val mergeMeshes = children.filter { it in sameMatMeshes }.map { it as Mesh }
                if (mergeMeshes.size > 1) {
                    val r = mergeMeshes[0]
                    for (i in 1 until mergeMeshes.size) {
                        val m = mergeMeshes[i]
                        if (m.geometry.attributeHash == r.geometry.attributeHash) {
                            r.geometry.addGeometry(m.geometry)
                            removeNode(m)
                        }
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

            children.filterIsInstance<Mesh>().forEach {
                it.geometry.batchUpdate(true) {
                    forEach { v ->
                        transform.transform(v.position, 1f)
                        transform.transform(v.normal, 0f)
                        val tan3 = v.tangent.getXyz(MutableVec3f())
                        transform.transform(tan3, 0f)
                        v.tangent.set(tan3, v.tangent.w)
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

        private fun GltfNode.makeNode(model: Model, cfg: ModelGenerateConfig): TransformGroup {
            val modelNdName = name ?: "node_${model.nodes.size}"
            val nodeGrp = TransformGroup(modelNdName)
            modelNodes[this] = nodeGrp
            model.nodes[modelNdName] = nodeGrp

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

            return nodeGrp
        }

        private fun GltfNode.createMeshes(model: Model, nodeGrp: TransformGroup, cfg: ModelGenerateConfig) {
            meshRef?.primitives?.forEachIndexed { index, p ->
                val name = "${meshRef?.name ?: "${nodeGrp.name}.mesh"}_$index"
                val geometry = p.toGeometry(cfg.generateNormals, accessors)
                if (!geometry.isEmpty()) {
                    val mesh = Mesh(geometry, name)
                    nodeGrp += mesh

                    meshesByMaterial.getOrPut(p.material) { mutableSetOf() } += mesh
                    meshMaterials[mesh] = p.materialRef

                    if (cfg.loadAnimations && cfg.applySkins && skin >= 0) {
                        mesh.skin = model.skins[skin]
                        val skeletonRoot = skins[skin].skeleton
                        if (skeletonRoot >= 0) {
                            nodeGrp -= mesh
                            modelNodes[nodes[skeletonRoot]]!! += mesh
                        }
                        mesh.isFrustumChecked = false
                    }
                    if (cfg.loadAnimations && cfg.applyMorphTargets && p.targets.isNotEmpty()) {
                        mesh.morphWeights = FloatArray(p.targets.sumBy { it.size })
                        mesh.isFrustumChecked = false
                    }

                    if (cfg.applyMaterials) {
                        var renderDeferred = cfg.materialConfig.isDeferredShading
                        val useVertexColor = p.attributes.containsKey(GltfMesh.Primitive.ATTRIBUTE_COLOR_0)
                        val pbrConfig = PbrMaterialConfig().apply {
                            val material = p.materialRef
                            if (material != null) {
                                material.applyTo(this, useVertexColor, this@GltfFile)
                            } else {
                                albedo = Color.GRAY
                                albedoSource = Albedo.STATIC_ALBEDO
                            }
                            if (mesh.skin != null) {
                                isSkinned = true
                            }
                            if (mesh.morphWeights != null) {
                                morphAttributes += mesh.geometry.getMorphAttributes()
                            }

                            cfg.materialConfig.let { matCfg ->
                                shadowMaps += matCfg.shadowMaps
                                matCfg.scrSpcAmbientOcclusionMap?.let { useScreenSpaceAmbientOcclusion(it) }
                                useImageBasedLighting(matCfg.environmentMaps)
                            }
                            cfg.pbrBlock?.invoke(this, p)

                            if (alphaMode is AlphaModeBlend) {
                                mesh.isOpaque = false
                                // transparent / blended meshes must be rendered in forward pass
                                renderDeferred = false
                            }

                            albedoMap?.let { model.textures[it.name ?: "tex_${model.textures.size}"] = it }
                            emissiveMap?.let { model.textures[it.name ?: "tex_${model.textures.size}"] = it }
                            normalMap?.let { model.textures[it.name ?: "tex_${model.textures.size}"] = it }
                            roughnessMap?.let { model.textures[it.name ?: "tex_${model.textures.size}"] = it }
                            metallicMap?.let { model.textures[it.name ?: "tex_${model.textures.size}"] = it }
                            occlusionMap?.let { model.textures[it.name ?: "tex_${model.textures.size}"] = it }
                            displacementMap?.let { model.textures[it.name ?: "tex_${model.textures.size}"] = it }
                        }

                        if (renderDeferred) {
                            pbrConfig.isHdrOutput = true
                            mesh.shader = DeferredPbrShader(pbrConfig)
                        } else {
                            pbrConfig.isHdrOutput = false
                            mesh.shader = PbrShader(pbrConfig)
                        }
                    }
                    model.meshes[name] = mesh
                }
            }
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

