package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.FrameData
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.BackendFeatures
import de.fabmax.kool.pipeline.backend.DeviceCoordinates
import de.fabmax.kool.pipeline.backend.RenderBackend
import de.fabmax.kool.util.Buffer
import de.fabmax.kool.util.LongHash
import kotlinx.coroutines.CompletableDeferred
import kotlin.js.js
import kotlin.time.Duration

expect class RenderBackendWebGpu : RenderBackend {
    override val name: String
    override val apiName: String
    override val deviceName: String
    override val features: BackendFeatures
    override val deviceCoordinates: DeviceCoordinates
    override val frameGpuTime: Duration
    override val isAsyncRendering: Boolean

    val adapter: GPUAdapter
    val device: GPUDevice
    val canvasContext: GPUCanvasContext
    val canvasFormat: GPUTextureFormat
    val numSamples: Int
    val framebufferSize: Vec2i

    internal val timestampQuery: WgpuTimestamps
    val isTimestampQuerySupported: Boolean
    val pipelineManager: WgpuPipelineManager
    internal val textureLoader: WgpuTextureLoader
    val clearHelper: ClearHelper

    fun createBuffer(descriptor: GPUBufferDescriptor, info: String?): GpuBufferWgpu
    fun createTexture(descriptor: GPUTextureDescriptor): WgpuTextureResource

    override fun renderFrame(frameData: FrameData, ctx: KoolContext)
    override fun cleanup(ctx: KoolContext)
    override fun generateKslShader(
        shader: KslShader,
        pipeline: DrawPipeline
    ): ShaderCode

    override fun generateKslComputeShader(
        shader: KslComputeShader,
        pipeline: ComputePipeline
    ): ComputeShaderCode

    override fun createOffscreenPass2d(parentPass: OffscreenPass2d): OffscreenPass2dImpl
    override fun createOffscreenPassCube(parentPass: OffscreenPassCube): OffscreenPassCubeImpl
    override fun createComputePass(parentPass: ComputePass): ComputePassImpl
    override fun <T : ImageData> uploadTextureData(tex: Texture<T>)
    override fun downloadTextureData(
        texture: Texture<*>,
        deferred: CompletableDeferred<ImageData>
    )

    override fun downloadBuffer(
        buffer: GpuBuffer,
        deferred: CompletableDeferred<Unit>,
        resultBuffer: Buffer
    )
}

data class WebGpuShaderCode(
    val vertexSrc: String,
    val vertexEntryPoint: String,
    val fragmentSrc: String,
    val fragmentEntryPoint: String
): ShaderCode {
    override val hash = LongHash {
        this += vertexSrc.hashCode().toLong() shl 32 or fragmentSrc.hashCode().toLong()
    }
}

data class WebGpuComputeShaderCode(val computeSrc: String, val computeEntryPoint: String): ComputeShaderCode {
    override val hash = LongHash {
        this += computeSrc
    }
}

private fun isNoWgpuSupport(): Boolean = js("!navigator.gpu")
