package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.toRad
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.lang.KslProgram
import de.fabmax.kool.modules.ksl.lang.KslUniformBuffer
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.Uniform1i
import de.fabmax.kool.pipeline.Uniform4fv
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import kotlin.math.cos
import kotlin.math.min

fun KslProgram.sceneLightData(maxLights: Int = 4) = SceneLightData(this, maxLights).also { uniformBuffers += it }

class SceneLightData(program: KslProgram, val maxLightCount: Int) : KslUniformBuffer(), KslShader.KslShaderListener {

    val lightPositions = program.uniformFloat4Array(UNIFORM_NAME_LIGHT_POSITIONS, maxLightCount).value
    val lightDirections = program.uniformFloat4Array(UNIFORM_NAME_LIGHT_DIRECTIONS, maxLightCount).value
    val lightColors = program.uniformFloat4Array(UNIFORM_NAME_LIGHT_COLORS, maxLightCount).value
    val lightCount = program.uniformInt1(UNIFORM_NAME_LIGHT_COUNT)

    private lateinit var uLightPositions: Uniform4fv
    private lateinit var uLightDirections: Uniform4fv
    private lateinit var uLightColors: Uniform4fv
    private lateinit var uLightCount: Uniform1i

    override fun onShaderCreated(shader: KslShader, pipeline: Pipeline, ctx: KoolContext) {
        uLightPositions = shader.uniforms[UNIFORM_NAME_LIGHT_POSITIONS] as Uniform4fv
        uLightDirections = shader.uniforms[UNIFORM_NAME_LIGHT_DIRECTIONS] as Uniform4fv
        uLightColors = shader.uniforms[UNIFORM_NAME_LIGHT_COLORS] as Uniform4fv
        uLightCount = shader.uniforms[UNIFORM_NAME_LIGHT_COUNT] as Uniform1i
    }

    override fun onUpdate(cmd: DrawCommand) {
        val lighting = cmd.renderPass.lighting
        if (lighting != null) {
            uLightCount.value = min(lighting.lights.size, maxLightCount)
            for (i in 0 until uLightCount.value) {
                val light = lighting.lights[i]
                uLightColors.value[i].set(light.color)
                uLightPositions.value[i].set(light.position, light.type.encoded)
                uLightDirections.value[i].set(light.direction, cos((light.spotAngle / 2).toRad()))
            }
        } else {
            uLightCount.value = 0
        }
    }

    companion object {
        const val UNIFORM_NAME_LIGHT_POSITIONS = "uLightPositions"
        const val UNIFORM_NAME_LIGHT_DIRECTIONS = "uLightDirections"
        const val UNIFORM_NAME_LIGHT_COLORS = "uLightColors"
        const val UNIFORM_NAME_LIGHT_COUNT = "uLightCount"
    }
}