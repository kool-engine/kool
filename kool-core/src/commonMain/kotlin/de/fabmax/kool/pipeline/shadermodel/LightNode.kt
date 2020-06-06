package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.drawqueue.DrawCommand
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.math.toRad
import de.fabmax.kool.pipeline.Uniform1i
import de.fabmax.kool.pipeline.Uniform4f
import de.fabmax.kool.pipeline.Uniform4fv
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
    private val uLightCnt = Uniform1i("lightCount")
    private val uPositions = Uniform4fv("lightPositions", maxLights)
    private val uColors = Uniform4fv("lightColors", maxLights)
    private val uDirections = Uniform4fv("lightDirections", maxLights)

    var inShaodwFacs = Array(maxLights) { ShaderNodeIoVar(ModelVar1fConst(1f)) }

    override val outLightCount = ShaderNodeIoVar(ModelVar1i(uLightCnt.name), this)

    var isReducedSoi = false

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

        val strength = if (isReducedSoi) {
            // reduce light's sphere of influence and let strength reach 0.0
            """
                float powerSqrt = sqrt(power);
                float strength = clamp(power / (1.0 + dist * dist) * (powerSqrt - dist) / powerSqrt, 0.0, power);
            """
        } else {
            "float strength = power / (1.0 + dist * dist);"
        }

        generator.appendFunction("light_getRadiance", """
            vec3 light_getRadiance(int idx, vec3 fragToLight, float innerAngle) {
                if (${uPositions.name}[idx].w == float(${Light.Type.DIRECTIONAL.encoded})) {
                    return ${uColors.name}[idx].rgb * ${uColors.name}[idx].w;
                }
                float dist = length(fragToLight);
                float power = ${uColors.name}[idx].w;
                $strength
                if (${uPositions.name}[idx].w == float(${Light.Type.POINT.encoded})) {
                    return ${uColors.name}[idx].rgb * strength;
                } else {
                    // spot light
                    vec3 lightDir = -normalize(fragToLight);
                    float spotAng = ${uDirections.name}[idx].w;
                    float innerAng = spotAng + (1.0 - spotAng) * (1.0 - innerAngle);
                    float ang = dot(lightDir, ${uDirections.name}[idx].xyz);
                    float angVal = cos(clamp((innerAng - ang) / (innerAng - spotAng), 0.0, 1.0) * $PI) * 0.5 + 0.5;
                    return ${uColors.name}[idx].rgb * strength * angVal;
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

class SingleLightUniformDataNode(shaderGraph: ShaderGraph) : ShaderNode("lightUniformDataNd_${shaderGraph.nextNodeId}", shaderGraph) {
    var light: Light? = null

    private val uPosition = Uniform4f("u${name}_pos")
    private val uColor = Uniform4f("u${name}_color")
    private val uDirection = Uniform4f("u${name}_dir")

    val outLightPos = ShaderNodeIoVar(ModelVar4f(uPosition.name), this)
    val outLightColor = ShaderNodeIoVar(ModelVar4f(uColor.name), this)
    val outLightDir = ShaderNodeIoVar(ModelVar4f(uDirection.name), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        shaderGraph.descriptorSet.apply {
            uniformBuffer(name, shaderGraph.stage) {
                +{ uPosition }
                +{ uColor }
                +{ uDirection }

                onUpdate = { _, _ ->
                    encodeLightSetup()
                }
            }
        }
    }

    private fun encodeLightSetup() {
        val lgt = light
        if (lgt != null) {
            uColor.value.set(lgt.color)
            uPosition.value.set(lgt.position, lgt.type.encoded)
            uDirection.value.set(lgt.direction, cos((lgt.spotAngle / 2).toRad()))
        } else {
            uColor.value.set(Vec4f.ZERO)
        }
    }
}

class SingleLightNode(shaderGraph: ShaderGraph) : LightNode("lightNd_${shaderGraph.nextNodeId}", shaderGraph) {
    var inLightPos = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO))
    var inLightColor = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO))
    var inLightDir = ShaderNodeIoVar(ModelVar4fConst(Vec4f(1f, 0f, 0f, 60f)))
    var inShaodwFac = ShaderNodeIoVar(ModelVar1fConst(1f))

    override val outLightCount = ShaderNodeIoVar(ModelVar1iConst(1))

    var isReducedSoi = false

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inShaodwFac, inLightPos, inLightDir, inLightColor)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendFunction("${name}_getFragToLight", """
            vec3 ${name}_getFragToLight(vec3 fragPos, vec4 lightPos, vec4 lightDir) {
                if (lightPos.w == float(${Light.Type.DIRECTIONAL.encoded})) {
                    return -lightDir.xyz;
                }
                // same for point and spot lights
                return lightPos.xyz - fragPos;
            }
            """)

        val strength = if (isReducedSoi) {
            // reduce light's sphere of influence and let strength reach 0.0
            """
                float powerSqrt = sqrt(power);
                float strength = clamp(power / (1.0 + dist * dist) * (powerSqrt - dist) / powerSqrt, 0.0, power);
            """
        } else {
            "float strength = power / (1.0 + dist * dist);"
        }

        generator.appendFunction("${name}_getRadiance", """
            vec3 ${name}_getRadiance(vec3 fragToLight, vec4 lightPos, vec4 lightColor, vec4 lightDir, float innerAngle) {
                if (lightPos.w == float(${Light.Type.DIRECTIONAL.encoded})) {
                    return lightColor.rgb * lightColor.w;
                }
                float dist = length(fragToLight);
                float power = lightColor.w;
                $strength
                if (lightPos.w == float(${Light.Type.POINT.encoded})) {
                    return lightColor.rgb * strength;
                } else {
                    // spot light
                    vec3 negFtl = -normalize(fragToLight);
                    float spotAng = lightDir.w;
                    float innerAng = spotAng + (1.0 - spotAng) * (1.0 - innerAngle);
                    float ang = dot(negFtl, lightDir.xyz);
                    float angVal = cos(clamp((innerAng - ang) / (innerAng - spotAng), 0.0, 1.0) * $PI) * 0.5 + 0.5;
                    return lightColor.rgb * strength * angVal;
                }
            }
            """)
    }

    override fun callVec3GetFragToLight(idx: String, fragPos: String): String {
        return "${name}_getFragToLight($fragPos, ${inLightPos.ref4f()}, ${inLightDir.ref4f()})"
    }

    override fun callVec3GetRadiance(idx: String, fragToLight: String, innerAngle: String): String {
        return "(${name}_getRadiance($fragToLight, ${inLightPos.ref4f()}, ${inLightColor.ref4f()}, ${inLightDir.ref4f()}, $innerAngle) * ${inShaodwFac.ref1f()})"
    }
}