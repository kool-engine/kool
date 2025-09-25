package de.fabmax.kool.platform.swing

import de.fabmax.kool.*
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ui2.UiScale
import de.fabmax.kool.platform.KoolWindowJvm
import de.fabmax.kool.util.BufferedList
import de.fabmax.kool.util.WindowTitleHoverHandler
import org.lwjgl.vulkan.KHRSurface
import org.lwjgl.vulkan.VkInstance
import org.lwjgl.vulkan.awt.AWTVK
import java.awt.Canvas
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import kotlin.math.roundToInt

internal class CanvasWrapper(val canvas: Canvas) : KoolWindowJvm {
    val input: SwingInput = SwingInput(this)

    private val canvasScale: Vec2f get() {
        val t = canvas.graphicsConfiguration?.defaultTransform
        return if (t == null) Vec2f.ONES else Vec2f(t.scaleX.toFloat(), t.scaleY.toFloat())
    }

    private val canvasSize: Vec2i get() {
        val s = canvasScale
        return Vec2i(
            x = (canvas.width * s.x).roundToInt(),
            y = (canvas.height * s.y).roundToInt()
        )
    }

    override var isMouseOverWindow: Boolean = false; internal set

    override val parentScreenScale: Float = canvasScale.x
    override var positionInScreen: Vec2i = Vec2i.ZERO
        set(_) {}

    override var sizeOnScreen: Vec2i
        get() = Vec2i(canvas.width, canvas.height)
        set(_) {}
    override var renderResolutionFactor: Float = 1f
        set(_) {}

    override val framebufferSize: Vec2i get() = canvasSize
    override val size: Vec2i get() = canvasSize
    override val renderScale: Float get() = parentScreenScale * renderResolutionFactor

    override var title: String
        get() = "Swing Canvas"
        set(_) {}

    override var flags: WindowFlags = WindowFlags.DEFAULT
        private set(value) {
            if (value != field) {
                val oldFlags = field
                field = value
                flagListeners.updated().forEach { it.onFlagsChanged(oldFlags, value) }
            }
        }

    override val capabilities: WindowCapabilities = WindowCapabilities.NONE

    override val resizeListeners: BufferedList<WindowResizeListener> = BufferedList()
    override val scaleChangeListeners: BufferedList<ScaleChangeListener> = BufferedList()
    override val flagListeners: BufferedList<WindowFlagsListener> = BufferedList()
    override val closeListeners: BufferedList<WindowCloseListener> = BufferedList()
    override val dragAndDropListeners: BufferedList<DragAndDropListener> = BufferedList()

    override var windowTitleHoverHandler: WindowTitleHoverHandler = WindowTitleHoverHandler()

    init {
        canvas.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                resizeListeners.updated().forEach { it.onResize(canvasSize) }
            }
            override fun componentShown(e: ComponentEvent) { flags = flags.copy(isVisible = true) }
            override fun componentHidden(e: ComponentEvent) { flags = flags.copy(isVisible = false) }
        })
        canvas.addFocusListener(object : FocusAdapter() {
            override fun focusGained(e: FocusEvent) { flags = flags.copy(isFocused = true) }
            override fun focusLost(e: FocusEvent) { flags = flags.copy(isFocused = false) }
        })
        flags = flags.copy(
            isFocused = canvas.isFocusOwner,
            isVisible = canvas.isVisible,
        )
        UiScale.updateUiScaleFromWindowScale(renderScale)
    }

    override fun pollEvents() { }

    override fun createVulkanSurface(instance: VkInstance): Long {
        return AWTVK.create(canvas, instance)
    }

    override fun destroyVulkanSurface(surface: Long, instance: VkInstance) {
        KHRSurface.vkDestroySurfaceKHR(instance, surface, null)
    }

    override fun swapBuffers() { }

    override fun close() { }
}