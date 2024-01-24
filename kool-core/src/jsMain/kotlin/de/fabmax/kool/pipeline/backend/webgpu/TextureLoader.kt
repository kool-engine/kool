package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.TextureData
import de.fabmax.kool.platform.ImageTextureData
import kotlin.math.floor
import kotlin.math.log2
import kotlin.math.max

internal class TextureLoader(val backend: RenderBackendWebGpu) {

    private val device: GPUDevice get() = backend.device
    private val mipmapGenerator2d = MipmapGenerator2d()

    fun loadTexture2d(tex: Texture2d, data: TextureData) {
        val image = (data as ImageTextureData).data
        val size = intArrayOf(data.width, data.height)

        val usage = if (tex.props.generateMipMaps) {
            GPUTextureUsage.COPY_DST or GPUTextureUsage.TEXTURE_BINDING or GPUTextureUsage.RENDER_ATTACHMENT
        } else {
            GPUTextureUsage.COPY_DST or GPUTextureUsage.TEXTURE_BINDING
        }
        val mipLevels = if (tex.props.generateMipMaps) {
            floor(log2(max(data.width.toFloat(), data.height.toFloat()))).toInt() + 1
        } else {
            1
        }
        val texDesc = GPUTextureDescriptor(
            size = size,
            format = GPUTextureFormat.rgba8unorm,
            usage = usage,
            mipLevelCount = mipLevels
        )

        val gpuTex = device.createTexture(texDesc)
        device.queue.copyExternalImageToTexture(
            GPUImageCopyExternalImage(image),
            GPUImageCopyTextureTagged(gpuTex),
            size
        )

        if (tex.props.generateMipMaps) {
            mipmapGenerator2d.generateMipLevels(texDesc, gpuTex)
        }

        tex.loadedTexture = LoadedTextureWebGpu(gpuTex, data.width, data.height, 1)
    }

    private inner class MipmapGenerator2d {
        val shaderModule = device.createShaderModule("""
            var<private> pos : array<vec2f, 4> = array<vec2f, 4>(
                vec2f(-1, 1), vec2f(1, 1),
                vec2f(-1, -1), vec2f(1, -1)
            );

            struct VertexOutput {
                @builtin(position) position : vec4f,
                @location(0) texCoord : vec2f
            };
        
            @vertex
            fn vertexMain(@builtin(vertex_index) vertexIndex : u32) -> VertexOutput {
                var output : VertexOutput;
                output.texCoord = pos[vertexIndex] * vec2f(0.5, -0.5) + vec2f(0.5);
                output.position = vec4f(pos[vertexIndex], 0, 1);
                return output;
            }
        
            @group(0) @binding(0) var imgSampler : sampler;
            @group(0) @binding(1) var img : texture_2d<f32>;
        
            @fragment
            fn fragmentMain(@location(0) texCoord : vec2f) -> @location(0) vec4f {
                return textureSample(img, imgSampler, texCoord);
            }
        """.trimIndent())

        val sampler = device.createSampler(minFilter = GPUFilterMode.linear)
        val pipelines = mutableMapOf<GPUTextureFormat, GPURenderPipeline>()

        fun getRenderPipeline(format: GPUTextureFormat): GPURenderPipeline = pipelines.getOrPut(format) {
            device.createRenderPipeline(
                GPURenderPipelineDescriptor(
                    vertex = GPUVertexState(
                        module = shaderModule,
                        entryPoint = "vertexMain"
                    ),
                    fragment = GPUFragmentState(
                        module = shaderModule,
                        entryPoint = "fragmentMain",
                        targets = arrayOf(GPUColorTargetState(format))
                    ),
                    primitive = GPUPrimitiveStateStrip(GPUIndexFormat.uint32),
                    layout = GPUAutoLayoutMode.auto
                )
            )
        }

        fun generateMipLevels(texDesc: GPUTextureDescriptor, texture: GPUTexture) {
            val pipeline = getRenderPipeline(texDesc.format)
            var srcView = texture.createView(baseMipLevel = 0, mipLevelCount = 1)
            val cmdEncoder = device.createCommandEncoder()

            for (i in 1 until texDesc.mipLevelCount) {
                val dstView = texture.createView(baseMipLevel = i, mipLevelCount = 1)
                val passEncoder = cmdEncoder.beginRenderPass(
                    colorAttachments = arrayOf(GPURenderPassColorAttachmentLoad(
                        view = dstView,
                        storeOp = GPUStoreOp.store
                    ))
                )
                val bindGroup = device.createBindGroup(
                    layout = pipeline.getBindGroupLayout(0),
                    entries = arrayOf(
                        GPUBindGroupEntry(
                            binding = 0,
                            resource = sampler
                        ),
                        GPUBindGroupEntry(
                            binding = 1,
                            resource = srcView
                        ),
                    )
                )
                passEncoder.setPipeline(pipeline)
                passEncoder.setBindGroup(0, bindGroup)
                passEncoder.draw(4)
                passEncoder.end()
                srcView = dstView
            }
            device.queue.submit(arrayOf(cmdEncoder.finish()))
        }
    }
}