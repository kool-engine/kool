package de.fabmax.kool.audio

import de.fabmax.kool.platform.Math

/**
 * @author fabmax
 */

open class Oscillator(val shape: Wave, var frequency: Float = 440f) : SampleNode() {

    var phaseShift = 0f
        set(value) {
            field = Math.clamp(value, 0f, 1f)
        }

    override fun clock(t: Double) {
        sample = shape[t * frequency + phaseShift]
    }

    fun clockAndPlay(t: Double, freq: Float): Float {
        frequency = freq
        return super.clockAndPlay(t)
    }
}
