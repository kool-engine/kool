package de.fabmax.kool

import android.app.Activity
import android.opengl.GLSurfaceView
import de.fabmax.kool.gl.AndroidGlBindings

/**
 * Base Activity for using Kool on android.
 */
abstract class KoolActivity : Activity() {

    private var glSurfaceView: GLSurfaceView? = null

    fun createContext(): RenderContext {
        val glSurfaceView = GLSurfaceView(this)
        val ctx = createContext(glSurfaceView)

        setContentView(glSurfaceView)
        return ctx
    }

    fun createContext(glSurfaceView: GLSurfaceView): RenderContext {
        this.glSurfaceView = glSurfaceView

        // fixme: testing: next line enables OpenGL ES 2.0, no 3.0 support yet...
        glSurfaceView.setEGLContextClientVersion(2)

        val ctx = createContext(WrapperInitProps(AndroidPlatformImpl(), AndroidGlBindings())) as AndroidRenderContext
        glSurfaceView.setRenderer(ctx)
        glSurfaceView.preserveEGLContextOnPause = true

        return ctx
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView?.onResume()
    }
}
