package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.math.Vec3f

abstract class MathOpNode(name: String, graph: ShaderGraph) : ShaderNode("math_${graph.nextNodeId}", graph) {
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

class MinNode(graph: ShaderGraph) : MathOpNode("min_${graph.nextNodeId}", graph) {
    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("${output.declare()} = min($left, $right);")
    }
}

class MaxNode(graph: ShaderGraph) : MathOpNode("max_${graph.nextNodeId}", graph) {
    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("${output.declare()} = max($left, $right);")
    }
}

class MixNode(graph: ShaderGraph) : MathOpNode("mix_${graph.nextNodeId}", graph) {
    var mixFac = ShaderNodeIoVar(ModelVar1fConst(0.5f))

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(mixFac)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("${output.declare()} = mix($left, $right, ${mixFac.ref1f()});")
    }
}

class VecFromColorNode(graph: ShaderGraph) : ShaderNode("vecFromColor_${graph.nextNodeId}", graph) {
    var input = ShaderNodeIoVar(ModelVar3fConst(Vec3f.X_AXIS))
    val output = ShaderNodeIoVar(ModelVar3f("${name}_out"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(input)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("${output.declare()} = (${input.ref3f()} - 0.5) * 2.0;")
    }
}

class DotNode(graph: ShaderGraph) : ShaderNode("dot_${graph.nextNodeId}", graph) {
    var inA = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    var inB = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    val output = ShaderNodeIoVar(ModelVar1f("${name}_out"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inA, inB)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("${output.declare()} = dot($inA, $inB);")
    }
}

class DistanceNode(graph: ShaderGraph) : ShaderNode("dist_${graph.nextNodeId}", graph) {
    var inA = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    var inB = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    val output = ShaderNodeIoVar(ModelVar1f("${name}_out"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inA, inB)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("${output.declare()} = length($inA - $inB);")
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

class ViewDirNode(graph: ShaderGraph) : ShaderNode("viewDir_${graph.nextNodeId}", graph) {
    var inCamPos = ShaderNodeIoVar(ModelVar3fConst(Vec3f.Z_AXIS))
    var inWorldPos = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    val output = ShaderNodeIoVar(ModelVar3f("${name}_out"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inCamPos, inWorldPos)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("${output.declare()} = normalize(${inWorldPos.ref3f()} - ${inCamPos.ref3f()});")
    }
}

class ReflectNode(graph: ShaderGraph) : ShaderNode("reflect_${graph.nextNodeId}", graph) {
    var inDirection = ShaderNodeIoVar(ModelVar3fConst(Vec3f.X_AXIS))
    var inNormal = ShaderNodeIoVar(ModelVar3fConst(Vec3f.Y_AXIS))
    val outDirection = ShaderNodeIoVar(ModelVar3f("${name}_out"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inDirection, inNormal)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("${outDirection.declare()} = reflect(${inDirection.ref3f()}, ${inNormal.ref3f()});")
    }
}

class RefractNode(graph: ShaderGraph) : ShaderNode("refract_${graph.nextNodeId}", graph) {
    var inDirection = ShaderNodeIoVar(ModelVar3fConst(Vec3f.X_AXIS))
    var inNormal = ShaderNodeIoVar(ModelVar3fConst(Vec3f.Y_AXIS))
    var inIor = ShaderNodeIoVar(ModelVar1fConst(1.4f))
    val outDirection = ShaderNodeIoVar(ModelVar3f("${name}_out"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inDirection, inNormal)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("${outDirection.declare()} = refract(${inDirection.ref3f()}, ${inNormal.ref3f()}, ${inIor.ref1f()});")
    }
}
