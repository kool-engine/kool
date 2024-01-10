package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.modules.ksl.KslShaderListener
import de.fabmax.kool.modules.ksl.lang.KslDataBlock
import de.fabmax.kool.modules.ksl.lang.KslProgram
import de.fabmax.kool.pipeline.PipelineBase
import de.fabmax.kool.pipeline.ShaderBase
import de.fabmax.kool.pipeline.Uniform4f
import de.fabmax.kool.pipeline.drawqueue.DrawCommand

fun KslProgram.morphWeightData(): MorphWeightData {
    return (dataBlocks.find { it is MorphWeightData } as? MorphWeightData) ?: MorphWeightData(this)
}

class MorphWeightData(program: KslProgram) : KslDataBlock, KslShaderListener {
    override val name = NAME

    val weightsA = program.uniformFloat4("uMorphWeightsA")
    val weightsB = program.uniformFloat4("uMorphWeightsB")
    private var uMorphWeightsA: Uniform4f? = null
    private var uMorphWeightsB: Uniform4f? = null

    init {
        program.shaderListeners += this
    }

    override fun onShaderCreated(shader: ShaderBase<*>, pipeline: PipelineBase) {
        uMorphWeightsA = shader.uniforms["uMorphWeightsA"] as? Uniform4f
        uMorphWeightsB = shader.uniforms["uMorphWeightsB"] as? Uniform4f
    }

    override fun onUpdate(cmd: DrawCommand) {
        cmd.mesh.morphWeights?.let { w ->
            uMorphWeightsA?.let { wA ->
                if (w.size > 0) wA.value.x = w[0]
                if (w.size > 1) wA.value.y = w[1]
                if (w.size > 2) wA.value.z = w[2]
                if (w.size > 3) wA.value.w = w[3]
            }
            uMorphWeightsB?.let { wB ->
                if (w.size > 4) wB.value.x = w[4]
                if (w.size > 5) wB.value.y = w[5]
                if (w.size > 6) wB.value.z = w[6]
                if (w.size > 7) wB.value.w = w[7]
            }
        }
    }

    companion object {
        const val NAME = "MorphWeightData"
    }
}