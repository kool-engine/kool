package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.math.numMipLevels
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.gl.pxSize
import de.fabmax.kool.util.logW
import io.ygdrasil.webgpu.BindGroupEntry
import io.ygdrasil.webgpu.ColorTargetState
import io.ygdrasil.webgpu.DepthStencilState
import io.ygdrasil.webgpu.Extent3D
import io.ygdrasil.webgpu.FragmentState
import io.ygdrasil.webgpu.GPUCommandEncoder
import io.ygdrasil.webgpu.GPUCompareFunction
import io.ygdrasil.webgpu.GPUDevice
import io.ygdrasil.webgpu.GPUFilterMode
import io.ygdrasil.webgpu.GPULoadOp
import io.ygdrasil.webgpu.GPUOrigin3D
import io.ygdrasil.webgpu.GPUPrimitiveTopology
import io.ygdrasil.webgpu.GPURenderPipeline
import io.ygdrasil.webgpu.GPUStoreOp
import io.ygdrasil.webgpu.GPUTexelCopyBufferLayout
import io.ygdrasil.webgpu.GPUTexture
import io.ygdrasil.webgpu.GPUTextureDescriptor
import io.ygdrasil.webgpu.GPUTextureDimension
import io.ygdrasil.webgpu.GPUTextureFormat
import io.ygdrasil.webgpu.GPUTextureUsage
import io.ygdrasil.webgpu.GPUTextureViewDimension
import io.ygdrasil.webgpu.Origin3D
import io.ygdrasil.webgpu.PrimitiveState
import io.ygdrasil.webgpu.RenderPassDepthStencilAttachment
import io.ygdrasil.webgpu.RenderPipelineDescriptor
import io.ygdrasil.webgpu.TexelCopyBufferLayout
import io.ygdrasil.webgpu.TexelCopyTextureInfo
import io.ygdrasil.webgpu.TextureDescriptor
import io.ygdrasil.webgpu.VertexState


internal class WgpuTextureLoader(val backend: GPUBackend) {
    private val loadedTextures = mutableMapOf<String, WgpuTextureResource>()

    private val device: GPUDevice get() = backend.device
    private val multiSampledDepthTextureCopy = MultiSampledDepthTextureCopy()
    val mipmapGenerator = MipmapGenerator()

    fun loadTexture(tex: Texture<*>) {
        val data = checkNotNull(tex.uploadData)
        tex.uploadData = null

        check(tex.format == data.format) {
            "Image data format doesn't match texture format: ${data.format} != ${tex.format}"
        }

        var loaded = loadedTextures[data.id]
        if (loaded != null && loaded.isReleased) { loadedTextures -= data.id }

        loaded = when {
            tex is Texture1d && data is ImageData1d -> loadTexture1d(tex, data)
            tex is Texture2d && data is ImageData2d -> loadTexture2d(tex, data)
            tex is Texture3d && data is ImageData3d -> loadTexture3d(tex, data)
            tex is TextureCube && data is ImageDataCube -> loadTextureCube(tex, data)
            tex is Texture2dArray && data is ImageData3d -> loadTexture2dArray(tex, data)
            tex is TextureCubeArray && data is ImageDataCubeArray -> loadTextureCubeArray(tex, data)
            else -> error("Invalid texture / image data combination: ${tex::class.simpleName} / ${data::class.simpleName}")
        }
        tex.gpuTexture?.release()
        tex.gpuTexture = loaded
    }

    private fun loadTexture1d(tex: Texture1d, data: ImageData1d): WgpuTextureResource {
        val size = Extent3D(data.width.toUInt())
        val usage = setOf(GPUTextureUsage.CopyDst, GPUTextureUsage.TextureBinding)
        if (tex.mipMapping.isMipMapped) {
            logW { "generateMipMaps requested for Texture1d ${tex.name}: not supported on WebGPU" }
        }

        val texDesc = TextureDescriptor(
            size = size,
            format = data.format.wgpu,
            dimension = GPUTextureDimension.OneD,
            usage = usage
        )

        val gpuTex = backend.createTexture(texDesc)
        copyTextureData(data, gpuTex.gpuTexture, size)
        return gpuTex
    }

    private fun loadTexture2d(tex: Texture2d, data: ImageData2d): WgpuTextureResource {
        val size = Extent3D(data.width.toUInt(), data.height.toUInt())
        val usage = setOf(GPUTextureUsage.CopyDst, GPUTextureUsage.TextureBinding, GPUTextureUsage.RenderAttachment)
        val levels = tex.mipMapping.numLevels(data.width, data.height)
        val texDesc = TextureDescriptor(
            size = size,
            format = data.format.wgpu,
            usage = usage,
            mipLevelCount = levels
        )

        val gpuTex = backend.createTexture(texDesc)
        copyTextureData(data, gpuTex.gpuTexture, size)
        if (tex.mipMapping.isMipMapped) {
            mipmapGenerator.generateMipLevels(texDesc, gpuTex.gpuTexture)
        }
        return gpuTex
    }

    private fun loadTexture3d(tex: Texture3d, data: ImageData3d): WgpuTextureResource {
        val size = Extent3D(data.width.toUInt(), data.height.toUInt(), data.depth.toUInt())
        val usage = setOf(GPUTextureUsage.CopyDst, GPUTextureUsage.TextureBinding)
        if (tex.mipMapping.isMipMapped) {
            logW { "generateMipMaps requested for Texture3d ${tex.name}: not yet implemented on WebGPU" }
        }

        val texDesc = TextureDescriptor(
            size = size,
            format = data.format.wgpu,
            usage = usage,
            dimension = GPUTextureDimension.ThreeD,
        )

        val gpuTex = backend.createTexture(texDesc)
        copyTextureData(data, gpuTex.gpuTexture, size)
        return gpuTex
    }

    private fun loadTextureCube(tex: TextureCube, data: ImageDataCube): WgpuTextureResource {
        val usage = setOf(GPUTextureUsage.CopyDst, GPUTextureUsage.TextureBinding, GPUTextureUsage.RenderAttachment)
        val levels = tex.mipMapping.numLevels(data.width, data.height)
        val texDesc = TextureDescriptor(
            size = Extent3D(data.width.toUInt(), data.height.toUInt(), 6u),
            format = data.format.wgpu,
            usage = usage,
            mipLevelCount = levels
        )

        val gpuTex = backend.createTexture(texDesc)
        copyTextureData(data, gpuTex.gpuTexture, Extent3D(data.width.toUInt(), data.height.toUInt()))
        if (tex.mipMapping.isMipMapped) {
            mipmapGenerator.generateMipLevels(texDesc, gpuTex.gpuTexture)
        }
        return gpuTex
    }

    private  fun loadTexture2dArray(tex: Texture2dArray, data: ImageData3d): WgpuTextureResource {
        val size = Extent3D(data.width.toUInt(), data.height.toUInt(), data.depth.toUInt())
        val usage = setOf(GPUTextureUsage.CopyDst, GPUTextureUsage.TextureBinding, GPUTextureUsage.RenderAttachment)
        val levels = tex.mipMapping.numLevels(data.width, data.height)
        val texDesc = TextureDescriptor(
            size = size,
            format = data.format.wgpu,
            usage = usage,
            mipLevelCount = levels
        )

        val gpuTex = backend.createTexture(texDesc)
        copyTextureData(data, gpuTex.gpuTexture, size)
        if (tex.mipMapping.isMipMapped) {
            mipmapGenerator.generateMipLevels(texDesc, gpuTex.gpuTexture)
        }
        return gpuTex
    }

    private  fun loadTextureCubeArray(tex: TextureCubeArray, data: ImageDataCubeArray): WgpuTextureResource {
        val usage = setOf(GPUTextureUsage.CopyDst, GPUTextureUsage.TextureBinding, GPUTextureUsage.RenderAttachment)
        val levels = tex.mipMapping.numLevels(data.width, data.height)
        val texDesc = TextureDescriptor(
            size = Extent3D(data.width.toUInt(), data.height.toUInt(), 6u * data.slices.toUInt()),
            format = data.format.wgpu,
            usage = usage,
            mipLevelCount = levels
        )

        val gpuTex = backend.createTexture(texDesc)
        copyTextureData(data, gpuTex.gpuTexture, Extent3D(data.width.toUInt(), data.height.toUInt()))
        if (tex.mipMapping.isMipMapped) {
            mipmapGenerator.generateMipLevels(texDesc, gpuTex.gpuTexture)
        }
        return gpuTex
    }

    fun copyTexture2d(src: GPUTexture, dst: GPUTexture, mipLevels: Int, encoder: GPUCommandEncoder) {
        val width = src.width
        val height = src.height
        val arrayLayers = src.depthOrArrayLayers    // is 6 for cube maps

        for (mipLevel in 0 until mipLevels) {
            encoder.copyTextureToTexture(
                source = TexelCopyTextureInfo(src, mipLevel = mipLevel.toUInt()),
                destination = TexelCopyTextureInfo(dst, mipLevel = mipLevel.toUInt()),
                copySize = Extent3D(width shr mipLevel, height shr mipLevel, arrayLayers)
            )
        }
    }

    fun resolveMultiSampledDepthTexture(src: GPUTexture, dst: GPUTexture, encoder: GPUCommandEncoder, mipLevel: Int = 0, layer: Int = 0) {
        multiSampledDepthTextureCopy.copyTexture(src, dst, encoder, mipLevel, layer)
    }

    private fun MipMapping.numLevels(width: Int, height: Int): UInt = when (this) {
        MipMapping.Full -> numMipLevels(width, height).toUInt()
        is MipMapping.Limited -> numLevels.toUInt()
        MipMapping.Off -> 1u
    }

    private fun copyTextureData(src: ImageData, dst: GPUTexture, size: Extent3D) {
        when (src) {
            is BufferedImageData1d -> copyTextureData(src, dst, size, Origin3D(0u, 0u, 0u))
            is BufferedImageData2d -> copyTextureData(src, dst, size, Origin3D(0u, 0u, 0u))
            is BufferedImageData3d -> copyTextureData(src, dst, size, Origin3D(0u, 0u, 0u))
            is ImageDataCube -> {
                copyTextureData(src.posX, dst, size, Origin3D(0u, 0u, 0u))
                copyTextureData(src.negX, dst, size, Origin3D(0u, 0u, 1u))
                copyTextureData(src.posY, dst, size, Origin3D(0u, 0u, 2u))
                copyTextureData(src.negY, dst, size, Origin3D(0u, 0u, 3u))
                copyTextureData(src.posZ, dst, size, Origin3D(0u, 0u, 4u))
                copyTextureData(src.negZ, dst, size, Origin3D(0u, 0u, 5u))
            }
            is ImageDataCubeArray -> {
                src.cubes.forEachIndexed { i, cube ->
                    val i = i.toUInt()
                    copyTextureData(cube.posX, dst, size, Origin3D(0u, 0u, i * 6u + 0u))
                    copyTextureData(cube.negX, dst, size, Origin3D(0u, 0u, i * 6u + 1u))
                    copyTextureData(cube.posY, dst, size, Origin3D(0u, 0u, i * 6u + 2u))
                    copyTextureData(cube.negY, dst, size, Origin3D(0u, 0u, i * 6u + 3u))
                    copyTextureData(cube.posZ, dst, size, Origin3D(0u, 0u, i * 6u + 4u))
                    copyTextureData(cube.negZ, dst, size, Origin3D(0u, 0u, i * 6u + 5u))
                }
            }
            is ImageData2dArray -> {
                val size2d = Extent3D(size.width, size.height)
                for (i in src.images.indices) {
                    copyTextureData(src.images[i], dst, size2d, Origin3D(0u, 0u, i.toUInt()))
                }
            }
            else -> copyTextureData(src, dst, size, Origin3D(0u, 0u, 0u))
        }
    }

    private fun copyTextureData(src: ImageData, dst: GPUTexture, size: Extent3D, dstOrigin: GPUOrigin3D) {
        copyNativeTextureData(src, dst, size, dstOrigin, device)
    }

    inner class MipmapGenerator {
        private val shaderModule = device.createShaderModule("""
            var<private> pos: array<vec2f, 4> = array<vec2f, 4>(
                vec2f(-1.0, 1.0), vec2f(1.0, 1.0),
                vec2f(-1.0, -1.0), vec2f(1.0, -1.0)
            );

            struct VertexOutput {
                @builtin(position) position: vec4f,
                @location(0) texCoord: vec2f
            };
        
            @vertex
            fn vertexMain(@builtin(vertex_index) vertexIndex: u32) -> VertexOutput {
                var output: VertexOutput;
                output.texCoord = pos[vertexIndex] * vec2f(0.5, -0.5) + vec2f(0.5);
                output.position = vec4f(pos[vertexIndex], 0.0, 1.0);
                return output;
            }
        
            @group(0) @binding(0) var imgSampler: sampler;
            @group(0) @binding(1) var img: texture_2d<f32>;
        
            @fragment
            fn fragmentMain(@location(0) texCoord: vec2f) -> @location(0) vec4f {
                return textureSample(img, imgSampler, texCoord);
            }
        """.trimIndent())

        private val sampler = device.createSampler(minFilter = GPUFilterMode.Linear)
        private val pipelines = mutableMapOf<GPUTextureFormat, GPURenderPipeline>()

        private fun getRenderPipeline(format: GPUTextureFormat): GPURenderPipeline = pipelines.getOrPut(format) {
            device.createRenderPipeline(
                RenderPipelineDescriptor(
                    vertex = VertexState(
                        module = shaderModule,
                        entryPoint = "vertexMain"
                    ),
                    fragment = FragmentState(
                        module = shaderModule,
                        entryPoint = "fragmentMain",
                        targets = listOf(ColorTargetState(format))
                    ),
                    primitive = PrimitiveState(topology = GPUPrimitiveTopology.TriangleStrip),
                )
            )
        }

        fun generateMipLevels(texDesc: GPUTextureDescriptor, texture: GPUTexture) {
            val cmdEncoder = device.createCommandEncoder()
            generateMipLevels(texDesc, texture, cmdEncoder)
            device.queue.submit(listOf(cmdEncoder.finish()))
        }

        fun generateMipLevels(texDesc: GPUTextureDescriptor, texture: GPUTexture, cmdEncoder: GPUCommandEncoder) {
            val pipeline = getRenderPipeline(texDesc.format)
            val layers = texDesc.size.depthOrArrayLayers.toInt()

            for (layer in 0 until layers) {
                var srcView = texture.createView(baseMipLevel = 0, mipLevelCount = 1, baseArrayLayer = layer, arrayLayerCount = 1, dimension = GPUTextureViewDimension.TwoD)
                for (i in 1 until texDesc.mipLevelCount.toInt()) {
                    val dstView = texture.createView(baseMipLevel = i, mipLevelCount = 1, baseArrayLayer = layer, arrayLayerCount = 1, dimension = GPUTextureViewDimension.TwoD)
                    val passEncoder = cmdEncoder.beginRenderPass(
                        colorAttachments = listOf(io.ygdrasil.webgpu.RenderPassColorAttachment(
                            view = dstView,
                            storeOp = GPUStoreOp.Store,
                            loadOp = GPULoadOp.Load,
                        ))
                    )
                    val bindGroup = device.createBindGroup(
                        layout = pipeline.getBindGroupLayout(0u),
                        entries = listOf(
                            BindGroupEntry(
                                binding = 0u,
                                resource = sampler
                            ),
                            BindGroupEntry(
                                binding = 1u,
                                resource = srcView
                            ),
                        )
                    )
                    passEncoder.setPipeline(pipeline)
                    passEncoder.setBindGroup(0u, bindGroup)
                    passEncoder.draw(4u)
                    passEncoder.end()
                    srcView = dstView
                }
            }
        }
    }

    private inner class MultiSampledDepthTextureCopy {
        private val shaderModule = device.createShaderModule("""
            var<private> pos: array<vec2f, 4> = array<vec2f, 4>(
                vec2f(-1.0, 1.0), vec2f(1.0, 1.0),
                vec2f(-1.0, -1.0), vec2f(1.0, -1.0)
            );

            struct VertexOutput {
                @builtin(position) position: vec4f,
                @location(0) texCoord: vec2f
            };
        
            @vertex
            fn vertexMain(@builtin(vertex_index) vertexIndex: u32) -> VertexOutput {
                var output: VertexOutput;
                output.texCoord = pos[vertexIndex] * vec2f(0.5, -0.5) + vec2f(0.5);
                output.position = vec4f(pos[vertexIndex], 0.0, 1.0);
                return output;
            }
        
            @group(0) @binding(0) var img: texture_multisampled_2d<f32>;
        
            @fragment
            fn fragmentMain(@location(0) texCoord: vec2f) -> @builtin(frag_depth) f32 {
                let dim = vec2f(textureDimensions(img));
                return textureLoad(img, vec2u(texCoord * dim), 0).x;
            }
        """.trimIndent())

        private val pipelines = mutableMapOf<GPUTextureFormat, GPURenderPipeline>()

        private fun getRenderPipeline(format: GPUTextureFormat): GPURenderPipeline = pipelines.getOrPut(format) {
            device.createRenderPipeline(
                RenderPipelineDescriptor(
                    vertex = VertexState(
                        module = shaderModule,
                        entryPoint = "vertexMain"
                    ),
                    fragment = FragmentState(
                        module = shaderModule,
                        entryPoint = "fragmentMain",
                        targets = listOf()
                    ),
                    depthStencil = DepthStencilState(
                        format = format,
                        depthWriteEnabled = true,
                        depthCompare = GPUCompareFunction.Always
                    ),
                    primitive = PrimitiveState(topology = GPUPrimitiveTopology.TriangleStrip)
                )
            )
        }

        fun copyTexture(src: GPUTexture, dst: GPUTexture, cmdEncoder: GPUCommandEncoder, mipLevel: Int, layer: Int) {
            val pipeline = getRenderPipeline(src.format)

            val srcView = src.createView(baseMipLevel = mipLevel, mipLevelCount = 1, baseArrayLayer = layer, arrayLayerCount = 1)
            val dstView = dst.createView(baseMipLevel = mipLevel, mipLevelCount = 1, baseArrayLayer = layer, arrayLayerCount = 1)
            val passEncoder = cmdEncoder.beginRenderPass(
                colorAttachments = emptyList(),
                depthStencilAttachment = RenderPassDepthStencilAttachment(
                    view = dstView,
                    depthLoadOp = GPULoadOp.Clear,
                    depthStoreOp = GPUStoreOp.Store,
                    depthClearValue = 1f
                )
            )
            val bindGroup = device.createBindGroup(
                layout = pipeline.getBindGroupLayout(0u),
                entries = listOf(
                    BindGroupEntry(
                        binding = 0u,
                        resource = srcView
                    ),
                )
            )
            passEncoder.setPipeline(pipeline)
            passEncoder.setBindGroup(0u, bindGroup)
            passEncoder.draw(4u)
            passEncoder.end()
        }
    }
}

expect internal fun copyNativeTextureData(
    src: ImageData,
    dst: GPUTexture,
    size: Extent3D,
    dstOrigin: GPUOrigin3D,
    device: GPUDevice
)

internal val ImageData.gpuImageDataLayout: GPUTexelCopyBufferLayout get() {
    return when (this) {
        is BufferedImageData1d -> gpuImageDataLayout
        is BufferedImageData2d -> gpuImageDataLayout
        is BufferedImageData3d -> gpuImageDataLayout
        else -> error("Invalid TextureData type: $this")
    }
}

private val BufferedImageData1d.gpuImageDataLayout: GPUTexelCopyBufferLayout get() {
    val bytesPerRow = format.pxSize * width
    return TexelCopyBufferLayout(bytesPerRow = bytesPerRow.toUInt(), rowsPerImage = 1u)
}

private val BufferedImageData2d.gpuImageDataLayout: GPUTexelCopyBufferLayout get() {
    val bytesPerRow = format.pxSize * width
    return TexelCopyBufferLayout(bytesPerRow = bytesPerRow.toUInt(), rowsPerImage = height.toUInt())
}

private val BufferedImageData3d.gpuImageDataLayout: GPUTexelCopyBufferLayout get() {
    val bytesPerRow = format.pxSize * width
    return TexelCopyBufferLayout(bytesPerRow = bytesPerRow.toUInt(), rowsPerImage = height.toUInt())
}