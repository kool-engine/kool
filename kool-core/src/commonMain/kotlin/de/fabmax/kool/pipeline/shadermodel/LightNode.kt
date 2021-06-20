package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.math.toRad
import de.fabmax.kool.pipeline.Uniform1i
import de.fabmax.kool.pipeline.Uniform4f
import de.fabmax.kool.pipeline.Uniform4fv
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.scene.Light
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min

class MultiLightNode(shaderGraph: ShaderGraph, val maxLights: Int = 4) : ShaderNode("lightNd_${shaderGraph.nextNodeId}", shaderGraph) {
    private val uLightCnt = Uniform1i("uLightCount")
    private val uPositions = Uniform4fv("uLightPositions", maxLights)
    private val uColors = Uniform4fv("uLightColors", maxLights)
    private val uDirections = Uniform4fv("uLightDirections", maxLights)

    var inShadowFacs = Array(maxLights) { ShaderNodeIoVar(ModelVar1fConst(1f)) }
    var inFragPos = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    var inSpotInnerAngle = ShaderNodeIoVar(ModelVar1fConst(0.8f))

    val outLightCount = ShaderNodeIoVar(ModelVar1i(uLightCnt.name), this)
    val outFragToLightDirection = ShaderNodeIoVar(ModelVar3fv("${name}_outLightDirs"), this)
    val outRadiance = ShaderNodeIoVar(ModelVar3fv("${name}_outRadiance"), this)
    val outAvgShadowFac = ShaderNodeIoVar(ModelVar1f("${name}_outAvgShadowFac"), this)

    var isReducedSoi = false

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)

        dependsOn(*inShadowFacs)
        dependsOn(inFragPos, inSpotInnerAngle)
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

        val facs = (0 until maxLights).map { i -> inShadowFacs[i].ref1f() }.joinToString(", ")
        val arrayInit = (0 until maxLights).map { "vec3(0.0)" }.joinToString(", ")
        generator.appendMain("""
            float[] ${name}_shadowFacs = float[] ($facs);
            ${outFragToLightDirection.declare()} = vec3[$maxLights]($arrayInit);
            ${outRadiance.declare()} = vec3[$maxLights]($arrayInit);
            ${outAvgShadowFac.declare()} = 0.0;
            for (int i = 0; i < $uLightCnt; i++) {
                ${outFragToLightDirection.ref3f("i")} = light_getFragToLight(i, ${inFragPos.ref3f()});
                float ${name}_sf = ${name}_shadowFacs[i];
                $outAvgShadowFac += ${name}_sf;
                ${outRadiance.ref3f("i")} = light_getRadiance(i, ${outFragToLightDirection.ref3f("i")}, ${inSpotInnerAngle.ref1f("i")}) * ${name}_sf;
            }
            $outAvgShadowFac /= max(1.0, float($uLightCnt));
            """)
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

class SingleLightNode(shaderGraph: ShaderGraph) : ShaderNode("lightNd_${shaderGraph.nextNodeId}", shaderGraph) {
    var inLightPos = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO))
    var inLightColor = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO))
    var inLightDir = ShaderNodeIoVar(ModelVar4fConst(Vec4f(1f, 0f, 0f, 60f)))
    var inMaxIntensity = ShaderNodeIoVar(ModelVar1fConst(100f))

    var inShaodwFac = ShaderNodeIoVar(ModelVar1fConst(1f))
    var inFragPos = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    var inSpotInnerAngle = ShaderNodeIoVar(ModelVar1fConst(0.8f))

    val outLightCount = ShaderNodeIoVar(ModelVar1iConst(1), this)
    val outFragToLightDirection = ShaderNodeIoVar(ModelVar3f("${name}_outLightDirs"), this)
    val outRadiance = ShaderNodeIoVar(ModelVar3f("${name}_outRadiance"), this)

    var isReducedSoi = false

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inShaodwFac, inLightPos, inLightDir, inLightColor)
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendFunction("light_getFragToLight", """
            vec3 light_getFragToLight(vec3 fragPos, vec4 lightPos, vec4 lightDir) {
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

        generator.appendFunction("light_getRadiance", """
            vec3 light_getRadiance(vec3 fragToLight, vec4 lightPos, vec4 lightColor, vec4 lightDir, float innerAngle, float maxIntensity) {
                if (lightPos.w == float(${Light.Type.DIRECTIONAL.encoded})) {
                    return lightColor.rgb * lightColor.w;
                }
                float dist = length(fragToLight);
                float power = lightColor.w;
                $strength
                float strClamped = clamp(strength, 0.0, maxIntensity);
                if (lightPos.w == float(${Light.Type.POINT.encoded})) {
                    return lightColor.rgb * strClamped;
                } else {
                    // spot light
                    vec3 negFtl = -normalize(fragToLight);
                    float spotAng = lightDir.w;
                    float innerAng = spotAng + (1.0 - spotAng) * (1.0 - innerAngle);
                    float ang = dot(negFtl, lightDir.xyz);
                    float angVal = cos(clamp((innerAng - ang) / (innerAng - spotAng), 0.0, 1.0) * $PI) * 0.5 + 0.5;
                    return lightColor.rgb * strClamped * angVal;
                }
            }
            """)

        generator.appendMain("""
            ${outFragToLightDirection.declare()} = light_getFragToLight(${inFragPos.ref3f()}, ${inLightPos.ref3f()}, ${inLightDir.ref3f()});
            ${outRadiance.declare()} = light_getRadiance(${outFragToLightDirection.ref3f()},
                         ${inLightPos.ref3f()}, ${inLightColor.ref4f()}, ${inLightDir.ref4f()},
                         ${inSpotInnerAngle.ref1f()}, ${inMaxIntensity.ref1f()}) * ${inShaodwFac.ref1f()};
        """)
    }
}

class SinglePointLightNode(shaderGraph: ShaderGraph) : ShaderNode("pointLightNd_${shaderGraph.nextNodeId}", shaderGraph) {
    var inLightPos = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO))
    var inLightColor = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO))
    var inMaxIntensity = ShaderNodeIoVar(ModelVar1fConst(100f))

    var inShadowFac = ShaderNodeIoVar(ModelVar1fConst(1f))
    var inFragPos = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))

    val outLightCount = ShaderNodeIoVar(ModelVar1iConst(1), this)
    val outFragToLightDirection = ShaderNodeIoVar(ModelVar3f("${name}_outLightDirs"), this)
    val outRadiance = ShaderNodeIoVar(ModelVar3f("${name}_outRadiance"), this)

    var isReducedSoi = false

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inLightPos, inLightColor, inShadowFac, inFragPos)
    }

    override fun generateCode(generator: CodeGenerator) {
        val strength = if (isReducedSoi) {
            // reduce light's sphere of influence and let strength reach 0.0
            """
                float powerSqrt = sqrt(power);
                float strength = clamp(power / (1.0 + dist * dist) * (powerSqrt - dist) / powerSqrt, 0.0, power);
            """
        } else {
            "float strength = power / (1.0 + dist * dist);"
        }

        generator.appendFunction("pointLight_getRadiance", """
            vec3 pointLight_getRadiance(vec3 fragToLight, vec4 lightPos, vec4 lightColor, float maxIntensity) {
                float dist = length(fragToLight);
                float power = lightColor.w;
                $strength
                return lightColor.rgb * clamp(strength, 0.0, maxIntensity);
            }
            """)

        generator.appendMain("""
            ${outFragToLightDirection.declare()} = ${inLightPos.ref3f()} - ${inFragPos.ref3f()};
            ${outRadiance.declare()} = pointLight_getRadiance($outFragToLightDirection, ${inLightPos.ref4f()},
                        ${inLightColor.ref4f()}, ${inMaxIntensity.ref1f()}) * ${inShadowFac.ref1f()};
        """)
    }
}

class SingleSpotLightNode(shaderGraph: ShaderGraph) : ShaderNode("spotLightNd_${shaderGraph.nextNodeId}", shaderGraph) {
    var inLightPos = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO))
    var inLightColor = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO))
    var inLightDir = ShaderNodeIoVar(ModelVar4fConst(Vec4f(1f, 0f, 0f, 60f)))
    var inMaxIntensity = ShaderNodeIoVar(ModelVar1fConst(100f))

    var inShadowFac = ShaderNodeIoVar(ModelVar1fConst(1f))
    var inFragPos = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
    var inSpotCoreRatio = ShaderNodeIoVar(ModelVar1fConst(0.8f))

    val outLightCount = ShaderNodeIoVar(ModelVar1iConst(1), this)
    val outFragToLightDirection = ShaderNodeIoVar(ModelVar3f("${name}_outLightDirs"), this)
    val outRadiance = ShaderNodeIoVar(ModelVar3f("${name}_outRadiance"), this)

    var isReducedSoi = false

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inShadowFac, inLightPos, inLightDir, inLightColor, inFragPos)
    }

    override fun generateCode(generator: CodeGenerator) {
        val strength = if (isReducedSoi) {
            // reduce light's sphere of influence and let strength reach 0.0
            """
                float powerSqrt = sqrt(power);
                float strength = clamp(power / (1.0 + dist * dist) * (powerSqrt - dist) / powerSqrt, 0.0, power);
            """
        } else {
            "float strength = power / (1.0 + dist * dist);"
        }

        generator.appendFunction("spotLight_getRadiance", """
            vec3 ${name}_getRadiance(vec3 fragToLight, vec4 lightPos, vec4 lightColor, vec4 lightDir, float coreRatio, float maxIntensity) {
                float dist = length(fragToLight);
                float power = lightColor.w;
                $strength
                vec3 negFtl = -normalize(fragToLight);
                float spotAng = lightDir.w;
                float innerAng = spotAng + (1.0 - spotAng) * (1.0 - coreRatio);
                float ang = dot(negFtl, lightDir.xyz);
                float angVal = smoothstep(spotAng, innerAng, ang);
                return lightColor.rgb * clamp(strength, 0.0, maxIntensity) * angVal;
            }
            """)

        generator.appendMain("""
            ${outFragToLightDirection.declare()} = ${inLightPos.ref3f()} - ${inFragPos.ref3f()};
            ${outRadiance.declare()} = ${name}_getRadiance($outFragToLightDirection, ${inLightPos.ref4f()},
                        ${inLightColor.ref4f()}, ${inLightDir.ref4f()}, $inSpotCoreRatio, ${inMaxIntensity.ref1f()}) * ${inShadowFac.ref1f()};
        """)
    }
}