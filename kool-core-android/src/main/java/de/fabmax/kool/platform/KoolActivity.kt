package de.fabmax.kool.platform

import android.app.Activity
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View


/**
 * Base Activity for using Kool on android.
 */
abstract class KoolActivity : Activity(), View.OnTouchListener {

    private var glSurfaceView: GLSurfaceView? = null

    private var ctx: AndroidRenderContext? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        onCreateContext()
    }

    protected fun createDefaultKoolContext(): AndroidRenderContext {
        val glView = GLSurfaceView(this)
        val ctx = createKoolContext(glView)

        setContentView(glView)
        return ctx
    }

    protected fun createKoolContext(glSurfaceView: GLSurfaceView): AndroidRenderContext {
        this.glSurfaceView = glSurfaceView

        glSurfaceView.setOnTouchListener(this)

        val ctx = AndroidRenderContext(this)
        glSurfaceView.setEGLContextFactory(ctx)
        glSurfaceView.setRenderer(ctx)
        glSurfaceView.preserveEGLContextOnPause = true

        return ctx
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        val inputMgr = ctx?.inputMgr ?: return false
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

    open fun onCreateContext() {
        // creates the default Kool render context
        ctx = createDefaultKoolContext()
    }

    open fun onKoolContextCreated(ctx: AndroidRenderContext) {
        // default impl does nothing
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
