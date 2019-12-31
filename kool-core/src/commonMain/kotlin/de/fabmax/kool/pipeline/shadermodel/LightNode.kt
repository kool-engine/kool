package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.drawqueue.DrawCommand
import de.fabmax.kool.pipeline.Uniform1i
import de.fabmax.kool.pipeline.Uniform2fv
import de.fabmax.kool.pipeline.Uniform4fv
import de.fabmax.kool.scene.Light
import kotlin.math.min

class LightNode(shaderGraph: ShaderGraph, val maxLights: Int = 4) : ShaderNode("Lights", shaderGraph) {
    val uLightCnt = Uniform1i("lightCount")
    val uPositions = Uniform4fv("lightPositions", maxLights)
    val uColors = Uniform4fv("lightColors", maxLights)
    val uDirections = Uniform4fv("lightDirections", maxLights)
    val uSpotCfg = Uniform2fv("lightSpotCfgs", maxLights)

    val outLightCount = ShaderNodeIoVar(ModelVar1i(uLightCnt.name), this)
    val outLightPos = ShaderNodeIoVar(ModelVar4f(uPositions.name), this)
    val outLightDir = ShaderNodeIoVar(ModelVar4f(uDirections.name), this)
    val outLightColor = ShaderNodeIoVar(ModelVar4f(uColors.name), this)
    val outLightSpotCfg = ShaderNodeIoVar(ModelVar2f(uSpotCfg.name), this)

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        shaderGraph.descriptorSet.apply {
            uniformBuffer(name, shaderGraph.stage) {
                +{ uPositions }
                +{ uColors }
                +{ uDirections }
                +{ uSpotCfg }

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
                uDirections.value[i].set(light.direction)
                uSpotCfg.value[i].set(light.spotConfig)
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
                return fragPos - ${uPositions.name}[idx].xyz;
            }
            """)

        generator.appendFunction("light_getStrength", """
            float light_getStrength(int idx, float lightDist, vec3 lightDir) {
                if (${uPositions.name}[idx].w == ${Light.Type.DIRECTIONAL.encoded}) {
                    return ${uColors.name}[idx].w;
                } else if (${uPositions.name}[idx].w == ${Light.Type.POINT.encoded}) {
                    return ${uColors.name}[idx].w / (lightDist * lightDist);
                } else {    // spot light
                    return ${uColors.name}[idx].w / (lightDist * lightDist);
                }
            }
            """)
    }

    fun generateGetDirection(idx: String, fragPos: String): String {
        return "light_getDirection($idx, $fragPos)"
    }

    fun generateGetStrength(idx: String, lightDist: String, lightDir: String): String {
        return "light_getStrength($idx, $lightDist, $lightDir)"
    }
}