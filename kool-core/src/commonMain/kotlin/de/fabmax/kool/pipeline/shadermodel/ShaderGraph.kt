package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.DescriptorSetLayout
import de.fabmax.kool.pipeline.PushConstantRange
import de.fabmax.kool.pipeline.ShaderStage

class ShaderInterfaceIoVar(val location: Int, val variable: ModelVar, val isFlat: Boolean, val locationInc: Int = 1)

open class ShaderGraph(val model: ShaderModel, val stage: ShaderStage) {
    val descriptorSet = DescriptorSetLayout.Builder()
    val pushConstants = PushConstantRange.Builder()

    val inputs = mutableListOf<ShaderInterfaceIoVar>()
    private val mutOutputs = mutableListOf<ShaderInterfaceIoVar>()
    val outputs: List<ShaderInterfaceIoVar>
        get() = mutOutputs

    protected val mutNodes = mutableListOf<ShaderNode>()
    val nodes: List<ShaderNode> get() = mutNodes

    var nextNodeId = 1
        internal set

    inline fun <reified T: ShaderNode> findNode(name: String): T? {
        return nodes.find { it.name == name && it is T } as T?
    }

    fun findNodeByName(name: String): ShaderNode? {
        return nodes.find { it.name == name}
    }

    fun addStageOutput(output: ModelVar, isFlat: Boolean, locationInc: Int = 1): ShaderInterfaceIoVar {
        val location = mutOutputs.sumOf { it.locationInc }
        val ifVar = ShaderInterfaceIoVar(location, output, isFlat, locationInc)
        mutOutputs += ifVar
        return ifVar
    }

    fun addNode(node: ShaderNode) {
        if (node.graph !== this) {
            throw IllegalStateException("Node can only be added to it's parent graph")
        }
        mutNodes.add(node)
    }

    open fun clear() {
        descriptorSet.clear()
        pushConstants.clear()
        inputs.clear()
        mutOutputs.clear()
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
                println("${model.modelInfo} - Remaining nodes:")
                nodes.forEach {
                    println(it.name)
                    it.dependencies.forEach { dep ->
                        val state = if (sortedNodes.contains(dep)) "[ok]" else "[missing]"
                        println("    -> ${dep.name} $state")
                    }
                }
                throw IllegalStateException("Unable to resolve shader graph (circular or missing dependency?)")
            }
        }
        mutNodes.addAll(sortedNodes)
    }
}

class VertexShaderGraph(model: ShaderModel) : ShaderGraph(model, ShaderStage.VERTEX_SHADER) {
    val requiredVertexAttributes = mutableSetOf<Attribute>()
    val requiredInstanceAttributes = mutableSetOf<Attribute>()
    var positionOutput = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO), null)
}

class FragmentShaderGraph(model: ShaderModel) : ShaderGraph(model, ShaderStage.FRAGMENT_SHADER)