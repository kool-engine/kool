package de.fabmax.kool.audio

import de.fabmax.kool.platform.Math

/**
 * @author fabmax
 */

abstract class SampleNode {

    var gain = 1f
    protected var sample = 0f

    abstract fun clock(t: Double)

    open fun play(): Float {
        return sample * gain
    }

    fun clockAndPlay(t: Double): Float {
        clock(t)
        return play()
    }

    companion object {
        fun perc(sample: Float, decay: Float, f: Float): Float {
            return sample * Math.max(0f, 0.889f - (f * decay) / ((f * decay) + 1))
        }

        fun percB(sample: Float, decay: Float, f: Float): Float {
            return sample * Math.max(0f, 0.95f - (f * decay) / ((f * decay) + 1))
        }
    }
}
