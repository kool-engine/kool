package de.fabmax.kool

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.util.BufferedList

interface KoolWindow {
    /**
     * OS-level screen scale - typically 1.0 (100%) for traditional 96 dpi low-res displays and > 1.0 for
     * hidpi / retina displays. By default, the parent screen scale is applied to UI elements.
     */
    val parentScreenScale: Float

    /**
     * Window position in scaled screen space (i.e., considering [parentScreenScale]).
     */
    var positionInScreen: Vec2i

    /**
     * Window size in scaled screen space (i.e., considering [parentScreenScale]). For example, a full-screen
     * window on a 4k screen with 150% scale has a `screenSize` of `(3840, 2160) / 1.5 = (2560, 1440)`
     */
    var sizeOnScreen: Vec2i

    /**
     * User settable render resolution factor. Default value 1.0. Can be set to values < 1 to save performance
     * at the cost of more pixelated output. Does not affect the size of UI elements.
     */
    var renderResolutionFactor: Float

    /**
     * Physical size of this window in pixels without any scaling. This is equivalent to
     *   [sizeOnScreen] * [parentScreenScale]
     */
    val framebufferSize: Vec2i

    /**
     * Final render surface size in pixels. This is equivalent to
     *   [framebufferSize] * [renderResolutionFactor]
     * and
     *   [sizeOnScreen] * [parentScreenScale] * [renderResolutionFactor]
     */
    val size: Vec2i

    /**
     * Final scale that needs to be applied to UI elements to match the parent screen scale considering the
     * [renderResolutionFactor].
     */
    val renderScale: Float

    var title: String
    var flags: WindowFlags

    val capabilities: WindowCapabilities

    val resizeListeners: BufferedList<WindowResizeListener>
    val scaleChangeListeners: BufferedList<ScaleChangeListener>
    val flagListeners: BufferedList<WindowFlagsListener>
    val closeListeners: BufferedList<WindowCloseListener>

    fun close()
}

data class WindowCapabilities(
    val canSetSize: Boolean,
    val canSetPosition: Boolean,
    val canSetFullscreen: Boolean,
    val canMaximize: Boolean,
    val canMinimize: Boolean,
    val canSetVisibility: Boolean,
    val canSetTitle: Boolean,
    val canHideTitleBar: Boolean,
)

data class WindowFlags(
    val isFullscreen: Boolean,
    val isMaximized: Boolean,
    val isMinimized: Boolean,
    val isVisible: Boolean,
    val isFocused: Boolean,
    val isHiddenTitleBar: Boolean,
) {
    companion object {
        val DEFAULT = WindowFlags(
            isFullscreen = false,
            isMaximized = false,
            isMinimized = false,
            isVisible = false,
            isFocused = false,
            isHiddenTitleBar = false,
        )
    }
}

fun KoolWindow.setFullscreen(flag: Boolean) { flags = flags.copy(isFullscreen = flag) }
fun KoolWindow.setMaximized(flag: Boolean) { flags = flags.copy(isMaximized = flag) }
fun KoolWindow.setMinimized(flag: Boolean) { flags = flags.copy(isMinimized = flag) }
fun KoolWindow.setVisible(flag: Boolean) { flags = flags.copy(isVisible = flag) }
fun KoolWindow.setFocused(flag: Boolean) { flags = flags.copy(isFocused = flag) }
fun KoolWindow.setTitleBarVisibility(flag: Boolean) { flags = flags.copy(isHiddenTitleBar = flag) }

fun interface WindowResizeListener {
    fun onResize(newSize: Vec2i)
}

fun interface ScaleChangeListener {
    fun onScaleChanged(newScale: Float)
}

fun interface WindowFlagsListener {
    fun onFlagsChanged(oldFlags: WindowFlags, newFlags: WindowFlags)
}

fun interface WindowFlagListener {
    fun onFlagChanged(newFlags: WindowFlags)
}

fun interface WindowCloseListener {
    fun onCloseRequest(): Boolean
}

fun KoolWindow.onResize(listener: WindowResizeListener) {
    resizeListeners.stageAdd(listener)
}

fun KoolWindow.onScaleChange(listener: ScaleChangeListener) {
    scaleChangeListeners.stageAdd(listener)
}

fun KoolWindow.onWindowCloseRequest(listener: WindowCloseListener) {
    closeListeners.stageAdd(listener)
}

fun KoolWindow.onWindowFlagsChanged(listener: WindowFlagsListener) {
    flagListeners.stageAdd(listener)
}

fun KoolWindow.onWindowFocusChanged(listener: WindowFlagListener) {
    flagListeners += WindowFlagsListener { oldFlags, newFlags ->
        if (oldFlags.isFocused != newFlags.isFocused) {
            listener.onFlagChanged(newFlags)
        }
    }
}
