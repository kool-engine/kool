package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.drawqueue.DrawCommand
import de.fabmax.kool.math.toRad
import de.fabmax.kool.pipeline.Uniform1i
import de.fabmax.kool.pipeline.Uniform4fv
import de.fabmax.kool.scene.Light
import kotlin.math.cos
import kotlin.math.min

class LightNode(shaderGraph: ShaderGraph, val maxLights: Int = 4) : ShaderNode("Lights", shaderGraph) {
    val uLightCnt = Uniform1i("lightCount")
    val uPositions = Uniform4fv("lightPositions", maxLights)
    val uColors = Uniform4fv("lightColors", maxLights)
    val uDirections = Uniform4fv("lightDirections", maxLights)

    val outLightCount = ShaderNodeIoVar(ModelVar1i(uLightCnt.name), this)
    val outLightPos = ShaderNodeIoVar(ModelVar4f(uPositions.name), this)
    val outLightDir = ShaderNodeIoVar(ModelVar4f(uDirections.name), this)
    val outLightColor = ShaderNodeIoVar(ModelVar4f(uColors.name), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        shaderGraph.descriptorSet.apply {
            uniformBuffer(name, shaderGraph.stage) {
                +{ uPositions }
                +{ uColors }
                +{ uDirections }

                onUpdate = { _, cmd ->
                    encodeLightSetup(cmd)
                }
            }
        }
        shaderGraph.pushConstants.apply {
            stages += shaderGraph.stage
            +{ uLightCnt }
        }
    }

    private fun encodeLightSetup(cmd: DrawCommand) {
        val lights = cmd.scene?.lighting?.lights
        if (lights != null) {
            uLightCnt.value = min(lights.size, maxLights)
            for (i in 0 until uLightCnt.value) {
                val light = lights[i]

                uColors.value[i].set(light.color)
                uPositions.value[i].set(light.position, light.type.encoded)
                uDirections.value[i].set(light.direction, cos((light.spotAngle / 2).toRad()))
            }
        }
    }

    override fun generateCode(generator: CodeGenerator) {
        generator.appendFunction("light_getDirection", """
            vec3 light_getDirection(int idx, vec3 fragPos) {
                if (${uPositions.name}[idx].w == ${Light.Type.DIRECTIONAL.encoded}) {
                    return -${uDirections.name}[idx].xyz;
                }
                // same for point and spot lights
                return ${uPositions.name}[idx].xyz - fragPos;
            }
            """)

        generator.appendFunction("light_getStrength", """
            float light_getStrength(int idx, vec3 lightDir, float innerAngle) {
                if (${uPositions.name}[idx].w == ${Light.Type.DIRECTIONAL.encoded}) {
                    return ${uColors.name}[idx].w;
                }
                float dist = length(lightDir);
                if (${uPositions.name}[idx].w == ${Light.Type.POINT.encoded}) {
                    return ${uColors.name}[idx].w / (dist * dist);
                } else {
                    // spot light
                    lightDir = -normalize(lightDir);
                    float spotAng = ${uDirections.name}[idx].w;
                    float ang = dot(lightDir, ${uDirections.name}[idx].xyz);
                    float angVal = cos(clamp((spotAng - ang) / (1.0 - innerAngle), 0.0, 1.0) * 3.141592) * 0.5 + 0.5;
                    return ${uColors.name}[idx].w / (dist * dist) * angVal;
                }
            }
            """)
    }

    fun generateGetDirection(idx: String, fragPos: String): String {
        return "light_getDirection($idx, $fragPos)"
    }

    fun generateGetStrength(idx: String, lightDir: String, innerAngle: String): String {
        return "light_getStrength($idx, $lightDir, $innerAngle)"
    }
}