package de.fabmax.kool.modules.audio

/**
 * @author fabmax
 */
class Shaker(val bpm: Float) : SampleNode() {

    private val highPass = HighPassFilter(1.5f, this)

    override fun generate(dt: Float): Float {
        val pc2 = (t % (60f / bpm) / 8).toFloat()
        var pc1 = 230f
        if ((t + 0.5) % 0.5 > 0.25) {
            pc1 = 150f
        }
        return highPass.filter(perc(noise(), pc1, pc2, 0.95f)) * 0.1f
    }
}