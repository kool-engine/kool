package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Mesh
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class ShaderModel {
    val stages = mutableMapOf(
            ShaderStage.VERTEX_SHADER to VertexShaderGraph(),
            ShaderStage.FRAGMENT_SHADER to FragmentShaderGraph()
    )

    val vertexStage: VertexShaderGraph
        get() = stages[ShaderStage.VERTEX_SHADER] as VertexShaderGraph
    val fragmentStage: FragmentShaderGraph
        get() = stages[ShaderStage.FRAGMENT_SHADER] as FragmentShaderGraph

    fun setup(mesh: Mesh, buildCtx: Pipeline.BuildContext, ctx: KoolContext) {
        stages.values.forEach { it.setup() }

        setupAttributes(mesh, buildCtx)
        val descBuilder = DescriptorSetLayout.Builder()
        buildCtx.descriptorSetLayouts += descBuilder
        stages.values.forEach { stage ->
            descBuilder.descriptors += stage.descriptorSet.descriptors
            buildCtx.pushConstantRanges += stage.pushConstants
        }
    }

    private fun setupAttributes(mesh: Mesh, buildCtx: Pipeline.BuildContext) {
        val vertLayoutAttribs = mutableListOf<VertexLayout.Attribute>()
        val verts = mesh.meshData.vertexList

        vertexStage.requiredVertexAttributes.forEachIndexed { iAttrib, attrib ->
            if (!mesh.meshData.vertexAttributes.contains(attrib)) {
                throw NoSuchElementException("Mesh does not include required vertex attribute: ${attrib.name}")
            }
            val off = verts.attributeOffsets[attrib] ?: throw NoSuchElementException()
            vertLayoutAttribs += VertexLayout.Attribute(iAttrib, off, attrib.type, attrib.name)
        }

        if (buildCtx.vertexLayout.bindings.isNotEmpty()) {
            TODO("multiple attribute bindings are not yet implemented: attribute location must be changed")
        }

        buildCtx.vertexLayout.bindings += VertexLayout.Binding(0, InputRate.VERTEX, vertLayoutAttribs, verts.strideBytesF)
    }

    abstract inner class StageBuilder(val stage: ShaderGraph) {
        operator fun ShaderNode.unaryPlus() {
            stage.nodes += this
        }

        fun <T: ShaderNode> addNode(node: T): T {
            +node
            return node
        }

        fun pushConstantNode1f(name: String) = addNode(PushConstantNode1f(name))
        fun pushConstantNode2f(name: String) = addNode(PushConstantNode2f(name))
        fun pushConstantNode3f(name: String) = addNode(PushConstantNode3f(name))
        fun pushConstantNode4f(name: String) = addNode(PushConstantNode4f(name))

        fun textureNode(texName: String) = addNode(TextureNode(texName))

        fun textureSamplerNode(texNode: TextureNode, texCoords: ShaderNodeIoVar? = null): TextureSamplerNode {
            val texSampler = addNode(TextureSamplerNode(texNode))
            texCoords?.let { texSampler.inTexCoord = it }
            return texSampler
        }
    }

    inner class VertexStageBuilder : StageBuilder(vertexStage) {
        fun attributeNode(attribute: Attribute) = addNode(AttributeNode(attribute))

        fun stageInterfaceNode(name: String, input: ShaderNodeIoVar?): StageInterfaceNode {
            val ifNode = StageInterfaceNode(name)
            input?.let { ifNode.input = it }
            addNode(ifNode.vertexNode)
            fragmentStage.nodes += ifNode.fragmentNode
            return ifNode
        }

        fun mvpNode() = addNode(UniformBufferMvp())

        fun premultipliedMvpNode() = addNode(UniformBufferPremultipliedMvp())

        fun simpleVertexPositionNode() =
                vertexPositionNode(attributeNode(Attribute.POSITIONS).output, premultipliedMvpNode().outMvpMat)

        fun vertexPositionNode(inPosition: ShaderNodeIoVar? = null, inMvp: ShaderNodeIoVar? = null): VertexPosTransformNode {
            val pos = addNode(VertexPosTransformNode())
            inPosition?.let { pos.inPosition = it }
            inMvp?.let { pos.inMvp = it }
            return pos
        }
    }

    inner class FragmentStageBuilder : StageBuilder(fragmentStage) {
        fun unlitMaterialNode(albedo: ShaderNodeIoVar? = null): UnlitMaterialNode {
            val mat = addNode(UnlitMaterialNode())
            albedo?.let { mat.inAlbedo = it }
            return mat
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
