package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.scene.Light
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.CascadedShadowMap
import de.fabmax.kool.util.MeshInstanceList
import de.fabmax.kool.util.SimpleShadowMap
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class ShaderModel(val modelName: String) {
    val stages = mutableMapOf(
            ShaderStage.VERTEX_SHADER to VertexShaderGraph(this),
            ShaderStage.FRAGMENT_SHADER to FragmentShaderGraph(this)
    )

    var dumpCode = false

    val vertexStageGraph: VertexShaderGraph
        get() = stages[ShaderStage.VERTEX_SHADER] as VertexShaderGraph
    val fragmentStageGraph: FragmentShaderGraph
        get() = stages[ShaderStage.FRAGMENT_SHADER] as FragmentShaderGraph

    inline fun <reified T: ShaderNode> findNode(name: String, stage: ShaderStage = ShaderStage.ALL): T? {
        stages.values.forEach {
            if (it.stage.mask and stage.mask != 0) {
                val node = it.findNode<T>(name)
                if (node != null) {
                    return node
                }
            }
        }
        return null
    }

    fun findNodeByName(name: String): ShaderNode? {
        stages.values.forEach {
            val node = it.findNodeByName(name)
            if (node != null) {
                return node
            }
        }
        return null
    }

    inline fun <reified T: ShaderNode> findNodeByType(stage: ShaderStage = ShaderStage.ALL): T? {
        stages.values.forEach {
            if (it.stage.mask and stage.mask != 0) {
                val node = it.nodes.filterIsInstance<T>().firstOrNull()
                if (node != null) {
                    return node
                }
            }
        }
        return null
    }

    fun setup(mesh: Mesh, builder: Pipeline.Builder) {
        builder.name = modelName
        stages.values.forEach { it.clear() }
        stages.values.forEach { it.setup() }
        setupAttributes(mesh, builder)

        // merge all defined push constants into a single push constant range
        val pushBuilder = PushConstantRange.Builder()
        val pushUpdateFuns = mutableListOf<((PushConstantRange, DrawCommand) -> Unit)>()

        val descBuilder = DescriptorSetLayout.Builder()

        stages.values.forEach { stage ->
            descBuilder.descriptors += stage.descriptorSet.descriptors

            pushBuilder.pushConstants += stage.pushConstants.pushConstants
            pushBuilder.stages += stage.pushConstants.stages
            stage.pushConstants.onUpdate?.let { pushUpdateFuns += it }
        }
        if (descBuilder.descriptors.isNotEmpty()) {
            builder.descriptorSetLayouts += descBuilder
        }
        if (pushBuilder.pushConstants.isNotEmpty()) {
            builder.pushConstantRanges += pushBuilder
            if (pushUpdateFuns.isNotEmpty()) {
                pushBuilder.onUpdate = { rng, cmd ->
                    for (i in pushUpdateFuns.indices) {
                        pushUpdateFuns[i](rng, cmd)
                    }
                }
            }
        }
    }

    private fun setupAttributes(mesh: Mesh, builder: Pipeline.Builder) {
        var attribLocation = 0
        val verts = mesh.geometry
        val vertLayoutAttribs = mutableListOf<VertexLayout.VertexAttribute>()
        val vertLayoutAttribsI = mutableListOf<VertexLayout.VertexAttribute>()
        var iBinding = 0
        vertexStageGraph.requiredVertexAttributes.forEach { attrib ->
            val off = verts.attributeByteOffsets[attrib] ?:
                    throw NoSuchElementException("Mesh does not include required vertex attribute: ${attrib.name}")

            if (attrib.type.isInt) {
                vertLayoutAttribsI += VertexLayout.VertexAttribute(attribLocation, off, attrib)
            } else {
                vertLayoutAttribs += VertexLayout.VertexAttribute(attribLocation, off, attrib)
            }
            attribLocation += attrib.props.nSlots
        }

        builder.vertexLayout.bindings += VertexLayout.Binding(iBinding++, InputRate.VERTEX, vertLayoutAttribs, verts.byteStrideF)
        if (vertLayoutAttribsI.isNotEmpty()) {
            builder.vertexLayout.bindings += VertexLayout.Binding(iBinding++, InputRate.VERTEX, vertLayoutAttribsI, verts.byteStrideI)
        }

        val insts = mesh.instances
        if (insts != null) {
            val instLayoutAttribs = mutableListOf<VertexLayout.VertexAttribute>()
            vertexStageGraph.requiredInstanceAttributes.forEach { attrib ->
                val off = insts.attributeOffsets[attrib] ?:
                        throw NoSuchElementException("Mesh does not include required instance attribute: ${attrib.name}")
                instLayoutAttribs += VertexLayout.VertexAttribute(attribLocation, off, attrib)
                attribLocation += attrib.props.nSlots
            }
            builder.vertexLayout.bindings += VertexLayout.Binding(iBinding, InputRate.INSTANCE, instLayoutAttribs, insts.strideBytesF)
        } else if (vertexStageGraph.requiredInstanceAttributes.isNotEmpty()) {
            throw IllegalStateException("Shader model requires instance attributes, but mesh doesn't provide any")
        }
    }

    abstract class StageBuilder(val stage: ShaderGraph) {
        fun <T: ShaderNode> addNode(node: T): T {
            stage.addNode(node)
            return node
        }

        fun combineNode(type: GlslType): CombineNode {
            val combNode = addNode(CombineNode(type, stage))
            return combNode
        }

        fun combineXyzWNode(xyz: ShaderNodeIoVar? = null, w: ShaderNodeIoVar? = null): Combine31Node {
            val combNode = addNode(Combine31Node(stage))
            xyz?.let { combNode.inXyz = it }
            w?.let { combNode.inW = it }
            return combNode
        }

        fun dotNode(a: ShaderNodeIoVar? = null, b: ShaderNodeIoVar? = null): DotNode {
            val dotNode = addNode(DotNode(stage))
            a?.let { dotNode.inA = it }
            b?.let { dotNode.inB = it }
            return dotNode
        }

        fun splitNode(input: ShaderNodeIoVar, channels: String): SplitNode {
            val splitNode = addNode(SplitNode(channels, stage))
            splitNode.input = input
            return splitNode
        }

        fun addNode(left: ShaderNodeIoVar? = null, right: ShaderNodeIoVar? = null): AddNode {
            val addNode = addNode(AddNode(stage))
            left?.let { addNode.left = it }
            right?.let { addNode.right = it }
            return addNode
        }

        fun subtractNode(left: ShaderNodeIoVar? = null, right: ShaderNodeIoVar? = null): SubtractNode {
            val subNode = addNode(SubtractNode(stage))
            left?.let { subNode.left = it }
            right?.let { subNode.right = it }
            return subNode
        }

        fun divideNode(left: ShaderNodeIoVar? = null, right: ShaderNodeIoVar? = null): DivideNode {
            val divNode = addNode(DivideNode(stage))
            left?.let { divNode.left = it }
            right?.let { divNode.right = it }
            return divNode
        }

        fun multiplyNode(left: ShaderNodeIoVar?, right: Float): MultiplyNode {
            return multiplyNode(left, ShaderNodeIoVar(ModelVar1fConst(right)))
        }

        fun multiplyNode(left: ShaderNodeIoVar? = null, right: ShaderNodeIoVar? = null): MultiplyNode {
            val mulNode = addNode(MultiplyNode(stage))
            left?.let { mulNode.left = it }
            right?.let { mulNode.right = it }
            return mulNode
        }

        fun minNode(left: ShaderNodeIoVar? = null, right: ShaderNodeIoVar? = null): MinNode {
            val minNode = addNode(MinNode(stage))
            left?.let { minNode.left = it }
            right?.let { minNode.right = it }
            return minNode
        }

        fun maxNode(left: ShaderNodeIoVar? = null, right: ShaderNodeIoVar? = null): MaxNode {
            val maxNode = addNode(MaxNode(stage))
            left?.let { maxNode.left = it }
            right?.let { maxNode.right = it }
            return maxNode
        }

        fun mixNode(left: ShaderNodeIoVar? = null, right: ShaderNodeIoVar? = null, fac: ShaderNodeIoVar? = null): MixNode {
            val mixNode = addNode(MixNode(stage))
            left?.let { mixNode.left = it }
            right?.let { mixNode.right = it }
            fac?.let { mixNode.mixFac = it }
            return mixNode
        }

        fun vecFromColorNode(input: ShaderNodeIoVar? = null): VecFromColorNode {
            val nrmNode = addNode(VecFromColorNode(stage))
            input?.let { nrmNode.input = input }
            return nrmNode
        }

        fun viewDirNode(inCamPos: ShaderNodeIoVar? = null, inWorldPos: ShaderNodeIoVar? = null): ViewDirNode {
            val viewDirNd = addNode(ViewDirNode(stage))
            inCamPos?.let { viewDirNd.inCamPos = it }
            inWorldPos?.let { viewDirNd.inWorldPos = it }
            return viewDirNd
        }

        fun distanceNode(inA: ShaderNodeIoVar? = null, inB: ShaderNodeIoVar? = null): DistanceNode {
            val distNode = addNode(DistanceNode(stage))
            inA?.let { distNode.inA = it }
            inB?.let { distNode.inB = it }
            return distNode
        }

        fun normalizeNode(input: ShaderNodeIoVar? = null): NormalizeNode {
            val nrmNode = addNode(NormalizeNode(stage))
            input?.let { nrmNode.input = input }
            return nrmNode
        }

        fun reflectNode(inDirection: ShaderNodeIoVar? = null, inNormal: ShaderNodeIoVar? = null): ReflectNode {
            val reflectNd = addNode(ReflectNode(stage))
            inDirection?.let { reflectNd.inDirection = it }
            inNormal?.let { reflectNd.inNormal = it }
            return reflectNd
        }

        fun refractNode(inDirection: ShaderNodeIoVar? = null, inNormal: ShaderNodeIoVar? = null, inIor: ShaderNodeIoVar? = null): RefractNode {
            val refractNd = addNode(RefractNode(stage))
            inDirection?.let { refractNd.inDirection = it }
            inNormal?.let { refractNd.inNormal = it }
            inIor?.let { refractNd.inIor = it }
            return refractNd
        }

        fun normalMapNode(normalMapColor: ShaderNodeIoVar? = null,
                          normal: ShaderNodeIoVar? = null, tangent: ShaderNodeIoVar? = null): NormalMapNode {
            val nrmMappingNd = addNode(NormalMapNode(stage))
            normalMapColor?.let { nrmMappingNd.inNormalMapColor = it }
            normal?.let { nrmMappingNd.inNormal = it }
            tangent?.let { nrmMappingNd.inTangent = it }
            return nrmMappingNd
        }

        fun normalMapNode(texture: Texture2dNode, textureCoord: ShaderNodeIoVar,
                          normal: ShaderNodeIoVar? = null, tangent: ShaderNodeIoVar? = null): NormalMapNode {
            val samplerNode = addNode(Texture2dSamplerNode(texture, stage))
            samplerNode.inTexCoord = textureCoord

            val nrmMappingNd = addNode(NormalMapNode(stage))
            nrmMappingNd.inNormalMapColor = samplerNode.outColor
            normal?.let { nrmMappingNd.inNormal = it }
            tangent?.let { nrmMappingNd.inTangent = it }
            return nrmMappingNd
        }

        fun displacementMapNode(texture: Texture2dNode, textureCoord: ShaderNodeIoVar? = null, pos: ShaderNodeIoVar? = null,
                                normal: ShaderNodeIoVar? = null, strength: ShaderNodeIoVar? = null): DisplacementMapNode {
            val dispMappingNd = addNode(DisplacementMapNode(texture, stage))
            textureCoord?.let { dispMappingNd.inTexCoord = it }
            pos?.let { dispMappingNd.inPosition = it }
            normal?.let { dispMappingNd.inNormal = it }
            strength?.let { dispMappingNd.inStrength = it }
            return dispMappingNd
        }

        fun gammaNode(inputColor: ShaderNodeIoVar? = null, gamma: ShaderNodeIoVar? = null): GammaNode {
            val gammaNd = addNode(GammaNode(stage))
            inputColor?.let { gammaNd.inColor = it }
            gamma?.let { gammaNd.inGamma = it }
            return gammaNd
        }

        fun hdrToLdrNode(inputColor: ShaderNodeIoVar? = null): HdrToLdrNode {
            val hdr2ldr = addNode(HdrToLdrNode(stage))
            inputColor?.let { hdr2ldr.inColor = it }
            return hdr2ldr
        }

        fun premultiplyColorNode(inColor: ShaderNodeIoVar? = null): PremultiplyColorNode {
            val preMult = addNode(PremultiplyColorNode(stage))
            inColor?.let { preMult.inColor = it }
            return preMult
        }

        fun colorAlphaNode(inColor: ShaderNodeIoVar? = null, inAlpha: ShaderNodeIoVar? = null): ColorAlphaNode {
            val colAlpha = addNode(ColorAlphaNode(stage))
            inColor?.let { colAlpha.inColor = it }
            inAlpha?.let { colAlpha.inAlpha = it }
            return colAlpha
        }

        fun namedVariable(name: String, defaultInVar: ShaderNodeIoVar? = null): NamedVariableNode {
            val namedNd = addNode(NamedVariableNode(name, stage))
            defaultInVar?.let { namedNd.input = it }
            return namedNd
        }

        fun constFloat(value: Float) = ShaderNodeIoVar(ModelVar1fConst(value))
        fun constVec2f(value: Vec2f) = ShaderNodeIoVar(ModelVar2fConst(value))
        fun constVec3f(value: Vec3f) = ShaderNodeIoVar(ModelVar3fConst(value))
        fun constVec4f(value: Vec4f) = ShaderNodeIoVar(ModelVar4fConst(value))

        fun constInt(value: Int) = ShaderNodeIoVar(ModelVar1iConst(value))
        fun constVec2i(value: Vec2i) = ShaderNodeIoVar(ModelVar2iConst(value))
        fun constVec3i(value: Vec3i) = ShaderNodeIoVar(ModelVar3iConst(value))
        fun constVec4i(value: Vec4i) = ShaderNodeIoVar(ModelVar4iConst(value))

        fun pushConstantNode1f(name: String) = addNode(PushConstantNode1f(Uniform1f(name), stage))
        fun pushConstantNode2f(name: String) = addNode(PushConstantNode2f(Uniform2f(name), stage))
        fun pushConstantNode3f(name: String) = addNode(PushConstantNode3f(Uniform3f(name), stage))
        fun pushConstantNode4f(name: String) = addNode(PushConstantNode4f(Uniform4f(name), stage))
        fun pushConstantNodeColor(name: String) = addNode(PushConstantNodeColor(UniformColor(name), stage))

        fun pushConstantNode1f(u: Uniform1f) = addNode(PushConstantNode1f(u, stage))
        fun pushConstantNode2f(u: Uniform2f) = addNode(PushConstantNode2f(u, stage))
        fun pushConstantNode3f(u: Uniform3f) = addNode(PushConstantNode3f(u, stage))
        fun pushConstantNode4f(u: Uniform4f) = addNode(PushConstantNode4f(u, stage))
        fun pushConstantNodeColor(u: UniformColor) = addNode(PushConstantNodeColor(u, stage))

        fun pushConstantNode1i(name: String) = addNode(PushConstantNode1i(Uniform1i(name), stage))
        fun pushConstantNode2i(name: String) = addNode(PushConstantNode2i(Uniform2i(name), stage))
        fun pushConstantNode3i(name: String) = addNode(PushConstantNode3i(Uniform3i(name), stage))
        fun pushConstantNode4i(name: String) = addNode(PushConstantNode4i(Uniform4i(name), stage))

        fun pushConstantNode1i(u: Uniform1i) = addNode(PushConstantNode1i(u, stage))
        fun pushConstantNode2i(u: Uniform2i) = addNode(PushConstantNode2i(u, stage))
        fun pushConstantNode3i(u: Uniform3i) = addNode(PushConstantNode3i(u, stage))
        fun pushConstantNode4i(u: Uniform4i) = addNode(PushConstantNode4i(u, stage))

        fun uniformMat3fNode(name: String) = addNode(UniformMat3fNode(stage, name))
        fun uniformMat4fNode(name: String) = addNode(UniformMat4fNode(stage, name))

        fun morphWeightsNode(nWeights: Int) = addNode(MorphWeightsNode(nWeights, stage))

        fun getMorphWeightNode(iWeight: Int, morphWeightsNode: MorphWeightsNode) =
                getMorphWeightNode(iWeight, morphWeightsNode.outWeights0, morphWeightsNode.outWeights1)

        fun getMorphWeightNode(iWeight: Int, weights0: ShaderNodeIoVar? = null, weights1: ShaderNodeIoVar? = null): GetMorphWeightNode {
            val getWeightNd = addNode(GetMorphWeightNode(iWeight, stage))
            weights0?.let { getWeightNd.inWeights0 = it }
            weights1?.let { getWeightNd.inWeights1 = it }
            return getWeightNd
        }

        fun texture1dNode(texName: String) = addNode(Texture1dNode(stage, texName))

        fun texture2dNode(texName: String) = addNode(Texture2dNode(stage, texName))

        fun texture3dNode(texName: String) = addNode(Texture3dNode(stage, texName))

        fun textureCubeNode(texName: String) = addNode(TextureCubeNode(stage, texName))

        fun texture1dSamplerNode(texNode: Texture1dNode, texCoords: ShaderNodeIoVar? = null, premultiply: Boolean = false): Texture1dSamplerNode {
            val texSampler = addNode(Texture1dSamplerNode(texNode, stage, premultiply))
            texCoords?.let { texSampler.inTexCoord = it }
            return texSampler
        }

        fun texture2dSamplerNode(texNode: Texture2dNode, texCoords: ShaderNodeIoVar? = null, premultiply: Boolean = false): Texture2dSamplerNode {
            val texSampler = addNode(Texture2dSamplerNode(texNode, stage, premultiply))
            texCoords?.let { texSampler.inTexCoord = it }
            return texSampler
        }

        fun texture3dSamplerNode(texNode: Texture3dNode, texCoords: ShaderNodeIoVar? = null, premultiply: Boolean = false): Texture3dSamplerNode {
            val texSampler = addNode(Texture3dSamplerNode(texNode, stage, premultiply))
            texCoords?.let { texSampler.inTexCoord = it }
            return texSampler
        }

        fun textureCubeSamplerNode(texNode: TextureCubeNode, texCoords: ShaderNodeIoVar? = null, premultiply: Boolean = false): TextureCubeSamplerNode {
            val texSampler = addNode(TextureCubeSamplerNode(texNode, stage, premultiply))
            texCoords?.let { texSampler.inTexCoord = it }
            return texSampler
        }

        fun noiseTextureSamplerNode(texNode: Texture2dNode, texSize: ShaderNodeIoVar? = null): NoiseTextureSamplerNode {
            val sampler = addNode(NoiseTextureSamplerNode(texNode, stage))
            texSize?.let { sampler.inTexSize = it }
            return sampler
        }

        fun equiRectSamplerNode(texNode: Texture2dNode, texCoords: ShaderNodeIoVar? = null, decodeRgbe: Boolean = false, premultiply: Boolean = false): EquiRectSamplerNode {
            val texSampler = addNode(EquiRectSamplerNode(texNode, stage, decodeRgbe, premultiply))
            texCoords?.let { texSampler.inTexCoord = it }
            return texSampler
        }

        fun vec3TransformNode(input: ShaderNodeIoVar? = null, inMat: ShaderNodeIoVar? = null,
                              w: Float = 1f, invert: Boolean = false): Vec3TransformNode {
            val tfNode = addNode(Vec3TransformNode(stage, w, invert))
            input?.let { tfNode.inVec = it }
            inMat?.let { tfNode.inMat = it }
            return tfNode
        }

        fun vec4TransformNode(input: ShaderNodeIoVar? = null, inMat: ShaderNodeIoVar? = null,
                              w: Float = 1f): Vec4TransformNode {
            val tfNode = addNode(Vec4TransformNode(stage, w))
            input?.let { tfNode.inVec = it }
            inMat?.let { tfNode.inMat = it }
            return tfNode
        }
    }

    inner class VertexStageBuilder : StageBuilder(vertexStageGraph) {
        var positionOutput: ShaderNodeIoVar
            get() = vertexStageGraph.positionOutput
            set(value) { vertexStageGraph.positionOutput = value }

        fun attrColors() = attributeNode(Attribute.COLORS)
        fun attrNormals() = attributeNode(Attribute.NORMALS)
        fun attrPositions() = attributeNode(Attribute.POSITIONS)
        fun attrTangents() = attributeNode(Attribute.TANGENTS)
        fun attrTexCoords() = attributeNode(Attribute.TEXTURE_COORDS)
        fun attrJoints() = attributeNode(Attribute.JOINTS)
        fun attrWeights() = attributeNode(Attribute.WEIGHTS)
        fun attributeNode(attribute: Attribute) = addNode(AttributeNode(attribute, stage))

        fun instanceAttrModelMat() = instanceAttributeNode(MeshInstanceList.MODEL_MAT)
        fun instanceAttributeNode(attribute: Attribute) = addNode(InstanceAttributeNode(attribute, stage))

        fun pointSize(inPointSize: ShaderNodeIoVar? = null): PointSizeNode {
            val pointNd = addNode(PointSizeNode(stage))
            inPointSize?.let { pointNd.inSize = inPointSize }
            return pointNd
        }

        fun stageInterfaceNode(name: String, input: ShaderNodeIoVar? = null, isFlat: Boolean = false): StageInterfaceNode {
            val ifNode = StageInterfaceNode(name, vertexStageGraph, fragmentStageGraph)
            ifNode.isFlat = isFlat
            input?.let { ifNode.input = it }
            addNode(ifNode.vertexNode)
            fragmentStageGraph.addNode(ifNode.fragmentNode)
            return ifNode
        }

        fun mvpNode() = addNode(UniformBufferMvp(stage))

        fun skinTransformNode(inJoints: ShaderNodeIoVar? = null, inWeights: ShaderNodeIoVar? = null, maxJoints: Int = 16): SkinTransformNode {
            val skinNd = addNode(SkinTransformNode(stage, maxJoints))
            inJoints?.let { skinNd.inJoints = it }
            inWeights?.let { skinNd.inWeights = it }
            return skinNd
        }

        fun premultipliedMvpNode() = addNode(UniformBufferPremultipliedMvp(stage))

        fun simpleShadowMapNode(shadowMap: SimpleShadowMap, depthMapName: String,
                                inWorldPos: ShaderNodeIoVar? = null, inWorldNrm: ShaderNodeIoVar? = null): SimpleShadowMapNode {
            val shadowMapNode = SimpleShadowMapNode(shadowMap, vertexStageGraph, fragmentStageGraph)
            addNode(shadowMapNode.vertexNode)
            fragmentStageGraph.addNode(shadowMapNode.fragmentNode)

            val depthMap = Texture2dNode(fragmentStageGraph, depthMapName).apply { isDepthTexture = true }
            fragmentStageGraph.addNode(depthMap)

            inWorldPos?.let { shadowMapNode.inWorldPos = it }
            inWorldNrm?.let { shadowMapNode.inWorldNrm = it }
            shadowMapNode.depthMap = depthMap

            return shadowMapNode
        }

        fun cascadedShadowMapNode(cascadedShadowMap: CascadedShadowMap, depthMapName: String,
                                  inViewPos: ShaderNodeIoVar? = null, inWorldPos: ShaderNodeIoVar? = null,
                                  inWorldNrm: ShaderNodeIoVar? = null): CascadedShadowMapNode {
            val shadowMapNode = CascadedShadowMapNode(cascadedShadowMap, vertexStageGraph, fragmentStageGraph)
            addNode(shadowMapNode.vertexNode)
            fragmentStageGraph.addNode(shadowMapNode.fragmentNode)

            val depthMap = Texture2dNode(fragmentStageGraph, depthMapName).apply {
                isDepthTexture = true
                arraySize = cascadedShadowMap.numCascades
            }
            fragmentStageGraph.addNode(depthMap)

            inWorldPos?.let { shadowMapNode.inWorldPos = it }
            inWorldNrm?.let { shadowMapNode.inWorldNrm = it }
            inViewPos?.let { shadowMapNode.inViewPosition = it }
            shadowMapNode.depthMap = depthMap

            return shadowMapNode
        }

        fun simpleVertexPositionNode() = vec4TransformNode(attrPositions().output, premultipliedMvpNode().outMvpMat)

        fun fullScreenQuadPositionNode(inTexCoords: ShaderNodeIoVar? = null): FullScreenQuadTexPosNode {
            val quadNode = addNode(FullScreenQuadTexPosNode(stage))
            inTexCoords?.let { quadNode.inTexCoord = it }
            return quadNode
        }
    }

    inner class FragmentStageBuilder : StageBuilder(fragmentStageGraph) {

        fun blurNode(inputTex: Texture2dNode? = null, texCoord: ShaderNodeIoVar? = null): BlurNode {
            val nd = addNode(BlurNode(stage))
            inputTex?.let { nd.inTexture = it }
            texCoord?.let { nd.inTexCoord = it }
            return nd
        }

        fun flipBacksideNormalNode(inNormal: ShaderNodeIoVar? = null): FlipBacksideNormalNode {
            val nd = addNode(FlipBacksideNormalNode(stage))
            inNormal?.let { nd.inNormal = it }
            return nd
        }

        fun multiLightNode(fragPos: ShaderNodeIoVar? = null, maxLights: Int = 4): MultiLightNode {
            val lightNd = addNode(MultiLightNode(stage, maxLights))
            fragPos?.let { lightNd.inFragPos = it }
            return lightNd
        }

        fun singleLightNode(light: Light? = null): SingleLightNode {
            val lightNd = addNode(SingleLightNode(stage))
            light?.let {
                val lightDataNd = addNode(SingleLightUniformDataNode(stage))
                lightDataNd.light = light
                lightNd.inLightPos = lightDataNd.outLightPos
                lightNd.inLightDir = lightDataNd.outLightDir
                lightNd.inLightColor = lightDataNd.outLightColor
            }
            return lightNd
        }

        fun screenCoordNode(viewport: ShaderNodeIoVar? = null): ScreenCoordNode {
            val coordNd = addNode(ScreenCoordNode(stage))
            viewport?.let { coordNd.inViewport = it }
            return coordNd
        }

        fun deferredSimpleShadowMapNode(shadowMap: SimpleShadowMap, depthMapName: String,
                                        worldPos: ShaderNodeIoVar, worldNrm: ShaderNodeIoVar): SimpleShadowMapFragmentNode {
            val depthMapNd = addNode(Texture2dNode(stage, depthMapName)).apply { isDepthTexture = true }
            val lightSpaceTf = addNode(SimpleShadowMapTransformNode(shadowMap, stage)).apply {
                inWorldPos = worldPos
                inWorldNrm = worldNrm
            }
            return addNode(SimpleShadowMapFragmentNode(shadowMap, stage)).apply {
                inPosLightSpace = lightSpaceTf.outPosLightSpace
                inNrmZLightSpace = lightSpaceTf.outNrmZLightSpace
                depthMap = depthMapNd
            }
        }

        fun deferredCascadedShadowMapNode(shadowMap: CascadedShadowMap, depthMapName: String,
                                          viewPos: ShaderNodeIoVar, worldPos: ShaderNodeIoVar, worldNrm: ShaderNodeIoVar): CascadedShadowMapFragmentNode {
            val depthMapNd = addNode(Texture2dNode(stage, depthMapName)).apply {
                isDepthTexture = true
                arraySize = shadowMap.numCascades
            }
            val lightSpaceTf = addNode(CascadedShadowMapTransformNode(shadowMap, stage)).apply {
                inWorldPos = worldPos
                inWorldNrm = worldNrm
            }
            return addNode(CascadedShadowMapFragmentNode(shadowMap, stage)).apply {
                inViewZ = splitNode(viewPos, "z").output
                inPosLightSpace = lightSpaceTf.outPosLightSpace
                inNrmZLightSpace = lightSpaceTf.outNrmZLightSpace
                depthMap = depthMapNd
            }
        }

        fun unlitMaterialNode(color: ShaderNodeIoVar? = null): UnlitMaterialNode {
            val mat = addNode(UnlitMaterialNode(stage))
            color?.let { mat.inColor = it }
            return mat
        }

        /**
         * Phong shader with multiple light sources. The shader assumes normals, fragment and cam positions are in
         * world space.
         */
        fun phongMaterialNode(albedo: ShaderNodeIoVar? = null, normal: ShaderNodeIoVar? = null,
                              fragPos: ShaderNodeIoVar? = null, camPos: ShaderNodeIoVar? = null): PhongMaterialNode {
            val mat = addNode(PhongMaterialNode(stage))
            albedo?.let { mat.inAlbedo = it }
            normal?.let { mat.inNormal = it }
            camPos?.let { mat.inCamPos = it }
            fragPos?.let { mat.inFragPos = it }
            return mat
        }

        fun pbrMaterialNode(reflectionMap: TextureCubeNode? = null, brdfLut: Texture2dNode? = null,
                            albedo: ShaderNodeIoVar? = null, normal: ShaderNodeIoVar? = null,
                            fragPos: ShaderNodeIoVar? = null, viewDir: ShaderNodeIoVar? = null): PbrMaterialNode {
            val mat = addNode(PbrMaterialNode(reflectionMap, brdfLut, stage))
            albedo?.let { mat.inAlbedo = it }
            normal?.let { mat.inNormal = it }
            viewDir?.let { mat.inViewDir = it }
            fragPos?.let { mat.inFragPos = it }
            return mat
        }

        fun pbrLightNode(): PbrLightNode {
            return addNode(PbrLightNode(stage))
        }

        fun colorOutput(color0: ShaderNodeIoVar? = null, channels: Int = 1, alpha: ShaderNodeIoVar? = null): FragmentColorOutNode {
            val colorOut = addNode(FragmentColorOutNode(stage, channels))
            color0?.let { colorOut.inColors[0] = it }
            colorOut.alpha = alpha
            return colorOut
        }

        fun discardAlpha(alpha: ShaderNodeIoVar? = null, alphaCutoff: ShaderNodeIoVar? = null): DiscardAlphaNode {
            val discardAlpha = addNode(DiscardAlphaNode(stage))
            alpha?.let { discardAlpha.inAlpha = it }
            alphaCutoff?.let { discardAlpha.inAlphaCutoff = it }
            return discardAlpha
        }

        fun depthOutput(depth: ShaderNodeIoVar? = null): FragmentDepthOutNode {
            val depthOut = addNode(FragmentDepthOutNode(stage))
            depth?.let { depthOut.inDepth = it }
            return depthOut
        }
    }
}

inline fun ShaderModel.vertexStage(block: ShaderModel.VertexStageBuilder.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    VertexStageBuilder().block()
}

inline fun ShaderModel.fragmentStage(block: ShaderModel.FragmentStageBuilder.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    FragmentStageBuilder().block()
}
