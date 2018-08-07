package de.fabmax.kool.modules.audio

/**
 * @author fabmax
 */
class Snare(val bpm: Float) : SampleNode() {

    private val osc = Oscillator(Wave.SQUARE, 175f).apply { gain = 0.156f }

    private val lowPass = LowPassFilter(30f, this)

    override fun generate(dt: Float): Float {
        val s = osc.next(dt) + noise(0.73f)
        val pc2 = ((t + 0.5) % (60f / bpm)).toFloat()
        var pc1 = 120f
        if ((t % 2) > 1) {
            pc1 = 105f
        }
        return lowPass.filter(perc(s, pc1, pc2) * 0.6f) * 5
    }
}