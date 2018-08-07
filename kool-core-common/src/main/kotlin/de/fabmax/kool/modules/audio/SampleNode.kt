package de.fabmax.kool.modules.audio

import de.fabmax.kool.math.clamp
import de.fabmax.kool.math.randomF
import kotlin.math.max
import kotlin.math.pow

/**
 * @author fabmax
 */

abstract class SampleNode {

    var gain = 1f
    var t = 0.0
        protected set

    protected var sample = 0f

    fun current(): Float {
        return sample
    }

    fun next(dt: Float): Float {
        t += dt
        sample = generate(dt) * gain
        return sample
    }

    protected abstract fun generate(dt: Float): Float

    companion object {
        private val NOTE_TABLE = Array(15, { oct ->
            FloatArray(100, { n -> 2.0.pow((n-20 - 33.0 + 12.0 * (oct-5)) / 12.0).toFloat() * 440f })
        })

        fun clip(value: Float, clip: Float): Float {
            return value.clamp(-clip, clip)
        }

        fun noise(amplitude: Float = 1f): Float {
            return randomF(-amplitude, amplitude)
        }

        fun note(note: Int, octave: Int): Float {
            val o = octave.clamp(-5, 9) + 5
            val n = note.clamp(-20, 79) + 20
            return NOTE_TABLE[o][n]
        }

        fun perc(sample: Float, decay: Float, f: Float, c: Float = 0.889f): Float {
            return sample * max(0f, c - (f * decay) / ((f * decay) + 1))
        }
    }
}
