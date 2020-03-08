package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.ShaderStage
import de.fabmax.kool.util.Color

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
            vec3 calcBumpedNormal(vec3 n, vec3 t, vec2 uv, float strength) {
                vec3 normal = normalize(n);
                vec3 tangent = normalize(t);
                tangent = normalize(tangent - dot(tangent, normal) * normal);
                vec3 bitangent = cross(normal, tangent);
                vec3 bumpMapNormal = ${generator.sampleTexture2d(texture.name, "uv")}.xyz;
                bumpMapNormal = 2.0 * bumpMapNormal - vec3(1.0, 1.0, 1.0);
                mat3 tbn = mat3(tangent, bitangent, normal);
                return normalize(mix(normal, tbn * bumpMapNormal, strength));
            }
        """)

        generator.appendMain("${outNormal.declare()} = calcBumpedNormal(${inNormal.ref3f()}, ${inTangent.ref3f()}, ${inTexCoord.ref2f()}, ${inStrength.ref1f()});")
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

class ColorAlphaNode(graph: ShaderGraph) : ShaderNode("colorAlphaNode_${graph.nextNodeId}", graph) {
    var inColor = ShaderNodeIoVar(ModelVar4fConst(Color.MAGENTA), null)
    var inAlpha = ShaderNodeIoVar(ModelVar1fConst(1f), null)
    val outAlphaColor = ShaderNodeIoVar(ModelVar4f("${name}_outColor"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inColor, inAlpha)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("""
            ${outAlphaColor.declare()} = vec4(${inColor.ref3f()}, ${inColor.ref4f()}.a * ${inAlpha.ref1f()});
            """)
    }
}

class PremultiplyColorNode(graph: ShaderGraph) : ShaderNode("colorPreMult_${graph.nextNodeId}", graph) {
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

class GammaNode(graph: ShaderGraph) : ShaderNode("Pre-Multiply Color", graph) {
    var inColor = ShaderNodeIoVar(ModelVar4fConst(Color.MAGENTA))
    var inGamma = ShaderNodeIoVar(ModelVar1fConst(1f / 2.2f))
    val outColor = ShaderNodeIoVar(ModelVar4f("preMultColor_$nodeId"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inColor, inGamma)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("${outColor.declare()} = vec4(pow(${inColor.ref3f()}, vec3(1.0/${inGamma.ref1f()})), ${inColor.ref4f()}.a);")
    }
}

class HdrToLdrNode(graph: ShaderGraph) : ShaderNode("hdrToLdr_${graph.nextNodeId}", graph) {
    var inColor = ShaderNodeIoVar(ModelVar4fConst(Color.MAGENTA))
    var inGamma = ShaderNodeIoVar(ModelVar1fConst(2.2f))
    val outColor = ShaderNodeIoVar(ModelVar4f("${name}_outColor"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inColor, inGamma)
    }

    private fun generateUncharted2(generator: CodeGenerator) {
        generator.appendFunction("uncharted2", """
            vec3 uncharted2Tonemap_func(vec3 x) {
                float A = 0.15;     // shoulder strength
                float B = 0.50;     // linear strength
                float C = 0.10;     // linear angle
                float D = 0.20;     // toe strength
                float E = 0.02;     // toe numerator
                float F = 0.30;     // toe denominator  --> E/F = toe angle
                return ((x*(A*x+C*B)+D*E)/(x*(A*x+B)+D*F))-E/F;
            }
            
            vec3 uncharted2Tonemap(vec3 rgbLinear) {
                float W = 11.2;     // linear white point value
                float ExposureBias = 2.0;
                vec3 curr = uncharted2Tonemap_func(ExposureBias * rgbLinear);
                vec3 whiteScale = 1.0 / uncharted2Tonemap_func(vec3(W));
                return curr * whiteScale;
            }
        """)

        generator.appendMain("""
            vec3 ${name}_color = uncharted2Tonemap(${inColor.ref3f()});
            ${outColor.declare()} = vec4(pow(${name}_color, vec3(1.0/${inGamma.ref1f()})), ${inColor.ref4f()}.a);
        """)
    }

    private fun generateReinhard(generator: CodeGenerator) {
        generator.appendMain("""
            vec3 ${name}_color = ${inColor.ref3f()} / (${inColor.ref3f()} + vec3(1.0));
            ${outColor.declare()} = vec4(pow(${name}_color, vec3(1.0/${inGamma.ref1f()})), ${inColor.ref4f()}.a);
        """)
    }

    private fun generateJimHejlRichardBurgessDawson(generator: CodeGenerator) {
        generator.appendMain("""
            vec3 ${name}_color = max(vec3(0), ${inColor.ref3f()} - 0.004);
            ${outColor.declare()} = vec4((${name}_color * (6.2 * ${name}_color + 0.5)) / (${name}_color * (6.2 * ${name}_color + 1.7) + 0.06), 1.0);
        """)
    }

    override fun generateCode(generator: CodeGenerator) {
        generateUncharted2(generator)
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

class MultiplyNode(graph: ShaderGraph) : ShaderNode("multiply_${graph.nextNodeId}", graph) {
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

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("${output.declare()} = $left * $right;")
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

    private var layout = 0

    val vertexNode = object : ShaderNode(name, vertexGraph, ShaderStage.VERTEX_SHADER.mask) {
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

    val fragmentNode = object : ShaderNode(name, fragmentGraph, ShaderStage.FRAGMENT_SHADER.mask) {
        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            shaderGraph.inputs += ShaderInterfaceIoVar(layout, output.variable)
        }
    }
}