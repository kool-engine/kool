package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec4f
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

    val camUbo = KslUniformBuffer("CameraUniforms", program, BindGroupScope.SCENE).apply {
        viewMat = uniformMat4(UNIFORM_NAME_VIEW_MAT)
        projMat = uniformMat4(UNIFORM_NAME_PROJ_MAT)
        viewProjMat = uniformMat4(UNIFORM_NAME_VIEW_PROJ_MAT)
        viewport = uniformFloat4(UNIFORM_NAME_VIEWPORT)

        position = uniformFloat3(UNIFORM_NAME_CAM_POSITION)
        direction = uniformFloat3(UNIFORM_NAME_CAM_DIRECTION)
        clip = uniformFloat2(UNIFORM_NAME_CAM_CLIP)
    }

    private var uPosition: UniformBinding3f? = null
    private var uDirection: UniformBinding3f? = null
    private var uClip: UniformBinding2f? = null

    private var uViewMat: UniformBindingMat4f? = null
    private var uProjMat: UniformBindingMat4f? = null
    private var uViewProjMat: UniformBindingMat4f? = null
    private var uViewport: UniformBinding4f? = null

    init {
        program.shaderListeners += this
        program.dataBlocks += this
        program.uniformBuffers += camUbo
    }

    override fun onShaderCreated(shader: ShaderBase<*>) {
        uPosition = shader.uniform3f(UNIFORM_NAME_CAM_POSITION)
        uDirection = shader.uniform3f(UNIFORM_NAME_CAM_DIRECTION)
        uClip = shader.uniform2f(UNIFORM_NAME_CAM_CLIP)
        uViewMat = shader.uniformMat4f(UNIFORM_NAME_VIEW_MAT)
        uProjMat = shader.uniformMat4f(UNIFORM_NAME_PROJ_MAT)
        uViewProjMat = shader.uniformMat4f(UNIFORM_NAME_VIEW_PROJ_MAT)
        uViewport = shader.uniform4f(UNIFORM_NAME_VIEWPORT)
    }

    override fun onUpdate(cmd: DrawCommand) {
        val q = cmd.queue
        val vp = q.view.viewport
        uViewport?.set(Vec4f(vp.x.toFloat(), vp.y.toFloat(), vp.width.toFloat(), vp.height.toFloat()))

        val cam = q.view.camera
        uPosition?.set(cam.globalPos)
        uDirection?.set(cam.globalLookDir)
        uClip?.set(Vec2f(cam.clipNear, cam.clipFar))

        uProjMat?.set(q.projMat)
        uViewMat?.set(q.viewMatF)
        uViewProjMat?.set(q.viewProjMatF)
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