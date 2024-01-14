package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableMat4d
import de.fabmax.kool.math.MutableMat4f
import de.fabmax.kool.math.toMutableMat4f
import de.fabmax.kool.modules.ksl.KslShaderListener
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.BindGroupScope
import de.fabmax.kool.pipeline.BufferPosition
import de.fabmax.kool.pipeline.ShaderBase
import de.fabmax.kool.pipeline.UniformBufferLayout
import de.fabmax.kool.pipeline.drawqueue.DrawCommand

fun KslProgram.mvpMatrix(): MvpMatrixData {
    return (dataBlocks.find { it is MvpMatrixData } as? MvpMatrixData) ?: MvpMatrixData(this)
}

fun KslProgram.modelMatrix(): ModelMatrixData {
    return (dataBlocks.find { it is ModelMatrixData } as? ModelMatrixData) ?: ModelMatrixData(this)
}

abstract class MeshMatrixData(program: KslProgram, val uniformName: String) : KslDataBlock, KslShaderListener {
    val matrix: KslUniformMatrix<KslMat4, KslFloat4>

    private val matrixUbo = KslUniformBuffer("${uniformName}_ubo", program, BindGroupScope.MESH).apply {
        matrix = uniformMat4(uniformName)
    }

    private var uboLayout: UniformBufferLayout? = null
    private var bufferPos: BufferPosition? = null

    init {
        program.shaderListeners += this
        program.dataBlocks += this
        program.uniformBuffers += matrixUbo
    }

    override fun onShaderCreated(shader: ShaderBase<*>) {
        val binding = shader.createdPipeline!!.findBindingLayout<UniformBufferLayout> { it.hasUniform(uniformName) }
        uboLayout = binding?.second
        uboLayout?.let {
            bufferPos = it.layout.uniformPositions[uniformName]
        }
    }

    protected fun putMatrixToBuffer(matrix: Mat4f, cmd: DrawCommand) {
        val pipeline = cmd.pipeline
        val bindingLayout = uboLayout
        if (pipeline != null && bindingLayout != null) {
            val uboData = cmd.mesh.meshPipelineData
                .getPipelineData(pipeline)
                .uniformBufferBindingData(bindingLayout.bindingIndex)

            uboData.isBufferDirty = true
            uboData.buffer.position = bufferPos!!.byteIndex
            matrix.putTo(uboData.buffer)
        }
    }
}

class MvpMatrixData(program: KslProgram) : MeshMatrixData(program, "uMvpMat") {
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
        putMatrixToBuffer(tmpMat4f, cmd)
    }

    companion object {
        const val NAME = "MvpMatrixData"
    }
}

class ModelMatrixData(program: KslProgram) : MeshMatrixData(program, "uModelMat") {
    override val name = NAME

    private val tmpMat4f = MutableMat4f()

    override fun onUpdate(cmd: DrawCommand) {
        if (cmd.queue.isDoublePrecision) {
            cmd.modelMatD.toMutableMat4f(tmpMat4f)
            putMatrixToBuffer(tmpMat4f, cmd)
        } else {
            putMatrixToBuffer(cmd.modelMatF, cmd)
        }
    }

    companion object {
        const val NAME = "ModelMatrixData"
    }
}
