package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.ShaderStage
import de.fabmax.kool.util.Color

class TextureSamplerNode(val texture: TextureNode, graph: ShaderGraph, val premultiply: Boolean = true) :
        ShaderNode("tex_sampler_${texture.name}_${graph.nextNodeId}", graph) {

    var inTexCoord = ShaderNodeIoVar(ModelVar2fConst(Vec2f.ZERO))
    val outColor: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar4f("${name}_outColor"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(texture)
        dependsOn(inTexCoord)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("${outColor.declare()} = ${generator.sampleTexture2d(texture.name, inTexCoord.ref2f())};")
        if (premultiply) {
            generator.appendMain("${outColor.ref3f()} *= ${outColor.ref4f()}.a;")
        }
    }
}

class CubeMapSamplerNode(val cubeMap: CubeMapNode, graph: ShaderGraph, val premultiply: Boolean = true) :
        ShaderNode("cubeSampler_${cubeMap.name}_${graph.nextNodeId}", graph) {

    var inTexCoord = ShaderNodeIoVar(ModelVar3fConst(Vec3f.NEG_X_AXIS))
    val outColor: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar4f("${name}_outColor"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(cubeMap)
        dependsOn(inTexCoord)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("${outColor.declare()} = ${generator.sampleTextureCube(cubeMap.name, inTexCoord.ref3f())};")
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

class HdrToLdrNode(graph: ShaderGraph) : ShaderNode("hdrToLdr_${graph.nextNodeId}", graph) {
    var inColor = ShaderNodeIoVar(ModelVar4fConst(Color.MAGENTA))
    var inGamma = ShaderNodeIoVar(ModelVar1fConst(2.2f))
    var inExposure = ShaderNodeIoVar(ModelVar1fConst(0.4f))
    var inContrast = ShaderNodeIoVar(ModelVar1fConst(0.85f))
    val outColor = ShaderNodeIoVar(ModelVar4f("${name}_outColor"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inColor, inGamma, inExposure, inContrast)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("""
            // tone mapping
            // simple method: 0..infinity -> [0..1)
            //vec3 ${name}_color = ${inColor.ref3f()} / (${inColor.ref3f()} + vec3(1.0));
            
            // slightly advanced method 0..exposure^(-1/contrast) -> [0..1]
            // exposure = 0.4, contrast = 0.85 -> [0..~3] with decent saturation
            vec3 ${name}_color = ${inExposure.ref1f()} * pow(${inColor.ref3f()}, vec3(${inContrast.ref1f()}));
            
            // gamma correction
            ${outColor.declare()} = vec4(pow(${name}_color, vec3(1.0/${inGamma.ref1f()})), ${inColor.ref4f()}.a);
        """)
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