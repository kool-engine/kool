package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.modules.ksl.KslShaderListener
import de.fabmax.kool.modules.ksl.lang.KslDataBlock
import de.fabmax.kool.modules.ksl.lang.KslProgram
import de.fabmax.kool.pipeline.ShaderBase
import de.fabmax.kool.pipeline.UniformBinding4f
import de.fabmax.kool.pipeline.DrawCommand

fun KslProgram.morphWeightData(): MorphWeightData {
    return (dataBlocks.find { it is MorphWeightData } as? MorphWeightData) ?: MorphWeightData(this)
}

class MorphWeightData(program: KslProgram) : KslDataBlock, KslShaderListener {
    override val name = NAME

    val weightsA = program.uniformFloat4("uMorphWeightsA")
    val weightsB = program.uniformFloat4("uMorphWeightsB")

    private var uMorphWeightsA: UniformBinding4f? = null
    private var uMorphWeightsB: UniformBinding4f? = null
    private val cachedWeightsA = MutableVec4f()
    private val cachedWeightsB = MutableVec4f()

    init {
        program.shaderListeners += this
    }

    override fun onShaderCreated(shader: ShaderBase<*>) {
        uMorphWeightsA = shader.uniform4f("uMorphWeightsA")
        uMorphWeightsB = shader.uniform4f("uMorphWeightsB")
    }

    override fun onUpdate(cmd: DrawCommand) {
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