package de.fabmax.kool.math

interface Noise2d {
    fun eval(x: Float, y: Float): Float

    fun eval(p: Vec2f) = eval(p.x, p.y)
}

interface Noise3d {
    fun eval(x: Float, y: Float, z: Float): Float

    fun eval(p: Vec3f) = eval(p.x, p.y, p.z)
}