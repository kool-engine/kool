package de.fabmax.kool.platform

import android.app.Activity
import android.app.ActivityManager
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import de.fabmax.kool.PlatformImpl
import de.fabmax.kool.RenderContext
import de.fabmax.kool.TextureData
import de.fabmax.kool.WrapperInitProps
import de.fabmax.kool.gl.AndroidGlBindings
import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.FontProps
import kotlinx.coroutines.experimental.launch
import java.io.ByteArrayOutputStream


/**
 * Base Activity for using Kool on android.
 */
abstract class KoolActivity : Activity(), View.OnTouchListener {

    private var glSurfaceView: GLSurfaceView? = null
    private val fontMapGenerator = FontMapGenerator(this, 1024, 1024)

    private var ctx: AndroidRenderContext? = null

    fun createContext(): AndroidRenderContext {
        val glView = GLSurfaceView(this)
        val ctx = createContext(glView)

        setContentView(glView)
        return ctx
    }

    fun createContext(glSurfaceView: GLSurfaceView): AndroidRenderContext {
        this.glSurfaceView = glSurfaceView

        glSurfaceView.setOnTouchListener(this)

        val ctx = de.fabmax.kool.createContext(WrapperInitProps(AndroidPlatformImpl(), AndroidGlBindings())) as AndroidRenderContext
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

//            if (ptrAction == MotionEvent.ACTION_DOWN) {
//                Log.d("KoolActivity", "pointer down: $pointerId, pos = ($x, $y)")
//            }
//            if (ptrAction == MotionEvent.ACTION_UP) {
//                Log.d("KoolActivity", "pointer up: $pointerId, pos = ($x, $y)")
//            }
        }
        return true
    }

    open fun onCreateContext() {
        // creates the Kool render context
        ctx = createContext()
    }

    open fun onKoolContextCreated(ctx: AndroidRenderContext) {
        // default impl does nothing
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onCreateContext()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView?.onPause()
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView?.onResume()
    }

    private inner class AndroidPlatformImpl : PlatformImpl {
        private val memInfo = ActivityManager.MemoryInfo()

        override fun createContext(props: RenderContext.InitProps): RenderContext {
            return AndroidRenderContext(this@KoolActivity)
        }

        override fun createCharMap(fontProps: FontProps): CharMap {
            return fontMapGenerator.createCharMap(fontProps)
        }

        override fun loadAsset(assetPath: String, onLoad: (ByteArray) -> Unit) {
            launch {
                assets.open(assetPath)?.use {
                    val t = System.nanoTime()
                    val data = ByteArrayOutputStream()
                    val buf = ByteArray(128 * 1024)
                    while (it.available() > 0) {
                        val len = it.read(buf)
                        data.write(buf, 0, len)
                    }
                    val bytes = data.toByteArray()
                    Log.d("KoolActivity", "Loaded asset \"$assetPath\" in ${(System.nanoTime() - t) / 1e6} ms (${bytes.size / (1024.0*1024.0)} MB)")

                    onLoad(bytes)
                }
            }
        }

        override fun loadTextureAsset(assetPath: String): TextureData {
            return ImageTextureData(assetPath, this@KoolActivity)
        }

        override fun openUrl(url: String) {
            // todo
        }

        override fun getMemoryInfo(): String {
            val rt = Runtime.getRuntime()
            val freeMem = rt.freeMemory()
            val totalMem = rt.totalMemory()
            return "Heap: ${(totalMem - freeMem) / 1024 / 1024} / ${totalMem / 1024 / 1024} MB"
        }
    }
}
