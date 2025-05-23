package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.pipeline.ClearColorFill
import de.fabmax.kool.pipeline.ClearDepthFill
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Float32Buffer
import io.ygdrasil.webgpu.BindGroupEntry
import io.ygdrasil.webgpu.BindGroupLayoutEntry
import io.ygdrasil.webgpu.BlendComponent
import io.ygdrasil.webgpu.BlendState
import io.ygdrasil.webgpu.BufferBinding
import io.ygdrasil.webgpu.BufferBindingLayout
import io.ygdrasil.webgpu.BufferDescriptor
import io.ygdrasil.webgpu.ColorTargetState
import io.ygdrasil.webgpu.DepthStencilState
import io.ygdrasil.webgpu.FragmentState
import io.ygdrasil.webgpu.GPUBlendFactor
import io.ygdrasil.webgpu.GPUBufferUsage
import io.ygdrasil.webgpu.GPUCompareFunction
import io.ygdrasil.webgpu.GPUPrimitiveTopology
import io.ygdrasil.webgpu.GPURenderPipeline
import io.ygdrasil.webgpu.GPUShaderStage
import io.ygdrasil.webgpu.GPUTextureFormat
import io.ygdrasil.webgpu.GPUVertexState
import io.ygdrasil.webgpu.MultisampleState
import io.ygdrasil.webgpu.PipelineLayoutDescriptor
import io.ygdrasil.webgpu.PrimitiveState
import io.ygdrasil.webgpu.RenderPipelineDescriptor
import io.ygdrasil.webgpu.VertexState

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
        val clearValues = Float32Buffer(8)
        var prevColor: Color? = null
        var prevDepth = 0f

        val clearValuesBuffer: GpuBufferWgpu = backend.createBuffer(
            BufferDescriptor(
                label = "clearHelper-clearValues",
                size = 32u,
                usage = setOf(GPUBufferUsage.Uniform, GPUBufferUsage.CopyDst)
            ),
            "clearHelper-clearValues"
        )
        val bindGroupLayout = backend.device.createBindGroupLayout(listOf(
            BindGroupLayoutEntry(
                binding = 0u,
                visibility = setOf(GPUShaderStage.Vertex, GPUShaderStage.Fragment),
                buffer = BufferBindingLayout()
            )
        ))
        val bindGroup = backend.device.createBindGroup(bindGroupLayout, listOf(
            BindGroupEntry(0u, BufferBinding(clearValuesBuffer.buffer))
        ))

        val clearColorOnly by lazy { makeClearPipeline(true, false) }
        val clearDepthOnly by lazy { makeClearPipeline(false, true) }
        val clearColorAndDepth by lazy { makeClearPipeline(true, true) }

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
                backend.device.queue.writeBuffer(clearValuesBuffer.buffer, 0u, clearValues.buffer, 0u)
            }

            val clearPipeline = when {
                clearColor == null -> clearDepthOnly
                rp.depthAttachment?.clearDepth != ClearDepthFill -> clearColorOnly
                else -> clearColorAndDepth
            }

            passEncoderState.passEncoder.setPipeline(clearPipeline)
            passEncoderState.passEncoder.setBindGroup(0u, bindGroup)
            passEncoderState.passEncoder.draw(4u)
        }

        private fun makeClearPipeline(isClearColor: Boolean, isClearDepth: Boolean): GPURenderPipeline {
            val colorFormat = gpuRenderPass.colorTargetFormats[0]
            val depthFormat = gpuRenderPass.depthFormat
            val colorSrcFactor = if (isClearColor) GPUBlendFactor.One else GPUBlendFactor.Zero
            val colorDstFactor = if (isClearColor) GPUBlendFactor.Zero else GPUBlendFactor.One

            return backend.device.createRenderPipeline(
                RenderPipelineDescriptor(
                    vertex = VertexState(
                        module = shaderModule,
                        entryPoint = "vertexMain"
                    ),
                    fragment = FragmentState(
                        module = shaderModule,
                        entryPoint = "fragmentMain",
                        targets = listOf(
                            ColorTargetState(
                                colorFormat, BlendState(
                                    color = BlendComponent(srcFactor = colorSrcFactor, dstFactor = colorDstFactor),
                                    alpha = BlendComponent(srcFactor = colorSrcFactor, dstFactor = colorDstFactor),
                                )
                            )
                        )
                    ),
                    depthStencil = depthFormat?.let {
                        DepthStencilState(
                            format = depthFormat,
                            depthWriteEnabled = isClearDepth,
                            depthCompare = GPUCompareFunction.Always
                        )
                    }
                    ,
                    primitive = PrimitiveState(topology = GPUPrimitiveTopology.TriangleStrip),
                    layout = backend.device.createPipelineLayout(
                        PipelineLayoutDescriptor(
                            label = "clear-pipeline-layout",
                            bindGroupLayouts = listOf(bindGroupLayout)
                        )
                    ),
                    multisample = MultisampleState(gpuRenderPass.numSamples.toUInt())
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