package de.fabmax.kool.platform

import de.fabmax.kool.DesktopImpl
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFWVidMode

class MonitorSpec(val monitor: Long) {
    val widthMm: Int
    val heightMm: Int
    val widthPx: Int
    val heightPx: Int
    val posX: Int
    val posY: Int
    val dpi: Float

    val vidmode: GLFWVidMode = GLFW.glfwGetVideoMode(monitor)!!

    init {
        val x = IntArray(1)
        val y = IntArray(1)

        GLFW.glfwGetMonitorPhysicalSize(monitor, x, y)
        widthMm = x[0]
        heightMm = y[0]
        GLFW.glfwGetMonitorPos(monitor, x, y)
        posX = x[0]
        posY = y[0]

        widthPx = vidmode.width()
        heightPx = vidmode.height()

        dpi = widthPx.toFloat() / (widthMm / 25.4f)
    }

    fun isOnMonitor(x: Int, y: Int): Boolean = (x >= posX && x < posX + widthPx && y >= posY && y < posY + heightPx)

    fun distance(x: Int, y: Int): Double {
        if (isOnMonitor(x, y)) {
            return -1.0
        } else {
            var dx = 0.0
            var dy = 0.0
            if (x < posX) {
                dx = (posX - x).toDouble()
            } else if (x > posX + widthPx) {
                dx = (x - posX - widthPx).toDouble()
            }
            if (y < posY) {
                dy = (posY - y).toDouble()
            } else if (y > posY + heightPx) {
                dy = (y - posY - heightPx).toDouble()
            }
            return Math.sqrt(dx * dx + dy * dy)
        }
    }
}

fun getMonitorSpecAt(x: Int, y: Int): MonitorSpec {
    var nearestMon: MonitorSpec? = null
    var dist = Double.MAX_VALUE
    for (i in DesktopImpl.monitors.indices) {
        val d = DesktopImpl.monitors[i].distance(x, y)
        if (d < dist) {
            dist = d
            nearestMon = DesktopImpl.monitors[i]
        }
    }
    return nearestMon!!
}

fun getResolutionAt(x: Int, y: Int): Float {
    return getMonitorSpecAt(x, y).dpi
}
