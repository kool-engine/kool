package de.fabmax.kool.platform

import de.fabmax.kool.math.MutableVec2i
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.util.WindowTitleHoverHandler
import de.fabmax.kool.util.logW
import de.fabmax.kool.util.memStack
import org.lwjgl.glfw.GLFW.glfwSetWindowPos
import org.lwjgl.glfw.GLFW.glfwSetWindowSize
import org.lwjgl.glfw.GLFWNativeWin32.glfwGetWin32Window
import org.lwjgl.system.windows.RECT
import org.lwjgl.system.windows.User32.*
import org.lwjgl.system.windows.WindowProc

interface PlatformWindowHelper {
    fun hideTitleBar(windowPtr: Long)

    fun getWindowPos(windowPtr: Long, reportedPosX: Int, reportedPosY: Int) = Vec2i(reportedPosX, reportedPosY)
    fun setWindowPos(windowPtr: Long, x: Int, y: Int) = glfwSetWindowPos(windowPtr, x, y)
    fun setWindowSize(windowPtr: Long, width: Int, height: Int) = glfwSetWindowSize(windowPtr, width, height)
}

class PlatformWindowHelperWindows(val glfwWindow: GlfwWindow) : PlatformWindowHelper {
    private val windowDragStartCursor = MutableVec2i()
    private val windowDragStartPos = MutableVec2i()

    private val hoverHandler: WindowTitleHoverHandler get() = glfwWindow.ctx.windowTitleHoverHandler
    private var isHiddenTitleBar by glfwWindow::isHiddenTitleBar
    private var isMaximized = false
    private val nonMaxPos = MutableVec2i()
    private val nonMaxSize = MutableVec2i()

    private val topBorder = 1
    private val nonTopBorder = 8

    private var nonClientLeftButtonDown = false

    override fun hideTitleBar(windowPtr: Long) {
        val glfwHWnd = glfwGetWin32Window(windowPtr)
        val originalWndProcPtr = GetWindowLongPtr(null, glfwHWnd, GWL_WNDPROC)

        val wndProc = WindowProc.create { hWnd, uMsg, wParam, lParam ->
            when (uMsg) {
                WM_NCLBUTTONDOWN -> {
                    if (isHoverWindowButton()) {
                        nonClientLeftButtonDown = true
                        0L
                    } else {
                        nCallWindowProc(originalWndProcPtr, hWnd, uMsg, wParam, lParam)
                    }
                }
                WM_NCLBUTTONUP -> {
                    if (nonClientLeftButtonDown) {
                        nonClientLeftButtonDown = false
                        hoverHandler.handleClick()
                        0L
                    } else {
                        nCallWindowProc(originalWndProcPtr, hWnd, uMsg, wParam, lParam)
                    }
                }
                WM_NCMOUSELEAVE -> {
                    hoverHandler.leaveHover()
                    nCallWindowProc(originalWndProcPtr, hWnd, uMsg, wParam, lParam)
                }

                WM_NCCALCSIZE -> {
                    if (wParam == 1L && lParam != 0L) {
                        val rect = RECT.create(lParam)
                        rect.top(rect.top() + topBorder)
                        rect.right(rect.right() - nonTopBorder)
                        rect.bottom(rect.bottom() - nonTopBorder)
                        rect.left(rect.left() + nonTopBorder)
                    }
                    0L
                }

                WM_NCHITTEST -> {
                    val y = lParam.toInt() shr 16
                    val x = lParam.toInt() and 0xffff
                    val clientX = x - glfwWindow.windowPosX - nonTopBorder
                    val clientY = y - glfwWindow.windowPosY - topBorder
                    val resizeBorder = 4

                    if (clientY < resizeBorder && !isMaximized) {
                        when {
                            clientX <= resizeBorder -> HTTOPLEFT.toLong()
                            clientX >= glfwWindow.framebufferWidth - resizeBorder -> HTTOPRIGHT.toLong()
                            else -> HTTOP.toLong()
                        }
                    } else {
                        val r = nCallWindowProc(originalWndProcPtr, hWnd, uMsg, wParam, lParam)
                        if (r != HTCLIENT.toLong()) r else {
                            when (hoverHandler.checkHover(clientX, clientY)) {
                                WindowTitleHoverHandler.HoverState.NONE -> r
                                WindowTitleHoverHandler.HoverState.TITLE_BAR -> HTCAPTION.toLong()
                                WindowTitleHoverHandler.HoverState.MIN_BUTTON -> HTMINBUTTON.toLong()
                                WindowTitleHoverHandler.HoverState.MAX_BUTTON -> HTMAXBUTTON.toLong()
                                WindowTitleHoverHandler.HoverState.CLOSE_BUTTON -> HTCLOSE.toLong()
                            }
                        }
                    }
                }

                else -> nCallWindowProc(originalWndProcPtr, hWnd, uMsg, wParam, lParam)
            }
        }

        SetWindowLongPtr(null, glfwHWnd, GWL_WNDPROC, wndProc.address())
        isHiddenTitleBar = true
    }

    private fun isHoverWindowButton(): Boolean {
        return hoverHandler.hoverState == WindowTitleHoverHandler.HoverState.MIN_BUTTON ||
                hoverHandler.hoverState == WindowTitleHoverHandler.HoverState.MAX_BUTTON ||
                hoverHandler.hoverState == WindowTitleHoverHandler.HoverState.CLOSE_BUTTON
    }

    override fun getWindowPos(windowPtr: Long, reportedPosX: Int, reportedPosY: Int): Vec2i {
        return memStack {
            val hWnd = glfwGetWin32Window(windowPtr)
            val rect = RECT.calloc(this)
            GetWindowRect(null, hWnd, rect)
            Vec2i(rect.left(), rect.top())
        }
    }

    override fun setWindowPos(windowPtr: Long, x: Int, y: Int) {
        if (isHiddenTitleBar) {
            val hWnd = glfwGetWin32Window(windowPtr)
            SetWindowPos(null, hWnd, 0L, x, y, 0, 0, SWP_FRAMECHANGED or SWP_NOSIZE)
        } else {
            super.setWindowPos(windowPtr, x, y)
        }
    }

    override fun setWindowSize(windowPtr: Long, width: Int, height: Int) {
        if (isHiddenTitleBar) {
            val hWnd = glfwGetWin32Window(windowPtr)
            SetWindowPos(null, hWnd, 0L, 0, 0, width + nonTopBorder * 2, height + nonTopBorder + topBorder, SWP_FRAMECHANGED or SWP_NOMOVE)
        } else {
            super.setWindowSize(windowPtr, width, height)
        }
    }
}

class PlatformWindowHelperCommon : PlatformWindowHelper {
    override fun hideTitleBar(windowPtr: Long) {
        logW { "Hide title bar is not supported on OS ${OsInfo.os}" }
    }
}