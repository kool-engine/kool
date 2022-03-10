package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.KoolContext
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.lang.KslProgram
import de.fabmax.kool.modules.ksl.lang.KslUniformBuffer
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.UniformMat4f
import de.fabmax.kool.pipeline.drawqueue.DrawCommand

fun KslProgram.mvpMatrix() = MvpMatrixData(this).also { uniformBuffers += it }
fun KslProgram.modelMatrix() = ModelMatrixData(this).also { uniformBuffers += it }
fun KslProgram.viewMatrix() = ViewMatrixData(this).also { uniformBuffers += it }
fun KslProgram.projMatrix() = ProjMatrixData(this).also { uniformBuffers += it }

abstract class MatrixData(program: KslProgram, val uniformName: String) : KslUniformBuffer(), KslShader.KslShaderListener {
    val matrix = program.uniformMat4(uniformName)

    protected lateinit var uMatrix: UniformMat4f

    override fun onShaderCreated(shader: KslShader, pipeline: Pipeline, ctx: KoolContext) {
        uMatrix = shader.uniforms[uniformName] as UniformMat4f
    }
}

class MvpMatrixData(program: KslProgram) : MatrixData(program, "uMvpMat") {
    override fun onUpdate(cmd: DrawCommand) {
        uMatrix.value.set(cmd.mvpMat)
    }
}

class ModelMatrixData(program: KslProgram) : MatrixData(program, "uModelMat") {
    override fun onUpdate(cmd: DrawCommand) {
        uMatrix.value.set(cmd.modelMat)
    }
}

class ViewMatrixData(program: KslProgram) : MatrixData(program, "uViewMat") {
    override fun onUpdate(cmd: DrawCommand) {
        uMatrix.value.set(cmd.viewMat)
    }
}

class ProjMatrixData(program: KslProgram) : MatrixData(program, "uProjMat") {
    override fun onUpdate(cmd: DrawCommand) {
        uMatrix.value.set(cmd.projMat)
    }
}
