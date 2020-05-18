package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.drawqueue.DrawCommand
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.CascadedShadowMap
import de.fabmax.kool.util.MeshInstanceList
import de.fabmax.kool.util.SimpleShadowMap
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class ShaderModel(val modelInfo: String = "") {
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

    fun setup(mesh: Mesh, buildCtx: Pipeline.BuildContext) {
        stages.values.forEach { it.setup() }

        setupAttributes(mesh, buildCtx)

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
            buildCtx.descriptorSetLayouts += descBuilder
        }
        if (pushBuilder.pushConstants.isNotEmpty()) {
            buildCtx.pushConstantRanges += pushBuilder
            if (pushUpdateFuns.isNotEmpty()) {
                pushBuilder.onUpdate = { rng, cmd ->
                    for (i in pushUpdateFuns.indices) {
                        pushUpdateFuns[i](rng, cmd)
                    }
                }
            }
        }
    }

    private fun setupAttributes(mesh: Mesh, buildCtx: Pipeline.BuildContext) {
        var attribLocation = 0
        val verts = mesh.geometry
        val vertLayoutAttribs = mutableListOf<VertexLayout.VertexAttribute>()
        vertexStageGraph.requiredVertexAttributes.forEach { attrib ->
            val off = verts.attributeOffsets[attrib] ?:
                    throw NoSuchElementException("Mesh does not include required vertex attribute: ${attrib.name}")
            vertLayoutAttribs += VertexLayout.VertexAttribute(attribLocation++, off, attrib)
        }
        buildCtx.vertexLayout.bindings += VertexLayout.Binding(0, InputRate.VERTEX, vertLayoutAttribs, verts.strideBytesF)

        val insts = mesh.instances
        if (insts != null) {
            val instLayoutAttribs = mutableListOf<VertexLayout.VertexAttribute>()
            vertexStageGraph.requiredInstanceAttributes.forEach { attrib ->
                val off = insts.attributeOffsets[attrib] ?:
                        throw NoSuchElementException("Mesh does not include required instance attribute: ${attrib.name}")
                instLayoutAttribs += VertexLayout.VertexAttribute(attribLocation++, off, attrib)
            }
            buildCtx.vertexLayout.bindings += VertexLayout.Binding(1, InputRate.INSTANCE, instLayoutAttribs, insts.strideBytesF)
        } else if (vertexStageGraph.requiredInstanceAttributes.isNotEmpty()) {
            throw IllegalStateException("Shader model requires instance attributes, but mesh doesn't provide any")
        }
    }

    abstract inner class StageBuilder(val stage: ShaderGraph) {
        fun <T: ShaderNode> addNode(node: T): T {
            stage.addNode(node)
            return node
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

        fun normalizeNode(input: ShaderNodeIoVar? = null): NormalizeNode {
            val nrmNode = addNode(NormalizeNode(stage))
            input?.let { nrmNode.input = input }
            return nrmNode
        }

        fun normalMapNode(texture: TextureNode, textureCoord: ShaderNodeIoVar? = null,
                          normal: ShaderNodeIoVar? = null, tangent: ShaderNodeIoVar? = null): NormalMapNode {
            val nrmMappingNd = addNode(NormalMapNode(texture, stage))
            textureCoord?.let { nrmMappingNd.inTexCoord = it }
            normal?.let { nrmMappingNd.inNormal = it }
            tangent?.let { nrmMappingNd.inTangent = it }
            return nrmMappingNd
        }

        fun displacementMapNode(texture: TextureNode, textureCoord: ShaderNodeIoVar? = null, pos: ShaderNodeIoVar? = null,
                                normal: ShaderNodeIoVar? = null, strength: ShaderNodeIoVar? = null): DisplacementMapNode {
            val dispMappingNd = addNode(DisplacementMapNode(texture, stage))
            textureCoord?.let { dispMappingNd.inTexCoord = it }
            pos?.let { dispMappingNd.inPosition = it }
            normal?.let { dispMappingNd.inNormal = it }
            strength?.let { dispMappingNd.inStrength = it }
            return dispMappingNd
        }

        fun gammaNode(inputColor: ShaderNodeIoVar? = null): GammaNode {
            val gamma = addNode(GammaNode(stage))
            inputColor?.let { gamma.inColor = it }
            return gamma
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

        fun textureNode(texName: String) = addNode(TextureNode(stage, texName))

        fun textureSamplerNode(texNode: TextureNode, texCoords: ShaderNodeIoVar? = null, premultiply: Boolean = false): TextureSamplerNode {
            val texSampler = addNode(TextureSamplerNode(texNode, stage, premultiply))
            texCoords?.let { texSampler.inTexCoord = it }
            return texSampler
        }

        fun equiRectSamplerNode(texNode: TextureNode, texCoords: ShaderNodeIoVar? = null, premultiply: Boolean = false): EquiRectSamplerNode {
            val texSampler = addNode(EquiRectSamplerNode(texNode, stage, premultiply))
            texCoords?.let { texSampler.inTexCoord = it }
            return texSampler
        }

        fun cubeMapNode(texName: String) = addNode(CubeMapNode(stage, texName))

        fun cubeMapSamplerNode(texNode: CubeMapNode, texCoords: ShaderNodeIoVar? = null, premultiply: Boolean = false): CubeMapSamplerNode {
            val texSampler = addNode(CubeMapSamplerNode(texNode, stage, premultiply))
            texCoords?.let { texSampler.inTexCoord = it }
            return texSampler
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
        fun attributeNode(attribute: Attribute) = addNode(AttributeNode(attribute, stage))

        fun instanceAttrModelMat() = instanceAttributeNode(MeshInstanceList.MODEL_MAT)
        fun instanceAttributeNode(attribute: Attribute) = addNode(InstanceAttributeNode(attribute, stage))

        fun stageInterfaceNode(name: String, input: ShaderNodeIoVar? = null): StageInterfaceNode {
            val ifNode = StageInterfaceNode(name, vertexStageGraph, fragmentStageGraph)
            input?.let { ifNode.input = it }
            addNode(ifNode.vertexNode)
            fragmentStageGraph.addNode(ifNode.fragmentNode)
            return ifNode
        }

        fun mvpNode() = addNode(UniformBufferMvp(stage))

        fun premultipliedMvpNode() = addNode(UniformBufferPremultipliedMvp(stage))

        fun simpleShadowMapNode(shadowMap: SimpleShadowMap, depthMapName: String, inVertexPos: ShaderNodeIoVar? = null, inModelMat: ShaderNodeIoVar? = null): SimpleShadowMapNode {
            val shadowMapNode = SimpleShadowMapNode(shadowMap, vertexStageGraph, fragmentStageGraph)
            addNode(shadowMapNode.vertexNode)
            fragmentStageGraph.addNode(shadowMapNode.fragmentNode)

            val depthMap = TextureNode(fragmentStageGraph, depthMapName).apply { isDepthTexture = true }
            fragmentStageGraph.addNode(depthMap)

            inVertexPos?.let { shadowMapNode.inPosition = it }
            inModelMat?.let { shadowMapNode.inModelMat = it }
            shadowMapNode.depthMap = depthMap

            return shadowMapNode
        }

        fun cascadedShadowMapNode(cascadedShadowMap: CascadedShadowMap, depthMapName: String,
                                  inClipPos: ShaderNodeIoVar? = null, inVertexPos: ShaderNodeIoVar? = null,
                                  inModelMat: ShaderNodeIoVar? = null): CascadedShadowMapNode {
            val shadowMapNode = CascadedShadowMapNode(cascadedShadowMap, vertexStageGraph, fragmentStageGraph)
            addNode(shadowMapNode.vertexNode)
            fragmentStageGraph.addNode(shadowMapNode.fragmentNode)

            val depthMap = TextureNode(fragmentStageGraph, depthMapName).apply {
                isDepthTexture = true
                arraySize = cascadedShadowMap.numCascades
            }
            fragmentStageGraph.addNode(depthMap)

            inVertexPos?.let { shadowMapNode.inPosition = it }
            inModelMat?.let { shadowMapNode.inModelMat = it }
            inClipPos?.let { shadowMapNode.inClipPosition = it }
            shadowMapNode.depthMap = depthMap

            return shadowMapNode
        }

        fun simpleVertexPositionNode() = vertexPositionNode(attrPositions().output, premultipliedMvpNode().outMvpMat)

        fun transformNode(input: ShaderNodeIoVar? = null, inMat: ShaderNodeIoVar? = null,
                          w: Float = 1f, invert: Boolean = false): TransformNode {
            val tf = addNode(TransformNode(stage, w, invert))
            input?.let { tf.input = it }
            inMat?.let { tf.inMat = it }
            return tf
        }

        fun vertexPositionNode(inPosition: ShaderNodeIoVar? = null, inMvp: ShaderNodeIoVar? = null): VertexPosTransformNode {
            val pos = addNode(VertexPosTransformNode(stage))
            inPosition?.let { pos.inPosition = it }
            inMvp?.let { pos.inMvp = it }
            return pos
        }
    }

    inner class FragmentStageBuilder : StageBuilder(fragmentStageGraph) {

        fun multiLightNode(maxLights: Int = 4) = addNode(MultiLightNode(stage, maxLights))

        fun unlitMaterialNode(albedo: ShaderNodeIoVar? = null): UnlitMaterialNode {
            val mat = addNode(UnlitMaterialNode(stage))
            albedo?.let { mat.inColor = it }
            return mat
        }

        /**
         * Phong shader with multiple light sources. The shader assumes normals, fragment and cam positions are in
         * world space.
         */
        fun phongMaterialNode(albedo: ShaderNodeIoVar? = null, normal: ShaderNodeIoVar? = null,
                              fragPos: ShaderNodeIoVar? = null, camPos: ShaderNodeIoVar? = null, lightNode: LightNode): PhongMaterialNode {
            val mat = addNode(PhongMaterialNode(lightNode, stage))
            albedo?.let { mat.inAlbedo = it }
            normal?.let { mat.inNormal = it }
            camPos?.let { mat.inCamPos = it }
            fragPos?.let { mat.inFragPos = it }
            return mat
        }

        fun pbrMaterialNode(lightNode: LightNode, reflectionMap: CubeMapNode? = null, brdfLut: TextureNode? = null,
                            albedo: ShaderNodeIoVar? = null, normal: ShaderNodeIoVar? = null,
                            fragPos: ShaderNodeIoVar? = null, camPos: ShaderNodeIoVar? = null): PbrMaterialNode {
            val mat = addNode(PbrMaterialNode(lightNode, reflectionMap, brdfLut, stage))
            albedo?.let { mat.inAlbedo = it }
            normal?.let { mat.inNormal = it }
            camPos?.let { mat.inCamPos = it }
            fragPos?.let { mat.inFragPos = it }
            return mat
        }

        fun colorOutput(color0: ShaderNodeIoVar? = null, channels: Int = 1): FragmentColorOutNode {
            val colorOut = addNode(FragmentColorOutNode(stage, channels))
            color0?.let { colorOut.inColors[0] = color0 }
            return colorOut
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
