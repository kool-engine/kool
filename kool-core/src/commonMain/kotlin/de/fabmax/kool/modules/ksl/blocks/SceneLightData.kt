package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.modules.ksl.KslShaderListener
import de.fabmax.kool.modules.ksl.lang.KslDataBlock
import de.fabmax.kool.modules.ksl.lang.KslProgram
import de.fabmax.kool.pipeline.ShaderBase
import de.fabmax.kool.pipeline.UniformBinding1i
import de.fabmax.kool.pipeline.UniformBinding4fv
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import kotlin.math.min

fun KslProgram.sceneLightData(maxLights: Int) = SceneLightData(this, maxLights)

class SceneLightData(program: KslProgram, val maxLightCount: Int) : KslDataBlock, KslShaderListener {
    override val name = NAME

    val encodedPositions = program.uniformFloat4Array(UNIFORM_NAME_LIGHT_POSITIONS, maxLightCount)
    val encodedDirections = program.uniformFloat4Array(UNIFORM_NAME_LIGHT_DIRECTIONS, maxLightCount)
    val encodedColors = program.uniformFloat4Array(UNIFORM_NAME_LIGHT_COLORS, maxLightCount)
    val lightCount = program.uniformInt1(UNIFORM_NAME_LIGHT_COUNT)

    private var uLightPositions: UniformBinding4fv? = null
    private var uLightDirections: UniformBinding4fv? = null
    private var uLightColors: UniformBinding4fv? = null
    private var uLightCount: UniformBinding1i? = null

    init {
        program.dataBlocks += this
        program.shaderListeners += this
    }

    override fun onShaderCreated(shader: ShaderBase<*>) {
        uLightPositions = shader.uniform4fv(UNIFORM_NAME_LIGHT_POSITIONS)
        uLightDirections = shader.uniform4fv(UNIFORM_NAME_LIGHT_DIRECTIONS)
        uLightColors = shader.uniform4fv(UNIFORM_NAME_LIGHT_COLORS)
        uLightCount = shader.uniform1i(UNIFORM_NAME_LIGHT_COUNT)
    }

    override fun onUpdate(cmd: DrawCommand) {
        val lighting = cmd.queue.renderPass.lighting
        if (lighting != null) {
            val lightPos = uLightPositions ?: return
            val lightDir = uLightDirections ?: return
            val lightCol = uLightColors ?: return
            val lightCnt = uLightCount ?: return

            val setLightCount = min(lighting.lights.size, maxLightCount)
            lightCnt.set(setLightCount)
            for (i in 0 until setLightCount) {
                val light = lighting.lights[i]
                light.updateEncodedValues()
                lightPos.set(i, light.encodedPosition)
                lightDir.set(i, light.encodedDirection)
                lightCol.set(i, light.encodedColor)
            }
        } else {
            uLightCount?.set(0)
        }
    }

    companion object {
        const val NAME = "SceneLightData"

        const val UNIFORM_NAME_LIGHT_POSITIONS = "uLightPositions"
        const val UNIFORM_NAME_LIGHT_DIRECTIONS = "uLightDirections"
        const val UNIFORM_NAME_LIGHT_COLORS = "uLightColors"
        const val UNIFORM_NAME_LIGHT_COUNT = "uLightCount"
    }
}