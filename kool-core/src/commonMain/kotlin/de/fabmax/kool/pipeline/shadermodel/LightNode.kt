package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.KoolException
import de.fabmax.kool.drawqueue.DrawCommand
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.toRad
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Light
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min

abstract class LightNode(name: String, shaderGraph: ShaderGraph) : ShaderNode(name, shaderGraph){
    abstract val outLightCount: ShaderNodeIoVar

    abstract fun callVec3GetFragToLight(idx: String, fragPos: String): String
    abstract fun callVec3GetRadiance(idx: String, fragToLight: String, innerAngle: String): String
}

class MultiLightNode(shaderGraph: ShaderGraph, val maxLights: Int = 4) : LightNode("lightNd_${shaderGraph.nextNodeId}", shaderGraph) {
    val uLightCnt = Uniform1i("lightCount")
    val uPositions = Uniform4fv("lightPositions", maxLights)
    val uColors = Uniform4fv("lightColors", maxLights)
    val uDirections = Uniform4fv("lightDirections", maxLights)

    var inShaodwFacs = Array(maxLights) { ShaderNodeIoVar(ModelVar1fConst(1f)) }

    override val outLightCount = ShaderNodeIoVar(ModelVar1i(uLightCnt.name), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)

        dependsOn(*inShaodwFacs)
        shaderGraph.descriptorSet.apply {
            uniformBuffer(name, shaderGraph.stage) {
                +{ uPositions }
                +{ uColors }
                +{ uDirections }
                +{ uLightCnt }

                onUpdate = { _, cmd ->
                    encodeLightSetup(cmd)
                }
            }
        }
    }

    private fun encodeLightSetup(cmd: DrawCommand) {
        val lighting = cmd.renderPass.lighting
        if (lighting != null) {
            uLightCnt.value = min(lighting.lights.size, maxLights)
            for (i in 0 until uLightCnt.value) {
                val light = lighting.lights[i]

                uColors.value[i].set(light.color)
                uPositions.value[i].set(light.position, light.type.encoded)
                uDirections.value[i].set(light.direction, cos((light.spotAngle / 2).toRad()))
            }
        } else {
            uLightCnt.value = 0
        }
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendFunction("light_getFragToLight", """
            vec3 light_getFragToLight(int idx, vec3 fragPos) {
                if (${uPositions.name}[idx].w == float(${Light.Type.DIRECTIONAL.encoded})) {
                    return -${uDirections.name}[idx].xyz;
                }
                // same for point and spot lights
                return ${uPositions.name}[idx].xyz - fragPos;
            }
            """)

        generator.appendFunction("light_getRadiance", """
            vec3 light_getRadiance(int idx, vec3 fragToLight, float innerAngle) {
                if (${uPositions.name}[idx].w == float(${Light.Type.DIRECTIONAL.encoded})) {
                    return ${uColors.name}[idx].rgb * ${uColors.name}[idx].w;
                }
                float dist = length(fragToLight);
                if (${uPositions.name}[idx].w == float(${Light.Type.POINT.encoded})) {
                    return ${uColors.name}[idx].rgb * ${uColors.name}[idx].w / (dist * dist);
                } else {
                    // spot light
                    vec3 lightDir = -normalize(fragToLight);
                    float spotAng = ${uDirections.name}[idx].w;
                    float innerAng = spotAng + (1.0 - spotAng) * (1.0 - innerAngle);
                    float ang = dot(lightDir, ${uDirections.name}[idx].xyz);
                    float angVal = cos(clamp((innerAng - ang) / (innerAng - spotAng), 0.0, 1.0) * $PI) * 0.5 + 0.5;
                    return ${uColors.name}[idx].rgb * ${uColors.name}[idx].w / (dist * dist) * angVal;
                }
            }
            """)

        val facs = (0 until maxLights).map { i -> inShaodwFacs[i].ref1f() }.joinToString(", ")
        generator.appendMain("float[] ${name}_shadowFacs = float[] ($facs);")
    }

    override fun callVec3GetFragToLight(idx: String, fragPos: String): String {
        return "light_getFragToLight($idx, $fragPos)"
    }

    override fun callVec3GetRadiance(idx: String, fragToLight: String, innerAngle: String): String {
        return "(light_getRadiance($idx, $fragToLight, $innerAngle) * ${name}_shadowFacs[$idx])"
    }
}

class ShadowMapNode(lightIndex: Int, vertexGraph: ShaderGraph, fragmentGraph: ShaderGraph) {
    var depthTexture: TextureNode? = null

    val uShadowMapVP = UniformMat4f("shadowMapVP_${vertexGraph.nextNodeId}")

    var inDepthOffset: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar1fConst(-0.01f))
    var inPosition: ShaderNodeIoVar = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    var inModelMat: ShaderNodeIoVar? = null

    private val ifPosLightSpace = StageInterfaceNode("posLightSpace_${vertexGraph.nextNodeId}", vertexGraph, fragmentGraph)

    init {
        vertexGraph.addNode(ifPosLightSpace.vertexNode)
        fragmentGraph.addNode(ifPosLightSpace.fragmentNode)
    }

    val vertexNode = object : ShaderNode("shadowMap_${vertexGraph.nextNodeId}", vertexGraph, ShaderStage.VERTEX_SHADER.mask) {
        val outPosLightSpace = ShaderNodeIoVar(ModelVar4f("shadowMap_${vertexGraph.nextNodeId}_posLightSpace"), this)

        init {
            ifPosLightSpace.input = outPosLightSpace
        }

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            val inModel = inModelMat ?: throw KoolException("Model matrix input not set")
            dependsOn(inPosition, inModel)

            shaderGraph.descriptorSet.apply {
                uniformBuffer(name, shaderGraph.stage) {
                    +{ uShadowMapVP }
                    onUpdate = { _, cmd ->
                        cmd.renderPass.lighting?.let {
                            if (lightIndex < it.lights.size) {
                                uShadowMapVP.value.set(it.lights[lightIndex].lightViewProjMat)
                            }
                        }
                    }
                }
            }
        }

        override fun generateCode(generator: CodeGenerator) {
            val modelMat = inModelMat?.variable ?: throw KoolException("Model matrix input not set")
            generator.appendMain("${outPosLightSpace.declare()} = ${uShadowMapVP.name} * (${modelMat.refAsType(GlslType.MAT_4F)} * vec4(${inPosition.ref3f()}, 1.0));")
        }
    }

    val fragmentNode: ShaderNode = object : ShaderNode("shadowMap_${fragmentGraph.nextNodeId}", fragmentGraph, ShaderStage.FRAGMENT_SHADER.mask) {
        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            val depthTex = depthTexture ?: throw KoolException("Depth texture input not set")
            dependsOn(depthTex)
        }

        override fun generateCode(generator: CodeGenerator) {
            val depthTex = depthTexture ?: throw KoolException("Depth texture input not set")

            generator.appendMain("""
                float ${name}_size = float(textureSize(${depthTex.name}, 0).x);
                float ${name}_scale = 1.0 / float(${name}_size);
                vec4 ${name}_pos = ${ifPosLightSpace.output.ref4f()};
                ${name}_pos.z += ${inDepthOffset.ref1f()};
                vec2 ${name}_offset = vec2(float(fract(${name}_pos.x * ${name}_size * 0.5) > 0.25),
                                           float(fract(${name}_pos.y * ${name}_size * 0.5) > 0.25));
                ${outShadowFac.declare()} = 0.0;
            """)

            // dithered pcf shadow map sampling
            // https://developer.nvidia.com/gpugems/gpugems/part-ii-lighting-and-shadows/chapter-11-shadow-map-antialiasing
            var nSamples = 0
            listOf(Vec2f(-1.5f, 0.5f), Vec2f(0.5f, 0.5f), Vec2f(-1.5f, -1.5f), Vec2f(0.5f, -1.5f)).forEach { off ->
                val projCoord = "vec4(${name}_pos.xy + (${name}_offset + vec2(${off.x}, ${off.y})) * ${name}_scale * ${name}_pos.w, ${name}_pos.z, ${name}_pos.w)"
                generator.appendMain("${outShadowFac.name} += ${generator.sampleTexture2dDepth(depthTex.name, projCoord)};")
                nSamples++
            }
            generator.appendMain("${outShadowFac.name} *= ${1f / nSamples};")
        }
    }

    val outShadowFac = ShaderNodeIoVar(ModelVar1f("${fragmentNode.name}_shadowFac"), fragmentNode)
}