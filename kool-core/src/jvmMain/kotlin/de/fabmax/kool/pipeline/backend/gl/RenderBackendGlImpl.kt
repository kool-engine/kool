package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.GlfwWindow
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.RenderBackendJvm
import de.fabmax.kool.util.Viewport
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose
import org.lwjgl.glfw.GLFW.glfwSwapBuffers
import org.lwjgl.opengl.EXTTextureFilterAnisotropic
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20.GL_MAX_TEXTURE_IMAGE_UNITS
import org.lwjgl.opengl.GL20.GL_VERTEX_PROGRAM_POINT_SIZE
import org.lwjgl.opengl.GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS

class RenderBackendGlImpl(ctx: KoolContext) : RenderBackendGl(GlImpl, ctx), RenderBackendJvm {
    override val glfwWindow: GlfwWindow

    override val name = "Common GL backend"
    override val version: ApiVersion
    override val capabilities: GlCapabilities

    init {
        glfwWindow = createWindow()

        // This line is critical for LWJGL's interoperation with GLFW's OpenGL context, or any context that is managed
        // externally. LWJGL detects the context that is current in the current thread, creates the GLCapabilities
        // instance and makes the OpenGL bindings available for use.
        GL.createCapabilities()

        version = checkApiVersion()
        capabilities = determineCapabilities()

        glEnable(GL_VERTEX_PROGRAM_POINT_SIZE)
        glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS)
    }

    override fun getWindowViewport(result: Viewport) {
        result.set(0, 0, glfwWindow.framebufferWidth, glfwWindow.framebufferHeight)
    }

    override fun renderFrame(ctx: KoolContext) {
        super.renderFrame(ctx)
        glfwSwapBuffers(glfwWindow.windowPtr)
    }

    override fun copyTexturesFast(renderPass: OffscreenRenderPass2dGl) {
        TextureCopyHelper.copyTexturesFast(renderPass, this)
    }

    override fun copyTexturesFast(renderPass: OffscreenRenderPassCubeGl) {
        TextureCopyHelper.copyTexturesFast(renderPass, this)
    }

    override fun readTexturePixels(src: LoadedTextureGl, dst: TextureData) {
        TextureCopyHelper.readTexturePixels(src, dst)
    }

    override fun drawOffscreen(offscreenPass: OffscreenRenderPass) {
        when (offscreenPass) {
            is OffscreenRenderPass2d -> offscreenPass.impl.draw(ctx)
            is OffscreenRenderPassCube -> offscreenPass.impl.draw(ctx)
            is OffscreenRenderPass2dPingPong -> drawOffscreenPingPong(offscreenPass)
            else -> throw IllegalArgumentException("Offscreen pass type not implemented: $offscreenPass")
        }
    }

    private fun drawOffscreenPingPong(offscreenPass: OffscreenRenderPass2dPingPong) {
        for (i in 0 until offscreenPass.pingPongPasses) {
            offscreenPass.onDrawPing?.invoke(i)
            offscreenPass.ping.impl.draw(ctx)

            offscreenPass.onDrawPong?.invoke(i)
            offscreenPass.pong.impl.draw(ctx)
        }
    }

    private fun createWindow(): GlfwWindow {
        // do basic GLFW configuration before we create the window
        GLFW.glfwDefaultWindowHints()
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, KoolSystem.config.msaaSamples)

        // create window
        val glfwWindow = GlfwWindow(ctx as Lwjgl3Context)
        glfwWindow.isFullscreen = KoolSystem.config.isFullscreen

        // make the OpenGL context current
        GLFW.glfwMakeContextCurrent(glfwWindow.windowPtr)

        // enable V-sync if configured
        if (KoolSystem.config.isVsync) {
            GLFW.glfwSwapInterval(1)
        } else {
            GLFW.glfwSwapInterval(0)
        }

        // make the window visible
        if (KoolSystem.config.showWindowOnStart) {
            glfwWindow.isVisible = true
        }
        return glfwWindow
    }

    private fun determineCapabilities(): GlCapabilities {
        // check for anisotropic texture filtering support
        val extensions = glGetString(GL_EXTENSIONS)?.split(" ") ?: emptyList()

        val maxAnisotropy: Int
        val glTextureMaxAnisotropyExt: Int

        if (extensions.contains("GL_EXT_texture_filter_anisotropic")) {
            maxAnisotropy = glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT).toInt()
            glTextureMaxAnisotropyExt = EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT
        } else {
            maxAnisotropy = 1
            glTextureMaxAnisotropyExt = 0
        }
        val maxTexUnits = glGetInteger(GL_MAX_TEXTURE_IMAGE_UNITS)
        val canFastCopyTextures = version.isHigherOrEqualThan(4, 3)

        return GlCapabilities(
            maxTexUnits,
            maxAnisotropy,
            glTextureMaxAnisotropyExt,
            canFastCopyTextures
        )
    }

    private fun checkApiVersion(): ApiVersion {
        val versionStr = glGetString(GL_VERSION) ?: ""
        var major = 0
        var minor = 0
        if (versionStr.matches(Regex("^[0-9]\\.[0-9].*"))) {
            val parts = versionStr.split(Regex("[^0-9]"), 3)
            major = parts[0].toInt()
            minor = parts[1].toInt()
        }
        if (major < 3 || (major == 3 && minor < 3)) {
            throw RuntimeException("Minimum required OpenGL version is 3.3 but system version is $major.$minor")
        }

        val deviceName = glGetString(GL_RENDERER) ?: ""
        return ApiVersion(major, minor, GlFlavor.OpenGL, versionStr, deviceName)
    }

    override fun close(ctx: KoolContext) {
        glfwSetWindowShouldClose(glfwWindow.windowPtr, true)
    }

    override fun cleanup(ctx: KoolContext) {
        // for now, we leave the cleanup to the system...
    }

}
