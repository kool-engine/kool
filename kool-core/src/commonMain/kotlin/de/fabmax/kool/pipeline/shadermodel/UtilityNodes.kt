package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.GlslType
import de.fabmax.kool.pipeline.ShaderStage

class TextureSamplerNode(val texture: TextureNode, graph: ShaderGraph, val premultiply: Boolean = false) :
        ShaderNode("tex_sampler_${texture.name}_${graph.nextNodeId}", graph) {

    var inTexCoord = ShaderNodeIoVar(ModelVar2fConst(Vec2f.ZERO))
    val outColor: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar4f("${name}_outColor"), this)
    var texLod: ShaderNodeIoVar? = null

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(texture)
        dependsOn(inTexCoord)
        texLod?.let { dependsOn(it) }
    }

    override fun generateCode(generator: CodeGenerator) {
        val lod = texLod
        if (lod != null) {
            generator.appendMain("${outColor.declare()} = ${generator.sampleTexture2d(texture.name, inTexCoord.ref2f(), lod.ref1f())};")
        } else {
            generator.appendMain("${outColor.declare()} = ${generator.sampleTexture2d(texture.name, inTexCoord.ref2f())};")
        }
        if (premultiply) {
            generator.appendMain("${outColor.ref3f()} *= ${outColor.ref4f()}.a;")
        }
    }
}

class NoiseTextureSamplerNode(val texture: TextureNode, graph: ShaderGraph) :
        ShaderNode("noise_sampler_${texture.name}_${graph.nextNodeId}", graph) {

    var inTexSize = ShaderNodeIoVar(ModelVar2iConst(Vec2i(16, 16)))
    val outNoise: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar4f("${name}_outNoise"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(texture)
        dependsOn(inTexSize)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("""
            vec2 ${name}_texCoord = (vec2(ivec2(gl_FragCoord.xy) % ${inTexSize.ref2i()}) + 0.5) / ${inTexSize.ref2f()};
            ${outNoise.declare()} = ${generator.sampleTexture2d(texture.name, "${name}_texCoord")};
        """)
    }
}

class CubeMapSamplerNode(val cubeMap: CubeMapNode, graph: ShaderGraph, val premultiply: Boolean = false) :
        ShaderNode("cubeSampler_${cubeMap.name}_${graph.nextNodeId}", graph) {

    var inTexCoord = ShaderNodeIoVar(ModelVar3fConst(Vec3f.NEG_X_AXIS))
    val outColor: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar4f("${name}_outColor"), this)
    var texLod: ShaderNodeIoVar? = null

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(cubeMap)
        dependsOn(inTexCoord)
        texLod?.let { dependsOn(it) }
    }

    override fun generateCode(generator: CodeGenerator) {
        val lod = texLod
        if (lod != null) {
            generator.appendMain("${outColor.declare()} = ${generator.sampleTextureCube(cubeMap.name, inTexCoord.ref3f(), lod.ref1f())};")
        } else {
            generator.appendMain("${outColor.declare()} = ${generator.sampleTextureCube(cubeMap.name, inTexCoord.ref3f())};")
        }
        if (premultiply) {
            generator.appendMain("${outColor.ref3f()} *= ${outColor.ref4f()}.a;")
        }
    }
}

class EquiRectSamplerNode(val texture: TextureNode, graph: ShaderGraph, val premultiply: Boolean = false) :
        ShaderNode("equi_rect_sampler_${texture.name}_${graph.nextNodeId}", graph) {

    var inTexCoord = ShaderNodeIoVar(ModelVar3fConst(Vec3f.X_AXIS))
    val outColor: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar4f("${name}_outColor"), this)
    var texLod: ShaderNodeIoVar? = null

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(texture)
        dependsOn(inTexCoord)
        texLod?.let { dependsOn(it) }
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("""
            vec3 ${name}_in = normalize(${inTexCoord.ref3f()});
            vec2 ${name}_uv = vec2(atan(${name}_in.z, ${name}_in.x), -asin(${name}_in.y));
            ${name}_uv *= vec2(0.1591, 0.3183);
            ${name}_uv += 0.5;            
        """)

        val lod = texLod
        if (lod != null) {
            generator.appendMain("${outColor.declare()} = ${generator.sampleTexture2d(texture.name, name+"_uv", lod.ref1f())};")
        } else {
            generator.appendMain("${outColor.declare()} = ${generator.sampleTexture2d(texture.name, name+"_uv")};")
        }
        if (premultiply) {
            generator.appendMain("${outColor.ref3f()} *= ${outColor.ref4f()}.a;")
        }
    }
}

class AoMapSampleNode(val aoMap: TextureNode, graph: ShaderGraph) : ShaderNode("aoMapSampleNode_${graph.nextNodeId}", graph) {
    var inViewport = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO))

    val outAo = ShaderNodeIoVar(ModelVar1f("${name}_outAo"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        dependsOn(aoMap)
        dependsOn(inViewport)
        super.setup(shaderGraph)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("""
                vec2 aoMapSamplePos = gl_FragCoord.xy - ${inViewport.ref2f()};
                aoMapSamplePos.x /= $inViewport.z;
                aoMapSamplePos.y /= $inViewport.w;
                ${outAo.declare()} = ${generator.sampleTexture2d(aoMap.name, "aoMapSamplePos")}.r;
            """)
    }
}

class NormalMapNode(val texture: TextureNode, graph: ShaderGraph) :
        ShaderNode("normalMapping_${graph.nextNodeId}", graph) {

    var inTexCoord = ShaderNodeIoVar(ModelVar2fConst(Vec2f.NEG_X_AXIS))
    var inNormal = ShaderNodeIoVar(ModelVar3fConst(Vec3f.Y_AXIS))
    var inTangent = ShaderNodeIoVar(ModelVar3fConst(Vec3f.X_AXIS))
    var inStrength = ShaderNodeIoVar(ModelVar1fConst(1f))
    val outNormal: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3f("${name}_outNormal"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inTexCoord, inNormal, inTangent, inStrength)
    }

    override fun generateCode(generator: CodeGenerator) {
        super.generateCode(generator)

        generator.appendFunction("calcBumpedNormal", """
            vec3 calcBumpedNormal(vec3 n, vec4 t, vec2 uv, float strength) {
                vec3 normal = normalize(n);
                vec3 tangent = normalize(t.xyz);
                tangent = normalize(tangent - dot(tangent, normal) * normal);
                vec3 bitangent = cross(normal, tangent) * t.w;
                vec3 bumpMapNormal = ${generator.sampleTexture2d(texture.name, "uv")}.xyz;
                bumpMapNormal = 2.0 * bumpMapNormal - vec3(1.0, 1.0, 1.0);
                mat3 tbn = mat3(tangent, bitangent, normal);
                return normalize(mix(normal, tbn * bumpMapNormal, strength));
            }
        """)

        generator.appendMain("${outNormal.declare()} = calcBumpedNormal(${inNormal.ref3f()}, ${inTangent.ref4f()}, ${inTexCoord.ref2f()}, ${inStrength.ref1f()});")
    }
}

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

class DisplacementMapNode(val texture: TextureNode, graph: ShaderGraph) : ShaderNode("dispMap_${graph.nextNodeId}", graph) {
    var inTexCoord = ShaderNodeIoVar(ModelVar2fConst(Vec2f.NEG_X_AXIS))
    var inNormal = ShaderNodeIoVar(ModelVar3fConst(Vec3f.Y_AXIS))
    var inPosition = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    var inStrength = ShaderNodeIoVar(ModelVar1fConst(0.1f))
    val outPosition = ShaderNodeIoVar(ModelVar3f("${name}_outPos"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inTexCoord, inNormal, inPosition, inStrength)
    }

    override fun generateCode(generator: CodeGenerator) {
        super.generateCode(generator)
        generator.appendMain("""
            float ${name}_disp = ${generator.sampleTexture2d(texture.name, inTexCoord.ref2f())}.x * ${inStrength.ref1f()};
            ${outPosition.declare()} = ${inPosition.ref3f()} + ${inNormal.ref3f()} * ${name}_disp;
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

    val vertexNode = object : ShaderNode(name, vertexGraph, ShaderStage.VERTEX_SHADER.mask) {
        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(input)
            ifVar = shaderGraph.addStageOutput(output.variable, isFlat)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("${output.name} = ${input.refAsType(output.variable.type)};")
        }
    }

    val fragmentNode = object : ShaderNode(name, fragmentGraph, ShaderStage.FRAGMENT_SHADER.mask) {
        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            shaderGraph.inputs += ifVar
        }
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
