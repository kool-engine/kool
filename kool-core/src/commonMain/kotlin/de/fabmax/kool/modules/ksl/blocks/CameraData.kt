package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.modules.ksl.KslShaderListener
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.drawqueue.DrawCommand

fun KslProgram.cameraData(): CameraData {
    return (dataBlocks.find { it is CameraData } as? CameraData) ?: CameraData(this)
}

class CameraData(program: KslProgram) : KslDataBlock, KslShaderListener {

    override val name = NAME

    val position: KslUniformVector<KslFloat3, KslFloat1>
    val direction: KslUniformVector<KslFloat3, KslFloat1>
    val clip: KslUniformVector<KslFloat2, KslFloat1>

    val viewMat: KslUniformMatrix<KslMat4, KslFloat4>
    val projMat: KslUniformMatrix<KslMat4, KslFloat4>
    val viewProjMat: KslUniformMatrix<KslMat4, KslFloat4>
    val viewport: KslUniformVector<KslFloat4, KslFloat1>

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

    override fun onShaderCreated(shader: ShaderBase, pipeline: PipelineBase) {
        uPosition = shader.uniforms[UNIFORM_NAME_CAM_POSITION] as Uniform3f?
        uDirection = shader.uniforms[UNIFORM_NAME_CAM_DIRECTION] as Uniform3f?
        uClip = shader.uniforms[UNIFORM_NAME_CAM_CLIP] as Uniform2f?
        uViewMat = shader.uniforms[UNIFORM_NAME_VIEW_MAT] as UniformMat4f?
        uProjMat = shader.uniforms[UNIFORM_NAME_PROJ_MAT] as UniformMat4f?
        uViewProjMat = shader.uniforms[UNIFORM_NAME_VIEW_PROJ_MAT] as UniformMat4f?
        uViewport = shader.uniforms[UNIFORM_NAME_VIEWPORT] as Uniform4f?
    }

    override fun onUpdate(cmd: DrawCommand) {
        val q = cmd.queue
        val vp = q.view.viewport
        uViewport?.value?.set(vp.x.toFloat(), vp.y.toFloat(), vp.width.toFloat(), vp.height.toFloat())

        val cam = q.view.camera
        uPosition?.value?.set(cam.globalPos)
        uDirection?.value?.set(cam.globalLookDir)
        uClip?.value?.set(cam.clipNear, cam.clipFar)

        uProjMat?.value?.set(q.projMat)
        uViewMat?.value?.set(q.viewMatF)
        uViewProjMat?.value?.set(q.viewProjMatF)
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