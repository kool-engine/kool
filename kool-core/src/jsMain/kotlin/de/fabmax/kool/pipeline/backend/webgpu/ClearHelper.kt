package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.Float32BufferImpl

class ClearHelper(val backend: RenderBackendWebGpu) {
    private val shaderModule = backend.device.createShaderModule("""
            var<private> pos: array<vec2f, 4> = array<vec2f, 4>(
                vec2f(-1.0, 1.0), vec2f(1.0, 1.0),
                vec2f(-1.0, -1.0), vec2f(1.0, -1.0)
            );
        
            struct ClearValues {
                color: vec4f,
                depth: f32
            };
        
            @group(0) @binding(0) var<uniform> clearValues: ClearValues;
        
            @vertex
            fn vertexMain(@builtin(vertex_index) vertexIndex: u32) -> @builtin(position) vec4f {
                return vec4f(pos[vertexIndex], clearValues.depth, 1.0);
            }
        
            @fragment
            fn fragmentMain() -> @location(0) vec4f {
                return clearValues.color;
            }
        """.trimIndent())

    private val pipelines = mutableMapOf<ClearFormat, GPURenderPipeline>()
    private var bindGroup: GPUBindGroup? = null
    private var clearColorBuffer: WgpuBufferResource? = null
    private val clearValues = Float32Buffer(8) as Float32BufferImpl

    private fun getRenderPipeline(clearFormat: ClearFormat): GPURenderPipeline = pipelines.getOrPut(clearFormat) {
        println("make clear pipeline $clearFormat")
        backend.device.createRenderPipeline(
            GPURenderPipelineDescriptor(
                vertex = GPUVertexState(
                    module = shaderModule,
                    entryPoint = "vertexMain"
                ),
                fragment = GPUFragmentState(
                    module = shaderModule,
                    entryPoint = "fragmentMain",
                    targets = arrayOf(
                        GPUColorTargetState(
                            clearFormat.colorFormat, GPUBlendState(
                                color = GPUBlendComponent(srcFactor = GPUBlendFactor.srcAlpha, dstFactor = GPUBlendFactor.oneMinusSrcAlpha),
                                alpha = GPUBlendComponent(),
                            )
                        )
                    )
                ),
                depthStencil = clearFormat.depthFormat?.let { depthFormat ->
                    GPUDepthStencilState(
                        format = depthFormat,
                        depthWriteEnabled = clearFormat.isClearDepth,
                        depthCompare = GPUCompareFunction.always
                    )
                },
                primitive = GPUPrimitiveState(topology = GPUPrimitiveTopology.triangleStrip),
                layout = GPUAutoLayoutMode.auto,
                multisample = GPUMultisampleState(clearFormat.numSamples)
            )
        )
    }

    private fun makeBindGroup(layout: GPUBindGroupLayout): GPUBindGroup {
        clearColorBuffer = backend.createBuffer(
            GPUBufferDescriptor(
                label = "clearHelper-clearColor",
                size = 32,
                usage = GPUBufferUsage.UNIFORM or GPUBufferUsage.COPY_DST
            ),
            "clearHelper-clearColor"
        )
        val entry = GPUBindGroupEntry(0, GPUBufferBinding(clearColorBuffer!!.buffer))
        return backend.device.createBindGroup(layout, arrayOf(entry)).also { bindGroup = it }
    }

    fun clear(passEncoderState: RenderPassEncoderState) {
        val passEncoder = passEncoderState.passEncoder
        val gpuRenderPass = passEncoderState.gpuRenderPass
        val renderPass = passEncoderState.renderPass
        val clearDepth = renderPass.clearDepth
        val clearColor = renderPass.clearColor

        val key = ClearFormat(gpuRenderPass.colorTargetFormats[0], gpuRenderPass.depthFormat, clearColor != null, clearDepth, gpuRenderPass.numSamples)
        val pipeline = getRenderPipeline(key)

        val bindGrp = bindGroup ?: makeBindGroup(pipeline.getBindGroupLayout(0))
        val clr = clearColor ?: Color.BLACK.withAlpha(0f)
        clr.putTo(clearValues)
        clearValues[4] = if (renderPass.isReverseDepth) 0f else 1f
        backend.device.queue.writeBuffer(clearColorBuffer!!.buffer, 0L, clearValues.buffer)

        passEncoder.setPipeline(pipeline)
        passEncoder.setBindGroup(0, bindGrp)
        passEncoder.draw(4)
    }

    data class ClearFormat(val colorFormat: GPUTextureFormat, val depthFormat: GPUTextureFormat?, val isClearColor: Boolean, val isClearDepth: Boolean, val numSamples: Int)
}