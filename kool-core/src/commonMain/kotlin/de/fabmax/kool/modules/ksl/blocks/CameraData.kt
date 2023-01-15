package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.KoolContext
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.KslShaderListener
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.drawqueue.DrawCommand

fun KslProgram.cameraData(): CameraData {
    return (dataBlocks.find { it is CameraData } as? CameraData) ?: CameraData(this)
}

class CameraData(program: KslProgram) : KslDataBlock, KslShaderListener {

    override val name = NAME

    val position: KslUniformVector<KslTypeFloat3, KslTypeFloat1>
    val direction: KslUniformVector<KslTypeFloat3, KslTypeFloat1>
    val clip: KslUniformVector<KslTypeFloat2, KslTypeFloat1>

    val viewMat: KslUniformMatrix<KslTypeMat4, KslTypeFloat4>
    val projMat: KslUniformMatrix<KslTypeMat4, KslTypeFloat4>
    val viewProjMat: KslUniformMatrix<KslTypeMat4, KslTypeFloat4>
    val viewport: KslUniformVector<KslTypeFloat4, KslTypeFloat1>

    val clipNear: KslExprFloat1
        get() = clip.x
    val clipFar: KslExprFloat1
        get() = clip.y

    // todo: implement shared ubos
    val camUbo = KslUniformBuffer("CameraUniforms", program, false).apply {
        viewMat = uniformMat4(UNIFORM_NAME_VIEW_MAT)
        projMat = uniformMat4(UNIFORM_NAME_PROJ_MAT)
        viewProjMat = uniformMat4(UNIFORM_NAME_VIEW_PROJ_MAT)
        viewport = uniformFloat4(UNIFORM_NAME_VIEWPORT)

        position = uniformFloat3(UNIFORM_NAME_CAM_POSITION)
        direction = uniformFloat3(UNIFORM_NAME_CAM_DIRECTION)
        clip = uniformFloat2(UNIFORM_NAME_CAM_CLIP)
    }

    private var uPosition: Uniform3f? = null
    private var uDirection: Uniform3f? = null
    private var uClip: Uniform2f? = null

    private var uViewMat: UniformMat4f? = null
    private var uProjMat: UniformMat4f? = null
    private var uViewProjMat: UniformMat4f? = null
    private var uViewport: Uniform4f? = null

    init {
        program.shaderListeners += this
        program.dataBlocks += this
        program.uniformBuffers += camUbo
    }

    override fun onShaderCreated(shader: KslShader, pipeline: Pipeline, ctx: KoolContext) {
        uPosition = shader.uniforms[UNIFORM_NAME_CAM_POSITION] as Uniform3f?
        uDirection = shader.uniforms[UNIFORM_NAME_CAM_DIRECTION] as Uniform3f?
        uClip = shader.uniforms[UNIFORM_NAME_CAM_CLIP] as Uniform2f?
        uViewMat = shader.uniforms[UNIFORM_NAME_VIEW_MAT] as UniformMat4f?
        uProjMat = shader.uniforms[UNIFORM_NAME_PROJ_MAT] as UniformMat4f?
        uViewProjMat = shader.uniforms[UNIFORM_NAME_VIEW_PROJ_MAT] as UniformMat4f?
        uViewport = shader.uniforms[UNIFORM_NAME_VIEWPORT] as Uniform4f?
    }

    override fun onUpdate(cmd: DrawCommand) {
        val cam = cmd.renderPass.camera
        val vp = cmd.renderPass.viewport
        uPosition?.value?.set(cam.globalPos)
        uDirection?.value?.set(cam.globalLookDir)
        uClip?.value?.set(cam.clipNear, cam.clipFar)
        // fixme: it would be nicer to use the cam properties here instead of cmd ones (especially viewProj)
        //  however, this does not work for render passes with multiple command queues and changing cam configs
        //  (e.g. cube pass) because this method is called after all command queues are built and cam then contains
        //  the values from the last command queue
        uViewMat?.value?.set(cmd.viewMat)
        uProjMat?.value?.set(cmd.projMat)
        uViewProjMat?.value?.set(cmd.projMat)?.mul(cmd.viewMat)
        uViewport?.value?.set(vp.x.toFloat(), vp.y.toFloat(), vp.width.toFloat(), vp.height.toFloat())
    }

    companion object {
        const val NAME = "CameraData"

        const val UNIFORM_NAME_CAM_POSITION = "uCamPos"
        const val UNIFORM_NAME_CAM_DIRECTION = "uCamDir"
        const val UNIFORM_NAME_CAM_CLIP = "uCamClip"

        const val UNIFORM_NAME_VIEW_MAT = "uViewMat"
        const val UNIFORM_NAME_PROJ_MAT = "uProjMat"
        const val UNIFORM_NAME_VIEW_PROJ_MAT = "uViewProjMat"

        const val UNIFORM_NAME_VIEWPORT = "uViewport"
    }
}