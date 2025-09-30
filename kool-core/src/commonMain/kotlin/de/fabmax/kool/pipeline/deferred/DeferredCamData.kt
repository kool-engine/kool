
package de.fabmax.kool.pipeline.deferred

import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.modules.ksl.KslShaderListener
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.MemoryLayout
import de.fabmax.kool.util.Struct

fun KslProgram.deferredCameraData(): DeferredCamData {
    return (dataBlocks.find { it is DeferredCamData } as? DeferredCamData) ?: DeferredCamData(this)
}

class DeferredCamData(program: KslProgram) : KslDataBlock, KslShaderListener {
    override val name = "DeferredCamData"

    private val camUniform = program.uniformStruct("uDeferredCameraData", DeferredCamDataStruct, BindGroupScope.VIEW)

    val position: KslExprFloat3 get() = camUniform.struct.position.ksl
    val projMat: KslExprMat4 get() = camUniform.struct.proj.ksl
    val invViewMat: KslExprMat4 get() = camUniform.struct.invView.ksl
    val viewport: KslExprFloat4 get() = camUniform.struct.viewport.ksl

    private var structLayout: UniformBufferLayout<DeferredCamDataStruct>? = null
    private val viewportVec = MutableVec4f()

    init {
        program.shaderListeners += this
        program.dataBlocks += this
    }

    override fun onShaderCreated(shader: ShaderBase<*>) {
        val (_, binding) = shader.createdPipeline!!.getUniformBufferLayout<DeferredCamDataStruct>()
        structLayout = binding
    }

    override fun onUpdateDrawData(cmd: DrawCommand) {
        val layout = structLayout ?: return
        cmd.queue.view.viewPipelineData.updatePipelineData(cmd.pipeline, layout.bindingIndex) { viewData ->
            val binding = viewData.uniformStructBindingData(layout)
            val q = cmd.queue
            val vp = q.view.viewport
            val cam = q.view.camera
            binding.set {
                set(it.proj, q.projMat)
                set(it.invView, q.invViewMatF)
                set(it.viewport, viewportVec.set(vp.x.toFloat(), vp.y.toFloat(), vp.width.toFloat(), vp.height.toFloat()))
                set(it.position, cam.globalPos)
            }
        }
    }

    object DeferredCamDataStruct : Struct("DeferredCameraData", MemoryLayout.Std140) {
        val proj = mat4("projMat")
        val invView = mat4("invView")
        val viewport = float4("viewport")
        val position = float3("position")
    }
}
