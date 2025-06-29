package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.NodeId
import io.ygdrasil.webgpu.*

class WgpuDrawPipeline(
    val drawPipeline: DrawPipeline,
    private val vertexShaderModule: GPUShaderModule,
    private val fragmentShaderModule: GPUShaderModule,
    backend: RenderBackendWgpu4k,
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
                        GpuType.Float1 -> GPUVertexFormat.Float32 to 4
                        GpuType.Float2 -> GPUVertexFormat.Float32x2 to 8
                        GpuType.Float3 -> GPUVertexFormat.Float32x3 to 12
                        GpuType.Float4 -> GPUVertexFormat.Float32x4 to 16

                        GpuType.Int1 -> GPUVertexFormat.Sint32 to 4
                        GpuType.Int2 -> GPUVertexFormat.Sint32x2 to 8
                        GpuType.Int3 -> GPUVertexFormat.Sint32x3 to 12
                        GpuType.Int4 -> GPUVertexFormat.Sint32x4 to 16

                        GpuType.Uint1 -> GPUVertexFormat.Uint32 to 4
                        GpuType.Uint2 -> GPUVertexFormat.Uint32x2 to 8
                        GpuType.Uint3 -> GPUVertexFormat.Uint32x3 to 12
                        GpuType.Uint4 -> GPUVertexFormat.Uint32x4 to 16

                        GpuType.Bool1 -> GPUVertexFormat.Uint32 to 4
                        GpuType.Bool2 -> GPUVertexFormat.Uint32x2 to 8
                        GpuType.Bool3 -> GPUVertexFormat.Uint32x3 to 12
                        GpuType.Bool4 -> GPUVertexFormat.Uint32x4 to 16

                        GpuType.Mat2 -> GPUVertexFormat.Float32x2 to 8
                        GpuType.Mat3 -> GPUVertexFormat.Float32x3 to 12
                        GpuType.Mat4 -> GPUVertexFormat.Float32x4 to 16

                        is GpuType.Struct -> TODO("GpuType.STRUCT not implemented")
                    }

                    locations[attr].mapIndexed { i, loc ->
                        VertexAttribute(
                            format = format,
                            offset = (attr.bufferOffset.toLong() + stride * i).toULong(),
                            shaderLocation = loc.location.toUInt()
                        )
                    }
                }

                if (vertexBinding.strideBytes == 0) null else {
                    VertexBufferLayout(
                        arrayStride = vertexBinding.strideBytes.toULong(),
                        attributes = attributes,
                        stepMode = when (vertexBinding.inputRate) {
                            InputRate.VERTEX -> GPUVertexStepMode.Vertex
                            InputRate.INSTANCE -> GPUVertexStepMode.Instance
                        }
                    )
                }
            }
    }

    private fun createPipeline(passEncoderState: RenderPassEncoderState): GPURenderPipeline {
        val renderPass = passEncoderState.renderPass
        val gpuRenderPass = passEncoderState.gpuRenderPass

        val shaderCode = drawPipeline.shaderCode as RenderBackendWgpu4k.WebGpuShaderCode
        val vertexState = VertexState(
            module = vertexShaderModule,
            entryPoint = shaderCode.vertexEntryPoint,
            buffers = createVertexBufferLayout()
        )

        val blendMode = when (drawPipeline.pipelineConfig.blendMode) {
            BlendMode.DISABLED -> null
            BlendMode.BLEND_ADDITIVE -> BlendState(
                color = BlendComponent(srcFactor = GPUBlendFactor.One, dstFactor = GPUBlendFactor.One),
                alpha = BlendComponent(),
            )
            BlendMode.BLEND_MULTIPLY_ALPHA -> BlendState(
                color = BlendComponent(srcFactor = GPUBlendFactor.SrcAlpha, dstFactor = GPUBlendFactor.OneMinusSrcAlpha),
                alpha = BlendComponent(),
            )
            BlendMode.BLEND_PREMULTIPLIED_ALPHA -> BlendState(
                color = BlendComponent(srcFactor = GPUBlendFactor.One, dstFactor = GPUBlendFactor.OneMinusSrcAlpha),
                alpha = BlendComponent(),
            )
        }

        val topology = drawPipeline.vertexLayout.primitiveType.wgpu
        val primitiveState = PrimitiveState(
            topology = topology,
            cullMode = drawPipeline.pipelineConfig.cullMethod.wgpu,
            frontFace = if (renderPass.isMirrorY) GPUFrontFace.CW else GPUFrontFace.CCW,
            stripIndexFormat = if (topology == GPUPrimitiveTopology.TriangleStrip) GPUIndexFormat.Uint32 else null,
        )

        // do not set fragmentState null even if there are no color targets
        // we might still need the fragment shader to discard fragments (e.g. for a ShadowMap with alphaMask)
        val fragmentState = FragmentState(
            module = fragmentShaderModule,
            entryPoint = shaderCode.fragmentEntryPoint,
            targets = gpuRenderPass.colorTargetFormats.map { ColorTargetState(it, blendMode) }
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
                DepthStencilState(
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
            multisample = MultisampleState(gpuRenderPass.numSamples.toUInt())
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

        var slot = 0u
        gpuInsts?.instanceBuffer?.let { passEncoder.setVertexBuffer(slot++, it) }
        gpuGeom.floatBuffer?.let { passEncoder.setVertexBuffer(slot++, it) }
        gpuGeom.intBuffer?.let { passEncoder.setVertexBuffer(slot, it) }
        passEncoder.setIndexBuffer(gpuGeom.indexBuffer, GPUIndexFormat.Uint32)
        return true
    }

    override fun removeUser(user: Any) {
        (user as? Mesh)?.let { users.remove(it.id) }
        if (users.isEmpty()) {
            release()
        }
    }
}