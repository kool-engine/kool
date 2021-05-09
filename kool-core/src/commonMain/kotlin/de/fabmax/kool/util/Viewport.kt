package de.fabmax.kool.util

import kotlin.math.abs

class Viewport(var x: Int, var ySigned: Int, var width: Int, var heightSigned: Int) {
    constructor() : this(0, 0, 0, 0)

    val y: Int
        get() = if (heightSigned < 0) ySigned + heightSigned else ySigned
    val height: Int
        get() = abs(heightSigned)

    val aspectRatio get() = width.toFloat() / height.toFloat()

    fun isInViewport(x: Float, y: Float) = x >= this.x && x < this.x + width && y >= this.y && y < this.y + height

    fun set(x: Int, ySigned: Int, width: Int, heightSigned: Int) {
        this.x = x
        this.width = width
        this.ySigned = ySigned
        this.heightSigned = heightSigned
    }
}