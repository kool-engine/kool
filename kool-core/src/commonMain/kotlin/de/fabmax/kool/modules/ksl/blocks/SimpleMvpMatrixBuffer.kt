package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.KoolContext
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.lang.KslProgram
import de.fabmax.kool.modules.ksl.lang.KslUniformBuffer
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.UniformMat4f
import de.fabmax.kool.pipeline.drawqueue.DrawCommand

fun KslProgram.mvpMatrix(): SimpleMvpMatrixBuffer {
    val simpleMvp = SimpleMvpMatrixBuffer(this)
    uniformBuffers += simpleMvp
    return simpleMvp
}

class SimpleMvpMatrixBuffer(program: KslProgram) : KslUniformBuffer(), KslShader.KslShaderListener {
    val outMvp = program.uniformMat4(UNIFORM_NAME_MVP_MAT)

    private lateinit var uMvp: UniformMat4f

    override fun onShaderCreated(shader: KslShader, pipeline: Pipeline, ctx: KoolContext) {
        uMvp = shader.uniforms[UNIFORM_NAME_MVP_MAT] as UniformMat4f
    }

    override fun onUpdate(cmd: DrawCommand) {
        uMvp.value.set(cmd.mvpMat)
    }

    companion object {
        const val UNIFORM_NAME_MVP_MAT = "uMvpMat"
    }
}