package de.fabmax.kool.modules.audio

open class WavNode(var wavFile: WavFile) : AudioNode() {
    var pos = 0.0
    var loop = true

    override fun nextSample(dt: Float): Float {
        pos += dt * speed
        val iSample = pos * wavFile.dwSamplesPerSec
        return if (iSample < wavFile.numSamples || loop) {
            val i = (iSample % wavFile.numSamples).toInt()
            wavFile.channels[0][i] * gain
        } else {
            0f
        }
    }
}