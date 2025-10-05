package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.FrameData
import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJvm
import de.fabmax.kool.pipeline.backend.BackendFeatures
import de.fabmax.kool.pipeline.backend.RenderBackendJvm
import de.fabmax.kool.platform.GlWindowCallbacks
import de.fabmax.kool.platform.KoolWindowJvm
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.createGlWindow
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.logD
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.glEnable
import org.lwjgl.opengl.GL20.GL_VERTEX_PROGRAM_POINT_SIZE
import org.lwjgl.opengl.GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

actual fun createRenderBackendGl(ctx: KoolContext): RenderBackendGl = RenderBackendGlImpl(ctx as Lwjgl3Context)

class RenderBackendGlImpl(ctx: Lwjgl3Context) :
    RenderBackendGl(KoolSystem.configJvm.numSamples, GlImpl, ctx), RenderBackendJvm
{
    override val name = "OpenGL"
    override var features: BackendFeatures = BackendFeatures.NONE
        private set

    override val window: KoolWindowJvm
    override var glslGeneratorHints: GlslGenerator.Hints = GlslGenerator.Hints(glslVersionStr = "#version 330 core")
        private set

    private var timer: TimeQuery? = null
    override var frameGpuTime: Duration = 0.0.seconds

    override val isAsyncRendering: Boolean = KoolSystem.configJvm.asyncSceneUpdate

    private val initSignal = CompletableDeferred<Unit>()

    private val glCallbacks = object : GlWindowCallbacks {
        override fun initGl() {
            // This line is critical for LWJGL's interoperation with GLFW's OpenGL context, or any context that is managed
            // externally. LWJGL detects the context that is current in the current thread, creates the GLCapabilities
            // instance and makes the OpenGL bindings available for use.
            GL.createCapabilities()

            GlImpl.initOpenGl(this@RenderBackendGlImpl)
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
            initSignal.complete(Unit)
        }

        override fun drawFrame() {
            runBlocking { ctx.renderFrame()}
        }
    }

    init {
        logD { "Create GL window" }
        window = createWindow()
        runBlocking { initSignal.await() }
    }

    override fun renderFrame(frameData: FrameData, ctx: KoolContext) {
        val t = timer
        if (t != null) {
            if (t.isAvailable) {
                frameGpuTime = t.getQueryResult()
            }
            t.timedScope {
                super.renderFrame(frameData, ctx)
            }
        } else {
            super.renderFrame(frameData, ctx)
        }
        window.swapBuffers()
    }

    private fun createWindow(): KoolWindowJvm {
        val window = KoolSystem.configJvm.windowSubsystem.createGlWindow(glCallbacks, ctx as Lwjgl3Context)
        window.setFullscreen(KoolSystem.configJvm.isFullscreen)
        window.setVisible(KoolSystem.configJvm.showWindowOnStart)
        return window
    }

    override fun cleanup(ctx: KoolContext) {
        // for now, we leave the cleanup to the system...
    }
}
