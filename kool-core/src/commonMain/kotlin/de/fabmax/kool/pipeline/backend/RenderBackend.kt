package de.fabmax.kool.pipeline.backend

import de.fabmax.kool.KoolContext
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.Buffer
import kotlinx.coroutines.CompletableDeferred
import kotlin.time.Duration

interface RenderBackend {

    val name: String
    val apiName: String
    val deviceName: String

    val features: BackendFeatures

    val deviceCoordinates: DeviceCoordinates
    val depthRange: DepthRange get() = deviceCoordinates.ndcDepthRange
    val isInvertedNdcY: Boolean get() = deviceCoordinates.ndcYDirection == NdcYDirection.TOP_TO_BOTTOM

    val frameGpuTime: Duration

    fun renderFrame(ctx: KoolContext)
    fun cleanup(ctx: KoolContext)

    fun generateKslShader(shader: KslShader, pipeline: DrawPipeline): ShaderCode
    fun generateKslComputeShader(shader: KslComputeShader, pipeline: ComputePipeline): ComputeShaderCode

    fun createOffscreenPass2d(parentPass: OffscreenPass2d): OffscreenPass2dImpl
    fun createOffscreenPassCube(parentPass: OffscreenPassCube): OffscreenPassCubeImpl
    fun createComputePass(parentPass: ComputePass): ComputePassImpl

    fun initStorageTexture(storageTexture: StorageTexture, width: Int, height: Int, depth: Int)

    fun <T: ImageData> uploadTextureData(tex: Texture<T>)
    fun downloadTextureData(texture: Texture<*>, deferred: CompletableDeferred<ImageData>)
    fun downloadBuffer(buffer: GpuBuffer, deferred: CompletableDeferred<Unit>, resultBuffer: Buffer)
}

class DeviceCoordinates(
    val ndcYDirection: NdcYDirection,
    val ndcDepthRange: DepthRange
) {
    companion object {
        val OPEN_GL = DeviceCoordinates(NdcYDirection.BOTTOM_TO_TOP, DepthRange.NEGATIVE_ONE_TO_ONE)
        val OPEN_GL_ZERO_TO_ONE = DeviceCoordinates(NdcYDirection.BOTTOM_TO_TOP, DepthRange.ZERO_TO_ONE)
        val WEB_GPU = DeviceCoordinates(NdcYDirection.TOP_TO_BOTTOM, DepthRange.ZERO_TO_ONE)
        val VULKAN = DeviceCoordinates(NdcYDirection.TOP_TO_BOTTOM, DepthRange.ZERO_TO_ONE)
    }
}

enum class NdcYDirection {
    TOP_TO_BOTTOM,
    BOTTOM_TO_TOP
}

enum class DepthRange {
    NEGATIVE_ONE_TO_ONE,
    ZERO_TO_ONE
}