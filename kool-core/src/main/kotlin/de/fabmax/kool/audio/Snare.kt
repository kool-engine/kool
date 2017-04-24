package de.fabmax.kool.audio

import de.fabmax.kool.platform.Math

/**
 * @author fabmax
 */
class Snare(val bpm: Float) : SampleNode() {

    private val osc = Oscillator(Wave.SQUARE, 175f).apply { gain = OSC_AMPL }

    private val lowPass = LowPassFilter(30f, this)

    override fun clock(t: Double) {
        val s = osc.clockAndPlay(t) + (Math.random().toFloat() * 2 - 1) * NOISE_AMPL
        val pc2 = ((t + 0.5) % (60f / bpm)).toFloat()
        var pc1 = 120f
        if ((t % 2) > 1) {
            pc1 = 105f
        }
        sample = perc(s, pc1, pc2) * PERC_AMPL
        sample = lowPass.clockAndPlay(t) * 5
    }

    companion object {
        private const val OSC_AMPL = 0.156f
        private const val NOISE_AMPL = 0.73f
        private const val PERC_AMPL = 0.6f
    }
}