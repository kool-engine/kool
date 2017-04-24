package de.fabmax.kool.audio

import de.fabmax.kool.platform.Math

/**
 * @author fabmax
 */
class Kick(val bpm: Float) : SampleNode() {
    private val osc1 = Oscillator(Wave.SINE).apply { frequency = 49f }
    private val osc2 = Oscillator(Wave.SAW).apply { frequency = 49f / 5f }

    private val lowPass = LowPassFilter(240f, this)

    override fun clock(t: Double) {
        val osc1Clipped = Math.clamp(osc1.clockAndPlay(t), -CLIP1, CLIP1)
        val osc2Clipped = Math.clamp(osc2.clockAndPlay(t), -CLIP2, CLIP2)
        val osc = Math.clamp(osc1Clipped + osc2Clipped, -CLIP3, CLIP3)
        sample = perc(osc, PERC_C1, (t % (60f / bpm)).toFloat()) * PERC_AMPL
        sample = lowPass.clockAndPlay(t) + click((60f / bpm), t) * CLICK_AMPL
    }

    private fun click(x: Float, t: Double): Float {
        return 1f - 2 * (t % x).toFloat() / x
    }

    companion object {
        private const val CLIP1 = 0.37f
        private const val CLIP2 = 0.07f
        private const val CLIP3 = 0.6f

        private const val PERC_C1 = 54f

        private const val CLICK_AMPL = 0.054f
        private const val PERC_AMPL = 4f
    }
}
