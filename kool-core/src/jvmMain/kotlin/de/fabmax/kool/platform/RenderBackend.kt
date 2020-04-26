package de.fabmax.kool.platform

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.pipeline.BufferedTextureData
import de.fabmax.kool.pipeline.CubeMapTexture
import de.fabmax.kool.pipeline.CubeMapTextureData
import de.fabmax.kool.pipeline.Texture

interface RenderBackend {

    val apiName: String
    val deviceName: String

    val windowWidth: Int
    val windowHeight: Int
    val windowViewport: KoolContext.Viewport

    val glfwWindowHandle: Long

    val projCorrectionMatrix: Mat4d
    val depthBiasMatrix: Mat4d

    fun drawFrame(ctx: Lwjgl3Context)
    fun destroy(ctx: Lwjgl3Context)

    fun loadTex2d(tex: Texture, data: BufferedTextureData, recv: (Texture) -> Unit)
    fun loadTexCube(tex: CubeMapTexture, data: CubeMapTextureData, recv: (CubeMapTexture) -> Unit)

}