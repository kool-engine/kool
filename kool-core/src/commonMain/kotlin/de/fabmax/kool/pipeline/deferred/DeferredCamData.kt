package de.fabmax.kool.pipeline.deferred

import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.KslShaderListener
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.drawqueue.DrawCommand

fun KslProgram.deferredCameraData(): DeferredCamData {
    return (dataBlocks.find { it is DeferredCamData } as? DeferredCamData) ?: DeferredCamData(this)
}

class DeferredCamData(program: KslProgram) : KslDataBlock, KslShaderListener {

    override val name = "DeferredCamData"

    val position: KslUniformVector<KslFloat3, KslFloat1>
    val projMat: KslUniformMatrix<KslMat4, KslFloat4>
    val invViewMat: KslUniformMatrix<KslMat4, KslFloat4>
    val viewport: KslUniformVector<KslFloat4, KslFloat1>

    val camUbo = KslUniformBuffer("CameraUniforms", program, BindGroupScope.SCENE).apply {
        projMat = uniformMat4("uProjMat")
        invViewMat = uniformMat4("uInvViewMat")
        viewport = uniformFloat4("uViewport")
        position = uniformFloat3("uCamPos")
    }

    private var uPosition: UniformBinding3f? = null
    private var uProjMat: UniformBindingMat4f? = null
    private var uInvViewMat: UniformBindingMat4f? = null
    private var uViewport: UniformBinding4f? = null

    init {
        program.shaderListeners += this
        program.dataBlocks += this
        program.uniformBuffers += camUbo
    }

    override fun onShaderCreated(shader: ShaderBase<*>) {
        uPosition = shader.uniform3f("uCamPos")
        uProjMat = shader.uniformMat4f("uProjMat")
        uInvViewMat = shader.uniformMat4f("uInvViewMat")
        uViewport = shader.uniform4f("uViewport")
    }

    override fun onUpdate(cmd: DrawCommand) {
        val q = cmd.queue
        val vp = q.view.viewport
        uViewport?.set(Vec4f(vp.x.toFloat(), vp.y.toFloat(), vp.width.toFloat(), vp.height.toFloat()))

        val cam = q.view.camera
        uPosition?.set(cam.globalPos)
        uProjMat?.set(q.projMat)
        uInvViewMat?.set(q.invViewMatF)
    }
}