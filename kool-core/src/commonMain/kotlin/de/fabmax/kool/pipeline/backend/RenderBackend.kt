package de.fabmax.kool.pipeline.backend

import de.fabmax.kool.KoolContext
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.Viewport

interface RenderBackend {

    val name: String
    val apiName: String
    val deviceName: String

    val depthRange: DepthRange
    val canBlitRenderPasses: Boolean
    val isOnscreenInfiniteDepthCapable: Boolean

    fun renderFrame(ctx: KoolContext)
    fun close(ctx: KoolContext)
    fun cleanup(ctx: KoolContext)

    fun getWindowViewport(result: Viewport)

    fun generateKslShader(shader: KslShader, pipeline: PipelineBase): ShaderCode

    fun createOffscreenPass2d(parentPass: OffscreenRenderPass2d): OffscreenPass2dImpl
    fun createOffscreenPassCube(parentPass: OffscreenRenderPassCube): OffscreenPassCubeImpl
    fun uploadTextureToGpu(tex: Texture, data: TextureData)

}

enum class DepthRange {
    NEGATIVE_ONE_TO_ONE,
    ZERO_TO_ONE
}