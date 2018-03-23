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
abstract class KoolActivity : Activity() {

    private var ctx: AndroidRenderContext? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ctx = AndroidRenderContext(this, this::onKoolContextCreated)
        setContentView(ctx!!.glView)
    }

    open fun onKoolContextCreated(ctx: AndroidRenderContext) {
        // default impl does nothing
    }

    override fun onPause() {
        super.onPause()
        ctx?.onPause()
    }

    override fun onResume() {
        super.onResume()
        ctx?.onResume()
    }
}
