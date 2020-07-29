package de.fabmax.kool.util.ibl

import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.pipeline.shadermodel.*


class EnvCubeSamplerNode(val texture: CubeMapNode, graph: ShaderGraph) : ShaderNode("envCubeSampler", graph) {
    var maxLightIntensity = ShaderNodeIoVar(ModelVar1fConst(5000f))

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(texture)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendFunction("sampleEnv", """
                vec3 sampleEnv(vec3 texCoord, float mipLevel) {
                    vec3 envColor = ${generator.sampleTextureCube(texture.name, "texCoord", "mipLevel")}.rgb;
                    return min(envColor, vec3(${maxLightIntensity.ref1f()}));
                }
            """)
    }
}

class EnvEquiRectSamplerNode(val texture: TextureNode, graph: ShaderGraph) : ShaderNode("envEquiRectSampler", graph) {
    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(texture)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendFunction("sampleEnv", """
                vec3 sampleEnv(vec3 texCoord, float mipLevel) {
                    vec3 equiRect_in = normalize(texCoord);
                    vec2 uv = vec2(atan(equiRect_in.z, equiRect_in.x), -asin(equiRect_in.y));
                    uv *= vec2(0.1591, 0.3183);
                    uv += 0.5;
                    return ${generator.sampleTexture2d(texture.name, "uv", "mipLevel")}.rgb;
                }
            """)
    }
}

class RgbeDecoderNode(graph: ShaderGraph) : ShaderNode("rgbeDecoder", graph) {
    var inRgbe = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO))
    var maxLightIntensity = ShaderNodeIoVar(ModelVar1fConst(20f))

    val outColor = ShaderNodeIoVar(ModelVar3f("decodedRgbe"), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inRgbe, maxLightIntensity)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendMain("""
                vec3 fRgb = ${inRgbe.ref3f()};
                float fExp = ${inRgbe.ref4f()}.a * 255.0 - 128.0;
                ${outColor.declare()} = min(fRgb * pow(2.0, fExp), vec3(${maxLightIntensity.ref1f()}));
            """)
    }
}
