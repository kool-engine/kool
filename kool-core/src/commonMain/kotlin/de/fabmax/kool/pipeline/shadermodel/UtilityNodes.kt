package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.ShaderStage
import de.fabmax.kool.util.Color

class TextureSamplerNode(val texture: TextureNode, graph: ShaderGraph, val premultiply: Boolean = true) :
        ShaderNode("Sample Texture ${texture.texName}", graph) {

    var inTexCoord = ShaderNodeIoVar(ModelVar2fConst(Vec2f.ZERO), this)
    val outColor: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar4f("texSampler_${nodeId}_outColor"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(texture)
        dependsOn(inTexCoord)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("${outColor.declare()} = ${generator.sampleTexture2d(texture.texName, inTexCoord.ref2f())};")
        if (premultiply) {
            generator.appendMain("${outColor.ref3f()} *= ${outColor.ref4f()}.a;")
        }
    }
}

class AttributeNode(val attribute: Attribute, graph: ShaderGraph) :
        ShaderNode("Vertex Attribute ${attribute.name}", graph, ShaderStage.VERTEX_SHADER.mask) {
    val output = ShaderNodeIoVar(ModelVar(attribute.glslSrcName, attribute.type), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        shaderGraph as VertexShaderGraph
        shaderGraph.requiredVertexAttributes += attribute
    }
}

class PremultiplyColorNode(graph: ShaderGraph) : ShaderNode("Pre-Multiply Color", graph) {
    var inColor = ShaderNodeIoVar(ModelVar4fConst(Color.MAGENTA))
    val outColor = ShaderNodeIoVar(ModelVar4f("preMultColor_$nodeId"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inColor)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("${outColor.declare()} = vec4(${inColor.ref3f()} * ${inColor.ref4f()}.a, ${inColor.ref4f()}.a);")
    }
}

// fixme: for now this assumes the only stages are vertex and fragment
class StageInterfaceNode(val name: String, vertexGraph: ShaderGraph, fragmentGraph: ShaderGraph) {
    // written in source stage (i.e. vertex shader)
    var input = ShaderNodeIoVar(ModelVar1fConst(0f))
        set(value) {
            field = value
            output = ShaderNodeIoVar(ModelVar("stageIf_${name}", value.variable.type), fragmentNode)
        }

    // accessible in target stage (i.e. fragment shader)
    lateinit var output: ShaderNodeIoVar

    private var layout = 0

    val vertexNode = object : ShaderNode("Stage Interface Src $name", vertexGraph, ShaderStage.VERTEX_SHADER.mask) {
        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(input)
            layout = shaderGraph.outputs.size
            shaderGraph.outputs += ShaderInterfaceIoVar(layout, output.variable)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("${output.name} = ${input.refAsType(output.variable.type)};")
        }
    }

    val fragmentNode = object : ShaderNode("Stage Interface Dst $name", fragmentGraph, ShaderStage.FRAGMENT_SHADER.mask) {
        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            shaderGraph.inputs += ShaderInterfaceIoVar(layout, output.variable)
        }
    }
}