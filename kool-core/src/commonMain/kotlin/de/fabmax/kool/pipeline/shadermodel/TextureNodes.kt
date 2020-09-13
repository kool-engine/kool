package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f

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

class TextureSampler3dNode(val texture: Texture3dNode, graph: ShaderGraph, val premultiply: Boolean = false) :
        ShaderNode("tex3d_sampler_${texture.name}_${graph.nextNodeId}", graph) {

    var inTexCoord = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
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
            generator.appendMain("${outColor.declare()} = ${generator.sampleTexture3d(texture.name, inTexCoord.ref3f(), lod.ref1f())};")
        } else {
            generator.appendMain("${outColor.declare()} = ${generator.sampleTexture3d(texture.name, inTexCoord.ref3f())};")
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

class EquiRectSamplerNode(val texture: TextureNode, graph: ShaderGraph, val decodeRgbe: Boolean = false, val premultiply: Boolean = false) :
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

        if (decodeRgbe) {
            generator.appendMain("""
                vec3 ${name}_fRgb = $outColor.rgb;
                float ${name}_fExp = $outColor.a * 255.0 - 128.0;
                $outColor = vec4(${name}_fRgb * pow(2.0, ${name}_fExp), 1.0);
            """)
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
            vec3 calcBumpedNormal(vec3 n, vec4 t, vec3 bumpN, float strength) {
                vec3 normal = normalize(n);
                vec3 tangent = normalize(t.xyz);
                tangent = normalize(tangent - dot(tangent, normal) * normal);
                vec3 bitangent = cross(normal, tangent) * t.w;
                mat3 tbn = mat3(tangent, bitangent, normal);
                return normalize(mix(normal, tbn * bumpN, strength));
            }
        """)

        generator.appendMain("""
            vec3 ${name}_bumpN = ${generator.sampleTexture2d(texture.name, inTexCoord.ref2f())}.xyz * 2.0 - 1.0;
            ${outNormal.declare()} = calcBumpedNormal(${inNormal.ref3f()}, ${inTangent.ref4f()}, ${name}_bumpN, ${inStrength.ref1f()});
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

class RefractionSamplerNode(graph: ShaderGraph) : ShaderNode("refractionSampler_${graph.nextNodeId}", graph) {
    var reflectionMap: CubeMapNode? = null
    lateinit var refractionColor: TextureNode
    lateinit var viewProj: ShaderNodeIoVar

    var inMaterialThickness = ShaderNodeIoVar(ModelVar1fConst(1f))
    var inRefractionDir = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    var inFragPos = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))

    val outColor = ShaderNodeIoVar(ModelVar4f("${name}_outColor"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(refractionColor)
        dependsOn(reflectionMap)
        dependsOn(viewProj, inRefractionDir, inFragPos, inMaterialThickness)
    }

    override fun generateCode(generator: CodeGenerator) {
        val envColor = reflectionMap?.let { generator.sampleTextureCube(it.name, inRefractionDir.ref3f()) } ?: "vec4(0.0)"
        generator.appendMain("""
                vec3 ${name}_refrPos = ${inFragPos.ref3f()} + ${inRefractionDir.ref3f()} * ${inMaterialThickness.ref1f()};
                vec4 ${name}_clip = $viewProj * vec4(${name}_refrPos, 1.0);
                vec2 ${name}_refrSample = (${name}_clip.xy / ${name}_clip.w) * 0.5 + 0.5;
                
                bool ${name}_useReflMap = ${name}_refrSample.x < 0.0 || ${name}_refrSample.x > 1.0
                                        || ${name}_refrSample.y < 0.0 || ${name}_refrSample.y > 1.0;
                
                ${outColor.declare()} = vec4(0.0);
                if (!${name}_useReflMap) {
                    $outColor = ${generator.sampleTexture2d(refractionColor.name, "${name}_refrSample")};
                }
                if ($outColor.a == 0.0) {
                    $outColor = $envColor;
                }
            """)
    }
}