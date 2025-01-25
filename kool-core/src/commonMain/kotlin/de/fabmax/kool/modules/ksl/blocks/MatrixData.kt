package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableMat4d
import de.fabmax.kool.math.MutableMat4f
import de.fabmax.kool.math.toMutableMat4f
import de.fabmax.kool.modules.ksl.KslShaderListener
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.positioned

fun KslProgram.mvpMatrix(): MvpMatrixData {
    return (dataBlocks.find { it is MvpMatrixData } as? MvpMatrixData) ?: MvpMatrixData(this)
}

fun KslProgram.modelMatrix(): ModelMatrixData {
    return (dataBlocks.find { it is ModelMatrixData } as? ModelMatrixData) ?: ModelMatrixData(this)
}

fun KslProgram.invProjMatrix(): InvProjMatrixData {
    return (dataBlocks.find { it is InvProjMatrixData } as? InvProjMatrixData) ?: InvProjMatrixData(this)
}

abstract class MeshMatrixData(program: KslProgram, val uniformName: String) : KslDataBlock, KslShaderListener {
    val matrix: KslUniformMatrix<KslMat4, KslFloat4>

    private val matrixUbo = KslUniformBuffer("${uniformName}_ubo", program, BindGroupScope.MESH).apply {
        matrix = uniformMat4(uniformName)
    }

    protected var uboLayout: UniformBufferLayout? = null
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

    protected fun putMatrixToBuffer(matrix: Mat4f, groupData: BindGroupData) {
        val bindingLayout = uboLayout ?: return
        val uboData = groupData.uniformBufferBindingData(bindingLayout.bindingIndex)
        uboData.markDirty()
        uboData.buffer.position = bufferPos!!.byteIndex
        matrix.putTo(uboData.buffer)
    }
}

class MvpMatrixData(program: KslProgram) : MeshMatrixData(program, "uMvpMat") {
    override val name = NAME

    private val tmpMat4d = MutableMat4d()
    private val tmpMat4f = MutableMat4f()

    override fun onUpdate(cmd: DrawCommand) {
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
        putMatrixToBuffer(tmpMat4f, uboData)
    }

    companion object {
        const val NAME = "MvpMatrixData"
    }
}

class ModelMatrixData(program: KslProgram) : MeshMatrixData(program, "uModelMat") {
    override val name = NAME

    private val tmpMat4f = MutableMat4f()

    override fun onUpdate(cmd: DrawCommand) {
        val bindingLayout = uboLayout ?: return
        val uboData = cmd.mesh.meshPipelineData.getPipelineDataUpdating(cmd.pipeline, bindingLayout.bindingIndex) ?: return
        if (uboData.modCnt == cmd.mesh.modelMatrixData.modCount) return
        uboData.modCnt = cmd.mesh.modelMatrixData.modCount

        if (cmd.queue.isDoublePrecision) {
            cmd.modelMatD.toMutableMat4f(tmpMat4f)
            putMatrixToBuffer(tmpMat4f, uboData)
        } else {
            putMatrixToBuffer(cmd.modelMatF, uboData)
        }
    }

    companion object {
        const val NAME = "ModelMatrixData"
    }
}


class InvProjMatrixData(program: KslProgram) : KslDataBlock, KslShaderListener {
    override val name = NAME
    val matrix: KslUniformMatrix<KslMat4, KslFloat4>

    private val matrixUbo = KslUniformBuffer("uInvProjMat_ubo", program, BindGroupScope.VIEW).apply {
        matrix = uniformMat4("uInvProjMat")
    }

    private var uboLayout: UniformBufferLayout? = null
    private var bufferPos: BufferPosition? = null
    private val tmpMat4f = MutableMat4f()

    init {
        program.shaderListeners += this
        program.dataBlocks += this
        program.uniformBuffers += matrixUbo
    }

    override fun onShaderCreated(shader: ShaderBase<*>) {
        val binding = shader.createdPipeline!!.findBindingLayout<UniformBufferLayout> { it.hasUniform("uInvProjMat") }
        uboLayout = binding?.second
        uboLayout?.let {
            bufferPos = it.layout.uniformPositions["uInvProjMat"]
        }
    }

    override fun onUpdate(cmd: DrawCommand) {
        val bindingLayout = uboLayout ?: return
        val viewData = cmd.queue.view.viewPipelineData.getPipelineDataUpdating(cmd.pipeline, bindingLayout.bindingIndex) ?: return

        val q = cmd.queue
        val cam = q.view.camera
        val ubo = viewData.uniformBufferBindingData(bindingLayout.bindingIndex)

        ubo.markDirty()
        ubo.buffer.positioned(bufferPos!!.byteIndex) { cam.invProj.putTo(it) }
    }

    companion object {
        const val NAME = "InvProjMatrixData"
    }
}