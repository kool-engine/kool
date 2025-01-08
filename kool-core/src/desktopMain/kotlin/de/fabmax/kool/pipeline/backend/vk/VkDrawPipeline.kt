package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.DrawCommand
import de.fabmax.kool.pipeline.DrawPipeline
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.NodeId

class VkDrawPipeline(
    val drawPipeline: DrawPipeline,
    private val vertexShaderModule: VkShaderModule,
    private val fragmentShaderModule: VkShaderModule,
    backend: RenderBackendVk,
): VkPipeline(drawPipeline, backend) {

    //private val vertexBufferLayout: List<GPUVertexBufferLayout> = createVertexBufferLayout(drawPipeline)
    private val renderPipelines = mutableMapOf<RenderPassVk<*>, VkGraphicsPipeline>()

    private val users = mutableSetOf<NodeId>()

    private fun createRenderPipeline(passEncoderState: RenderPassEncoderState<*>): VkGraphicsPipeline {
        TODO()
    }

    fun bind(cmd: DrawCommand, passEncoderState: RenderPassEncoderState<*>): Boolean {
        users.add(cmd.mesh.id)

        val pipelineData = drawPipeline.pipelineData
        val viewData = cmd.queue.view.viewPipelineData.getPipelineData(drawPipeline)
        val meshData = cmd.mesh.meshPipelineData.getPipelineData(drawPipeline)

        if (!pipelineData.checkBindings() || !viewData.checkBindings() || !meshData.checkBindings()) {
            return false
        }

        val renderPipeline = renderPipelines.getOrPut(passEncoderState.renderPassVk) {
            createRenderPipeline(passEncoderState)
        }

        passEncoderState.setPipeline(renderPipeline)
        viewData.getOrCreateVkData().bind(passEncoderState, cmd.queue.renderPass)
        pipelineData.getOrCreateVkData().bind(passEncoderState, cmd.queue.renderPass)
        meshData.getOrCreateVkData().bind(passEncoderState, cmd.queue.renderPass)
        bindVertexBuffers(passEncoderState, cmd)
        return true
    }

    private fun bindVertexBuffers(passEncoderState: RenderPassEncoderState<*>, cmd: DrawCommand) {
        TODO()
//        if (cmd.mesh.geometry.gpuGeometry == null) {
//            cmd.mesh.geometry.gpuGeometry = WgpuGeometry(cmd.mesh, backend)
//        }
//        val gpuGeom = cmd.mesh.geometry.gpuGeometry as WgpuGeometry
//        gpuGeom.checkBuffers()
//
//        var slot = 0
//
//        cmd.instances?.let { insts ->
//            if (insts.gpuInstances == null) {
//                insts.gpuInstances = WgpuInstances(insts, backend, cmd.mesh)
//            }
//            val gpuInsts = insts.gpuInstances as WgpuInstances
//            gpuInsts.checkBuffers()
//            gpuInsts.instanceBuffer?.let { passEncoder.setVertexBuffer(slot++, it) }
//        }
//        passEncoder.setVertexBuffer(slot++, gpuGeom.floatBuffer)
//        gpuGeom.intBuffer?.let { passEncoder.setVertexBuffer(slot, it) }
//        passEncoder.setIndexBuffer(gpuGeom.indexBuffer, GPUIndexFormat.uint32)
    }

    override fun removeUser(user: Any) {
        (user as? Mesh)?.let { users.remove(it.id) }
        if (users.isEmpty()) {
            release()
        }
    }
}