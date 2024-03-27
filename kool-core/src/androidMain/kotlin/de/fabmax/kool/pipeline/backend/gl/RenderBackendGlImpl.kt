package de.fabmax.kool.pipeline.backend.gl

import android.opengl.GLSurfaceView
import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configAndroid
import de.fabmax.kool.platform.AndroidContext
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class RenderBackendGlImpl(ctx: AndroidContext) :
    RenderBackendGl(KoolSystem.configAndroid.msaaSamples, GlImpl, ctx),
    GLSurfaceView.Renderer
{
    private val androidCtx = ctx

    var viewWidth: Int = 0
        private set
    var viewHeight: Int = 0
        private set

    private lateinit var lateGlslGeneratorHints: GlslGenerator.Hints
    override val glslGeneratorHints: GlslGenerator.Hints
        get() = lateGlslGeneratorHints

    override var frameGpuTime: Double = 0.0

    override fun cleanup(ctx: KoolContext) {
        // for now, we leave the cleanup to the system...
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig?) {
        GlImpl.initOpenGl(this)
        lateGlslGeneratorHints = GlslGenerator.Hints(
            glslVersionStr = "#version ${GlImpl.version.major}${GlImpl.version.minor}0 es"
            //glslVersionStr = "#version 300 es"
        )

        sceneRenderer.resolveDirect = false
//        glEnable(GL_VERTEX_PROGRAM_POINT_SIZE)
//        glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS)
        setupGl()
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        viewWidth = width
        viewHeight = height
    }

    override fun onDrawFrame(unused: GL10) {
        androidCtx.renderFrame()
    }
}
