package de.fabmax.kool.platform

import android.content.Context
import android.hardware.display.DisplayManager
import android.opengl.GLSurfaceView
import android.util.DisplayMetrics
import de.fabmax.kool.*
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ui2.UiScale
import de.fabmax.kool.util.BufferedList
import de.fabmax.kool.util.WindowTitleHoverHandler
import kotlin.math.max

class AndroidWindow(val ctx: KoolContextAndroid, config: KoolConfigAndroid) : KoolWindow {
    val surfaceView: GLSurfaceView = config.surfaceView ?: KoolSurfaceView(config.appContext)

    override val parentScreenScale: Float

    override var positionInScreen: Vec2i
        get() = Vec2i(0, 0)
        set(value) {}

    override var sizeOnScreen: Vec2i
        get() = framebufferSize
        set(value) {}

    override var renderResolutionFactor: Float = 1f

    override val framebufferSize: Vec2i
        get() = Vec2i(ctx.backend.viewWidth, ctx.backend.viewHeight)

    override val size: Vec2i
        get() = framebufferSize

    override val renderScale: Float
        get() = parentScreenScale * renderResolutionFactor

    override var title: String = ""

    private var _flags = WindowFlags()
    override val flags: WindowFlags
        get() = _flags
    override val capabilities: WindowCapabilities = WindowCapabilities.NONE

    override val resizeListeners: BufferedList<WindowResizeListener> = BufferedList()
    override val scaleChangeListeners: BufferedList<ScaleChangeListener> = BufferedList()
    override val flagListeners: BufferedList<WindowFlagsListener> = BufferedList()
    override val closeListeners: BufferedList<WindowCloseListener> = BufferedList()
    override val dragAndDropListeners: BufferedList<DragAndDropListener> = BufferedList()

    override var windowTitleHoverHandler: WindowTitleHoverHandler = WindowTitleHoverHandler()

    init {

        val metrics = DisplayMetrics()
        val displayManager = config.appContext.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        @Suppress("DEPRECATION")
        displayManager.displays[0].getMetrics(metrics)

        parentScreenScale = max(1f, metrics.density * config.scaleModifier)
        UiScale.updateUiScaleFromWindowScale(renderScale)

        surfaceView.setRenderer(ctx.backend)
    }

    override fun close() { }
}
