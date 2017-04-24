package de.fabmax.kool.audio

import de.fabmax.kool.platform.Math

/**
 * @author fabmax
 */
class HiHat(val bpm: Float) : SampleNode() {

    private val highPass = HighPassFilter(1.7f, this)

    override fun clock(t: Double) {
        val noise = Math.random().toFloat() * 2f - 1f
        val pc2 = (t % (60f / bpm)).toFloat()
        var pc1 = 266f
        if ((t / 2) % 0.5 > 0.25) {
            pc1 = 106f
        }
        sample = perc(noise, pc1, pc2)
        sample = highPass.clockAndPlay(t) * 0.2f
    }
}
