package de.fabmax.kool.audio

/**
 * @author fabmax
 */

class LowPassFilter(var coeff: Float, var input: SampleNode) : SampleNode() {
    override fun clock(t: Double) {
        val inSample = input.play()
        sample += (inSample - sample) / coeff
    }
}

class HighPassFilter(var coeff: Float, var input: SampleNode) : SampleNode() {
    override fun clock(t: Double) {
        val inSample = input.play()
        sample += inSample - sample * coeff
    }
}
