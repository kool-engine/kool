package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.KoolContext
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.Uniform2f
import de.fabmax.kool.pipeline.Uniform3f
import de.fabmax.kool.pipeline.drawqueue.DrawCommand

fun KslProgram.cameraData() = CameraData(this).also { uniformBuffers += it }

class CameraData(program: KslProgram) : KslUniformBuffer(), KslShader.KslShaderListener {
    val position = program.uniformFloat3(UNIFORM_NAME_CAM_POSITION)
    val direction = program.uniformFloat3(UNIFORM_NAME_CAM_DIRECTION)
    val clip = program.uniformFloat2(UNIFORM_NAME_CAM_CLIP)

    val clipNear: KslScalarExpression<KslTypeFloat1>
        get() = clip.x
    val clipFar: KslScalarExpression<KslTypeFloat1>
        get() = clip.y

    private lateinit var uPosition: Uniform3f
    private lateinit var uDirection: Uniform3f
    private lateinit var uClip: Uniform2f

    override fun onShaderCreated(shader: KslShader, pipeline: Pipeline, ctx: KoolContext) {
        uPosition = shader.uniforms[UNIFORM_NAME_CAM_POSITION] as Uniform3f
        uDirection = shader.uniforms[UNIFORM_NAME_CAM_DIRECTION] as Uniform3f
        uClip = shader.uniforms[UNIFORM_NAME_CAM_CLIP] as Uniform2f
    }

    override fun onUpdate(cmd: DrawCommand) {
        val cam = cmd.renderPass.camera
        uPosition.value.set(cam.globalPos)
        uDirection.value.set(cam.globalLookDir)
        uClip.value.set(cam.clipNear, cam.clipFar)
    }

    companion object {
        const val UNIFORM_NAME_CAM_POSITION = "uCamPos"
        const val UNIFORM_NAME_CAM_DIRECTION = "uCamDir"
        const val UNIFORM_NAME_CAM_CLIP = "uCamClip"
    }
}