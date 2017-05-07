package de.fabmax.kool.audio

import de.fabmax.kool.platform.Math

/**
 * @author fabmax
 */

open class Oscillator(val shape: Wave, var frequency: Float = 440f) : SampleNode() {

    protected var pos = 0f

    var phaseShift = 0f
        set(value) {
            field = Math.clamp(value, 0f, 1f)
        }

    override fun generate(dt: Float): Float {
        pos += dt * frequency
        if (pos > 1) {
            pos -= 1
        }
        return shape[pos + phaseShift]
    }

    fun next(dt: Float, freq: Float): Float {
        frequency = freq
        return next(dt)
    }
}
