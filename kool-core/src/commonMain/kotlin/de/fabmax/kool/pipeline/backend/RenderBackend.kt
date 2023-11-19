package de.fabmax.kool.pipeline.backend

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.Viewport

interface RenderBackend {

    val name: String
    val apiName: String
    val deviceName: String

    val projCorrectionMatrix: Mat4f
    val depthBiasMatrix: Mat4f
    val isReversedDepthAvailable: Boolean

    fun renderFrame(ctx: KoolContext)
    fun close(ctx: KoolContext)
    fun cleanup(ctx: KoolContext)

    fun getWindowViewport(result: Viewport)

    fun generateKslShader(shader: KslShader, pipelineLayout: Pipeline.Layout): ShaderCode

    fun createOffscreenPass2d(parentPass: OffscreenRenderPass2d): OffscreenPass2dImpl
    fun createOffscreenPassCube(parentPass: OffscreenRenderPassCube): OffscreenPassCubeImpl
    fun uploadTextureToGpu(tex: Texture, data: TextureData)

}