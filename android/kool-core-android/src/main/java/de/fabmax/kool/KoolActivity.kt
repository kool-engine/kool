package de.fabmax.kool

import android.app.Activity
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import de.fabmax.kool.gl.AndroidGlBindings
import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.FontProps
import kotlinx.coroutines.experimental.launch
import java.io.ByteArrayOutputStream


/**
 * Base Activity for using Kool on android.
 */
abstract class KoolActivity : Activity() {

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


        val ctx = createContext(WrapperInitProps(AndroidPlatformImpl(), AndroidGlBindings())) as AndroidRenderContext
        glSurfaceView.setEGLContextFactory(ctx)
        glSurfaceView.setRenderer(ctx)
        glSurfaceView.preserveEGLContextOnPause = true

        return ctx
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
            // todo
            return ""
        }
    }
}
