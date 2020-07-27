package de.fabmax.kool.platform.gl

import de.fabmax.kool.KoolException
import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.RenderBackend
import de.fabmax.kool.util.Viewport
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.EXTTextureFilterAnisotropic
import org.lwjgl.opengl.GL.createCapabilities
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL11C.glGetString
import org.lwjgl.opengl.GL20.GL_MAX_TEXTURE_IMAGE_UNITS
import org.lwjgl.opengl.GL20.GL_VERTEX_PROGRAM_POINT_SIZE
import org.lwjgl.opengl.GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS
import org.lwjgl.system.MemoryUtil

class GlRenderBackend(props: Lwjgl3Context.InitProps, val ctx: Lwjgl3Context) : RenderBackend {
    override val apiName: String
    override val deviceName: String

    override var windowWidth = 0
        private set
    override var windowHeight = 0
        private set
    override val glfwWindowHandle: Long

    override val shaderGenerator = ShaderGeneratorImplGL()

    override val projCorrectionMatrixScreen = Mat4d()
    override val projCorrectionMatrixOffscreen = Mat4d()
    override val depthBiasMatrix = Mat4d().translate(0.5, 0.5, 0.5).scale(0.5, 0.5, 0.5)

    val supportedExtensions = mutableSetOf<String>()
    val glCapabilities = GlCapabilities()

    internal val queueRenderer = QueueRendererGl(this, ctx)
    internal val afterRenderActions = mutableListOf<() -> Unit>()

    init {
        // configure GLFW
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
        glfwWindowHint(GLFW_SAMPLES, props.msaaSamples)

        // create window
        glfwWindowHandle = glfwCreateWindow(props.width, props.height, props.title, props.monitor, props.share)
        if (glfwWindowHandle == MemoryUtil.NULL) {
            throw KoolException("Failed to create the GLFW window")
        }

        glfwSetFramebufferSizeCallback(glfwWindowHandle) { _, width, height ->
            windowWidth = width
            windowHeight = height
        }

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
            for (j in scene.offscreenPasses.indices) {
                if (scene.offscreenPasses[j].isEnabled) {
                    drawOffscreen(scene.offscreenPasses[j])
                    scene.offscreenPasses[j].afterDraw(ctx)
                }
            }
            queueRenderer.renderQueue(scene.mainRenderPass.drawQueue)
            scene.mainRenderPass.afterDraw(ctx)
        }

        if (afterRenderActions.isNotEmpty()) {
            afterRenderActions.forEach { it() }
            afterRenderActions.clear()
        }
        // swap the color buffers
        glfwSwapBuffers(glfwWindowHandle)
    }

    private fun drawOffscreen(offscreenPass: OffscreenRenderPass) {
        when (offscreenPass) {
            is OffscreenRenderPass2d -> offscreenPass.impl.draw(ctx)
            is OffscreenRenderPassCube -> offscreenPass.impl.draw(ctx)
            else -> throw IllegalArgumentException("Offscreen pass type not implemented: $offscreenPass")
        }
    }

    override fun destroy(ctx: Lwjgl3Context) {
        // for now we leave the cleanup to the system...
    }

    override fun loadTex2d(tex: Texture, data: TextureData) {
        tex.loadedTexture = TextureLoader.loadTexture(ctx, tex.props, data)
        tex.loadingState = Texture.LoadingState.LOADED
    }

    override fun loadTexCube(tex: CubeMapTexture, data: CubeMapTextureData) {
        tex.loadedTexture = TextureLoader.loadTexture(ctx, tex.props, data)
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