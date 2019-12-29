package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.Color

class ShaderInterfaceIoVar(val location: Int, val variable: ModelVar)

open class ShaderGraph(val stage: ShaderStage) {
    val descriptorSet = DescriptorSetLayout.Builder()
    val pushConstants = mutableListOf<PushConstantRange.Builder>()

    val inputs = mutableListOf<ShaderInterfaceIoVar>()
    val outputs = mutableListOf<ShaderInterfaceIoVar>()

    val nodes = mutableListOf<ShaderNode>()

    open fun setup() {
        nodes.forEach { it.setup(this) }
    }

    open fun generateCode(generator: CodeGenerator, pipeline: Pipeline, ctx: KoolContext) {
        sortNodesByDependencies()
        nodes.forEach { it.generateCode(generator, pipeline, ctx) }
    }

    private fun sortNodesByDependencies() {
        val sortedNodes = linkedSetOf<ShaderNode>()
        while (nodes.isNotEmpty()) {
            val ndIt = nodes.iterator()
            var anyAdded = false
            for (nd in ndIt) {
                if (sortedNodes.containsAll(nd.dependsOn)) {
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
        nodes.addAll(sortedNodes)
    }
}

class VertexShaderGraph : ShaderGraph(ShaderStage.VERTEX_SHADER) {
    val requiredVertexAttributes = mutableSetOf<Attribute>()
    var positionOutput = ShaderNodeIoVar(null, ModelVar4fConst(Vec4f.ZERO))
}

class FragmentShaderGraph : ShaderGraph(ShaderStage.FRAGMENT_SHADER) {
    var colorOutput = ShaderNodeIoVar(null, ModelVar4fConst(Color.MAGENTA))
}