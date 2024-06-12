package de.fabmax.kool.platform

import de.fabmax.kool.math.MutableVec2i
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.util.logW
import de.fabmax.kool.util.memStack
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWNativeWin32.glfwGetWin32Window
import org.lwjgl.system.windows.MONITORINFOEX
import org.lwjgl.system.windows.POINT
import org.lwjgl.system.windows.RECT
import org.lwjgl.system.windows.User32.*
import org.lwjgl.system.windows.WindowProc

interface PlatformWindowHelper {
    fun hideTitleBar(windowPtr: Long)

    fun startWindowDrag(windowPtr: Long)
    fun windowDrag(windowPtr: Long)

    fun isMaximized(windowPtr: Long): Boolean = GLFW.glfwGetWindowAttrib(windowPtr, GLFW.GLFW_MAXIMIZED) != 0
    fun toggleMaximized(windowPtr: Long) {
        if (isMaximized(windowPtr)) {
            GLFW.glfwRestoreWindow(windowPtr)
        } else {
            GLFW.glfwMaximizeWindow(windowPtr)
        }
    }
}

class PlatformWindowHelperWindows(val glfwWindow: GlfwWindow) : PlatformWindowHelper {
    private val windowDragStartCursor = MutableVec2i()
    private val windowDragStartPos = MutableVec2i()

    private var isMaximized = false
    private val nonMaxPos = MutableVec2i()
    private val nonMaxSize = MutableVec2i()

    override fun hideTitleBar(windowPtr: Long) {
        val glfwHWnd = glfwGetWin32Window(windowPtr)

        val originalWndProcPtr = GetWindowLongPtr(glfwHWnd, GWL_WNDPROC)
        val wndProc = WindowProc.create { hWnd, uMsg, wParam, lParam ->
            when (uMsg) {
                WM_NCCALCSIZE -> {
                    if (wParam == 1L && lParam != 0L && !isMaximized) {
                        val rect = RECT.create(lParam)
                        rect.top(rect.top() + 1)
                        rect.right(rect.right() - 8)
                        rect.bottom(rect.bottom() - 8)
                        rect.left(rect.left() + 8)
                    }
                    0L
                }

                WM_NCPAINT -> 0L

                WM_NCHITTEST -> {
                    val y = lParam.toInt() shr 16
                    val x = lParam.toInt() and 0xffff
                    val clientX = x - glfwWindow.windowPosX
                    val clientY = y - glfwWindow.windowPosY
                    val resizeBorder = 4

                    if (clientY < resizeBorder) {
                        when {
                            clientX <= resizeBorder -> HTTOPLEFT.toLong()
                            clientX >= glfwWindow.framebufferWidth - resizeBorder -> HTTOPRIGHT.toLong()
                            else -> HTTOP.toLong()
                        }
                    } else {
                        nCallWindowProc(originalWndProcPtr, hWnd, uMsg, wParam, lParam)
                    }
                }

                else -> nCallWindowProc(originalWndProcPtr, hWnd, uMsg, wParam, lParam)
            }
        }

        var style = GetWindowLongPtr(glfwHWnd, GWL_STYLE)
        style = style or WS_THICKFRAME.toLong()
        style = style and WS_CAPTION.toLong().inv()
        SetWindowLongPtr(glfwHWnd, GWL_STYLE, style)
        SetWindowLongPtr(glfwHWnd, GWL_WNDPROC, wndProc.address())

        memStack {
            val windowRect = RECT.calloc(this)
            GetWindowRect(glfwHWnd, windowRect)
            val width = windowRect.right() - windowRect.left()
            val height = windowRect.bottom() - windowRect.top()
            SetWindowPos(glfwHWnd, 0L, 0, 0, width, height, SWP_FRAMECHANGED or SWP_NOMOVE)
        }

        glfwWindow.isTitleBarHidden = true
    }

    override fun startWindowDrag(windowPtr: Long) {
        val hWnd = glfwGetWin32Window(windowPtr)

        memStack {
            val point = POINT.calloc(this)
            val rect = RECT.calloc(this)
            GetCursorPos(point)
            GetWindowRect(hWnd, rect)
            windowDragStartCursor.set(point.x(), point.y())

            if (isMaximized) {
                demaximizeWindow(hWnd)

                val w = rect.right() - rect.left()
                windowDragStartPos.x = when {
                    windowDragStartCursor.x < w / 3 -> 0
                    windowDragStartCursor.x > w * 2 / 3 -> rect.right() - nonMaxSize.x
                    else -> windowDragStartCursor.x - nonMaxSize.x / 2
                }
                windowDragStartPos.y = rect.top()
            } else {
                windowDragStartPos.set(rect.left(), rect.top())
            }
        }
    }

    override fun windowDrag(windowPtr: Long) {
        memStack {
            val hWnd = glfwGetWin32Window(windowPtr)
            val point = POINT.calloc(this)
            GetCursorPos(point)

            val dragPos = windowDragStartPos + Vec2i(point.x(), point.y()) - windowDragStartCursor
            SetWindowPos(hWnd, 0L, dragPos.x, dragPos.y, 0, 0, SWP_FRAMECHANGED or SWP_NOSIZE)
        }
    }

    override fun isMaximized(windowPtr: Long): Boolean {
        return isMaximized
    }

    override fun toggleMaximized(windowPtr: Long) {
        val hWnd = glfwGetWin32Window(windowPtr)
        if (isMaximized) {
            demaximizeWindow(hWnd)
        } else {
            maximizeWindow(hWnd)
        }
    }

    private fun demaximizeWindow(hWnd: Long) {
        isMaximized = false

        var style = GetWindowLongPtr(hWnd, GWL_STYLE)
        style = style or WS_THICKFRAME.toLong()
        SetWindowLongPtr(hWnd, GWL_STYLE, style)

        SetWindowPos(
            hWnd, HWND_TOP,
            nonMaxPos.x,
            nonMaxPos.y,
            nonMaxSize.x,
            nonMaxSize.y,
            SWP_NOACTIVATE or SWP_NOZORDER
        )
    }

    private fun maximizeWindow(hWnd: Long) {
        isMaximized = true
        memStack {
            val windowRect = RECT.calloc(this)
            GetWindowRect(hWnd, windowRect)
            nonMaxPos.set(windowRect.left(), windowRect.top())
            nonMaxSize.set(windowRect.right() - windowRect.left(), windowRect.bottom() - windowRect.top())

            var style = GetWindowLongPtr(hWnd, GWL_STYLE)
            style = style and (
                WS_CAPTION or
                WS_THICKFRAME or
                WS_BORDER
            ).toLong().inv()
            SetWindowLongPtr(hWnd, GWL_STYLE, style)

            val hMonitor = MonitorFromWindow(hWnd, MONITOR_DEFAULTTONEAREST)
            val mi = MONITORINFOEX.calloc(this)
            mi.cbSize(MONITORINFOEX.SIZEOF)

            if (GetMonitorInfo(hMonitor, mi)) {
                SetWindowPos(
                    hWnd, HWND_TOP,
                    mi.rcWork().left(),
                    mi.rcWork().top(),
                    mi.rcWork().right() - mi.rcWork().left(),
                    mi.rcWork().bottom() - mi.rcWork().top(),
                    SWP_NOACTIVATE or SWP_NOZORDER
                )
            }
        }
    }
}

class PlatformWindowHelperCommon : PlatformWindowHelper {
    override fun hideTitleBar(windowPtr: Long) {
        logW { "Hide title bar is not supported on OS ${OsInfo.os}" }
    }

    override fun startWindowDrag(windowPtr: Long) {
        logW { "Window drag is not supported on OS ${OsInfo.os}" }
    }

    override fun windowDrag(windowPtr: Long) { }
}