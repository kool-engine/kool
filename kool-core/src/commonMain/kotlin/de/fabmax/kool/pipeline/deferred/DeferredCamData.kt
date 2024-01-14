package de.fabmax.kool.pipeline.deferred

import de.fabmax.kool.modules.ksl.KslShaderListener
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.BindGroupScope
import de.fabmax.kool.pipeline.BufferPosition
import de.fabmax.kool.pipeline.ShaderBase
import de.fabmax.kool.pipeline.UniformBufferLayout
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.util.positioned

fun KslProgram.deferredCameraData(): DeferredCamData {
    return (dataBlocks.find { it is DeferredCamData } as? DeferredCamData) ?: DeferredCamData(this)
}

class DeferredCamData(program: KslProgram) : KslDataBlock, KslShaderListener {

    override val name = "DeferredCamData"

    val position: KslUniformVector<KslFloat3, KslFloat1>
    val projMat: KslUniformMatrix<KslMat4, KslFloat4>
    val invViewMat: KslUniformMatrix<KslMat4, KslFloat4>
    val viewport: KslUniformVector<KslFloat4, KslFloat1>

    private val camUbo = KslUniformBuffer("CameraUniforms", program, BindGroupScope.VIEW).apply {
        projMat = uniformMat4("uProjMat")
        invViewMat = uniformMat4("uInvViewMat")
        viewport = uniformFloat4("uViewport")
        position = uniformFloat3("uCamPos")
    }

    private var uboLayout: UniformBufferLayout? = null
    private var bufferPosPosition: BufferPosition? = null
    private var bufferPosProjMat: BufferPosition? = null
    private var bufferPosInvViewMat: BufferPosition? = null
    private var bufferPosViewport: BufferPosition? = null

    init {
        program.shaderListeners += this
        program.dataBlocks += this
        program.uniformBuffers += camUbo
    }

    override fun onShaderCreated(shader: ShaderBase<*>) {
        val binding = shader.createdPipeline!!.findBindingLayout<UniformBufferLayout> { it.name == "CameraUniforms" }
        uboLayout = binding?.second
        uboLayout?.let {
            bufferPosPosition = it.layout.uniformPositions["uCamPos"]
            bufferPosProjMat = it.layout.uniformPositions["uProjMat"]
            bufferPosInvViewMat = it.layout.uniformPositions["uInvViewMat"]
            bufferPosViewport = it.layout.uniformPositions["uViewport"]
        }
    }

    override fun onUpdate(cmd: DrawCommand) {
        val q = cmd.queue
        val vp = q.view.viewport
        val cam = q.view.camera
        val pipeline = cmd.pipeline
        val bindingLayout = uboLayout

        if (pipeline != null && bindingLayout != null) {
            val uboData = cmd.queue.view.viewPipelineData
                .getPipelineData(pipeline)
                .uniformBufferBindingData(bindingLayout.bindingIndex)

            uboData.isBufferDirty = true
            val buffer = uboData.buffer

            buffer.positioned(bufferPosPosition!!.byteIndex) { cam.globalPos.putTo(it) }
            buffer.positioned(bufferPosProjMat!!.byteIndex) { q.projMat.putTo(it) }
            buffer.positioned(bufferPosInvViewMat!!.byteIndex) { q.invViewMatF.putTo(it) }
            buffer.positioned(bufferPosViewport!!.byteIndex) {
                it.putFloat32(vp.x.toFloat())
                it.putFloat32(vp.y.toFloat())
                it.putFloat32(vp.width.toFloat())
                it.putFloat32(vp.height.toFloat())
            }
        }
    }
}