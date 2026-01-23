package de.fabmax.kool.scene

import de.fabmax.kool.Assets
import de.fabmax.kool.math.Mat4fStack
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.QuatF
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.toQuatF
import de.fabmax.kool.modules.gltf.FloatAccessor
import de.fabmax.kool.modules.gltf.GltfAccessor
import de.fabmax.kool.modules.gltf.GltfAnimation
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.gltf.GltfLoadConfig
import de.fabmax.kool.modules.gltf.GltfMaterial
import de.fabmax.kool.modules.gltf.GltfMesh
import de.fabmax.kool.modules.gltf.GltfNode
import de.fabmax.kool.modules.gltf.GltfScene
import de.fabmax.kool.modules.gltf.Mat4fAccessor
import de.fabmax.kool.modules.gltf.Vec3fAccessor
import de.fabmax.kool.modules.gltf.Vec4fAccessor
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.pipeline.BlendMode
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.deferred.DeferredKslPbrShader
import de.fabmax.kool.pipeline.shading.AlphaMode
import de.fabmax.kool.pipeline.shading.DepthShader
import de.fabmax.kool.scene.animation.AnimatedTransformGroup
import de.fabmax.kool.scene.animation.Animation
import de.fabmax.kool.scene.animation.AnimationKey
import de.fabmax.kool.scene.animation.CubicRotationKey
import de.fabmax.kool.scene.animation.CubicScaleKey
import de.fabmax.kool.scene.animation.CubicTranslationKey
import de.fabmax.kool.scene.animation.CubicWeightKey
import de.fabmax.kool.scene.animation.MorphAnimatedMesh
import de.fabmax.kool.scene.animation.RotationAnimationChannel
import de.fabmax.kool.scene.animation.RotationKey
import de.fabmax.kool.scene.animation.ScaleAnimationChannel
import de.fabmax.kool.scene.animation.ScaleKey
import de.fabmax.kool.scene.animation.Skin
import de.fabmax.kool.scene.animation.TranslationAnimationChannel
import de.fabmax.kool.scene.animation.TranslationKey
import de.fabmax.kool.scene.animation.WeightAnimationChannel
import de.fabmax.kool.scene.animation.WeightKey
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.logW
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.get
import kotlin.collections.plusAssign
import kotlin.collections.set
import kotlin.math.min

class ModelTemplate(val scene: GltfScene, val gltfFile: GltfFile) : BaseReleasable() {
    val name = scene.name ?: "model_scene"
    val textures = mutableMapOf<String, Texture2d>()

    override fun doRelease() {
        textures.values.forEach { it.release() }
        geometryCache.values.forEach { it.release() }
    }

    val geometryCache: MutableMap<String, IndexedVertexList<*>> = mutableMapOf()
    fun getMeshGeometry(name: String, create: () -> IndexedVertexList<*>): IndexedVertexList<*> =
        geometryCache.getOrPut(name, create)

    val shaderCache: MutableMap<String, Pair<KslShader, DepthShader.Config?>> = mutableMapOf()
    fun getShaders(
        name: String,
        create: () -> Pair<KslShader, DepthShader.Config?>
    ): Pair<KslShader, DepthShader.Config?> =
        shaderCache.getOrPut(name, create)

    fun makeModel(cfg: GltfLoadConfig = GltfLoadConfig()): Model {
        return ModelGenerator().makeModel(cfg)
    }

    private inner class ModelGenerator {
        val modelAnimations = mutableListOf<Animation>()
        val modelNodes = mutableMapOf<GltfNode, Node>()
        val meshesByMaterial = mutableMapOf<Int, MutableSet<Mesh<*>>>()
        val meshMaterials = mutableMapOf<Mesh<*>, GltfMaterial?>()

        val animations get() = gltfFile.animations
        val skins get() = gltfFile.skins
        val accessors get() = gltfFile.accessors
        val nodes get() = gltfFile.nodes

        fun makeModel(cfg: GltfLoadConfig): Model {
            val model = Model(scene.name ?: "model_scene")
            scene.nodeRefs.forEach { nd -> model += nd.makeNode(model, cfg) }

            if (cfg.loadAnimations) makeTrsAnimations()
            if (cfg.loadAnimations) makeSkins(model)
            modelNodes.forEach { (node, grp) -> node.createMeshes(model, grp, cfg) }
            if (cfg.loadAnimations) makeMorphAnimations()
            modelAnimations.filter { it.channels.isNotEmpty() }.forEach { modelAnim ->
                modelAnim.prepareAnimation()
                model.animations += modelAnim
            }
            model.disableAllAnimations()

            if (cfg.applyTransforms && model.animations.isEmpty()) applyTransforms(model)
            if (cfg.mergeMeshesByMaterial) mergeMeshesByMaterial(model)
            if (cfg.sortNodesByAlpha) model.sortNodesByAlpha()
            if (cfg.removeEmptyNodes) model.removeEmpty()

            return model
        }

        private fun Node.removeEmpty() {
            children.filter { it !is Mesh<*> }.forEach {
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

                val animNodes = mutableMapOf<Node, AnimatedTransformGroup>()
                anim.channels.forEach { channel ->
                    val nodeGrp = modelNodes[channel.target.nodeRef]
                    if (nodeGrp != null) {
                        val animationNd = animNodes.getOrPut(nodeGrp) { AnimatedTransformGroup(nodeGrp) }
                        when (channel.target.path) {
                            GltfAnimation.Target.PATH_TRANSLATION -> makeTranslationAnimation(
                                channel,
                                animationNd,
                                modelAnim
                            )

                            GltfAnimation.Target.PATH_ROTATION -> makeRotationAnimation(channel, animationNd, modelAnim)
                            GltfAnimation.Target.PATH_SCALE -> makeScaleAnimation(channel, animationNd, modelAnim)
                        }
                    }
                }
            }
        }

        private fun makeTranslationAnimation(
            animCh: GltfAnimation.Channel,
            animNd: AnimatedTransformGroup,
            modelAnim: Animation
        ) {
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

            val bindTranslation = animNd.initTranslation
            val inTime = FloatAccessor(inputAcc)
            val outTranslation = Vec3fAccessor(outputAcc)
            for (i in 0 until min(inputAcc.count, outputAcc.count)) {
                val t = inTime.next()
                val transKey = if (interpolation == AnimationKey.Interpolation.CUBICSPLINE) {
                    val startTan = outTranslation.next()
                    val point = outTranslation.next()
                    val endTan = outTranslation.next()
                    CubicTranslationKey(
                        t,
                        point - bindTranslation,
                        startTan - bindTranslation,
                        endTan - bindTranslation
                    )
                } else {
                    TranslationKey(t, outTranslation.next() - bindTranslation)
                }
                transKey.interpolation = interpolation
                transChannel.keys[t] = transKey
            }
        }

        private fun makeRotationAnimation(
            animCh: GltfAnimation.Channel,
            animNd: AnimatedTransformGroup,
            modelAnim: Animation
        ) {
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

            val bindRotation = animNd.initRotation
            val inTime = FloatAccessor(inputAcc)
            val outRotation = Vec4fAccessor(outputAcc)
            for (i in 0 until min(inputAcc.count, outputAcc.count)) {
                val t = inTime.next()
                val rotKey = if (interpolation == AnimationKey.Interpolation.CUBICSPLINE) {
                    val startTan = outRotation.next().toQuatF()
                    val point = outRotation.next().toQuatF()
                    val endTan = outRotation.next().toQuatF()
                    CubicRotationKey(
                        t,
                        bindRotation.inverted().mul(point),
                        bindRotation.inverted().mul(startTan),
                        bindRotation.inverted().mul(endTan)
                    )
                } else {
                    RotationKey(t, bindRotation.inverted().mul(outRotation.next().toQuatF()))
                }
                rotKey.interpolation = interpolation
                rotChannel.keys[t] = rotKey
            }
        }

        private fun makeScaleAnimation(
            animCh: GltfAnimation.Channel,
            animNd: AnimatedTransformGroup,
            modelAnim: Animation
        ) {
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

            val bindScale = animNd.initScale
            val inTime = FloatAccessor(inputAcc)
            val outScale = Vec3fAccessor(outputAcc)
            for (i in 0 until min(inputAcc.count, outputAcc.count)) {
                val t = inTime.next()
                val scaleKey = if (interpolation == AnimationKey.Interpolation.CUBICSPLINE) {
                    val startTan = outScale.next()
                    val point = outScale.next()
                    val endTan = outScale.next()
                    CubicScaleKey(t, bindScale / point, bindScale / startTan, bindScale / endTan)
                } else {
                    ScaleKey(t, bindScale / outScale.next())
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
                        nodeGrp?.children?.filterIsInstance<Mesh<*>>()?.forEach {
                            makeWeightAnimation(gltfMesh!!, channel, MorphAnimatedMesh(it), modelAnim)
                        }
                    }
                }
            }
        }

        private fun makeWeightAnimation(
            gltfMesh: GltfMesh,
            animCh: GltfAnimation.Channel,
            animNd: MorphAnimatedMesh,
            modelAnim: Animation
        ) {
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
            val nAttribs = gltfMesh.primitives[0].targets.sumOf { it.size }
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
                        for (j in 0 until morphTargets[m].size) {
                            startTan[iAttrib++] = w
                        }
                    }
                    iAttrib = 0
                    for (m in morphTargets.indices) {
                        val w = outWeight.next()
                        for (j in 0 until morphTargets[m].size) {
                            point[iAttrib++] = w
                        }
                    }
                    iAttrib = 0
                    for (m in morphTargets.indices) {
                        val w = outWeight.next()
                        for (j in 0 until morphTargets[m].size) {
                            endTan[iAttrib++] = w
                        }
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

        private fun Node.sortNodesByAlpha(): Float {
            val childAlphas = mutableMapOf<Node, Float>()
            var avgAlpha = 0f
            for (child in children) {
                val a = if (child is Mesh<*> && !child.isOpaque) {
                    0f
                } else {
                    child.sortNodesByAlpha()
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

        private fun Node.mergeMeshesByMaterial() {
            children.filter { it.children.isNotEmpty() }.forEach { it.mergeMeshesByMaterial() }

            meshesByMaterial.values.forEach { sameMatMeshes ->
                val mergeMeshes = children.filter { it in sameMatMeshes }.map { it as Mesh<*> }
                if (mergeMeshes.size > 1) {
                    val r = mergeMeshes[0]
                    for (i in 1 until mergeMeshes.size) {
                        val m = mergeMeshes[i]
                        if (m.geometry.layout == r.geometry.layout) {
                            r.geometry.addGeometry(m.geometry)
                            removeNode(m)
                        }
                    }
                }
            }
        }

        private fun applyTransforms(model: Model) {
            val transform = Mat4fStack()
            transform.setIdentity()
            model.applyTransforms(transform, model)
        }

        private fun Node.applyTransforms(transform: Mat4fStack, rootGroup: Node) {
            transform.push()
            transform.mul(this.transform.matrixF)

            children.filterIsInstance<Mesh<*>>().forEach {
                it.geometry.forEach { v ->
                    transform.transform(v.position, 1f)
                    transform.transform(v.normal, 0f)
                    val tan3 = MutableVec3f(v.tangent.xyz)
                    transform.transform(tan3, 0f)
                    v.tangent.set(tan3, v.tangent.w)
                }
                it.updateGeometryBounds()
                if (rootGroup != this) {
                    rootGroup += it
                }
            }

            children.filter { it.children.isNotEmpty() }.forEach {
                it.applyTransforms(transform, rootGroup)
                removeNode(it)
            }

            transform.pop()
        }

        private fun GltfNode.makeNode(model: Model, cfg: GltfLoadConfig): Node {
            val modelNdName = name ?: "node_${model.nodes.size}"
            val nodeGrp = Node(modelNdName)
            modelNodes[this] = nodeGrp
            model.nodes[modelNdName] = nodeGrp

            if (matrix != null) {
                val t = MatrixTransformF()
                t.matrixF.set(matrix.toFloatArray())
                nodeGrp.transform = t
            } else {
                val t = translation?.let { Vec3f(it[0], it[1], it[2]) } ?: Vec3f.ZERO
                val r = rotation?.let { QuatF(it[0], it[1], it[2], it[3]) } ?: QuatF.IDENTITY
                val s = scale?.let { Vec3f(it[0], it[1], it[2]) } ?: Vec3f.ONES
                nodeGrp.transform.setCompositionOf(t, r, s)
            }

            childRefs.forEach {
                nodeGrp += it.makeNode(model, cfg)
            }

            return nodeGrp
        }

        private fun GltfNode.createMeshes(
            model: Model,
            nodeGrp: Node,
            cfg: GltfLoadConfig
        ) {
            meshRef?.primitives?.forEachIndexed { index, prim ->
                val name = "${meshRef?.name ?: "${nodeGrp.name}.mesh"}_$index"
                val geometry = getMeshGeometry(name) { prim.toGeometry(cfg, accessors) }
                if (!geometry.isEmpty()) {
                    var isFrustumChecked = true
                    var meshSkin: Skin? = null
                    var morphWeights: FloatArray? = null

                    if (cfg.loadAnimations && cfg.applySkins && skin >= 0) {
                        meshSkin = model.skins[skin]
                        isFrustumChecked = false
                    }
                    if (cfg.loadAnimations && cfg.applyMorphTargets && prim.targets.isNotEmpty()) {
                        morphWeights = FloatArray(prim.targets.sumOf { it.size })
                        isFrustumChecked = false
                    }

                    val instances = cfg.instanceLayout?.let { MeshInstanceList(it) }
                    val mesh = Mesh(
                        geometry = geometry,
                        instances = instances,
                        morphWeights = morphWeights,
                        skin = meshSkin,
                        name = name
                    ).apply { releaseGeometry = false }
                    mesh.isFrustumChecked = isFrustumChecked

                    nodeGrp += mesh
                    if (meshSkin != null) {
                        val skeletonRoot = skins[skin].skeleton
                        if (skeletonRoot >= 0) {
                            nodeGrp -= mesh
                            modelNodes[nodes[skeletonRoot]]!! += mesh
                        }
                    }

                    meshesByMaterial.getOrPut(prim.material) { mutableSetOf() } += mesh
                    meshMaterials[mesh] = prim.materialRef

                    if (cfg.applyMaterials) {
                        val (shader, depthShader) = getShaders(name) {
                            makeKslMaterial(prim, mesh, cfg)
                        }
                        mesh.shader = shader
                        mesh.depthShaderConfig = depthShader
                    }
                    model.meshes[name] = mesh
                }
            }
        }

        private fun makeKslMaterial(
            prim: GltfMesh.Primitive,
            mesh: Mesh<*>,
            cfg: GltfLoadConfig,
        ): Pair<KslShader, DepthShader.Config?> {
            var isDeferred = cfg.materialConfig.isDeferredShading
            val useVertexColor = prim.attributes.containsKey(GltfMesh.Primitive.ATTRIBUTE_COLOR_0)

            val pbrConfig = DeferredKslPbrShader.Config.Builder().apply {
                val material = prim.materialRef
                if (material != null) {
                    material.applyTo(this, useVertexColor, gltfFile, cfg.assetLoader ?: Assets.defaultLoader)
                } else {
                    color {
                        uniformColor(Color.GRAY.toLinear())
                    }
                }

                vertices {
                    modelMatrixComposition = cfg.materialConfig.modelMatrixComposition
                    if (mesh.instances != null) {
                        instancedModelMatrix()
                    }
                    if (mesh.skin != null) {
                        if (cfg.materialConfig.fixedNumberOfJoints > 0) {
                            enableArmatureFixedNumberOfBones(cfg.materialConfig.fixedNumberOfJoints)
                        } else {
                            enableArmature(mesh.skin.nodes.size)
                        }
                        if (cfg.materialConfig.fixedNumberOfJoints > 0 && cfg.materialConfig.fixedNumberOfJoints < mesh.skin.nodes.size) {
                            logW("GltfFile") { "\"${this@ModelTemplate.name}\": Number of joints exceeds the material config's fixedNumberOfJoints (mesh has ${mesh.skin.nodes.size}, materialConfig.fixedNumberOfJoints is ${cfg.materialConfig.fixedNumberOfJoints})" }
                        }
                    }
                    if (mesh.morphWeights != null) {
                        morphAttributes += mesh.geometry.getMorphAttributes()
                    }
                }

                cfg.materialConfig.let { matCfg ->
                    lighting {
                        maxNumberOfLights = matCfg.maxNumberOfLights
                        addShadowMaps(matCfg.shadowMaps)

                        matCfg.environmentMap?.let { ibl ->
                            imageBasedAmbientLight(ibl.irradianceMap)
                            reflectionMap = ibl.reflectionMap
                        }
                        matCfg.scrSpcAmbientOcclusionMap?.let {
                            enableSsao(it)
                        }
                    }
                }
                cfg.pbrBlock?.invoke(this, prim)

                if (alphaMode is AlphaMode.Blend) {
                    mesh.isOpaque = false
                    // transparent / blended meshes must be rendered in forward pass
                    isDeferred = false
                }

                colorCfg.primaryTexture?.defaultTexture?.let { textures[it.name] = it }
                emissionCfg.primaryTexture?.defaultTexture?.let { textures[it.name] = it }
                normalMapCfg.defaultNormalMap?.let { textures[it.name] = it }
                roughnessCfg.primaryTexture?.defaultTexture?.let { textures[it.name] = it }
                metallicCfg.primaryTexture?.defaultTexture?.let { textures[it.name] = it }
                aoCfg.primaryTexture?.defaultTexture?.let { textures[it.name] = it }
                vertexCfg.displacementCfg.primaryTexture?.defaultTexture?.let { textures[it.name] = it }
            }

            val shader = if (isDeferred) {
                pbrConfig.pipelineCfg.blendMode = BlendMode.DISABLED
                DeferredKslPbrShader(pbrConfig.build())
            } else {
                KslPbrShader(pbrConfig.build())
            }
            val depthShader = if (pbrConfig.alphaMode is AlphaMode.Mask) {
                DepthShader.Config.forMesh(
                    mesh,
                    pbrConfig.pipelineCfg.cullMethod,
                    pbrConfig.alphaMode,
                    pbrConfig.colorCfg.primaryTexture?.defaultTexture
                )
            } else null
            return Pair(shader, depthShader)
        }
    }
}