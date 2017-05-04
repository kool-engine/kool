package de.fabmax.kool.audio

import de.fabmax.kool.platform.Math

/**
 * @author fabmax
 */
object SynthieUtils {

    private val NOTE_TABLE = Array(15, { oct ->
        FloatArray(100, { n -> Math.pow(2.0, (n-20 - 33.0 + 12.0 * (oct-5)) / 12.0).toFloat() * 440f })
    })

    fun note(note: Int, octave: Int): Float {
        val o = Math.clamp(octave, -5, 9) + 5
        val n = Math.clamp(note, -20, 79) + 20
        return NOTE_TABLE[o][n]
    }

    fun noise(amplitude: Float = 1f): Float {
        return (Math.random().toFloat() * 2f - 1f) * amplitude
    }
}