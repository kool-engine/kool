package de.fabmax.kool.math.noise

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f

class LayeredNoise3d : Noise3d {
    val layers = mutableListOf<Noise3d>()

    override fun eval(x: Float, y: Float, z: Float): Float {
        var result = 0f
        for (i in layers.indices) {
            result += layers[i].eval(x, y, z)
        }
        return result
    }
}

class LayeredNoise2d : Noise2d {
    val layers = mutableListOf<Noise2d>()

    override fun eval(x: Float, y: Float): Float {
        var result = 0f
        for (i in layers.indices) {
            result += layers[i].eval(x, y)
        }
        return result
    }
}

class ScaledNoise3d(val noise: Noise3d, val amplitude: Float = 1f, val scale: Float = 1f, val center: Vec3f = Vec3f.ZERO) : Noise3d {
    override fun eval(x: Float, y: Float, z: Float): Float {
        return noise.eval(center.x + x * scale, center.y + y * scale, center.z + z * scale) * amplitude
    }
}

class ScaledNoise2d(val noise: Noise2d, val amplitude: Float = 1f, val scale: Float = 1f, val center: Vec2f = Vec2f.ZERO) : Noise2d {
    override fun eval(x: Float, y: Float): Float {
        return noise.eval(center.x + x * scale, center.y + y * scale) * amplitude
    }
}
