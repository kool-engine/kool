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

    private val pipelines = mutableMapOf<WgpuRenderPass, GPURenderPipeline>()
    private val users = mutableSetOf<NodeId>()

    private fun createVertexBufferLayout(): List<GPUVertexBufferLayout> {
        val bindings = drawPipeline.vertexLayout.bindings.filter { it.vertexAttributes.isNotEmpty() }
        return bindings
            .sortedBy { it.inputRate.name }     // INSTANCE first, VERTEX second
            .mapNotNull { vertexBinding ->
                val attributes = vertexBinding.vertexAttributes.flatMap { attr ->
                    val (format, stride) = when (attr.type) {
                        GpuType.Float1 -> GPUVertexFormat.float32 to 4
                        GpuType.Float2 -> GPUVertexFormat.float32x2 to 8
                        GpuType.Float3 -> GPUVertexFormat.float32x3 to 12
                        GpuType.Float4 -> GPUVertexFormat.float32x4 to 16

                        GpuType.Int1 -> GPUVertexFormat.sint32 to 4
                        GpuType.Int2 -> GPUVertexFormat.sint32x2 to 8
                        GpuType.Int3 -> GPUVertexFormat.sint32x3 to 12
                        GpuType.Int4 -> GPUVertexFormat.sint32x4 to 16

                        GpuType.Uint1 -> GPUVertexFormat.uint32 to 4
                        GpuType.Uint2 -> GPUVertexFormat.uint32x2 to 8
                        GpuType.Uint3 -> GPUVertexFormat.uint32x3 to 12
                        GpuType.Uint4 -> GPUVertexFormat.uint32x4 to 16

                        GpuType.Bool1 -> GPUVertexFormat.uint32 to 4
                        GpuType.Bool2 -> GPUVertexFormat.uint32x2 to 8
                        GpuType.Bool3 -> GPUVertexFormat.uint32x3 to 12
                        GpuType.Bool4 -> GPUVertexFormat.uint32x4 to 16

                        GpuType.Mat2 -> GPUVertexFormat.float32x2 to 8
                        GpuType.Mat3 -> GPUVertexFormat.float32x3 to 12
                        GpuType.Mat4 -> GPUVertexFormat.float32x4 to 16

                        is GpuType.Struct -> TODO("GpuType.STRUCT not implemented")
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

    private fun createPipeline(passEncoderState: RenderPassEncoderState): GPURenderPipeline {
        val renderPass = passEncoderState.renderPass
        val gpuRenderPass = passEncoderState.gpuRenderPass

        val shaderCode = drawPipeline.shaderCode as RenderBackendWebGpu.WebGpuShaderCode
        val vertexState = GPUVertexState(
            module = vertexShaderModule,
            entryPoint = shaderCode.vertexEntryPoint,
            buffers = createVertexBufferLayout().toTypedArray()
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
            renderPass.depthMode == DepthMode.Reversed && drawPipeline.autoReverseDepthFunc -> {
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

        val depthStencil = if (!renderPass.hasDepth) null else {
            gpuRenderPass.depthFormat?.let { depthFormat ->
                GPUDepthStencilState(
                    format = depthFormat,
                    depthWriteEnabled = drawPipeline.isWriteDepth,
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

    fun updateGeometry(cmd: DrawCommand) {
        if (cmd.geometry.numIndices == 0) return
        users.add(cmd.mesh.id)

        if (cmd.geometry.gpuGeometry == null) {
            cmd.geometry.gpuGeometry = WgpuGeometry(cmd.mesh, backend)
        }
        val gpuGeom = cmd.geometry.gpuGeometry as WgpuGeometry
        gpuGeom.checkBuffers()

        cmd.instances?.let { insts ->
            if (insts.gpuInstances == null) {
                insts.gpuInstances = WgpuInstances(insts, backend, cmd.mesh)
            }
            val gpuInsts = insts.gpuInstances as WgpuInstances
            gpuInsts.checkBuffers()
        }
    }

    fun bind(cmd: DrawCommand, passEncoderState: RenderPassEncoderState): Boolean {
        val pipelineData = drawPipeline.pipelineData
        val viewData = cmd.queue.view.viewPipelineData.getPipelineData(drawPipeline)
        val meshData = cmd.mesh.meshPipelineData.getPipelineData(drawPipeline)

        val pipelineDataOk = pipelineData.checkBindings()
        val viewDataOk = viewData.checkBindings()
        val meshDataOk = meshData.checkBindings()
        if (!viewDataOk || !pipelineDataOk || !meshDataOk) {
            return false
        }

        val pipeline = pipelines.getOrPut(passEncoderState.gpuRenderPass) { createPipeline(passEncoderState) }
        passEncoderState.setPipeline(pipeline)

        viewData.getOrCreateWgpuData().bind(passEncoderState)
        pipelineData.getOrCreateWgpuData().bind(passEncoderState)
        meshData.getOrCreateWgpuData().bind(passEncoderState)

        return bindVertexBuffers(passEncoderState.passEncoder, cmd)
    }

    private fun bindVertexBuffers(passEncoder: GPURenderPassEncoder, cmd: DrawCommand): Boolean {
        val gpuGeom = cmd.mesh.geometry.gpuGeometry as WgpuGeometry? ?: return false
        val gpuInsts = cmd.instances?.gpuInstances as WgpuInstances?

        var slot = 0
        gpuInsts?.instanceBuffer?.let { passEncoder.setVertexBuffer(slot++, it) }
        gpuGeom.floatBuffer?.let { passEncoder.setVertexBuffer(slot++, it) }
        gpuGeom.intBuffer?.let { passEncoder.setVertexBuffer(slot, it) }
        passEncoder.setIndexBuffer(gpuGeom.indexBuffer, GPUIndexFormat.uint32)
        return true
    }

    override fun removeUser(user: Any) {
        (user as? Mesh)?.let { users.remove(it.id) }
        if (users.isEmpty()) {
            release()
        }
    }
}