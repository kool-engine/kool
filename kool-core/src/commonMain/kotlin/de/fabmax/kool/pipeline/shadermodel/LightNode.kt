package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.drawqueue.DrawCommand
import de.fabmax.kool.math.toRad
import de.fabmax.kool.pipeline.Uniform1i
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
