package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.NodeId

class WgpuDrawPipeline(
    val drawPipeline: DrawPipeline,
    private val vertexShaderModule: GPUShaderModule,
    private val fragmentShaderModule: GPUShaderModule,
    backend: RenderBackendWebGpu,
): WgpuPipeline(drawPipeline, backend) {

    private val vertexBufferLayout: List<GPUVertexBufferLayout> = createVertexBufferLayout(drawPipeline)
    private val renderPipelines = mutableMapOf<WgpuRenderPass<*>, GPURenderPipeline>()

    private val users = mutableSetOf<NodeId>()

    private fun createVertexBufferLayout(pipeline: DrawPipeline): List<GPUVertexBufferLayout> {
        return pipeline.vertexLayout.bindings
            .sortedBy { it.inputRate.name }     // INSTANCE first, VERTEX second
            .mapNotNull { vertexBinding ->
                val attributes = vertexBinding.vertexAttributes.flatMap { attr ->
                    val (format, stride) = when (attr.type) {
                        GpuType.FLOAT1 -> GPUVertexFormat.float32 to 4
                        GpuType.FLOAT2 -> GPUVertexFormat.float32x2 to 8
                        GpuType.FLOAT3 -> GPUVertexFormat.float32x3 to 12
                        GpuType.FLOAT4 -> GPUVertexFormat.float32x4 to 16

                        GpuType.INT1 -> GPUVertexFormat.sint32 to 4
                        GpuType.INT2 -> GPUVertexFormat.sint32x2 to 8
                        GpuType.INT3 -> GPUVertexFormat.sint32x3 to 12
                        GpuType.INT4 -> GPUVertexFormat.sint32x4 to 16

                        GpuType.MAT2 -> GPUVertexFormat.float32x2 to 8
                        GpuType.MAT3 -> GPUVertexFormat.float32x3 to 12
                        GpuType.MAT4 -> GPUVertexFormat.float32x4 to 16
                    }

                    locations[attr].mapIndexed { i, loc ->
                        GPUVertexAttribute(
                            format = format,
                            offset = attr.bufferOffset.toLong() + stride * i,
                            shaderLocation = loc.location
                        )
                    }
                }

                if (vertexBinding.strideBytes == 0) null else {
                    GPUVertexBufferLayout(
                        arrayStride = vertexBinding.strideBytes.toLong(),
                        attributes = attributes.toTypedArray(),
                        stepMode = when (vertexBinding.inputRate) {
                            InputRate.VERTEX -> GPUVertexStepMode.vertex
                            InputRate.INSTANCE -> GPUVertexStepMode.instance
                        }
                    )
                }
            }
    }

    private fun createRenderPipeline(passEncoderState: RenderPassEncoderState<*>): GPURenderPipeline {
        val renderPass = passEncoderState.renderPass
        val gpuRenderPass = passEncoderState.gpuRenderPass

        val shaderCode = drawPipeline.shaderCode as RenderBackendWebGpu.WebGpuShaderCode
        val vertexState = GPUVertexState(
            module = vertexShaderModule,
            entryPoint = shaderCode.vertexEntryPoint,
            buffers = vertexBufferLayout.toTypedArray()
        )

        val blendMode = when (drawPipeline.pipelineConfig.blendMode) {
            BlendMode.DISABLED -> null
            BlendMode.BLEND_ADDITIVE -> GPUBlendState(
                color = GPUBlendComponent(srcFactor = GPUBlendFactor.one, dstFactor = GPUBlendFactor.one),
                alpha = GPUBlendComponent(),
            )
            BlendMode.BLEND_MULTIPLY_ALPHA -> GPUBlendState(
                color = GPUBlendComponent(srcFactor = GPUBlendFactor.srcAlpha, dstFactor = GPUBlendFactor.oneMinusSrcAlpha),
                alpha = GPUBlendComponent(),
            )
            BlendMode.BLEND_PREMULTIPLIED_ALPHA -> GPUBlendState(
                color = GPUBlendComponent(srcFactor = GPUBlendFactor.one, dstFactor = GPUBlendFactor.oneMinusSrcAlpha),
                alpha = GPUBlendComponent(),
            )
        }

        val primitiveState = GPUPrimitiveState(
            topology = drawPipeline.vertexLayout.primitiveType.wgpu,
            cullMode = drawPipeline.pipelineConfig.cullMethod.wgpu,
            frontFace = if (renderPass.isMirrorY) GPUFrontFace.cw else GPUFrontFace.ccw
        )

        // do not set fragmentState null even if there are no color targets
        // we might still need the fragment shader to discard fragments (e.g. for a ShadowMap with alphaMask)
        val fragmentState = GPUFragmentState(
            module = fragmentShaderModule,
            entryPoint = shaderCode.fragmentEntryPoint,
            targets = gpuRenderPass.colorTargetFormats.map { GPUColorTargetState(it, blendMode) }.toTypedArray()
        )

        val depthOp = when {
            passEncoderState.renderPass.isReverseDepth && drawPipeline.autoReverseDepthFunc -> {
                when (drawPipeline.pipelineConfig.depthTest) {
                    DepthCompareOp.LESS -> DepthCompareOp.GREATER
                    DepthCompareOp.LESS_EQUAL -> DepthCompareOp.GREATER_EQUAL
                    DepthCompareOp.GREATER -> DepthCompareOp.LESS
                    DepthCompareOp.GREATER_EQUAL -> DepthCompareOp.LESS_EQUAL
                    else -> drawPipeline.pipelineConfig.depthTest
                }
            }
            else -> drawPipeline.pipelineConfig.depthTest
        }

        val hasDepthAttachment = renderPass !is OffscreenRenderPass ||
                renderPass.depthAttachment != OffscreenRenderPass.DepthAttachmentNone

        val depthStencil = if (!hasDepthAttachment) null else {
            gpuRenderPass.depthFormat?.let { depthFormat ->
                GPUDepthStencilState(
                    format = depthFormat,
                    depthWriteEnabled = drawPipeline.pipelineConfig.isWriteDepth,
                    depthCompare = depthOp.wgpu
                )
            }
        }

        return device.createRenderPipeline(
            label = "${drawPipeline.name}-layout",
            layout = pipelineLayout,
            vertex = vertexState,
            fragment = fragmentState,
            depthStencil = depthStencil,
            primitive = primitiveState,
            multisample = GPUMultisampleState(gpuRenderPass.numSamples)
        )
    }

    fun bind(cmd: DrawCommand, passEncoderState: RenderPassEncoderState<*>): Boolean {
        users.add(cmd.mesh.id)

        val pipelineData = drawPipeline.pipelineData
        val viewData = cmd.queue.view.viewPipelineData.getPipelineData(drawPipeline)
        val meshData = cmd.mesh.meshPipelineData.getPipelineData(drawPipeline)

        if (!pipelineData.checkBindings(backend) || !viewData.checkBindings(backend) || !meshData.checkBindings(backend)) {
            return false
        }

        val renderPipeline = renderPipelines.getOrPut(passEncoderState.gpuRenderPass) {
            createRenderPipeline(passEncoderState)
        }

        passEncoderState.setPipeline(renderPipeline)
        viewData.getOrCreateWgpuData().bind(passEncoderState, cmd.queue.renderPass)
        pipelineData.getOrCreateWgpuData().bind(passEncoderState, cmd.queue.renderPass)
        meshData.getOrCreateWgpuData().bind(passEncoderState, cmd.queue.renderPass)
        bindVertexBuffers(passEncoderState.passEncoder, cmd)
        return true
    }

    private fun bindVertexBuffers(passEncoder: GPURenderPassEncoder, cmd: DrawCommand) {
        if (cmd.mesh.geometry.gpuGeometry == null) {
            cmd.mesh.geometry.gpuGeometry = WgpuGeometry(cmd.mesh, backend)
        }
        val gpuGeom = cmd.mesh.geometry.gpuGeometry as WgpuGeometry
        gpuGeom.checkBuffers()

        var slot = 0

        cmd.instances?.let { insts ->
            if (insts.gpuInstances == null) {
                insts.gpuInstances = WgpuInstances(insts, backend, cmd.mesh)
            }
            val gpuInsts = insts.gpuInstances as WgpuInstances
            gpuInsts.checkBuffers()
            gpuInsts.instanceBuffer?.let { passEncoder.setVertexBuffer(slot++, it) }
        }
        passEncoder.setVertexBuffer(slot++, gpuGeom.floatBuffer)
        gpuGeom.intBuffer?.let { passEncoder.setVertexBuffer(slot, it) }
        passEncoder.setIndexBuffer(gpuGeom.indexBuffer, GPUIndexFormat.uint32)
    }

    override fun removeUser(user: Any) {
        (user as? Mesh)?.let { users.remove(it.id) }
        if (users.isEmpty()) {
            release()
        }
    }
}