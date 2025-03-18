
package de.fabmax.kool.pipeline.deferred

import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.modules.ksl.KslShaderListener
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.BindGroupScope
import de.fabmax.kool.pipeline.DrawCommand
import de.fabmax.kool.pipeline.ShaderBase
import de.fabmax.kool.pipeline.UniformBufferLayout
import de.fabmax.kool.util.MemoryLayout
import de.fabmax.kool.util.Struct

fun KslProgram.deferredCameraData(): DeferredCamData {
    return (dataBlocks.find { it is DeferredCamData } as? DeferredCamData) ?: DeferredCamData(this)
}

class DeferredCamData(program: KslProgram) : KslDataBlock, KslShaderListener {
    override val name = "DeferredCamData"

    private val camUniform = program.uniformStruct("uDeferredCameraData", BindGroupScope.VIEW) { DeferredCamDataStruct() }

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
        val (_, binding) = shader.createdPipeline!!.getBindGroupItem<UniformBufferLayout<DeferredCamDataStruct>> {
            it.isStructInstanceOf<DeferredCamDataStruct>()
        }
        structLayout = binding
    }

    override fun onUpdate(cmd: DrawCommand) {
        val layout = structLayout ?: return
        val viewData = cmd.queue.view.viewPipelineData.getPipelineDataUpdating(cmd.pipeline, layout.bindingIndex) ?: return
        val binding = viewData.uniformStructBindingData(layout)
        val q = cmd.queue
        val vp = q.view.viewport
        val cam = q.view.camera

        binding.set {
            proj.set(q.projMat)
            invView.set(q.invViewMatF)
            viewport.set(viewportVec.set(vp.x.toFloat(), vp.y.toFloat(), vp.width.toFloat(), vp.height.toFloat()))
            position.set(cam.globalPos)
        }
    }

    class DeferredCamDataStruct : Struct("DeferredCameraData", MemoryLayout.Std140) {
        val proj = mat4("projMat")
        val invView = mat4("invView")
        val viewport = float4("viewport")
        val position = float3("position")
    }
}
