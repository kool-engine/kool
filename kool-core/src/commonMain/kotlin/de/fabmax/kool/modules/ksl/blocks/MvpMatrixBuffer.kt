package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.KoolContext
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.lang.KslProgram
import de.fabmax.kool.modules.ksl.lang.KslUniformBuffer
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.UniformMat4f
import de.fabmax.kool.pipeline.drawqueue.DrawCommand

fun KslProgram.mvpMatrix() = MvpMatrixBuffer(this).also { uniformBuffers += it }
fun KslProgram.modelMatrix() = ModelMatrixBuffer(this).also { uniformBuffers += it }
fun KslProgram.viewMatrix() = ViewMatrixBuffer(this).also { uniformBuffers += it }
fun KslProgram.projMatrix() = ProjMatrixBuffer(this).also { uniformBuffers += it }

abstract class MatrixBuffer(program: KslProgram, val uniformName: String) : KslUniformBuffer(), KslShader.KslShaderListener {
    val matrix = program.uniformMat4(uniformName)
    var uniform: UniformMat4f? = null

    override fun onShaderCreated(shader: KslShader, pipeline: Pipeline, ctx: KoolContext) {
        uniform = shader.uniforms[uniformName] as? UniformMat4f
    }
}

class MvpMatrixBuffer(program: KslProgram) : MatrixBuffer(program, "uMvpMat") {
    override fun onUpdate(cmd: DrawCommand) {
        uniform?.value?.set(cmd.mvpMat)
    }
}

class ModelMatrixBuffer(program: KslProgram) : MatrixBuffer(program, "uModelMat") {
    override fun onUpdate(cmd: DrawCommand) {
        uniform?.value?.set(cmd.modelMat)
    }
}

class ViewMatrixBuffer(program: KslProgram) : MatrixBuffer(program, "uViewMat") {
    override fun onUpdate(cmd: DrawCommand) {
        uniform?.value?.set(cmd.viewMat)
    }
}

class ProjMatrixBuffer(program: KslProgram) : MatrixBuffer(program, "uProjMat") {
    override fun onUpdate(cmd: DrawCommand) {
        uniform?.value?.set(cmd.projMat)
    }
}
