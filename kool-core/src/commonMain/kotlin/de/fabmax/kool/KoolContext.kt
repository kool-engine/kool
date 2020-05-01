package de.fabmax.kool

import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.shadermodel.ShaderGenerator
import de.fabmax.kool.scene.Scene

/**
 * @author fabmax
 */

expect fun createDefaultContext(): KoolContext

abstract class KoolContext {

    var screenDpi = 96f

    abstract val assetMgr: AssetManager

    abstract val shaderGenerator: ShaderGenerator

    val inputMgr = InputManager()
    val engineStats = EngineStats()

    open var viewport = Viewport(0, 0, 0, 0)
        protected set

    val projCorrectionMatrixScreen = Mat4d()
    val projCorrectionMatrixOffscreen = Mat4d()
    val depthBiasMatrix = Mat4d().translate(0.5, 0.5, 0.5).scale(0.5, 0.5, 0.5)

    val onRender: MutableList<(KoolContext) -> Unit> = mutableListOf()

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

    private val delayedCallbacks = mutableListOf<DelayedCallback>()
    internal val disposablePipelines = mutableListOf<Pipeline>()

    private val frameTimes = DoubleArray(25) { 0.017 }

    abstract val windowWidth: Int
    abstract val windowHeight: Int

    abstract fun openUrl(url: String)

    abstract fun run()

    abstract fun destroy()

    abstract fun getSysInfos(): List<String>

    fun runDelayed(frames: Int, callback: (KoolContext) -> Unit) {
        delayedCallbacks += DelayedCallback(frameIdx + frames, callback)
    }

    internal fun disposePipeline(pipeline: Pipeline) {
        disposablePipelines += pipeline
    }

    protected fun render(dt: Double) {
        if (delayedCallbacks.isNotEmpty()) {
            for (i in delayedCallbacks.indices.reversed()) {
                val callback = delayedCallbacks[i]
                if (callback.callOnFrame <= frameIdx) {
                    callback.callback(this)
                    delayedCallbacks.removeAt(i)
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
                scenes[i].renderScene(this)
            }
        }
    }

    fun applyRenderingHints() {
        // apply scene specific hints (shadow map type, etc.)
        //scenes.forEach { it.onRenderingHintsChanged(this) }
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
