package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.modules.ksl.KslShaderListener
import de.fabmax.kool.modules.ksl.lang.KslDataBlock
import de.fabmax.kool.modules.ksl.lang.KslProgram
import de.fabmax.kool.pipeline.DrawCommand
import de.fabmax.kool.pipeline.ShaderBase
import de.fabmax.kool.pipeline.UniformBinding4f

context(program: KslProgram)
fun morphWeightData(): MorphWeightData {
    return (program.dataBlocks.find { it is MorphWeightData } as? MorphWeightData) ?: MorphWeightData(program)
}

class MorphWeightData(program: KslProgram) : KslDataBlock(NAME, program), KslShaderListener {
    val weightsA = uniformFloat4("uMorphWeightsA")
    val weightsB = uniformFloat4("uMorphWeightsB")

    private var uMorphWeightsA: UniformBinding4f? = null
    private var uMorphWeightsB: UniformBinding4f? = null
    private val cachedWeightsA = MutableVec4f()
    private val cachedWeightsB = MutableVec4f()

    init {
        program.shaderListeners += this
    }

    override fun onShaderCreated(shader: ShaderBase<*>) {
        uMorphWeightsA = shader.bindUniformFloat4("uMorphWeightsA")
        uMorphWeightsB = shader.bindUniformFloat4("uMorphWeightsB")
    }

    override fun onUpdateDrawData(cmd: DrawCommand) {
        cmd.mesh.morphWeights?.let { w ->
            cachedWeightsA.let { wA ->
                if (w.size > 0) wA.x = w[0]
                if (w.size > 1) wA.y = w[1]
                if (w.size > 2) wA.z = w[2]
                if (w.size > 3) wA.w = w[3]
            }
            cachedWeightsB.let { wB ->
                if (w.size > 4) wB.x = w[4]
                if (w.size > 5) wB.y = w[5]
                if (w.size > 6) wB.z = w[6]
                if (w.size > 7) wB.w = w[7]
            }
            uMorphWeightsA?.set(cachedWeightsA)
            uMorphWeightsB?.set(cachedWeightsB)
        }
    }

    companion object {
        const val NAME = "MorphWeightData"
    }
}