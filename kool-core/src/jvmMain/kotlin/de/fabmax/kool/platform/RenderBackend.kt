package de.fabmax.kool.platform

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.Viewport

interface RenderBackend {

    val apiName: String
    val deviceName: String

    val glfwWindow: GlfwWindow

    val projCorrectionMatrix: Mat4f
    val depthBiasMatrix: Mat4f

    fun getWindowViewport(result: Viewport)

    fun drawFrame(ctx: Lwjgl3Context)
    fun close(ctx: Lwjgl3Context)
    fun cleanup(ctx: Lwjgl3Context)

    fun uploadTextureToGpu(tex: Texture, data: TextureData)

    fun createOffscreenPass2d(parentPass: OffscreenRenderPass2d): OffscreenPass2dImpl
    fun createOffscreenPassCube(parentPass: OffscreenRenderPassCube): OffscreenPassCubeImpl

    fun generateKslShader(shader: KslShader, pipelineLayout: Pipeline.Layout): ShaderCode
}