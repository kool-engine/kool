package de.fabmax.kool.modules.audio

/**
 * @author fabmax
 */
class Melody : SampleNode() {

    private val lfo1 = Oscillator(Wave.SINE, 1f / 32f).apply { gain = 140f }
    private val lfo2 = Oscillator(Wave.SINE, 0.5f).apply { gain = 0.2f; phaseShift = 0.5f }

    private val osc1 = Oscillator(Wave.SAW).apply { gain = 0.7f }
    private val osc2 = Oscillator(Wave.SQUARE).apply { gain = 0.4f }
    private val osc3 = Oscillator(Wave.SINE).apply { gain = 0.8f }
    private val osc4 = Oscillator(Wave.SQUARE).apply { gain = 1.2f }

    private val moodFilter = MoodFilter(this)

    private val chords = arrayOf(
            7, 7, 7, 12, 10, 10, 10, 15,
            7, 7, 7, 15, 15, 17, 10, 29,
            7, 7, 7, 24, 10, 10, 10, 19,
            7, 7, 7, 15, 29, 24, 15, 10
    )

    override fun generate(dt: Float): Float {
        val f = note(chords[(t * 4).toInt() % chords.size], 0)
        val osc = osc1.next(dt, f) + osc2.next(dt, f / 2f) + osc3.next(dt, f / 2f) + osc4.next(dt, f * 3f)
        return moodFilter.filter(lfo1.next(dt) + 1050, lfo2.next(dt), perc(osc, 48f, t.toFloat() % 0.125f), dt) * 0.25f
    }
}