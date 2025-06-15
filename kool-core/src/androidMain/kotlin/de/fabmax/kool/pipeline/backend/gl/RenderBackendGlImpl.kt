package de.fabmax.kool.pipeline.backend.gl

import android.opengl.GLSurfaceView
import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configAndroid
import de.fabmax.kool.pipeline.backend.BackendFeatures
import de.fabmax.kool.platform.KoolContextAndroid
import de.fabmax.kool.util.Color
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

actual fun createRenderBackendGl(ctx: KoolContext): RenderBackendGl = RenderBackendGlImpl(ctx as KoolContextAndroid)

class RenderBackendGlImpl(ctx: KoolContextAndroid) :
    RenderBackendGl(1, GlImpl, ctx),
    GLSurfaceView.Renderer
{
    override val name = "OpenGL ES"
    private val androidCtx = ctx

    private var _features: BackendFeatures? = null
    override val features: BackendFeatures get() = checkNotNull(_features) {
        "features are only available after the GL context is initialized"
    }

    var viewWidth: Int = 0
        private set
    var viewHeight: Int = 0
        private set

    private var lateGlslGeneratorHints: GlslGenerator.Hints
    override val glslGeneratorHints: GlslGenerator.Hints
        get() = lateGlslGeneratorHints

    private var timer: TimeQuery? = null
    override var frameGpuTime: Duration = 0.0.seconds

    private var isGlContextInitialized = false

    init {
        lateGlslGeneratorHints = GlslGenerator.Hints(glslVersionStr = "#version 300 es")
        sceneRenderer.resolveDirect = false
        useFloatDepthBuffer = KoolSystem.configAndroid.forceFloatDepthBuffer
    }

    override fun cleanup(ctx: KoolContext) {
        // for now, we leave the cleanup to the system...
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig?) {
        if (!isGlContextInitialized) {
            initGlContext()
            isGlContextInitialized = true
        }
    }

    private fun initGlContext() {
        GlImpl.initOpenGl(this)
        lateGlslGeneratorHints = GlslGenerator.Hints(glslVersionStr = "#version ${GlImpl.version.major}${GlImpl.version.minor}0 es")
        setupGl()

        _features = BackendFeatures(
            computeShaders = false,
            cubeMapArrays = false,
            reversedDepth = GlImpl.capabilities.hasClipControl,
            maxSamples = 4,
            readWriteStorageTextures = true,
            depthOnlyShaderColorOutput = Color.BLACK,
            maxComputeWorkGroupsPerDimension = GlImpl.capabilities.maxWorkGroupCount,
            maxComputeWorkGroupSize = GlImpl.capabilities.maxWorkGroupSize,
            maxComputeInvocationsPerWorkgroup = GlImpl.capabilities.maxWorkGroupInvocations
        )

        if (GlImpl.capabilities.hasTimestampQuery) {
            timer = TimeQuery(GlImpl)
        }
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        viewWidth = width
        viewHeight = height
    }

    override fun onDrawFrame(unused: GL10) {
        androidCtx.renderFrame()
    }

    override fun renderFrame(ctx: KoolContext) {
        val t = timer
        if (t != null) {
            if (t.isAvailable) {
                frameGpuTime = t.getQueryResult()
            }
            t.timedScope { super.renderFrame(ctx) }
        } else {
            super.renderFrame(ctx)
        }
    }
}
