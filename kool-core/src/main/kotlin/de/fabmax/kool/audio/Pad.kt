package de.fabmax.kool.audio

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
    private val nicePass = NiceFilter(48000f, this)

    private val chords = arrayOf(
            intArrayOf( 7, 12, 17, 10),
            intArrayOf(10, 15, 19, 24)
    )

    override fun clock(t: Double) {
        val p = chords[(t / 4).toInt() % chords.size]

        val osc = osc1.clockAndPlay(t, SynthieUtils.note(p[0], 1)) + osc2.clockAndPlay(t, SynthieUtils.note(p[1], 2)) +
                  osc3.clockAndPlay(t, SynthieUtils.note(p[2], 1)) + osc4.clockAndPlay(t, SynthieUtils.note(p[3], 0)) +
                  SynthieUtils.noise(0.7f)

        val nice = nicePass.filter(lfo2.clockAndPlay(t) + 1100, 0.05f, osc / 33f)
        sample = ((lfo1.clockAndPlay(t) + 0.5f) * highPass.filter(nice)) * 0.3f
    }
}
