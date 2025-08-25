package de.fabmax.kool

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.util.BufferedList

interface KoolWindow {
    val position: Vec2i
    val physicalSize: Vec2i
    val scaledSize: Vec2i
    val scale: Float
    val title: String
    val flags: WindowFlags

    val physicalResizeListeners: BufferedList<WindowResizeListener>
    val scaledResizeListeners: BufferedList<WindowResizeListener>
    val scaleChangeListeners: BufferedList<ScaleChangeListener>
    val flagListeners: BufferedList<WindowFlagListener>
    val closeListeners: BufferedList<WindowCloseListener>

    val capabilities: WindowCapabilities

    fun setPosition(position: Vec2i)
    fun setScaledSize(size: Vec2i)
    fun setTitle(newTitle: String)
    fun setTitleBarVisibility(visible: Boolean)
    fun setFullscreen(enabled: Boolean)
    fun setMaximized(enabled: Boolean)
    fun setMinimized(enabled: Boolean)
    fun setVisible(isVisible: Boolean)
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
)

fun interface WindowResizeListener {
    fun onResize(newSize: Vec2i)
}

fun interface ScaleChangeListener {
    fun onScaleChanged(newScale: Float)
}

fun interface WindowFlagListener {
    fun onFlagsChanged(newFlags: WindowFlags)
}

fun interface WindowCloseListener {
    fun onCloseRequest(): Boolean
}

fun KoolWindow.onScaledWindowResized(listener: WindowResizeListener) {
    scaledResizeListeners.stageAdd(listener)
}

fun KoolWindow.onPhysicalWindowResized(listener: WindowResizeListener) {
    physicalResizeListeners.stageAdd(listener)
}

fun KoolWindow.onScaleChange(listener: ScaleChangeListener) {
    scaleChangeListeners.stageAdd(listener)
}

fun KoolWindow.onWindowFlagsChanged(listener: WindowFlagListener) {
    flagListeners.stageAdd(listener)
}

fun KoolWindow.onWindowCloseRequest(listener: WindowCloseListener) {
    closeListeners.stageAdd(listener)
}
