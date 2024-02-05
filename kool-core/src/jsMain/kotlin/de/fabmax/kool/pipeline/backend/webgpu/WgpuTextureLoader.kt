package de.fabmax.kool.pipeline.backend.webgpu

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
import kotlin.math.floor
import kotlin.math.log2
import kotlin.math.max

internal class WgpuTextureLoader(val backend: RenderBackendWebGpu) {

    private val device: GPUDevice get() = backend.device
    val mipmapGenerator = MipmapGenerator()

    fun loadTexture(tex: Texture, data: TextureData) {
        when (tex) {
            is Texture1d -> loadTexture1d(tex, data)
            is Texture2d -> loadTexture2d(tex, data)
            is Texture3d -> loadTexture3d(tex, data)
            is TextureCube -> loadTextureCube(tex, data)
            else -> TODO("${tex::class.simpleName}")
        }
    }

    fun loadTexture1d(tex: Texture1d, data: TextureData) {
        val size = intArrayOf(data.width)
        val usage = GPUTextureUsage.COPY_DST or GPUTextureUsage.TEXTURE_BINDING
        if (tex.props.generateMipMaps) {
            logW { "generateMipMaps requested for Texture1d ${tex.name}: not supported on WebGPU" }
        }

        val texDesc = GPUTextureDescriptor(
            size = size,
            format = data.format.wgpu,
            dimension = GPUTextureDimension.texture1d,
            usage = usage
        )

        val gpuTex = backend.createTexture(texDesc, tex)
        copyTextureData(data, gpuTex.gpuTexture, size)
        tex.loadedTexture = WgpuLoadedTexture(gpuTex)
        tex.loadingState = Texture.LoadingState.LOADED
    }

    fun loadTexture2d(tex: Texture2d, data: TextureData) {
        val size = intArrayOf(data.width, data.height)
        val usage = GPUTextureUsage.COPY_DST or GPUTextureUsage.TEXTURE_BINDING or GPUTextureUsage.RENDER_ATTACHMENT
        val mipLevels = if (tex.props.generateMipMaps) {
            floor(log2(max(data.width.toFloat(), data.height.toFloat()))).toInt() + 1
        } else {
            1
        }
        val texDesc = GPUTextureDescriptor(
            size = size,
            format = data.format.wgpu,
            usage = usage,
            mipLevelCount = mipLevels
        )

        val gpuTex = backend.createTexture(texDesc, tex)
        copyTextureData(data, gpuTex.gpuTexture, size)
        if (tex.props.generateMipMaps) {
            mipmapGenerator.generateMipLevels(texDesc, gpuTex.gpuTexture)
        }

        tex.loadedTexture = WgpuLoadedTexture(gpuTex)
        tex.loadingState = Texture.LoadingState.LOADED
    }

    fun loadTexture3d(tex: Texture3d, data: TextureData) {
        val size = intArrayOf(data.width, data.height, data.depth)
        val usage = GPUTextureUsage.COPY_DST or GPUTextureUsage.TEXTURE_BINDING
        if (tex.props.generateMipMaps) {
            logW { "generateMipMaps requested for Texture3d ${tex.name}: not yet implemented on WebGPU" }
        }

        val texDesc = GPUTextureDescriptor(
            size = size,
            format = data.format.wgpu,
            usage = usage,
            dimension = GPUTextureDimension.texture3d,
        )

        val gpuTex = backend.createTexture(texDesc, tex)
        copyTextureData(data, gpuTex.gpuTexture, size)
        tex.loadedTexture = WgpuLoadedTexture(gpuTex)
        tex.loadingState = Texture.LoadingState.LOADED
    }

    fun loadTextureCube(tex: TextureCube, data: TextureData) {
        check(data is TextureDataCube || (data is TextureData3d && data.depth == 6)) {
            "Invalid cube texture data"
        }

        val usage = GPUTextureUsage.COPY_DST or GPUTextureUsage.TEXTURE_BINDING or GPUTextureUsage.RENDER_ATTACHMENT
        val texDesc = GPUTextureDescriptor(
            size = intArrayOf(data.width, data.height, 6),
            format = data.format.wgpu,
            usage = usage
        )

        val gpuTex = backend.createTexture(texDesc, tex)
        copyTextureData(data, gpuTex.gpuTexture, intArrayOf(data.width, data.height))
        if (tex.props.generateMipMaps) {
            mipmapGenerator.generateMipLevels(texDesc, gpuTex.gpuTexture)
        }

        tex.loadedTexture = WgpuLoadedTexture(gpuTex)
        tex.loadingState = Texture.LoadingState.LOADED
    }

    private fun copyTextureData(src: TextureData, dst: GPUTexture, size: IntArray) {
        when (src) {
            is ImageTextureData -> {
                device.queue.copyExternalImageToTexture(
                    source = GPUImageCopyExternalImage(src.data),
                    destination = GPUImageCopyTextureTagged(dst),
                    copySize = size
                )
            }
            is TextureData1d -> writeTextureData(src.arrayBufferView, src.gpuImageDataLayout, GPUImageCopyTexture(dst), size)
            is TextureData2d -> writeTextureData(src.arrayBufferView, src.gpuImageDataLayout, GPUImageCopyTexture(dst), size)
            is TextureData3d -> writeTextureData(src.arrayBufferView, src.gpuImageDataLayout, GPUImageCopyTexture(dst), size)
            is TextureDataCube -> {
                writeTextureData(src.posX.arrayBufferView, src.posX.gpuImageDataLayout, GPUImageCopyTexture(dst, origin = intArrayOf(0, 0, 0)), size)
                writeTextureData(src.negX.arrayBufferView, src.negX.gpuImageDataLayout, GPUImageCopyTexture(dst, origin = intArrayOf(0, 0, 1)), size)
                writeTextureData(src.posY.arrayBufferView, src.posY.gpuImageDataLayout, GPUImageCopyTexture(dst, origin = intArrayOf(0, 0, 2)), size)
                writeTextureData(src.negY.arrayBufferView, src.negY.gpuImageDataLayout, GPUImageCopyTexture(dst, origin = intArrayOf(0, 0, 3)), size)
                writeTextureData(src.posZ.arrayBufferView, src.posZ.gpuImageDataLayout, GPUImageCopyTexture(dst, origin = intArrayOf(0, 0, 4)), size)
                writeTextureData(src.negZ.arrayBufferView, src.negZ.gpuImageDataLayout, GPUImageCopyTexture(dst, origin = intArrayOf(0, 0, 5)), size)
            }
            else -> error("Not implemented: ${src::class.simpleName}")
        }
    }

    private fun writeTextureData(src: ArrayBufferView, layout: GPUImageDataLayout, dst: GPUImageCopyTexture, size: IntArray) {
        device.queue.writeTexture(
            destination = dst,
            data = src,
            dataLayout = layout,
            size = size
        )
    }

    private val TextureData.gpuImageDataLayout: GPUImageDataLayout get() {
        return when (this) {
            is TextureData1d -> gpuImageDataLayout
            is TextureData2d -> gpuImageDataLayout
            is TextureData3d -> gpuImageDataLayout
            else -> error("Invalid TextureData type: $this")
        }
    }

    private val TextureData1d.gpuImageDataLayout: GPUImageDataLayout get() {
        val bytesPerRow = format.pxSize * width
        return GPUImageDataLayout(bytesPerRow = bytesPerRow, rowsPerImage = 1)
    }

    private val TextureData2d.gpuImageDataLayout: GPUImageDataLayout get() {
        val bytesPerRow = format.pxSize * width
        return GPUImageDataLayout(bytesPerRow = bytesPerRow, rowsPerImage = height)
    }

    private val TextureData3d.gpuImageDataLayout: GPUImageDataLayout get() {
        val bytesPerRow = format.pxSize * width
        return GPUImageDataLayout(bytesPerRow = bytesPerRow, rowsPerImage = height)
    }

    private val TextureData.arrayBufferView: ArrayBufferView get() {
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
        // from: https://stackoverflow.com/questions/3026441/float32-to-float16
        val f32bits = f32.toBits()
        var f16bits = (f32bits shr 31) shl 5
        var tmp = (f32bits shr 23) and 0xff
        tmp = (tmp - 0x70) and ((((0x70 - tmp) shr 4) shr 27) and 0x1f)
        f16bits = (f16bits or tmp) shl 10
        f16bits = f16bits or ((f32bits shr 13) and 0x3ff)

        val byteI = index * 2
        set(byteI, (f16bits and 0xff).toByte())
        set(byteI+1, (f16bits shr 8).toByte())
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
            val cmdEncoder = device.createCommandEncoder()
            generateMipLevels(texDesc, texture, cmdEncoder)
            device.queue.submit(arrayOf(cmdEncoder.finish()))
        }

        fun generateMipLevels(texDesc: GPUTextureDescriptor, texture: GPUTexture, cmdEncoder: GPUCommandEncoder) {
            val pipeline = getRenderPipeline(texDesc.format)
            val layers = if (texDesc.size.size == 3) texDesc.size[2] else 1

            for (layer in 0 until layers) {
                var srcView = texture.createView(baseMipLevel = 0, mipLevelCount = 1, baseArrayLayer = layer, arrayLayerCount = 1)
                for (i in 1 until texDesc.mipLevelCount) {
                    val dstView = texture.createView(baseMipLevel = i, mipLevelCount = 1, baseArrayLayer = layer, arrayLayerCount = 1)
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
}