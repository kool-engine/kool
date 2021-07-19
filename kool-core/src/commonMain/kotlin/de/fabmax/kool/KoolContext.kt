package de.fabmax.kool

import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.pipeline.OffscreenRenderPass
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.shadermodel.ShaderGenerator
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Viewport

/**
 * @author fabmax
 */

expect fun createDefaultContext(): KoolContext

abstract class KoolContext {

    var screenDpi = 96f

    abstract val assetMgr: AssetManager

    abstract val shaderGenerator: ShaderGenerator

    abstract val inputMgr: InputManager

    val engineStats = EngineStats()

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
    private val backgroundScene = Scene("backgroundScene")
    val backgroundPasses: List<OffscreenRenderPass>
        get() = backgroundScene.offscreenPasses

    private val delayedCallbacks = mutableListOf<DelayedCallback>()
    internal val disposablePipelines = mutableListOf<Pipeline>()

    private val frameTimes = DoubleArray(25) { 0.017 }

    abstract val windowWidth: Int
    abstract val windowHeight: Int

    abstract fun openUrl(url: String)

    abstract fun run()

    abstract fun destroy()

    abstract fun getSysInfos(): List<String>

    abstract fun getWindowViewport(result: Viewport)

    fun runDelayed(frames: Int, callback: (KoolContext) -> Unit) {
        delayedCallbacks += DelayedCallback(frameIdx + frames, callback)
    }

    internal fun disposePipeline(pipeline: Pipeline) {
        disposablePipelines += pipeline
    }

    fun addBackgroundRenderPass(renderPass: OffscreenRenderPass) {
        backgroundScene.addOffscreenPass(renderPass)
    }

    fun removeBackgroundRenderPass(renderPass: OffscreenRenderPass) {
        backgroundScene.removeOffscreenPass(renderPass)
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

        backgroundScene.renderScene(this)

        // draw scene contents (back to front)
        for (i in scenes.indices) {
            if (scenes[i].isVisible) {
                scenes[i].renderScene(this)
            }
        }
    }

    private class DelayedCallback(val callOnFrame: Int, val callback: (KoolContext) -> Unit)

    companion object {
        // automatically updated by gradle script on build
        const val KOOL_VERSION = "0.8.0-210719.2132"
    }
}
