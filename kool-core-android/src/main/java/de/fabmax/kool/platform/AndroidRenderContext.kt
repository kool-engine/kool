package de.fabmax.kool.platform

import android.content.Intent
import android.net.Uri
import android.opengl.EGL14
import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.Log
import de.fabmax.kool.*
import de.fabmax.kool.gl.*
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay
import javax.microedition.khronos.opengles.GL10


/**
 * Android specific implementation of Kool's RenderContext
 */
class AndroidRenderContext(private val koolActivity: KoolActivity) :
        KoolContext(), GLSurfaceView.Renderer, GLSurfaceView.EGLContextFactory {

    override var glCapabilities: GlCapabilities = GlCapabilities.UNKNOWN_CAPABILITIES

    override val assetMgr = AndroidAssetManager(koolActivity)

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

    //
    // Functions overriden from KoolContext
    //

    override fun openUrl(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        koolActivity.startActivity(browserIntent)
    }

    override fun run() {
        // nothing to do here
    }

    override fun destroy() {
        // for now, we silently ignore this (if app is closing everything is deleted anyway...)
    }

    //
    // Functions overriden from GLSurfaceView.EGLContextFactory
    //

    override fun destroyContext(egl: EGL10, display: EGLDisplay, context: EGLContext) {
        egl.eglDestroyContext(display, context)
    }

    override fun createContext(egl: EGL10, display: EGLDisplay, config: EGLConfig): EGLContext {
        val attribList = intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 3, EGL10.EGL_NONE)
        var context = egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, attribList)

        if (egl.eglGetError() == EGL10.EGL_SUCCESS) {
            Log.d(TAG, "Successfully created OpenGL ES 3.0 context")
            isGLES30Context = true

        } else {
            if (context != null) {
                destroyContext(egl, display, context)
            }

            Log.d(TAG, "Falling back to OpenGL ES 2.0 context")
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

    //
    // Functions overriden from GLSurfaceView.Renderer
    //

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        // check available extensions
        val extensions = GLES30.glGetString(GL_EXTENSIONS).split(" ")
        //extensions.forEach { Log.i(TAG, it) }

        // 32-bit vertex indices
        val uint32Indices = extensions.contains("GL_OES_element_index_uint")

        // anisotropic texture filtering
        var anisotropicTexFilterInfo = AnisotropicTexFilterInfo.NOT_SUPPORTED
        if (extensions.contains("GL_EXT_texture_filter_anisotropic")) {
            val resultBuf = FloatArray(1)
            GLES30.glGetFloatv(GLES11Ext.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, resultBuf, 0)
            Log.i(TAG, "Anisotropic filtering available: max level = ${resultBuf[0]}")
            anisotropicTexFilterInfo = AnisotropicTexFilterInfo(resultBuf[0], GLES11Ext.GL_TEXTURE_MAX_ANISOTROPY_EXT)
        }

        // get number of available texture unis
        val resultBuf = IntArray(1)
        GLES30.glGetIntegerv(GL_MAX_TEXTURE_IMAGE_UNITS, resultBuf, 0)
        val maxTexUnits = resultBuf[0]

        if (isGLES30Context) {
            glCapabilities = GlCapabilities(
                    uint32Indices = uint32Indices,
                    shaderIntAttribs = true,
                    maxTexUnits = maxTexUnits,
                    depthTextures = true,
                    depthComponentIntFormat = GL_DEPTH_COMPONENT24,
                    depthFilterMethod = GL_NEAREST,
                    glslDialect = GlslDialect.GLSL_DIALECT_300_ES,
                    glVersion = GlVersion("OpenGL ES", 3, 0),
                    anisotropicTexFilterInfo = anisotropicTexFilterInfo)

        } else {
            glCapabilities = GlCapabilities(
                    uint32Indices = uint32Indices,
                    shaderIntAttribs = false,
                    maxTexUnits = maxTexUnits,
                    depthTextures = extensions.contains("GL_OES_depth_texture"),
                    depthComponentIntFormat = GL_DEPTH_COMPONENT,
                    depthFilterMethod = GL_NEAREST,
                    glslDialect = GlslDialect.GLSL_DIALECT_100,
                    glVersion = GlVersion("OpenGL ES", 2, 0),
                    anisotropicTexFilterInfo = anisotropicTexFilterInfo)
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

    companion object {
        const val TAG = "KoolCtx"
    }
}