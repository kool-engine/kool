package de.fabmax.kool.modules.audio

/**
 * @author fabmax
 */
class Pad : SampleNode() {

    private val lfo1 = Oscillator(Wave.SINE, 2f).apply { gain = 0.2f }
    private val lfo2 = Oscillator(Wave.SINE, 2f).apply { gain = 150f }

    private val osc1 = Oscillator(Wave.SAW).apply { gain = 5.1f }
    private val osc2 = Oscillator(Wave.SAW).apply { gain = 3.9f }
    private val osc3 = Oscillator(Wave.SAW).apply { gain = 4.0f }
    private val osc4 = Oscillator(Wave.SQUARE).apply { gain = 3.0f }

    private val highPass = HighPassFilter(0.5f, this)
    private val moodFilter = MoodFilter(this)

    private val chords = arrayOf(
            intArrayOf( 7, 12, 17, 10),
            intArrayOf(10, 15, 19, 24)
    )

    override fun generate(dt: Float): Float {
        val n = chords[(t / 4).toInt() % chords.size]
        val osc = osc1.next(dt, note(n[0], 1)) + osc2.next(dt, note(n[1], 2)) +
                  osc3.next(dt, note(n[2], 1)) + osc4.next(dt, note(n[3], 0)) + noise(0.7f)

        val s = moodFilter.filter(lfo2.next(dt) + 1100, 0.05f, osc / 33f, dt)
        return ((lfo1.next(dt) + 0.5f) * highPass.filter(s)) * 0.15f
    }
}
