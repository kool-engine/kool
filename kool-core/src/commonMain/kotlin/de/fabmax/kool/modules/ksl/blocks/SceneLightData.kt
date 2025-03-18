package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.modules.ksl.KslShaderListener
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.BindGroupScope
import de.fabmax.kool.pipeline.DrawCommand
import de.fabmax.kool.pipeline.ShaderBase
import de.fabmax.kool.pipeline.UniformBufferLayout
import de.fabmax.kool.util.MemoryLayout
import de.fabmax.kool.util.Struct
import kotlin.math.min

fun KslProgram.sceneLightData(maxLights: Int) = SceneLightData(this, maxLights)

class SceneLightData(program: KslProgram, val maxLightCount: Int) : KslDataBlock, KslShaderListener {
    override val name = "SceneLightData"

    private val lightUniform = program.uniformStruct("uLightData", BindGroupScope.VIEW) { LightDataStruct(maxLightCount) }

    val encodedPositions: KslExprFloat4Array get() = lightUniform.struct.encodedPositions.ksl
    val encodedDirections: KslExprFloat4Array get() = lightUniform.struct.encodedDirections.ksl
    val encodedColors: KslExprFloat4Array get() = lightUniform.struct.encodedColors.ksl
    val lightCount: KslExprInt1 get() = lightUniform.struct.lightCount.ksl

    private var structLayout: UniformBufferLayout<LightDataStruct>? = null

    init {
        program.dataBlocks += this
        program.shaderListeners += this
    }

    override fun onShaderCreated(shader: ShaderBase<*>) {
        val (_, binding) = shader.createdPipeline!!.getBindGroupItem<UniformBufferLayout<LightDataStruct>> {
            it.isStructInstanceOf<LightDataStruct>()
        }
        structLayout = binding
    }

    override fun onUpdate(cmd: DrawCommand) {
        val layout = structLayout ?: return
        val viewData = cmd.queue.view.viewPipelineData.getPipelineDataUpdating(cmd.pipeline, layout.bindingIndex) ?: return
        val binding = viewData.uniformStructBindingData(layout)
        val lighting = cmd.queue.renderPass.lighting

        binding.set {
            if (lighting == null) {
                lightCount.set(0)
            } else {
                val lightCount = min(lighting.lights.size, maxLightCount)
                this.lightCount.set(lightCount)
                for (i in 0 until lightCount) {
                    val light = lighting.lights[i]
                    light.updateEncodedValues()
                    encodedPositions[i] = light.encodedPosition
                    encodedDirections[i] = light.encodedDirection
                    encodedColors[i] = light.encodedColor
                }
            }
        }
    }

    class LightDataStruct(maxLightCount: Int) : Struct("LightData", MemoryLayout.Std140) {
        val encodedPositions = float4Array(maxLightCount, "encodedPositions")
        val encodedDirections = float4Array(maxLightCount, "encodedDirections")
        val encodedColors = float4Array(maxLightCount, "encodedColors")
        val lightCount = int1("lightCount")
    }
}