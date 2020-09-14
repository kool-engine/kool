package de.fabmax.kool.math

interface Noise2d {
    fun eval(x: Float, y: Float): Float
}

interface Noise3d {
    fun eval(x: Float, y: Float, z: Float): Float
}