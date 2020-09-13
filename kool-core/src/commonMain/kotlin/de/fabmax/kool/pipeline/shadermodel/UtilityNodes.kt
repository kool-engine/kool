package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.GlslType
import de.fabmax.kool.pipeline.ShaderStage

class FlipBacksideNormalNode(graph: ShaderGraph) :
        ShaderNode("flipBacksideNormal_${graph.nextNodeId}", graph, ShaderStage.FRAGMENT_SHADER.mask) {

    var inNormal = ShaderNodeIoVar(ModelVar3fConst(Vec3f.Y_AXIS))
    val outNormal: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3f("${name}_outNormal"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inNormal)
    }

    override fun generateCode(generator: CodeGenerator) {
        super.generateCode(generator)

        generator.appendMain("""
            ${outNormal.declare()} = ${inNormal.ref3f()} * (float(gl_FrontFacing) * 2.0 - 1.0);
        """)
    }
}

class SplitNode(val outChannels: String, graph: ShaderGraph) : ShaderNode("split_${graph.nextNodeId}", graph) {
    var input = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO))

    val output: ShaderNodeIoVar = when (outChannels.length) {
        1 -> ShaderNodeIoVar(ModelVar1f("${name}_out"), this)
        2 -> ShaderNodeIoVar(ModelVar2f("${name}_out"), this)
        3 -> ShaderNodeIoVar(ModelVar3f("${name}_out"), this)
        4 -> ShaderNodeIoVar(ModelVar4f("${name}_out"), this)
        else -> throw IllegalArgumentException("outChannels parameter must be between 1 and 4 characters long (e.g. 'xyz')")
    }

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(input)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("${output.declare()} = ${input.name}.$outChannels;")
    }
}

class CombineNode(outType: GlslType, graph: ShaderGraph) : ShaderNode("combine_${graph.nextNodeId}", graph) {
    var inX = ShaderNodeIoVar(ModelVar1fConst(0f))
    var inY = ShaderNodeIoVar(ModelVar1fConst(0f))
    var inZ = ShaderNodeIoVar(ModelVar1fConst(0f))
    var inW = ShaderNodeIoVar(ModelVar1fConst(0f))

    var output = when (outType) {
        GlslType.VEC_2F -> ShaderNodeIoVar(ModelVar2f("${name}_out"), this)
        GlslType.VEC_3F -> ShaderNodeIoVar(ModelVar3f("${name}_out"), this)
        GlslType.VEC_4F -> ShaderNodeIoVar(ModelVar4f("${name}_out"), this)
        else -> throw IllegalArgumentException("Only allowed out types are VEC_2F, VEC_3F and VEC_4F")
    }

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inX, inY, inZ, inW)
    }

    override fun generateCode(generator: CodeGenerator) {
        when (output.variable.type) {
            GlslType.VEC_2F -> generator.appendMain("${output.declare()} = vec2(${inX.ref1f()}, ${inY.ref1f()});")
            GlslType.VEC_3F -> generator.appendMain("${output.declare()} = vec3(${inX.ref1f()}, ${inY.ref1f()}, ${inZ.ref1f()});")
            GlslType.VEC_4F -> generator.appendMain("${output.declare()} = vec4(${inX.ref1f()}, ${inY.ref1f()}, ${inZ.ref1f()}, ${inW.ref1f()});")
            else -> throw IllegalArgumentException("Only allowed out types are VEC_2F, VEC_3F and VEC_4F")
        }
    }
}

class Combine31Node(graph: ShaderGraph) : ShaderNode("combine31_${graph.nextNodeId}", graph) {
    var inXyz = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    var inW = ShaderNodeIoVar(ModelVar1fConst(0f))

    var output = ShaderNodeIoVar(ModelVar4f("${name}_out"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inXyz, inW)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("${output.declare()} = vec4(${inXyz.ref3f()}, ${inW.ref1f()});")
    }
}

class AttributeNode(val attribute: Attribute, graph: ShaderGraph) :
        ShaderNode(attribute.name, graph, ShaderStage.VERTEX_SHADER.mask) {
    val output = ShaderNodeIoVar(ModelVar(attribute.name, attribute.type), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        shaderGraph as VertexShaderGraph
        shaderGraph.requiredVertexAttributes += attribute
    }
}

class InstanceAttributeNode(val attribute: Attribute, graph: ShaderGraph) :
        ShaderNode(attribute.name, graph, ShaderStage.VERTEX_SHADER.mask) {
    val output = ShaderNodeIoVar(ModelVar(attribute.name, attribute.type), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        shaderGraph as VertexShaderGraph
        shaderGraph.requiredInstanceAttributes += attribute
    }
}

class StageInterfaceNode(val name: String, vertexGraph: ShaderGraph, fragmentGraph: ShaderGraph) {
    // written in source stage (i.e. vertex shader)
    var input = ShaderNodeIoVar(ModelVar1fConst(0f))
        set(value) {
            output = ShaderNodeIoVar(ModelVar(name, value.variable.type), fragmentNode)
            field = value
        }
    // accessible in target stage (i.e. fragment shader)
    var output = ShaderNodeIoVar(ModelVar1f(name))
        private set

    var isFlat = false

    private lateinit var ifVar: ShaderInterfaceIoVar

    val vertexNode = InputNode(vertexGraph)
    val fragmentNode = OutputNode(fragmentGraph)

    inner class InputNode(graph: ShaderGraph) : ShaderNode(name, graph, ShaderStage.VERTEX_SHADER.mask) {
        val ifNode = this@StageInterfaceNode
        var input: ShaderNodeIoVar
            get() = ifNode.input
            set(value) { ifNode.input = value }

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(input)
            ifVar = shaderGraph.addStageOutput(output.variable, isFlat)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("${output.name} = ${input.refAsType(output.variable.type)};")
        }
    }

    inner class OutputNode(graph: ShaderGraph) : ShaderNode(name, graph, ShaderStage.FRAGMENT_SHADER.mask) {
        val ifNode = this@StageInterfaceNode
        val output: ShaderNodeIoVar
            get() = ifNode.output

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            shaderGraph.inputs += ifVar
        }
    }
}

class ScreenCoordNode(graph: ShaderGraph) : ShaderNode("screenCoord_${graph.nextNodeId}", graph, ShaderStage.FRAGMENT_SHADER.mask) {
    var inViewport = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO))

    val outScreenCoord = ShaderNodeIoVar(ModelVar4f("gl_FragCoord"), this)
    val outNormalizedScreenCoord = ShaderNodeIoVar(ModelVar3f("${name}_outNormalizedCoord"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inViewport)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("""
            ${outNormalizedScreenCoord.declare()} = ${outScreenCoord.ref3f()};
            $outNormalizedScreenCoord.xy - ${inViewport.ref2f()};
            $outNormalizedScreenCoord.xy /= ${inViewport.ref4f()}.zw;
        """)
    }
}

class FullScreenQuadTexPosNode(graph: ShaderGraph) : ShaderNode("fullScreenQuad_${graph.nextNodeId}", graph) {
    var inTexCoord = ShaderNodeIoVar(ModelVar2fConst(Vec2f.ZERO))
    var inDepth = ShaderNodeIoVar(ModelVar1fConst(0.999f))
    val outQuadPos = ShaderNodeIoVar(ModelVar4f("${name}_outPos"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inTexCoord, inDepth)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("${outQuadPos.declare()} = vec4(${inTexCoord.ref2f()} * 2.0 - 1.0, ${inDepth.ref1f()}, 1.0);")
    }
}

class GetMorphWeightNode(val iWeight: Int, graph: ShaderGraph) : ShaderNode("getMorphWeight_${graph.nextNodeId}", graph) {
    var inWeights0 = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO))
    var inWeights1 = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO))
    val outWeight = ShaderNodeIoVar(ModelVar1f("${name}_outW"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inWeights0, inWeights1)
    }

    override fun generateCode(generator: CodeGenerator) {
        val inW = if (iWeight < 4) { inWeights0 } else { inWeights1 }
        val c = when (iWeight % 4) {
            0 -> "x"
            1 -> "y"
            2 -> "z"
            else -> "w"
        }
        generator.appendMain("${outWeight.declare()} = ${inW.ref4f()}.$c;")
    }
}

class NamedVariableNode(name: String, graph: ShaderGraph) : ShaderNode(name, graph) {
    var input = ShaderNodeIoVar(ModelVar1fConst(0f))
        set(value) {
            field = value
            output = ShaderNodeIoVar(ModelVar(name, value.variable.type), this)
        }

    var output = ShaderNodeIoVar(ModelVar(name, GlslType.FLOAT), this)
        private set

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(input)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("${output.declare()} = $input;")
    }
}

class PointSizeNode(graph: ShaderGraph) : ShaderNode("pointSz", graph, ShaderStage.VERTEX_SHADER.mask) {
    var inSize = ShaderNodeIoVar(ModelVar1fConst(1f))

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inSize)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("gl_PointSize = ${inSize.ref1f()};")
    }
}