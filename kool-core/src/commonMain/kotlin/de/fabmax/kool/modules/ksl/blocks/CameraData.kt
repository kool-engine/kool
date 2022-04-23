package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.KoolContext
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.KslShaderListener
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.Uniform2f
import de.fabmax.kool.pipeline.Uniform3f
import de.fabmax.kool.pipeline.drawqueue.DrawCommand

fun KslProgram.cameraData() = CameraData(this).also { uniformBuffers += it.camUbo }

class CameraData(program: KslProgram) : KslShaderListener {

    val position: KslUniformVector<KslTypeFloat3, KslTypeFloat1>
    val direction: KslUniformVector<KslTypeFloat3, KslTypeFloat1>
    val clip: KslUniformVector<KslTypeFloat2, KslTypeFloat1>

    val viewMat: KslUniformMatrix<KslTypeMat4, KslTypeFloat4>
    val projMat: KslUniformMatrix<KslTypeMat4, KslTypeFloat4>

    val clipNear: KslScalarExpression<KslTypeFloat1>
        get() = clip.x
    val clipFar: KslScalarExpression<KslTypeFloat1>
        get() = clip.y

    // todo: implement shared ubos
    val camUbo = KslUniformBuffer("CameraUniforms", program, false).apply {
        position = uniformFloat3(UNIFORM_NAME_CAM_POSITION)
        direction = uniformFloat3(UNIFORM_NAME_CAM_DIRECTION)
        clip = uniformFloat2(UNIFORM_NAME_CAM_CLIP)

        viewMat = uniformMat4(UNIFORM_NAME_VIEW_MAT)
        projMat = uniformMat4(UNIFORM_NAME_PROJ_MAT)
    }

    private var uPosition: Uniform3f? = null
    private var uDirection: Uniform3f? = null
    private var uClip: Uniform2f? = null

    init {
        program.shaderListeners += this
    }

    override fun onShaderCreated(shader: KslShader, pipeline: Pipeline, ctx: KoolContext) {
        uPosition = shader.uniforms[UNIFORM_NAME_CAM_POSITION] as Uniform3f?
        uDirection = shader.uniforms[UNIFORM_NAME_CAM_DIRECTION] as Uniform3f?
        uClip = shader.uniforms[UNIFORM_NAME_CAM_CLIP] as Uniform2f?
    }

    override fun onUpdate(cmd: DrawCommand) {
        val cam = cmd.renderPass.camera
        uPosition?.value?.set(cam.globalPos)
        uDirection?.value?.set(cam.globalLookDir)
        uClip?.value?.set(cam.clipNear, cam.clipFar)
    }

    companion object {
        const val UNIFORM_NAME_CAM_POSITION = "uCamPos"
        const val UNIFORM_NAME_CAM_DIRECTION = "uCamDir"
        const val UNIFORM_NAME_CAM_CLIP = "uCamClip"

        const val UNIFORM_NAME_VIEW_MAT = "uViewMat"
        const val UNIFORM_NAME_PROJ_MAT = "uProjMat"
    }
}