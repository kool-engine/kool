package de.fabmax.kool.audio

import de.fabmax.kool.platform.Math

/**
 * @author fabmax
 */
class Shaker(val bpm: Float) : SampleNode() {

    private val highPass = HighPassFilter(1.5f, this)

    override fun clock(t: Double) {
        val noise = Math.random().toFloat() * 2f - 1f
        val pc2 = (t % (60f / bpm) / 8).toFloat()
        var pc1 = 230f
        if ((t + 0.5) % 0.5 > 0.25) {
            pc1 = 150f
        }
        sample = percB(noise, pc1, pc2)
        sample = highPass.clockAndPlay(t) * 0.2f
    }
}