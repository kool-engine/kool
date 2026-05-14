package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableMat4d
import de.fabmax.kool.math.MutableMat4f
import de.fabmax.kool.math.toMutableMat4f
import de.fabmax.kool.modules.ksl.KslShaderListener
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.BindGroupData.UniformBufferBindingData
import de.fabmax.kool.pipeline.BindGroupScope
import de.fabmax.kool.pipeline.DrawCommand
import de.fabmax.kool.pipeline.ShaderBase
import de.fabmax.kool.pipeline.UniformBufferLayout
import de.fabmax.kool.util.ModCounter
import de.fabmax.kool.util.setMat4

context(program: KslProgram)
fun mvpMatrix(): MvpMatrixData {
    return (program.dataBlocks.find { it is MvpMatrixData } as? MvpMatrixData) ?: MvpMatrixData(program)
}

context(program: KslProgram)
fun modelMatrix(): ModelMatrixData {
    return (program.dataBlocks.find { it is ModelMatrixData } as? ModelMatrixData) ?: ModelMatrixData(program)
}

context(program: KslProgram)
fun invProjMatrix(): InvProjMatrixData {
    return (program.dataBlocks.find { it is InvProjMatrixData } as? InvProjMatrixData) ?: InvProjMatrixData(program)
}

abstract class MatrixData(name: String, program: KslProgram, val uniformName: String, scope: BindGroupScope) : KslDataBlock(name, program), KslShaderListener {
    val matrix: KslUniformMatrix<KslMat4, KslFloat4>

    private val matrixUbo = KslUniformBuffer("${uniformName}_ubo", program, scope).apply {
        matrix = uniformMat4(uniformName)
    }

    protected var uboLayout: UniformBufferLayout<*>? = null
    private var matIndex = -1

    init {
        program.shaderListeners += this
        program.dataBlocks += this
        program.uniformBuffers += matrixUbo
    }

    override fun onShaderCreated(shader: ShaderBase<*>) {
        val binding = shader.createdPipeline!!.findBindGroupItem<UniformBufferLayout<*>> { it.hasUniform(uniformName) }
        uboLayout = binding?.second
        uboLayout?.let {
            matIndex = it.indexOfMember(uniformName)
        }
    }

    protected fun putMatrixToBuffer(matrix: Mat4f, uboData: UniformBufferBindingData<*>) {
        uboData.set { setMat4(it, matIndex, matrix) }
    }
}

class MvpMatrixData(program: KslProgram) : MatrixData(NAME, program, "uMvpMat", BindGroupScope.MESH) {
    private val tmpMat4d = MutableMat4d()
    private val tmpMat4f = MutableMat4f()

    override fun onUpdateDrawData(cmd: DrawCommand) {
        // Do not use getPipelineDataUpdating() here: MVP matrix needs to be always updated, in case this mesh /
        // pipeline combination is drawn in multiple views with different view matrices.
        //
        // fixme: This also means, that this onUpdate function has to be called in correct order between individual
        //  views. This is currently the case but might change in future. In that case, using a precomputed MVP matrix
        //  would not be possible anymore (work around: use separate model and view matrices and multiply them in
        //  the vertex shader)
        val uboData = cmd.mesh.meshPipelineData.getPipelineData(cmd.pipeline)

        if (cmd.queue.isDoublePrecision) {
            cmd.queue.viewProjMatD.mul(cmd.modelMatD, tmpMat4d)
            tmpMat4d.toMutableMat4f(tmpMat4f)
        } else {
            cmd.queue.viewProjMatF.mul(cmd.modelMatF, tmpMat4f)
        }
        val bindingLayout = uboLayout ?: return
        putMatrixToBuffer(tmpMat4f, uboData.uniformBufferBindingData(bindingLayout.bindingIndex))
    }

    companion object {
        const val NAME = "MvpMatrixData"
    }
}

class ModelMatrixData(program: KslProgram) : MatrixData(NAME, program, "uModelMat", BindGroupScope.MESH) {
    private val tmpMat4f = MutableMat4f()
    private val bindingModCounts = mutableMapOf<UniformBufferBindingData<*>, ModCounter>()

    override fun onUpdateDrawData(cmd: DrawCommand) {
        val bindingLayout = uboLayout ?: return
        cmd.mesh.meshPipelineData.updatePipelineData(cmd.pipeline, bindingLayout.bindingIndex) { uboData ->
            val uboBinding = uboData.uniformBufferBindingData(bindingLayout.bindingIndex)
            val bindingModCount = bindingModCounts.getOrPut(uboBinding) { ModCounter() }
            if (bindingModCount.isNotDirty(cmd.mesh.modelMatrixData.modCount)) return
            bindingModCount.reset(cmd.mesh.modelMatrixData.modCount)
            if (cmd.queue.isDoublePrecision) {
                cmd.modelMatD.toMutableMat4f(tmpMat4f)
                putMatrixToBuffer(tmpMat4f, uboBinding)
            } else {
                putMatrixToBuffer(cmd.modelMatF, uboBinding)
            }
        }
    }

    companion object {
        const val NAME = "ModelMatrixData"
    }
}


class InvProjMatrixData(program: KslProgram) : MatrixData(NAME, program, "uInvProjMat", BindGroupScope.VIEW) {
    override fun onUpdateDrawData(cmd: DrawCommand) {
        val bindingLayout = uboLayout ?: return
        cmd.queue.view.viewPipelineData.updatePipelineData(cmd.pipeline, bindingLayout.bindingIndex) { viewData ->
            val cam = cmd.queue.view.camera
            putMatrixToBuffer(cam.invProj, viewData.uniformBufferBindingData(bindingLayout.bindingIndex))
        }
    }

    companion object {
        const val NAME = "InvProjMatrixData"
    }
}