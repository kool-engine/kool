package de.fabmax.kool.math.noise

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f

fun interface Noise2d {
    fun eval(x: Float, y: Float): Float

    fun eval(p: Vec2f) = eval(p.x, p.y)
}

fun interface Noise3d {
    fun eval(x: Float, y: Float, z: Float): Float

    fun eval(p: Vec3f) = eval(p.x, p.y, p.z)
}