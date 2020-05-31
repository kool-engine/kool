package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.math.Vec3f

abstract class MathOpNode(name: String, graph: ShaderGraph) : ShaderNode("multiply_${graph.nextNodeId}", graph) {
    var left = ShaderNodeIoVar(ModelVar3fConst(Vec3f.X_AXIS))
        set(value) {
            output = ShaderNodeIoVar(ModelVar("${name}_out", value.variable.type), this)
            field = value
        }
    var right = ShaderNodeIoVar(ModelVar1fConst(1f))
    var output = ShaderNodeIoVar(ModelVar3f("${name}_out"), this)
        private set

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(left, right)
    }
}

class AddNode(graph: ShaderGraph) : MathOpNode("add_${graph.nextNodeId}", graph) {
    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("${output.declare()} = $left + $right;")
    }
}

class SubtractNode(graph: ShaderGraph) : MathOpNode("add_${graph.nextNodeId}", graph) {
    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("${output.declare()} = $left - $right;")
    }
}

class DivideNode(graph: ShaderGraph) : MathOpNode("divide_${graph.nextNodeId}", graph) {
    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("${output.declare()} = $left / $right;")
    }
}

class MultiplyNode(graph: ShaderGraph) : MathOpNode("multiply_${graph.nextNodeId}", graph) {
    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("${output.declare()} = $left * $right;")
    }
}

class NormalizeNode(graph: ShaderGraph) : ShaderNode("normalize_${graph.nextNodeId}", graph) {
    var input = ShaderNodeIoVar(ModelVar3fConst(Vec3f.X_AXIS))
    val output = ShaderNodeIoVar(ModelVar3f("${name}_out"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(input)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("${output.declare()} = normalize(${input.ref3f()});")
    }
}
