package de.fabmax.kool

import de.fabmax.kool.drawqueue.DrawQueue
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.pipeline.shadermodel.ShaderGenerator
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Color

/**
 * @author fabmax
 */

expect fun createDefaultContext(): KoolContext

abstract class KoolContext {

    var screenDpi = 96f

    abstract val glCapabilities: GlCapabilities

    abstract val assetMgr: AssetManager

    // fixme: move to / join with ShaderManager?
    abstract val shaderGenerator: ShaderGenerator

    val inputMgr = InputManager()
    val memoryMgr = MemoryManager()
    //val shaderMgr = ShaderManager()
    //val textureMgr = TextureManager()

    var viewport = Viewport(0, 0, 0, 0)
    var clearColor = Color(0.05f, 0.15f, 0.25f, 1f)

    val renderingHints = RenderingHints()
    var renderPass = RenderPass.SCREEN
    val mvpState = MvpState()
    val projCorrectionMatrix = Mat4f()

    val onRender: MutableList<(KoolContext) -> Unit> = mutableListOf()

    val drawQueue = DrawQueue()

    /**
     * Run time of this render context in seconds. This is the wall clock time between now and the first time render()
     * was called.
     */
    var time = 0.0
        protected set

    /**
     * Time between current and last call of render() in seconds.
     */
    var deltaT = 0.0f
        private set

    /**
     * Number of rendered frames.
     */
    var frameIdx = 0
        private set

    /**
     * Frames per second (averaged over last 25 frames)
     */
    var fps = 60.0
        private set

    val scenes: MutableList<Scene> = mutableListOf()
    val offscreenPasses = mutableListOf<OffscreenPass>()

    private val delayedCallbacks = mutableListOf<DelayedCallback>()

    private val frameTimes = DoubleArray(25) { 0.017 }

    abstract val windowWidth: Int
    abstract val windowHeight: Int

    abstract fun openUrl(url: String)

    abstract fun run()

    abstract fun destroy()

    abstract fun getSysInfos(): List<String>

    fun delay(frames: Int, callback: (KoolContext) -> Unit) {
        delayedCallbacks += DelayedCallback(frameIdx + frames, callback)
    }

    protected fun render(dt: Double) {
        if (delayedCallbacks.isNotEmpty()) {
            val it = delayedCallbacks.iterator()
            for (callback in it) {
                if (callback.callOnFrame <= frameIdx) {
                    callback.callback(this)
                    it.remove()
                }
            }
        }

        this.deltaT = dt.toFloat()
        time += dt
        frameIdx++

        frameTimes[frameIdx % frameTimes.size] = dt
        var sum = 0.0
        for (i in frameTimes.indices) { sum += frameTimes[i] }
        fps = (frameTimes.size / sum) * 0.1 + fps * 0.9

        inputMgr.onNewFrame(this)
//        textureMgr.onNewFrame(this)
//        shaderMgr.onNewFrame(this)

        // by default the viewport covers the full window
//        if (windowWidth != viewport.width || windowHeight != viewport.height) {
//            viewport = Viewport(0, 0, windowWidth, windowHeight)
//            applyAttributes()
//        }

        for (i in offscreenPasses.indices.reversed()) {
            if (offscreenPasses[i].isSingleShot && offscreenPasses[i].frameIdx > 0) {
                offscreenPasses.removeAt(i)
            } else {
                offscreenPasses[i].render(this)
            }
        }

        drawQueue.clear()
        for (i in onRender.indices) {
            onRender[i](this)
        }

        // process input for scenes in reverse order (front to back)
        for (i in scenes.indices.reversed()) {
            if (scenes[i].isVisible) {
                scenes[i].processInput(this)
            }
        }

        // draw scene contents (back to front)
        for (i in scenes.indices) {
            if (scenes[i].isVisible) {
                scenes[i].drawQueue = drawQueue
                scenes[i].renderScene(this)
            }
        }
    }

    fun applyRenderingHints() {
        // apply scene specific hints (shadow map type, etc.)
        scenes.forEach { it.onRenderingHintsChanged(this) }
        // regenerate shaders
        //shaderMgr.onRenderingHintsChanged(this)
    }

    data class Viewport(val x: Int, val y: Int, val width: Int, val height: Int) {
        val aspectRatio get() = width.toFloat() / height.toFloat()

        fun isInViewport(x: Float, y: Float) = x >= this.x && x < this.x + width &&
                y >= this.y && y < this.y + height
    }

    private class DelayedCallback(val callOnFrame: Int, val callback: (KoolContext) -> Unit)
}

enum class RenderPass {
    SHADOW,
    SCREEN
}
