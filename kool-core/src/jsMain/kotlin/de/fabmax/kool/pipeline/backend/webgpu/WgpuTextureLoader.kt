package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.math.float32ToFloat16
import de.fabmax.kool.math.numMipLevels
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.gl.pxSize
import de.fabmax.kool.platform.ImageTextureData
import de.fabmax.kool.util.Float32BufferImpl
import de.fabmax.kool.util.Uint16BufferImpl
import de.fabmax.kool.util.Uint8BufferImpl
import de.fabmax.kool.util.logW
import org.khronos.webgl.ArrayBufferView
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.khronos.webgl.set

internal class WgpuTextureLoader(val backend: RenderBackendWebGpu) {
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
        val size = intArrayOf(data.width)
        val usage = GPUTextureUsage.COPY_DST or GPUTextureUsage.TEXTURE_BINDING
        if (tex.mipMapping.isMipMapped) {
            logW { "generateMipMaps requested for Texture1d ${tex.name}: not supported on WebGPU" }
        }

        val texDesc = GPUTextureDescriptor(
            size = size,
            format = data.format.wgpu,
            dimension = GPUTextureDimension.texture1d,
            usage = usage
        )

        val gpuTex = backend.createTexture(texDesc)
        copyTextureData(data, gpuTex.gpuTexture, size)
        return gpuTex
    }

    private fun loadTexture2d(tex: Texture2d, data: ImageData2d): WgpuTextureResource {
        val size = intArrayOf(data.width, data.height)
        val usage = GPUTextureUsage.COPY_DST or GPUTextureUsage.TEXTURE_BINDING or GPUTextureUsage.RENDER_ATTACHMENT
        val levels = tex.mipMapping.numLevels(data.width, data.height)
        val texDesc = GPUTextureDescriptor(
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
        val size = intArrayOf(data.width, data.height, data.depth)
        val usage = GPUTextureUsage.COPY_DST or GPUTextureUsage.TEXTURE_BINDING
        if (tex.mipMapping.isMipMapped) {
            logW { "generateMipMaps requested for Texture3d ${tex.name}: not yet implemented on WebGPU" }
        }

        val texDesc = GPUTextureDescriptor(
            size = size,
            format = data.format.wgpu,
            usage = usage,
            dimension = GPUTextureDimension.texture3d,
        )

        val gpuTex = backend.createTexture(texDesc)
        copyTextureData(data, gpuTex.gpuTexture, size)
        return gpuTex
    }

    private fun loadTextureCube(tex: TextureCube, data: ImageDataCube): WgpuTextureResource {
        val usage = GPUTextureUsage.COPY_DST or GPUTextureUsage.TEXTURE_BINDING or GPUTextureUsage.RENDER_ATTACHMENT
        val levels = tex.mipMapping.numLevels(data.width, data.height)
        val texDesc = GPUTextureDescriptor(
            size = intArrayOf(data.width, data.height, 6),
            format = data.format.wgpu,
            usage = usage,
            mipLevelCount = levels
        )

        val gpuTex = backend.createTexture(texDesc)
        copyTextureData(data, gpuTex.gpuTexture, intArrayOf(data.width, data.height))
        if (tex.mipMapping.isMipMapped) {
            mipmapGenerator.generateMipLevels(texDesc, gpuTex.gpuTexture)
        }
        return gpuTex
    }

    private fun loadTexture2dArray(tex: Texture2dArray, data: ImageData3d): WgpuTextureResource {
        val size = intArrayOf(data.width, data.height, data.depth)
        val usage = GPUTextureUsage.COPY_DST or GPUTextureUsage.TEXTURE_BINDING or GPUTextureUsage.RENDER_ATTACHMENT
        val levels = tex.mipMapping.numLevels(data.width, data.height)
        val texDesc = GPUTextureDescriptor(
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

    private fun loadTextureCubeArray(tex: TextureCubeArray, data: ImageDataCubeArray): WgpuTextureResource {
        val usage = GPUTextureUsage.COPY_DST or GPUTextureUsage.TEXTURE_BINDING or GPUTextureUsage.RENDER_ATTACHMENT
        val levels = tex.mipMapping.numLevels(data.width, data.height)
        val texDesc = GPUTextureDescriptor(
            size = intArrayOf(data.width, data.height, 6 * data.slices),
            format = data.format.wgpu,
            usage = usage,
            mipLevelCount = levels
        )

        val gpuTex = backend.createTexture(texDesc)
        copyTextureData(data, gpuTex.gpuTexture, intArrayOf(data.width, data.height))
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
                source = GPUImageCopyTexture(src, mipLevel = mipLevel),
                destination = GPUImageCopyTexture(dst, mipLevel = mipLevel),
                copySize = intArrayOf(width shr mipLevel, height shr mipLevel, arrayLayers)
            )
        }
    }

    fun resolveMultiSampledDepthTexture(src: GPUTexture, dst: GPUTexture, encoder: GPUCommandEncoder, mipLevel: Int = 0, layer: Int = 0) {
        multiSampledDepthTextureCopy.copyTexture(src, dst, encoder, mipLevel, layer)
    }

    private fun MipMapping.numLevels(width: Int, height: Int): Int = when (this) {
        MipMapping.Full -> numMipLevels(width, height)
        is MipMapping.Limited -> numLevels
        MipMapping.Off -> 1
    }

    private fun copyTextureData(src: ImageData, dst: GPUTexture, size: IntArray) {
        when (src) {
            is ImageTextureData -> copyTextureData(src, dst, size, intArrayOf(0, 0, 0))
            is BufferedImageData1d -> copyTextureData(src, dst, size, intArrayOf(0, 0, 0))
            is BufferedImageData2d -> copyTextureData(src, dst, size, intArrayOf(0, 0, 0))
            is BufferedImageData3d -> copyTextureData(src, dst, size, intArrayOf(0, 0, 0))
            is ImageDataCube -> {
                copyTextureData(src.posX, dst, size, intArrayOf(0, 0, 0))
                copyTextureData(src.negX, dst, size, intArrayOf(0, 0, 1))
                copyTextureData(src.posY, dst, size, intArrayOf(0, 0, 2))
                copyTextureData(src.negY, dst, size, intArrayOf(0, 0, 3))
                copyTextureData(src.posZ, dst, size, intArrayOf(0, 0, 4))
                copyTextureData(src.negZ, dst, size, intArrayOf(0, 0, 5))
            }
            is ImageDataCubeArray -> {
                src.cubes.forEachIndexed { i, cube ->
                    copyTextureData(cube.posX, dst, size, intArrayOf(0, 0, i * 6 + 0))
                    copyTextureData(cube.negX, dst, size, intArrayOf(0, 0, i * 6 + 1))
                    copyTextureData(cube.posY, dst, size, intArrayOf(0, 0, i * 6 + 2))
                    copyTextureData(cube.negY, dst, size, intArrayOf(0, 0, i * 6 + 3))
                    copyTextureData(cube.posZ, dst, size, intArrayOf(0, 0, i * 6 + 4))
                    copyTextureData(cube.negZ, dst, size, intArrayOf(0, 0, i * 6 + 5))
                }
            }
            is ImageData2dArray -> {
                val size2d = intArrayOf(size[0], size[1])
                for (i in src.images.indices) {
                    copyTextureData(src.images[i], dst, size2d, intArrayOf(0, 0, i))
                }
            }
            else -> error("Not implemented: ${src::class.simpleName}")
        }
    }

    private fun copyTextureData(src: ImageData, dst: GPUTexture, size: IntArray, dstOrigin: IntArray) {
        when (src) {
            is BufferedImageData -> {
                device.queue.writeTexture(
                    data = src.arrayBufferView,
                    destination = GPUImageCopyTexture(dst, origin = dstOrigin),
                    dataLayout = src.gpuImageDataLayout,
                    size = size
                )
            }
            is ImageTextureData -> {
                device.queue.copyExternalImageToTexture(
                    source = GPUImageCopyExternalImage(src.data),
                    destination = GPUImageCopyTextureTagged(dst, origin = dstOrigin),
                    copySize = size
                )
            }
            else -> error("Invalid src data type: $src")
        }
    }

    private val ImageData.gpuImageDataLayout: GPUImageDataLayout get() {
        return when (this) {
            is BufferedImageData1d -> gpuImageDataLayout
            is BufferedImageData2d -> gpuImageDataLayout
            is BufferedImageData3d -> gpuImageDataLayout
            else -> error("Invalid TextureData type: $this")
        }
    }

    private val BufferedImageData1d.gpuImageDataLayout: GPUImageDataLayout get() {
        val bytesPerRow = format.pxSize * width
        return GPUImageDataLayout(bytesPerRow = bytesPerRow, rowsPerImage = 1)
    }

    private val BufferedImageData2d.gpuImageDataLayout: GPUImageDataLayout get() {
        val bytesPerRow = format.pxSize * width
        return GPUImageDataLayout(bytesPerRow = bytesPerRow, rowsPerImage = height)
    }

    private val BufferedImageData3d.gpuImageDataLayout: GPUImageDataLayout get() {
        val bytesPerRow = format.pxSize * width
        return GPUImageDataLayout(bytesPerRow = bytesPerRow, rowsPerImage = height)
    }

    private val ImageData.arrayBufferView: ArrayBufferView get() {
        check(this is BufferedImageData)

        val bufData = data
        return when {
            format.isF16 && bufData is Float32BufferImpl -> {
                val f32Array = bufData.buffer
                val f16Buffer = Uint8Array(f32Array.length * 2)
                for (i in 0 until f32Array.length) {
                    f16Buffer.putF16(i, f32Array[i])
                }
                f16Buffer
            }
            bufData is Uint8BufferImpl -> bufData.buffer
            bufData is Uint16BufferImpl -> bufData.buffer
            bufData is Float32BufferImpl -> bufData.buffer
            else -> throw IllegalArgumentException("Unsupported buffer type")
        }
    }

    private fun Uint8Array.putF16(index: Int, f32: Float) {
        float32ToFloat16(f32) { high, low ->
            val byteI = index * 2
            set(byteI, low)
            set(byteI+1, high)
        }
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

        private val sampler = device.createSampler(minFilter = GPUFilterMode.linear)
        private val pipelines = mutableMapOf<GPUTextureFormat, GPURenderPipeline>()

        private fun getRenderPipeline(format: GPUTextureFormat): GPURenderPipeline = pipelines.getOrPut(format) {
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
                    primitive = GPUPrimitiveState(topology = GPUPrimitiveTopology.triangleStrip),
                    layout = GPUAutoLayoutMode.auto
                )
            )
        }

        fun generateMipLevels(texDesc: GPUTextureDescriptor, texture: GPUTexture) {
            val cmdEncoder = device.createCommandEncoder()
            generateMipLevels(texDesc, texture, cmdEncoder)
            device.queue.submit(arrayOf(cmdEncoder.finish()))
        }

        fun generateMipLevels(texDesc: GPUTextureDescriptor, texture: GPUTexture, cmdEncoder: GPUCommandEncoder) {
            val pipeline = getRenderPipeline(texDesc.format)
            val layers = if (texDesc.size.size == 3) texDesc.size[2] else 1

            for (layer in 0 until layers) {
                var srcView = texture.createView(baseMipLevel = 0, mipLevelCount = 1, baseArrayLayer = layer, arrayLayerCount = 1, dimension = GPUTextureViewDimension.view2d)
                for (i in 1 until texDesc.mipLevelCount) {
                    val dstView = texture.createView(baseMipLevel = i, mipLevelCount = 1, baseArrayLayer = layer, arrayLayerCount = 1, dimension = GPUTextureViewDimension.view2d)
                    val passEncoder = cmdEncoder.beginRenderPass(
                        colorAttachments = arrayOf(GPURenderPassColorAttachment(
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
                GPURenderPipelineDescriptor(
                    vertex = GPUVertexState(
                        module = shaderModule,
                        entryPoint = "vertexMain"
                    ),
                    fragment = GPUFragmentState(
                        module = shaderModule,
                        entryPoint = "fragmentMain",
                        targets = arrayOf()
                    ),
                    depthStencil = GPUDepthStencilState(
                        format = format,
                        depthWriteEnabled = true,
                        depthCompare = GPUCompareFunction.always
                    ),
                    primitive = GPUPrimitiveState(topology = GPUPrimitiveTopology.triangleStrip),
                    layout = GPUAutoLayoutMode.auto
                )
            )
        }

        fun copyTexture(src: GPUTexture, dst: GPUTexture, cmdEncoder: GPUCommandEncoder, mipLevel: Int, layer: Int) {
            val pipeline = getRenderPipeline(src.format)

            val srcView = src.createView(baseMipLevel = mipLevel, mipLevelCount = 1, baseArrayLayer = layer, arrayLayerCount = 1)
            val dstView = dst.createView(baseMipLevel = mipLevel, mipLevelCount = 1, baseArrayLayer = layer, arrayLayerCount = 1)
            val passEncoder = cmdEncoder.beginRenderPass(
                colorAttachments = emptyArray(),
                depthStencilAttachment = GPURenderPassDepthStencilAttachment(
                    view = dstView,
                    depthLoadOp = GPULoadOp.clear,
                    depthStoreOp = GPUStoreOp.store
                )
            )
            val bindGroup = device.createBindGroup(
                layout = pipeline.getBindGroupLayout(0),
                entries = arrayOf(
                    GPUBindGroupEntry(
                        binding = 0,
                        resource = srcView
                    ),
                )
            )
            passEncoder.setPipeline(pipeline)
            passEncoder.setBindGroup(0, bindGroup)
            passEncoder.draw(4)
            passEncoder.end()
        }
    }
}