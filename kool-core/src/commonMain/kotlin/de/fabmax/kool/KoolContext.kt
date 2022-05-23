package de.fabmax.kool

import de.fabmax.kool.math.Mat4d
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.pipeline.OffscreenRenderPass
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.ShaderCode
import de.fabmax.kool.pipeline.ibl.BrdfLutPass
import de.fabmax.kool.pipeline.shadermodel.ShaderGenerator
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.Profiling
import de.fabmax.kool.util.Viewport
import de.fabmax.kool.util.logD
import kotlinx.coroutines.CompletableDeferred
import kotlin.math.roundToInt

/**
 * @author fabmax
 */

expect fun createDefaultContext(): KoolContext

abstract class KoolContext {

    var windowScale = 1f
        set(value) {
            val userValue = applicationCallbacks.onWindowScaleChange(value, this)
            if (userValue != field) {
                logD { "Window scale changed: (${(userValue * 100f).roundToInt()} %)" }
                field = userValue
                onWindowScaleChanged.forEach { it(this) }
            }
        }

    var isWindowFocused = true
        internal set(value) {
            field = value
            onWindowFocusChanged.forEach { it(this) }
        }

    var isProfileRenderPasses = false
        set(value) {
            field = value
            if (value) {
                Profiling.enableAutoPrint(10.0, this)
            } else {
                Profiling.disableAutoPrint(this)
            }
        }

    @Deprecated("Use windowScale instead", replaceWith = ReplaceWith("windowScale * 96f"))
    val screenDpi: Float
        get() = windowScale * 96f

    abstract val assetMgr: AssetManager

    abstract val shaderGenerator: ShaderGenerator

    abstract val inputMgr: InputManager

    val engineStats = EngineStats()

    val projCorrectionMatrixScreen = Mat4d()
    val projCorrectionMatrixOffscreen = Mat4d()
    val depthBiasMatrix = Mat4d().translate(0.5, 0.5, 0.5).scale(0.5, 0.5, 0.5)

    var applicationCallbacks: ApplicationCallbacks = object : ApplicationCallbacks { }
    val onWindowScaleChanged = mutableListOf<(KoolContext) -> Unit>()
    val onWindowFocusChanged = mutableListOf<(KoolContext) -> Unit>()
    val onRender = mutableListOf<(KoolContext) -> Unit>()

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

    val defaultPbrBrdfLut by lazy { BrdfLutPass(backgroundScene).also { addBackgroundRenderPass(it) }.copyColor() }

    private val delayedCallbacks = mutableListOf<DelayedCallback>()
    internal val disposablePipelines = mutableListOf<Pipeline>()

    private val frameTimes = DoubleArray(25) { 0.017 }

    abstract val windowWidth: Int
    abstract val windowHeight: Int
    abstract var isFullscreen: Boolean

    abstract fun openUrl(url: String, sameWindow: Boolean = true)

    abstract fun run()

    abstract fun close()

    abstract fun generateKslShader(shader: KslShader, pipelineLayout: Pipeline.Layout): ShaderCode

    abstract fun getSysInfos(): List<String>

    abstract fun getWindowViewport(result: Viewport)

    fun runDelayed(frames: Int, callback: (KoolContext) -> Unit) {
        delayedCallbacks += DelayedCallback(frameIdx + frames, callback)
    }

    suspend fun delayFrames(frames: Int) {
        if (frames > 0) {
            val lock = CompletableDeferred<Any>()
            runDelayed(frames) { lock.complete(Unit) }
            lock.await()
        }
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
        if (isProfileRenderPasses) {
            Profiling.enter("!main-render-loop")
        }

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

        if (isProfileRenderPasses) {
            Profiling.exit("!main-render-loop")
        }
    }

    private class DelayedCallback(val callOnFrame: Int, val callback: (KoolContext) -> Unit)

    companion object {
        // automatically updated by gradle script on build
        const val KOOL_VERSION = "0.9.0-SNAPSHOT"
    }
}
