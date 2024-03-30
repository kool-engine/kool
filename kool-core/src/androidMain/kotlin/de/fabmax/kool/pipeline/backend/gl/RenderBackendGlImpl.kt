package de.fabmax.kool.pipeline.backend.gl

import android.opengl.GLSurfaceView
import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configAndroid
import de.fabmax.kool.platform.KoolContextAndroid
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class RenderBackendGlImpl(ctx: KoolContextAndroid) :
    RenderBackendGl(1, GlImpl, ctx),
    GLSurfaceView.Renderer
{
    private val androidCtx = ctx

    var viewWidth: Int = 0
        private set
    var viewHeight: Int = 0
        private set

    private var lateGlslGeneratorHints: GlslGenerator.Hints
    override val glslGeneratorHints: GlslGenerator.Hints
        get() = lateGlslGeneratorHints

    private var timer: TimeQuery? = null
    override var frameGpuTime: Double = 0.0

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
                frameGpuTime = t.getQueryResultMillis()
            }
            t.timedScope { super.renderFrame(ctx) }
        } else {
            super.renderFrame(ctx)
        }
    }
}
