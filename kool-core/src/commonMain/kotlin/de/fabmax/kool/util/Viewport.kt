package de.fabmax.kool.util

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec2i

/**
 * Viewport is used by scenes and render passes to determine the output rectangle on screen / framebuffer. Similar
 * to regular UI coordinates (but different to traditional OpenGL), Viewport origin is in the upper left corner and
 * the y-axis points downwards.
 */
data class Viewport(val x: Int, val y: Int, val width: Int, val height: Int) {
    val aspectRatio get() = width.toFloat() / height.toFloat()

    fun isInViewport(p: Vec2i) = isInViewport(p.x, p.y)
    fun isInViewport(p: Vec2f) = isInViewport(p.x, p.y)

    fun isInViewport(px: Int, py: Int) = px >= x && px < x + width && py >= y && py < y + height
    fun isInViewport(px: Float, py: Float) = px >= x && px < x + width && py >= y && py < y + height

    fun equals(x: Int, y: Int, width: Int, height: Int): Boolean {
        return x == this.x && y == this.y && width == this.width && height == this.height
    }
}