package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.MutableMat4d
import de.fabmax.kool.math.MutableMat4f
import de.fabmax.kool.math.toMutableMat4f
import de.fabmax.kool.modules.ksl.KslShaderListener
import de.fabmax.kool.modules.ksl.lang.KslDataBlock
import de.fabmax.kool.modules.ksl.lang.KslProgram
import de.fabmax.kool.pipeline.ShaderBase
import de.fabmax.kool.pipeline.UniformBindingMat4f
import de.fabmax.kool.pipeline.drawqueue.DrawCommand

fun KslProgram.mvpMatrix(): MvpMatrixData {
    return (dataBlocks.find { it is MvpMatrixData } as? MvpMatrixData) ?: MvpMatrixData(this)
}

fun KslProgram.modelMatrix(): ModelMatrixData {
    return (dataBlocks.find { it is ModelMatrixData } as? ModelMatrixData) ?: ModelMatrixData(this)
}

abstract class MatrixData(program: KslProgram, val uniformName: String) : KslDataBlock, KslShaderListener {
    val matrix = program.uniformMat4(uniformName)

    protected lateinit var uMatrix: UniformBindingMat4f

    init {
        program.shaderListeners += this
        program.dataBlocks += this
    }

    override fun onShaderCreated(shader: ShaderBase<*>) {
        uMatrix = shader.uniformMat4f(uniformName)
    }
}

class MvpMatrixData(program: KslProgram) : MatrixData(program, "uMvpMat") {
    override val name = NAME

    private val tmpMat4d = MutableMat4d()
    private val tmpMat4f = MutableMat4f()

    override fun onUpdate(cmd: DrawCommand) {
        if (cmd.queue.isDoublePrecision) {
            cmd.queue.viewProjMatD.mul(cmd.modelMatD, tmpMat4d)
            tmpMat4d.toMutableMat4f(tmpMat4f)
        } else {
            cmd.queue.viewProjMatF.mul(cmd.modelMatF, tmpMat4f)
        }
        uMatrix.set(tmpMat4f)
    }

    companion object {
        const val NAME = "MvpMatrixData"
    }
}

class ModelMatrixData(program: KslProgram) : MatrixData(program, "uModelMat") {
    override val name = NAME

    private val tmpMat4f = MutableMat4f()

    override fun onUpdate(cmd: DrawCommand) {
        if (cmd.queue.isDoublePrecision) {
            cmd.modelMatD.toMutableMat4f(tmpMat4f)
            uMatrix.set(tmpMat4f)
        } else {
            uMatrix.set(cmd.modelMatF)
        }

    }

    companion object {
        const val NAME = "ModelMatrixData"
    }
}
