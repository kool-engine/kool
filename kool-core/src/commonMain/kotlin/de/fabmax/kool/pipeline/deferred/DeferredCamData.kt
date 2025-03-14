package de.fabmax.kool.pipeline.deferred

import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.modules.ksl.KslShaderListener
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.BindGroupScope
import de.fabmax.kool.pipeline.DrawCommand
import de.fabmax.kool.pipeline.ShaderBase
import de.fabmax.kool.pipeline.UniformBufferLayout
import de.fabmax.kool.util.setFloat3
import de.fabmax.kool.util.setFloat4
import de.fabmax.kool.util.setMat4

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

    private var uboLayout: UniformBufferLayout<*>? = null
    private var positionIndex = -1
    private var projMatIndex = -1
    private var invViewMatIndex = -1
    private var viewportIndex = -1
    private val viewportVec = MutableVec4f()

    init {
        program.shaderListeners += this
        program.dataBlocks += this
        program.uniformBuffers += camUbo
    }

    override fun onShaderCreated(shader: ShaderBase<*>) {
        val binding = shader.createdPipeline!!.findBindingLayout<UniformBufferLayout<*>> { it.name == "CameraUniforms" }
        uboLayout = binding?.second
        uboLayout?.let {
            positionIndex = it.indexOfMember("uCamPos")
            projMatIndex = it.indexOfMember("uProjMat")
            invViewMatIndex = it.indexOfMember("uInvViewMat")
            viewportIndex = it.indexOfMember("uViewport")
        }
    }

    override fun onUpdate(cmd: DrawCommand) {
        val q = cmd.queue
        val vp = q.view.viewport
        val cam = q.view.camera
        val bindingLayout = uboLayout

        if (bindingLayout != null) {
            val uboData = cmd.queue.view.viewPipelineData
                .getPipelineData(cmd.pipeline)
                .uniformBufferBindingData(bindingLayout.bindingIndex)

            uboData.markDirty()
            val struct = uboData.buffer.struct
            viewportVec.set(vp.x.toFloat(), vp.y.toFloat(), vp.width.toFloat(), vp.height.toFloat())

            struct.setFloat3(positionIndex, cam.globalPos)
            struct.setMat4(projMatIndex, q.projMat)
            struct.setMat4(invViewMatIndex, q.invViewMatF)
            struct.setFloat4(viewportIndex, viewportVec)
        }
    }
}