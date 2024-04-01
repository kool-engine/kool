package de.fabmax.kool.platform

import android.content.Context
import android.opengl.EGLExt.EGL_OPENGL_ES3_BIT_KHR
import android.opengl.GLSurfaceView
import android.opengl.GLSurfaceView.EGLConfigChooser
import de.fabmax.kool.input.PlatformInputAndroid
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logE
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGL10.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLDisplay

class KoolSurfaceView(context: Context) : GLSurfaceView(context) {
    init {
        setEGLContextClientVersion(3)
        preserveEGLContextOnPause = true
        //if (numSamples > 1) {
        //    setEGLConfigChooser(KoolConfigChooser(numSamples))
        //}

        isFocusable = true
        isFocusableInTouchMode = true
        setOnTouchListener(PlatformInputAndroid)
        setOnKeyListener(PlatformInputAndroid)
    }
}

class KoolConfigChooser(val numSamples: Int) : EGLConfigChooser {
    override fun chooseConfig(egl: EGL10, display: EGLDisplay?): EGLConfig? {
        // fixme: looks reasonable, but fails to find a suitable config...
        val attribs = intArrayOf(
            EGL_RED_SIZE,         8,
            EGL_GREEN_SIZE,       8,
            EGL_BLUE_SIZE,        8,
            EGL_ALPHA_SIZE,       0,
            EGL_DEPTH_SIZE,       16,
            EGL_RENDERABLE_TYPE,  EGL_OPENGL_ES3_BIT_KHR,
            EGL_SAMPLE_BUFFERS,   1,
            EGL_SAMPLES,          numSamples,
            EGL_NONE
        )
        val configs: Array<EGLConfig?> = arrayOfNulls(1)
        val configCounts = IntArray(1)
        egl.eglChooseConfig(display, attribs, configs, 1, configCounts)

        return if (configCounts[0] == 0) {
            logE { "Failed to choose a suitable EGL config!" }
            null
        } else {
            logD { "Found EGL config (num samples: $numSamples)" }
            configs[0]
        }
    }
}