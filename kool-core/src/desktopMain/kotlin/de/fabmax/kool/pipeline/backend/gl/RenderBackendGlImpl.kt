package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJvm
import de.fabmax.kool.pipeline.backend.BackendFeatures
import de.fabmax.kool.pipeline.backend.RenderBackendJvm
import de.fabmax.kool.platform.GlfwWindow
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.util.Color
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.glfwSwapBuffers
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.glEnable
import org.lwjgl.opengl.GL20.GL_VERTEX_PROGRAM_POINT_SIZE
import org.lwjgl.opengl.GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

actual fun createRenderBackendGl(ctx: KoolContext): RenderBackendGl = RenderBackendGlImpl(ctx)

class RenderBackendGlImpl(ctx: KoolContext) :
    RenderBackendGl(KoolSystem.configJvm.numSamples, GlImpl, ctx), RenderBackendJvm
{
    override val name = "OpenGL"
    override val features: BackendFeatures

    override val glfwWindow: GlfwWindow
    override val glslGeneratorHints: GlslGenerator.Hints

    private val timer: TimeQuery
    override var frameGpuTime: Duration = 0.0.seconds

    init {
        glfwWindow = createWindow()

        // This line is critical for LWJGL's interoperation with GLFW's OpenGL context, or any context that is managed
        // externally. LWJGL detects the context that is current in the current thread, creates the GLCapabilities
        // instance and makes the OpenGL bindings available for use.
        GL.createCapabilities()

        GlImpl.initOpenGl(this)
        glslGeneratorHints = GlslGenerator.Hints(
            glslVersionStr = "#version ${GlImpl.version.major}${GlImpl.version.minor}0 core"
        )

        sceneRenderer.resolveDirect = true
        glEnable(GL_VERTEX_PROGRAM_POINT_SIZE)
        glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS)
        setupGl()

        features = BackendFeatures(
            computeShaders = true,
            cubeMapArrays = true,
            reversedDepth = GlImpl.capabilities.hasClipControl,
            maxSamples = 4,
            readWriteStorageTextures = true,
            depthOnlyShaderColorOutput = Color.BLACK,
            maxComputeWorkGroupsPerDimension = GlImpl.capabilities.maxWorkGroupCount,
            maxComputeWorkGroupSize = GlImpl.capabilities.maxWorkGroupSize,
            maxComputeInvocationsPerWorkgroup = GlImpl.capabilities.maxWorkGroupInvocations
        )

        timer = TimeQuery(gl)
    }

    override fun renderFrame(ctx: KoolContext) {
        if (timer.isAvailable) {
            frameGpuTime = timer.getQueryResult()
        }

        timer.timedScope {
            super.renderFrame(ctx)
        }
        glfwSwapBuffers(glfwWindow.windowPtr)
    }

    private fun createWindow(): GlfwWindow {
        // do basic GLFW configuration before we create the window
        GLFW.glfwDefaultWindowHints()
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, KoolSystem.configJvm.numSamples)

        // create window
        val glfwWindow = GlfwWindow(ctx as Lwjgl3Context)
        glfwWindow.isFullscreen = KoolSystem.configJvm.isFullscreen

        // make the OpenGL context current
        GLFW.glfwMakeContextCurrent(glfwWindow.windowPtr)

        // enable V-sync if configured
        if (KoolSystem.configJvm.isVsync) {
            GLFW.glfwSwapInterval(1)
        } else {
            GLFW.glfwSwapInterval(0)
        }

        // make the window visible
        if (KoolSystem.configJvm.showWindowOnStart) {
            glfwWindow.isVisible = true
        }
        return glfwWindow
    }

    override fun cleanup(ctx: KoolContext) {
        // for now, we leave the cleanup to the system...
    }
}
