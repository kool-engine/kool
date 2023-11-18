package de.fabmax.kool.util

/**
 * Viewport is used by scenes and render passes to determine the output rectangle on screen / framebuffer. Similar
 * to regular UI coordinates (but different to traditional OpenGL), Viewport origin is in the upper left corner and
 * the y-axis points downwards.
 */
class Viewport(var x: Int, var y: Int, var width: Int, var height: Int) {

    val aspectRatio get() = width.toFloat() / height.toFloat()

    fun isInViewport(x: Float, y: Float) = x >= this.x && x < this.x + width && y >= this.y && y < this.y + height

    fun set(x: Int, y: Int, width: Int, height: Int) {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
    }
}