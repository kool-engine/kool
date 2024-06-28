package de.fabmax.kool

import de.fabmax.kool.input.KeyboardInput
import de.fabmax.kool.input.PointerInput
import de.fabmax.kool.modules.ui2.UiScale
import de.fabmax.kool.pipeline.OffscreenRenderPass
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.backend.RenderBackend
import de.fabmax.kool.pipeline.ibl.BrdfLutPass
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.*
import kotlin.math.roundToInt

/**
 * @author fabmax
 */

abstract class KoolContext {
    var windowScale = 1f
        set(value) {
            val userValue = applicationCallbacks.onWindowScaleChange(value, this)
            if (userValue != field) {
                logD { "Window scale changed: (${(userValue * 100f).roundToInt()} %)" }
                field = userValue
                UiScale.windowScale.set(field)
                UiScale.measuredScale = UiScale.windowScale.value * UiScale.uiScale.value
                onWindowScaleChanged.forEach { it(this) }
            }
        }

    var isWindowFocused = true
        protected set(value) {
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

    abstract val backend: RenderBackend

    var applicationCallbacks: ApplicationCallbacks = object : ApplicationCallbacks { }
    val onWindowScaleChanged = mutableListOf<(KoolContext) -> Unit>()
    val onWindowFocusChanged = mutableListOf<(KoolContext) -> Unit>()
    val onRender = BufferedList<(KoolContext) -> Unit>()

    /**
     * Frames per second (averaged over last 25 frames)
     */
    var fps = 60.0
        private set

    val scenes: BufferedList<Scene> = BufferedList()

    val backgroundScene = Scene("backgroundScene")
    val backgroundPasses: BufferedList<OffscreenRenderPass>
        get() = backgroundScene.offscreenPasses

    val defaultPbrBrdfLut: Texture2d by lazy {
        BrdfLutPass(backgroundScene).also { addBackgroundRenderPass(it) }.copyColor()
    }

    private val frameTimes = DoubleArray(25) { 0.017 }

    abstract val windowWidth: Int
    abstract val windowHeight: Int
    abstract var isFullscreen: Boolean

    abstract fun openUrl(url: String, sameWindow: Boolean = true)

    abstract fun run()

    abstract fun getSysInfos(): List<String>

    fun getWindowViewport(result: Viewport) {
        backend.getWindowViewport(result)
    }

    fun addBackgroundRenderPass(renderPass: OffscreenRenderPass) {
        backgroundScene.addOffscreenPass(renderPass)
    }

    fun removeBackgroundRenderPass(renderPass: OffscreenRenderPass) {
        backgroundScene.removeOffscreenPass(renderPass)
    }

    fun addScene(scene: Scene) {
        scenes += scene
    }

    fun removeScene(scene: Scene) {
        scenes -= scene
    }

    protected fun render(dt: Double) {
        if (isProfileRenderPasses) {
            Profiling.enter("!main-render-loop")
        }

        Time.deltaT = dt.toFloat()
        Time.gameTime += dt
        Time.frameCount++

        frameTimes[Time.frameCount % frameTimes.size] = dt
        var sum = 0.0
        for (i in frameTimes.indices) { sum += frameTimes[i] }
        fps = (frameTimes.size / sum) * 0.1 + fps * 0.9

        KeyboardInput.onNewFrame(this)
        PointerInput.onNewFrame(this)

        onRender.update()
        for (i in onRender.indices) {
            onRender[i](this)
        }

        if (!backgroundScene.isEmpty) {
            backgroundScene.renderScene(this)
        }

        // draw scene contents (back to front)
        scenes.update()
        for (i in scenes.indices) {
            if (scenes[i].isVisible) {
                scenes[i].renderScene(this)
            }
        }

        if (isProfileRenderPasses) {
            Profiling.exit("!main-render-loop")
        }
    }

    companion object {
        // automatically updated by gradle script on build
        const val KOOL_VERSION = "0.15.0"
    }
}
