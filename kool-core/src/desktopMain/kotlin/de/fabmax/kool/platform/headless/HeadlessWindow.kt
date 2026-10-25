package de.fabmax.kool.platform.headless

import de.fabmax.kool.*
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ui2.UiScale
import de.fabmax.kool.platform.KoolWindowJvm
import de.fabmax.kool.util.BufferedList
import de.fabmax.kool.util.WindowTitleHoverHandler
import org.lwjgl.vulkan.VkInstance
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.roundToInt

/**
 * Virtual window used in headless mode. It has no OS-level window and no surface. Its size and scale are set by
 * the host application (or test) via [setSize].
 */
class HeadlessWindow internal constructor(
    val subsystem: HeadlessWindowSubsystem,
    initialSize: Vec2i,
    initialScale: Float,
) : KoolWindowJvm {

    override var isMouseOverWindow: Boolean = false
        internal set

    override var parentScreenScale: Float = initialScale
        private set
    override val positionOnScreen: Vec2i = Vec2i.ZERO
    override val sizeOnScreen: Vec2i get() = Vec2i(
        (framebufferSize.x / parentScreenScale).roundToInt(),
        (framebufferSize.y / parentScreenScale).roundToInt()
    )

    override var renderResolutionFactor: Float = 1f
        set(value) {
            if (value != field) {
                field = value
                updateSizesAndScales()
            }
        }
    override var framebufferSize: Vec2i = initialSize.coerceValid()
        private set
    override var size: Vec2i = framebufferSize
        private set
    override val renderScale: Float get() = parentScreenScale * renderResolutionFactor

    override var title: String = KoolSystem.configJvm.windowTitle

    override var flags: WindowFlags = WindowFlags(isVisible = true, isFocused = true)
        private set(value) {
            if (value != field) {
                val oldFlags = field
                field = value
                flagListeners.updated().forEach { it.onFlagsChanged(oldFlags, value) }
            }
        }

    override val capabilities: WindowCapabilities = WindowCapabilities.NONE.copy(
        canSetSize = true,
        canSetVisibility = true,
        canSetTitle = true,
    )

    override val resizeListeners: BufferedList<WindowResizeListener> = BufferedList()
    override val scaleChangeListeners: BufferedList<ScaleChangeListener> = BufferedList()
    override val flagListeners: BufferedList<WindowFlagsListener> = BufferedList()
    override val closeListeners: BufferedList<WindowCloseListener> = BufferedList()
    override val dragAndDropListeners: BufferedList<DragAndDropListener> = BufferedList()

    override var windowTitleHoverHandler: WindowTitleHoverHandler = WindowTitleHoverHandler()

    private val requestedSize = AtomicReference<SizeRequest?>(null)
    private val requestedFlags = AtomicReference<((WindowFlags) -> WindowFlags)?>(null)

    init {
        updateSizesAndScales()
    }

    /**
     * Requests a new window size in framebuffer pixels and, optionally, a new content scale (e.g. the DPI scale of
     * the host UI). Can be called from any thread, the new size is applied at the beginning of the next frame.
     */
    fun setSize(framebufferSize: Vec2i, scale: Float = parentScreenScale) {
        requestedSize.set(SizeRequest(framebufferSize.coerceValid(), scale))
    }

    override fun setSizeOnScreen(size: Vec2i) {
        setSize(Vec2i((size.x * parentScreenScale).roundToInt(), (size.y * parentScreenScale).roundToInt()))
    }

    /**
     * Sets the focused state of this window. Can be called from any thread, the new state is applied at the
     * beginning of the next frame.
     */
    fun setFocused(flag: Boolean) = updateFlags { it.copy(isFocused = flag) }

    override fun setVisible(flag: Boolean) = updateFlags { it.copy(isVisible = flag) }

    // fullscreen, maximize, etc. have no meaning in headless mode and are silently ignored
    override fun setFullscreen(flag: Boolean) { }
    override fun setMaximized(flag: Boolean) { }
    override fun setMinimized(flag: Boolean) { }
    override fun setPositionOnScreen(pos: Vec2i) { }
    override fun setTitleBarVisibility(flag: Boolean) { }
    override fun requestFocus() = setFocused(true)

    private fun updateFlags(block: (WindowFlags) -> WindowFlags) {
        requestedFlags.getAndUpdate { prev -> if (prev == null) block else { f -> block(prev(f)) } }
    }

    /**
     * Applies pending size / flag changes and queued input events. Called by [HeadlessWindowSubsystem] on the
     * render thread before each frame.
     */
    override fun pollEvents() {
        requestedSize.getAndSet(null)?.let { request ->
            val isSizeChange = request.size != framebufferSize
            val isScaleChange = request.scale != parentScreenScale
            if (isSizeChange || isScaleChange) {
                framebufferSize = request.size
                parentScreenScale = request.scale
                updateSizesAndScales()
            }
        }
        requestedFlags.getAndSet(null)?.let { flags = it(flags) }
        subsystem.input.applyQueuedEvents()
    }

    private fun updateSizesAndScales() {
        size = Vec2i(
            (framebufferSize.x * renderResolutionFactor).toInt(),
            (framebufferSize.y * renderResolutionFactor).toInt()
        )
        UiScale.updateUiScaleFromWindowScale(renderScale)
        resizeListeners.updated().forEach { it.onResize(size) }
        scaleChangeListeners.updated().forEach { it.onScaleChanged(renderScale) }
    }

    override fun createVulkanSurface(instance: VkInstance): Long {
        throw UnsupportedOperationException("HeadlessWindow has no surface")
    }

    override fun destroyVulkanSurface(surface: Long, instance: VkInstance) { }

    override fun swapBuffers() {
        throw UnsupportedOperationException("HeadlessWindow does not support OpenGL")
    }

    override fun close() {
        subsystem.close()
    }

    private fun Vec2i.coerceValid() = Vec2i(x.coerceAtLeast(1), y.coerceAtLeast(1))

    private class SizeRequest(val size: Vec2i, val scale: Float)
}
