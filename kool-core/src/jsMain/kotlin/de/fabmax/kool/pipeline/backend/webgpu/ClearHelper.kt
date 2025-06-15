package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.ClearColorFill
import de.fabmax.kool.pipeline.ClearDepthFill
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.Float32BufferImpl

class ClearHelper(val backend: RenderBackendWebGpu) {
    private val clearPipelines = mutableMapOf<WgpuRenderPass, ClearPipeline>()
    private val shaderModule = backend.device.createShaderModule(SHADER_SRC)

    fun clear(passEncoderState: RenderPassEncoderState) {
        val clearPipeline = clearPipelines.getOrPut(passEncoderState.gpuRenderPass) {
            ClearPipeline(passEncoderState.gpuRenderPass)
        }
        clearPipeline.clear(passEncoderState)
    }

    private inner class ClearPipeline(val gpuRenderPass: WgpuRenderPass) {
        val clearValues = Float32Buffer(8) as Float32BufferImpl
        var prevColor: Color? = null
        var prevDepth = 0f

        val clearValuesBuffer: GpuBufferWgpu = backend.createBuffer(
            GPUBufferDescriptor(
                label = "clearHelper-clearValues",
                size = 32,
                usage = GPUBufferUsage.UNIFORM or GPUBufferUsage.COPY_DST
            ),
            "clearHelper-clearValues"
        )
        val bindGroupLayout = backend.device.createBindGroupLayout(arrayOf(
            GPUBindGroupLayoutEntryBuffer(
                binding = 0,
                visibility = GPUShaderStage.VERTEX or GPUShaderStage.FRAGMENT,
                buffer = GPUBufferBindingLayout()
            )
        ))
        val bindGroup: GPUBindGroup = backend.device.createBindGroup(bindGroupLayout, arrayOf(
            GPUBindGroupEntry(0, GPUBufferBinding(clearValuesBuffer.buffer))
        ))

        val clearColorOnly: GPURenderPipeline by lazy { makeClearPipeline(true, false) }
        val clearDepthOnly: GPURenderPipeline by lazy { makeClearPipeline(false, true) }
        val clearColorAndDepth: GPURenderPipeline by lazy { makeClearPipeline(true, true) }

        init {
            gpuRenderPass.onRelease { clearPipelines -= gpuRenderPass }
        }

        fun clear(passEncoderState: RenderPassEncoderState) {
            val rp = passEncoderState.renderPass
            val clearColor = (rp.colorAttachments[0].clearColor as? ClearColorFill)?.clearColor
            val clearDepth = rp.depthMode.far

            if (clearColor != prevColor || clearDepth != prevDepth) {
                prevColor = clearColor
                prevDepth = clearDepth
                clearValues.clear()
                clearColor?.putTo(clearValues)
                clearValues[4] = clearDepth
                backend.device.queue.writeBuffer(clearValuesBuffer.buffer, 0, clearValues.buffer, 0)
            }

            val clearPipeline = when {
                clearColor == null -> clearDepthOnly
                rp.depthAttachment?.clearDepth != ClearDepthFill -> clearColorOnly
                else -> clearColorAndDepth
            }

            passEncoderState.passEncoder.setPipeline(clearPipeline)
            passEncoderState.passEncoder.setBindGroup(0, bindGroup)
            passEncoderState.passEncoder.draw(4)
        }

        private fun makeClearPipeline(isClearColor: Boolean, isClearDepth: Boolean): GPURenderPipeline {
            val colorFormat: GPUTextureFormat = gpuRenderPass.colorTargetFormats[0]
            val depthFormat: GPUTextureFormat? = gpuRenderPass.depthFormat
            val colorSrcFactor = if (isClearColor) GPUBlendFactor.one else GPUBlendFactor.zero
            val colorDstFactor = if (isClearColor) GPUBlendFactor.zero else GPUBlendFactor.one

            return backend.device.createRenderPipeline(
                GPURenderPipelineDescriptor(
                    vertex = GPUVertexState(
                        module = shaderModule,
                        entryPoint = "vertexMain"
                    ),
                    fragment = GPUFragmentState(
                        module = shaderModule,
                        entryPoint = "fragmentMain",
                        targets = arrayOf(
                            GPUColorTargetState(colorFormat, GPUBlendState(
                                color = GPUBlendComponent(srcFactor = colorSrcFactor, dstFactor = colorDstFactor),
                                alpha = GPUBlendComponent(srcFactor = colorSrcFactor, dstFactor = colorDstFactor),
                            ))
                        )
                    ),
                    depthStencil = depthFormat?.let { depthFormat ->
                        GPUDepthStencilState(
                            format = depthFormat,
                            depthWriteEnabled = isClearDepth,
                            depthCompare = GPUCompareFunction.always
                        )
                    },
                    primitive = GPUPrimitiveState(topology = GPUPrimitiveTopology.triangleStrip),
                    layout = backend.device.createPipelineLayout(GPUPipelineLayoutDescriptor(
                        label = "clear-pipeline-layout",
                        bindGroupLayouts = arrayOf(bindGroupLayout)
                    )),
                    multisample = GPUMultisampleState(gpuRenderPass.numSamples)
                )
            )
        }
    }

    companion object {
        private val SHADER_SRC = """
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
        """.trimIndent()
    }
}