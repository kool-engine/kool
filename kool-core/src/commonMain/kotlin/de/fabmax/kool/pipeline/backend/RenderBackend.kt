package de.fabmax.kool.pipeline.backend

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.Viewport
import kotlinx.coroutines.CompletableDeferred

interface RenderBackend {

    val name: String
    val apiName: String
    val deviceName: String

    val features: BackendFeatures

    val deviceCoordinates: DeviceCoordinates
    val depthRange: DepthRange get() = deviceCoordinates.ndcDepthRange
    val isInvertedNdcY: Boolean get() = deviceCoordinates.ndcYDirection == NdcYDirection.TOP_TO_BOTTOM

    val frameGpuTime: Double

    fun renderFrame(ctx: KoolContext)
    fun cleanup(ctx: KoolContext)

    fun getWindowViewport(result: Viewport) {
        val ctx = KoolSystem.requireContext()
        result.set(0, 0, ctx.windowWidth, ctx.windowHeight)
    }

    fun generateKslShader(shader: KslShader, pipeline: DrawPipeline): ShaderCode
    fun generateKslComputeShader(shader: KslComputeShader, pipeline: ComputePipeline): ComputeShaderCode

    fun createOffscreenPass2d(parentPass: OffscreenRenderPass2d): OffscreenPass2dImpl
    fun createOffscreenPassCube(parentPass: OffscreenRenderPassCube): OffscreenPassCubeImpl
    fun createComputePass(parentPass: ComputeRenderPass): ComputePassImpl

    fun <T: ImageData> uploadTextureData(tex: Texture<T>)
    fun downloadTextureData(texture: Texture<*>, deferred: CompletableDeferred<ImageData>)
    fun downloadStorageBuffer(storage: StorageBuffer, deferred: CompletableDeferred<Unit>)
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