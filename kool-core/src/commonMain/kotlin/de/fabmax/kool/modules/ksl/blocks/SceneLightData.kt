package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.modules.ksl.KslShaderListener
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.BindGroupScope
import de.fabmax.kool.pipeline.BufferPosition
import de.fabmax.kool.pipeline.ShaderBase
import de.fabmax.kool.pipeline.UniformBufferLayout
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.util.positioned
import kotlin.math.min

fun KslProgram.sceneLightData(maxLights: Int) = SceneLightData(this, maxLights)

class SceneLightData(program: KslProgram, val maxLightCount: Int) : KslDataBlock, KslShaderListener {
    override val name = NAME

    val encodedPositions: KslUniformVectorArray<KslFloat4, KslFloat1>
    val encodedDirections: KslUniformVectorArray<KslFloat4, KslFloat1>
    val encodedColors: KslUniformVectorArray<KslFloat4, KslFloat1>
    val lightCount: KslUniformScalar<KslInt1>

    private val lightUbo = KslUniformBuffer("LightUniforms", program, BindGroupScope.VIEW).apply {
        encodedPositions = uniformFloat4Array(UNIFORM_NAME_LIGHT_POSITIONS, maxLightCount)
        encodedDirections = uniformFloat4Array(UNIFORM_NAME_LIGHT_DIRECTIONS, maxLightCount)
        encodedColors = uniformFloat4Array(UNIFORM_NAME_LIGHT_COLORS, maxLightCount)
        lightCount = uniformInt1(UNIFORM_NAME_LIGHT_COUNT)
    }

    private var uboLayout: UniformBufferLayout? = null
    private var bufferPosPositions: BufferPosition? = null
    private var bufferPosDirections: BufferPosition? = null
    private var bufferPosColors: BufferPosition? = null
    private var bufferPosLightCnt: BufferPosition? = null

    init {
        program.dataBlocks += this
        program.shaderListeners += this
        program.uniformBuffers += lightUbo
    }

    override fun onShaderCreated(shader: ShaderBase<*>) {
        val binding = shader.createdPipeline!!.findBindingLayout<UniformBufferLayout> { it.name == "LightUniforms" }
        uboLayout = binding?.second
        uboLayout?.let {
            bufferPosPositions = it.layout.uniformPositions[UNIFORM_NAME_LIGHT_POSITIONS]
            bufferPosDirections = it.layout.uniformPositions[UNIFORM_NAME_LIGHT_DIRECTIONS]
            bufferPosColors = it.layout.uniformPositions[UNIFORM_NAME_LIGHT_COLORS]
            bufferPosLightCnt = it.layout.uniformPositions[UNIFORM_NAME_LIGHT_COUNT]
        }
    }

    override fun onUpdate(cmd: DrawCommand) {
        val pipeline = cmd.pipeline ?: return
        val bindingLayout = uboLayout ?: return
        val viewData = cmd.queue.view.viewPipelineData.getPipelineDataUpdating(pipeline, bindingLayout.bindingIndex) ?: return
        val ubo = viewData.uniformBufferBindingData(bindingLayout.bindingIndex)
        val lighting = cmd.queue.renderPass.lighting

        if (lighting == null) {
            ubo.buffer.setInt32(bufferPosLightCnt!!.byteIndex, 0)
        } else {
            val lightCount = min(lighting.lights.size, maxLightCount)
            ubo.buffer.setInt32(bufferPosLightCnt!!.byteIndex, lightCount)
            for (i in 0 until lightCount) {
                val light = lighting.lights[i]
                light.updateEncodedValues()
                ubo.buffer.positioned(bufferPosPositions!!.byteIndex + 16 * i) { light.encodedPosition.putTo(it) }
                ubo.buffer.positioned(bufferPosDirections!!.byteIndex + 16 * i) { light.encodedDirection.putTo(it) }
                ubo.buffer.positioned(bufferPosColors!!.byteIndex + 16 * i) { light.encodedColor.putTo(it) }
            }
        }
        ubo.isBufferDirty = true
    }

    companion object {
        const val NAME = "SceneLightData"

        const val UNIFORM_NAME_LIGHT_POSITIONS = "uLightPositions"
        const val UNIFORM_NAME_LIGHT_DIRECTIONS = "uLightDirections"
        const val UNIFORM_NAME_LIGHT_COLORS = "uLightColors"
        const val UNIFORM_NAME_LIGHT_COUNT = "uLightCount"
    }
}