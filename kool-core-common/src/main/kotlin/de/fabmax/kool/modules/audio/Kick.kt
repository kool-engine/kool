package de.fabmax.kool.modules.audio

/**
 * @author fabmax
 */
class Kick(val bpm: Float) : SampleNode() {
    private val osc1 = Oscillator(Wave.SINE).apply { frequency = 50f }
    private val osc2 = Oscillator(Wave.SAW).apply { frequency = 10f }

    private val lowPass = LowPassFilter(240f, this)

    override fun generate(dt: Float): Float {
        val osc = clip(clip(osc1.next(dt), 0.37f) * 2 + clip(osc2.next(dt), 0.07f) * 4, 0.6f)

        val s = perc(osc, 54f, (t % (60f / bpm)).toFloat()) * 2
        return lowPass.filter(s) + click((60f / bpm), t) * 0.055f
    }

    private fun click(x: Float, t: Double): Float {
        return 1f - 2 * (t % x).toFloat() / x
    }
}
