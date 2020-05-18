package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.pipeline.Attribute
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
        super.setup(shaderGraph)
    }

    override fun generateCode(generator: CodeGenerator) {
        val invertY = if (generator.clipSpaceOrientation == CodeGenerator.ClipSpaceOrientation.Y_UP) {
            ""
        } else {
            "aoMapSamplePos.y = 1.0 - aoMapSamplePos.y;"
        }
        generator.appendMain("""
                vec2 aoMapSamplePos = gl_FragCoord.xy - ${inViewport.ref2f()};
                aoMapSamplePos.x /= $inViewport.z;
                aoMapSamplePos.y /= $inViewport.w;
                $invertY
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

    private lateinit var ifVar: ShaderInterfaceIoVar

    val vertexNode = object : ShaderNode(name, vertexGraph, ShaderStage.VERTEX_SHADER.mask) {
        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(input)
            ifVar = shaderGraph.addStageOutput(output.variable)
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