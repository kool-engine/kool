package de.fabmax.kool.modules.audio

import kotlin.math.PI
import kotlin.math.sin

/**
 * @author fabmax
 */

class LowPassFilter(var coeff: Float, var input: SampleNode) : SampleNode() {
    override fun generate(dt: Float): Float {
        return filter(input.next(dt))
    }

    fun filter(input: Float): Float {
        sample += (input - sample) / coeff
        return sample
    }
}

class HighPassFilter(var coeff: Float, var input: SampleNode) : SampleNode() {
    override fun generate(dt: Float): Float {
        return filter(input.next(dt))
    }

    fun filter(input: Float): Float {
        sample += input - sample * coeff
        return sample
    }
}

class MoodFilter(var input: SampleNode) : SampleNode() {

    var cutoff = 1000f
    var res = 0.05f

    private var y1 = 0f
    private var y2 = 0f
    private var y3 = 0f
    private var y4 = 0f
    private var oldx = 0f
    private var oldy1 = 0f
    private var oldy2 = 0f
    private var oldy3 = 0f

    override fun generate(dt: Float): Float {
        return filter(input.current(), dt)
    }

    fun filter(cutoff: Float, res: Float, input: Float, dt: Float): Float {
        this.cutoff = cutoff
        this.res = res
        return filter(input, dt)
    }

    fun filter(input: Float, dt: Float): Float {
        val cut = 2 * cutoff * dt
        val p = cut * (C1 - C2 * cut)
        val k = 2 * sin(cut * PI * 0.5f).toFloat() - 1
        val t1 = (1 - p) * C3
        val t2 = 12 + t1 * t1
        val r = res * (t2 + 6 * t1) / (t2 - 6 * t1)
        val x = input - r * y4

        y1 =  x * p + oldx  * p - k * y1
        y2 = y1 * p + oldy1 * p - k * y2
        y3 = y2 * p + oldy2 * p - k * y3
        y4 = y3 * p + oldy3 * p - k * y4

        y4 -= (y4 * y4 * y4) / 6

        oldx = x
        oldy1 = y1
        oldy2 = y2
        oldy3 = y3

        return y4
    }

    companion object {
        private const val C1 = 1.8f
        private const val C2 = 0.8f
        private const val C3 = 1.386f
    }
}
