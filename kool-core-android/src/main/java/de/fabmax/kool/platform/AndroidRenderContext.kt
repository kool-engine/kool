package de.fabmax.kool.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.opengl.EGL14
import android.opengl.GLES11Ext
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
import android.view.View
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
class AndroidRenderContext(val context: Context, val glView: GLSurfaceView, val onKoolContextCreated: (AndroidRenderContext) -> Unit) :
        KoolContext(), GLSurfaceView.Renderer, GLSurfaceView.EGLContextFactory, View.OnTouchListener {

    override var glCapabilities: GlCapabilities = GlCapabilities.UNKNOWN_CAPABILITIES

    override val assetMgr = AndroidAssetManager(context, ".")

    override var windowWidth = 0
        private set
    override var windowHeight = 0
        private set

    private var isGLES30Context = false

    private var prevRenderTime = System.nanoTime()
    private var isCreated = false

    constructor(context: Context, onKoolContextCreated: (AndroidRenderContext) -> Unit) :
            this(context, GLSurfaceView(context), onKoolContextCreated)

    init {
        // install logging handler
        de.fabmax.kool.util.Log.printer = { lvl, tag, message ->
            var t = tag ?: "Kool"
            if (t.length > 23) {
                t = t.substring(0..22)
            }
            when (lvl) {
                de.fabmax.kool.util.Log.Level.TRACE -> Log.d(t, message)
                de.fabmax.kool.util.Log.Level.DEBUG -> Log.d(t, message)
                de.fabmax.kool.util.Log.Level.INFO -> Log.i(t, message)
                de.fabmax.kool.util.Log.Level.WARN -> Log.w(t, message)
                de.fabmax.kool.util.Log.Level.ERROR -> Log.e(t, message)
                else -> { }
            }
        }

        // get screen resolution
        val dispMetrics = context.resources.displayMetrics
        val scale = 0.66f
        screenDpi = (dispMetrics.xdpi + dispMetrics.ydpi) / 2 * scale

        // setup GLSurfaceView
        glView.setOnTouchListener(this)
        glView.setEGLContextFactory(this)
        glView.setRenderer(this)
        glView.preserveEGLContextOnPause = true
    }

    //
    // Functions overridden from KoolContext
    //

    override fun openUrl(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(browserIntent)
    }

    override fun run() {
        // nothing to do here
    }

    override fun destroy() {
        // for now, we silently ignore this (if app is closing everything is deleted anyway...)
    }

    fun onPause() {
        glView.onPause()
    }

    fun onResume() {
        glView.onResume()
    }

    //
    // Functions overridden from GLSurfaceView.EGLContextFactory
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
    // Functions overridden from GLSurfaceView.Renderer
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

        // don't call onKoolContextCreated() yet, we first wan't to know how big our viewport is
        //onKoolContextCreated(this)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        windowWidth = width
        windowHeight = height

        if (!isCreated) {
            isCreated = true
            onKoolContextCreated(this)
        }
    }

    override fun onDrawFrame(gl: GL10) {
        // determine time delta
        val time = System.nanoTime()
        val dt = (time - prevRenderTime) / 1e9
        prevRenderTime = time

        // render engine content
        render(dt)
    }

    //
    // View.OnTouchListener
    //

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        val action = motionEvent.actionMasked

        for (i in 0 until motionEvent.pointerCount) {
            val pointerId = motionEvent.getPointerId(i)
            val x = motionEvent.getX(i)
            val y = motionEvent.getY(i)

            val ptrAction = when {
                action == MotionEvent.ACTION_POINTER_DOWN && i == motionEvent.actionIndex -> MotionEvent.ACTION_DOWN
                action == MotionEvent.ACTION_POINTER_UP && i == motionEvent.actionIndex -> MotionEvent.ACTION_UP
                else -> action
            }

            when (ptrAction) {
                MotionEvent.ACTION_DOWN -> inputMgr.handleTouchStart(pointerId, x, y)
                MotionEvent.ACTION_UP -> inputMgr.handleTouchEnd(pointerId)
                MotionEvent.ACTION_CANCEL -> inputMgr.handleTouchCancel(pointerId)
                else -> inputMgr.handleTouchMove(pointerId, x, y)
            }
        }
        return true
    }

    companion object {
        const val TAG = "KoolCtx"
    }
}