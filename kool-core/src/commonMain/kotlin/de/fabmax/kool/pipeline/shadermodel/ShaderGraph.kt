package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.DescriptorSetLayout
import de.fabmax.kool.pipeline.PushConstantRange
import de.fabmax.kool.pipeline.ShaderStage
import de.fabmax.kool.util.Color

class ShaderInterfaceIoVar(val location: Int, val variable: ModelVar)

open class ShaderGraph(val stage: ShaderStage) {
    val descriptorSet = DescriptorSetLayout.Builder()
    val pushConstants = PushConstantRange.Builder()

    val inputs = mutableListOf<ShaderInterfaceIoVar>()
    val outputs = mutableListOf<ShaderInterfaceIoVar>()

    protected val mutNodes = mutableListOf<ShaderNode>()
    val nodes: List<ShaderNode> get() = mutNodes

    internal var nextNodeId = 1

    fun <T: ShaderNode> findNode(name: String): T? {
        return nodes.find { it.name == name } as? T
    }

    fun addNode(node: ShaderNode) {
        if (node.graph !== this) {
            throw IllegalStateException("Node can only be added to it's parent graph")
        }
        mutNodes.add(node)
    }

    open fun setup() {
        nodes.forEach { it.setup(this) }
    }

    open fun generateCode(generator: CodeGenerator) {
        sortNodesByDependencies()
        nodes.forEach { it.generateCode(generator) }
    }

    private fun sortNodesByDependencies() {
        val sortedNodes = linkedSetOf<ShaderNode>()
        while (nodes.isNotEmpty()) {
            val ndIt = mutNodes.iterator()
            var anyAdded = false
            for (nd in ndIt) {
                if (sortedNodes.containsAll(nd.dependencies)) {
                    sortedNodes.add(nd)
                    ndIt.remove()
                    anyAdded = true
                }
            }
            if (!anyAdded) {
                println("Remaining nodes:")
                nodes.forEach { println(it.name) }
                throw IllegalStateException("Unable to resolve shader graph (circular or missing dependency?)")
            }
        }
        mutNodes.addAll(sortedNodes)
    }
}

class VertexShaderGraph : ShaderGraph(ShaderStage.VERTEX_SHADER) {
    val requiredVertexAttributes = mutableSetOf<Attribute>()
    var positionOutput = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO), null)
}

class FragmentShaderGraph : ShaderGraph(ShaderStage.FRAGMENT_SHADER) {
    var colorOutput = ShaderNodeIoVar(ModelVar4fConst(Color.MAGENTA), null)
}