package de.fabmax.kool

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.util.BufferedList
import de.fabmax.kool.util.WindowTitleHoverHandler

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
     * Physical size of this window in pixels.
     */
    val framebufferSize: Vec2i

    /**
     * Final output surface size in pixels considering all scaling factors.
     */
    val size: Vec2i

    /**
     * Final scale that needs to be applied to UI elements to match the parent screen scale considering the
     * [renderResolutionFactor].
     */
    val renderScale: Float

    var title: String
    val flags: WindowFlags

    val capabilities: WindowCapabilities

    val resizeListeners: BufferedList<WindowResizeListener>
    val scaleChangeListeners: BufferedList<ScaleChangeListener>
    val flagListeners: BufferedList<WindowFlagsListener>
    val closeListeners: BufferedList<WindowCloseListener>
    val dragAndDropListeners: BufferedList<DragAndDropListener>

    var windowTitleHoverHandler: WindowTitleHoverHandler

    fun setFullscreen(flag: Boolean) { }
    fun setMaximized(flag: Boolean) { }
    fun setMinimized(flag: Boolean) { }
    fun setVisible(flag: Boolean) { }
    fun setFocused(flag: Boolean) { }
    fun setTitleBarVisibility(flag: Boolean) { }

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
) {
    companion object {
        val NONE = WindowCapabilities(
            canSetSize = false,
            canSetPosition = false,
            canSetFullscreen = false,
            canMaximize = false,
            canMinimize = false,
            canSetVisibility = false,
            canSetTitle = false,
            canHideTitleBar = false,
        )
    }
}

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

fun interface WindowResizeListener {
    fun onResize(newSize: Vec2i)
}

fun interface ScaleChangeListener {
    /**
     * Called when the screen scale changes, e.g., because the window is moved onto another monitor with a different
     * scale.
     */
    fun onScaleChanged(newScale: Float)
}

fun interface WindowFlagsListener {
    fun onFlagsChanged(oldFlags: WindowFlags, newFlags: WindowFlags)
}

fun interface WindowFlagListener {
    fun onFlagChanged(newFlags: WindowFlags)
}

fun interface WindowCloseListener {
    /**
     * Called when the app (window / browser tab) is about to close. Can return true to proceed with closing the app
     * or false to stop it.
     * This is particular useful to implement a dialogs like "There is unsaved stuff, are you sure you want to close
     * this app? Yes / no / maybe".
     */
    fun onCloseRequest(): Boolean
}

interface DragAndDropListener {
    /**
     * Called when the user drags (and drops) files into the app window. Ideally, we also would want to have callbacks
     * containing the drag and drop state (e.g., cursor position) before files are dropped, but this is currently not
     * possible due to limited drag and drop support of GLFW on JVM.
     */
    fun onFileDrop(droppedFiles: List<LoadableFile>) { }
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
