package de.fabmax.kool

import android.opengl.EGL14
import android.opengl.GLSurfaceView
import android.util.Log
import de.fabmax.kool.gl.GL_DEPTH_COMPONENT
import de.fabmax.kool.gl.GL_DEPTH_COMPONENT24
import de.fabmax.kool.gl.GL_NEAREST
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay
import javax.microedition.khronos.opengles.GL10

/**
 * Created by max on 03.03.2018.
 */
class AndroidRenderContext(val koolActivity: KoolActivity) : RenderContext(), GLSurfaceView.Renderer, GLSurfaceView.EGLContextFactory {
    override var windowWidth = 0
        private set
    override var windowHeight = 0
        private set

    private var isGLES30Context = false

    private var prevRenderTime = System.nanoTime()

    init {
        val dispMetrics = koolActivity.resources.displayMetrics
        val scale = 0.66f
        screenDpi = (dispMetrics.xdpi + dispMetrics.ydpi) / 2 * scale
    }

    override fun run() {
        // nothing to do here
    }

    override fun destroy() {
        // for now, we silently ignore this (if app is closing everything is deleted anyway...)
    }

    override fun destroyContext(egl: EGL10, display: EGLDisplay, context: EGLContext) {
        egl.eglDestroyContext(display, context)
    }

    override fun createContext(egl: EGL10, display: EGLDisplay, config: EGLConfig): EGLContext {
        val attribList = intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 3, EGL10.EGL_NONE)
        var context = egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, attribList)

        if (egl.eglGetError() == EGL10.EGL_SUCCESS) {
            Log.d("KoolActivity", "Successfully created OpenGL ES 3.0 context")
            isGLES30Context = true

        } else {
            if (context != null) {
                destroyContext(egl, display, context)
            }

            Log.d("KoolActivity", "Falling back to OpenGL ES 2.0 context")
            isGLES30Context = false
            attribList[1] = 2
            context = egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, attribList)
            if (egl.eglGetError() != EGL10.EGL_SUCCESS) {
                destroyContext(egl, display, context)
                throw KoolException("Failed to create OpenGL ES context")
            }
        }
        return context
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        if (isGLES30Context) {
            glCapabilities = GlCapabilities(
                    uint32Indices = true,
                    shaderIntAttribs = true,
                    depthTextures = true,
                    depthComponentIntFormat = GL_DEPTH_COMPONENT24,
                    depthFilterMethod = GL_NEAREST,
                    glslDialect = GlslDialect.GLSL_DIALECT_300_ES,
                    glVersion = GlVersion("OpenGL ES", 3, 0),
                    anisotropicTexFilterInfo = AnisotropicTexFilterInfo.NOT_SUPPORTED)

        } else {
            glCapabilities = GlCapabilities(
                    uint32Indices = true,
                    shaderIntAttribs = false,
                    depthTextures = false,
                    depthComponentIntFormat = GL_DEPTH_COMPONENT,
                    depthFilterMethod = GL_NEAREST,
                    glslDialect = GlslDialect.GLSL_DIALECT_100,
                    glVersion = GlVersion("OpenGL ES", 2, 0),
                    anisotropicTexFilterInfo = AnisotropicTexFilterInfo.NOT_SUPPORTED)
        }

        koolActivity.onKoolContextCreated(this)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        windowWidth = width
        windowHeight = height
    }

    override fun onDrawFrame(gl: GL10) {
        // determine time delta
        val time = System.nanoTime()
        val dt = (time - prevRenderTime) / 1e9
        prevRenderTime = time

        // render engine content
        render(dt)
    }
}