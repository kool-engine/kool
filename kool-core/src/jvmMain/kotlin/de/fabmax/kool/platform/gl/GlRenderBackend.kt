package de.fabmax.kool.platform.gl

import de.fabmax.kool.DesktopImpl
import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolException
import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.RenderBackend
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Viewport
import de.fabmax.kool.util.logE
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.EXTTextureFilterAnisotropic
import org.lwjgl.opengl.GL.createCapabilities
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL11C.glGetString
import org.lwjgl.opengl.GL20.GL_MAX_TEXTURE_IMAGE_UNITS
import org.lwjgl.opengl.GL20.GL_VERTEX_PROGRAM_POINT_SIZE
import org.lwjgl.opengl.GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS

class GlRenderBackend(props: Lwjgl3Context.InitProps, val ctx: Lwjgl3Context) : RenderBackend {
    override val apiName: String
    override val deviceName: String

    private val glfwWindow: GlfwGlWindow
    override val windowWidth: Int
        get() = glfwWindow.framebufferWidth
    override val windowHeight: Int
        get() = glfwWindow.framebufferHeight
    override val glfwWindowHandle: Long
        get() = glfwWindow.windowPtr
    override var isFullscreen: Boolean
        get() = glfwWindow.isFullscreen
        set(value) { glfwWindow.isFullscreen = value }

    override val shaderGenerator = ShaderGeneratorImplGL()

    override val projCorrectionMatrixScreen = Mat4d()
    override val projCorrectionMatrixOffscreen = Mat4d()
    override val depthBiasMatrix = Mat4d().translate(0.5, 0.5, 0.5).scale(0.5, 0.5, 0.5)

    val supportedExtensions = mutableSetOf<String>()
    val glCapabilities = GlCapabilities()

    internal val queueRenderer = QueueRendererGl(this, ctx)
    internal val afterRenderActions = mutableListOf<() -> Unit>()

    private val openRenderPasses = mutableListOf<OffscreenRenderPass>()
    private val doneRenderPasses = mutableSetOf<OffscreenRenderPass>()

    init {
        // configure GLFW
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
        glfwWindowHint(GLFW_SAMPLES, props.msaaSamples)

        // create window
        val fsMonitor = if (props.monitor < 0) DesktopImpl.primaryMonitor else DesktopImpl.monitors[props.monitor]
        glfwWindow = GlfwGlWindow(props.width, props.height, props.title, fsMonitor)
        glfwWindow.isFullscreen = props.isFullscreen

        // make the OpenGL context current
        glfwMakeContextCurrent(glfwWindowHandle)
        // enable v-sync
        glfwSwapInterval(1)
        // make the window visible
        glfwShowWindow(glfwWindowHandle)

        // This line is critical for LWJGL's interoperation with GLFW's OpenGL context, or any context that is managed
        // externally. LWJGL detects the context that is current in the current thread, creates the GLCapabilities
        // instance and makes the OpenGL bindings available for use.
        createCapabilities()

        val versionStr = glGetString(GL_VERSION) ?: ""
        var versionMajor = 0
        var versionMinor = 0
        if (versionStr.matches(Regex("^[0-9]\\.[0-9].*"))) {
            val parts = versionStr.split(Regex("[^0-9]"), 3)
            versionMajor = parts[0].toInt()
            versionMinor = parts[1].toInt()
        }
        if (versionMajor < 3 || (versionMajor == 3 && versionMinor < 3)) {
            throw KoolException("Minimum required OpenGL version is 3.3 but system version is $versionMajor.$versionMinor")
        }

        apiName = "OpenGL $versionStr"
        deviceName = glGetString(GL_RENDERER) ?: ""

        // check for anisotropic texture filtering support
        supportedExtensions.addAll(glGetString(GL_EXTENSIONS)?.split(" ") ?: emptyList())
        if (supportedExtensions.contains("GL_EXT_texture_filter_anisotropic")) {
            glCapabilities.maxAnisotropy = glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT).toInt()
            glCapabilities.glTextureMaxAnisotropyExt = EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT
        }

        glCapabilities.maxTexUnits = glGetInteger(GL_MAX_TEXTURE_IMAGE_UNITS)
        glEnable(GL_VERTEX_PROGRAM_POINT_SIZE)
        glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS)
    }

    override fun getWindowViewport(result: Viewport) {
        result.set(0, 0, windowWidth, windowHeight)
    }

    override fun drawFrame(ctx: Lwjgl3Context) {
        if (ctx.disposablePipelines.isNotEmpty()) {
            queueRenderer.disposePipelines(ctx.disposablePipelines)
            ctx.disposablePipelines.clear()
        }

        for (j in ctx.backgroundPasses.indices) {
            if (ctx.backgroundPasses[j].isEnabled) {
                drawOffscreen(ctx.backgroundPasses[j])
                ctx.backgroundPasses[j].afterDraw(ctx)
            }
        }
        for (i in ctx.scenes.indices) {
            val scene = ctx.scenes[i]
            if (scene.isVisible) {
                doOffscreenPasses(scene, ctx)

                queueRenderer.renderQueue(scene.mainRenderPass.drawQueue)
                scene.mainRenderPass.afterDraw(ctx)
            }
        }

        if (afterRenderActions.isNotEmpty()) {
            afterRenderActions.forEach { it() }
            afterRenderActions.clear()
        }
        // swap the color buffers
        glfwSwapBuffers(glfwWindowHandle)
    }

    private fun doOffscreenPasses(scene: Scene, ctx: KoolContext) {
        for (i in scene.offscreenPasses.indices) {
            val rp = scene.offscreenPasses[i]
            if (rp.isEnabled) {
                openRenderPasses += rp
            }
        }
        doneRenderPasses.clear()
        while (openRenderPasses.isNotEmpty()) {
            var anyDrawn = false
            for (i in openRenderPasses.indices) {
                val pass = openRenderPasses[i]
                var skip = false
                for (j in pass.dependencies.indices) {
                    val dep = pass.dependencies[j]
                    if (dep !in doneRenderPasses) {
                        skip = true
                        break
                    }
                }
                if (!skip) {
                    anyDrawn = true
                    openRenderPasses -= pass
                    doneRenderPasses += pass
                    drawOffscreen(pass)
                    pass.afterDraw(ctx)
                    break
                }
            }
            if (!anyDrawn) {
                logE { "Failed to render all offscreen passes, remaining:" }
                openRenderPasses.forEach { logE { "  ${it.name}" } }
                openRenderPasses.clear()
                break
            }
        }
    }

    private fun drawOffscreen(offscreenPass: OffscreenRenderPass) {
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

    override fun destroy(ctx: Lwjgl3Context) {
        // for now we leave the cleanup to the system...
    }

    override fun loadTex2d(tex: Texture2d, data: TextureData) {
        tex.loadedTexture = TextureLoader.loadTexture2d(ctx, tex.props, data)
        tex.loadingState = Texture.LoadingState.LOADED
    }

    override fun loadTexCube(tex: TextureCube, data: TextureDataCube) {
        tex.loadedTexture = TextureLoader.loadTextureCube(ctx, tex.props, data)
        tex.loadingState = Texture.LoadingState.LOADED
    }

    override fun createOffscreenPass2d(parentPass: OffscreenPass2dImpl): OffscreenPass2dImpl.BackendImpl {
        return OffscreenPass2dGl(parentPass)
    }

    override fun createOffscreenPassCube(parentPass: OffscreenPassCubeImpl): OffscreenPassCubeImpl.BackendImpl {
        return OffscreenPassCubeGl(parentPass)
    }

    class GlCapabilities {
        var maxTexUnits = 16
            internal set
        var hasFloatTextures = false
            internal set
        var maxAnisotropy = 1
            internal set
        var glTextureMaxAnisotropyExt = 0
            internal set
    }
}