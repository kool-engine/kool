package de.fabmax.kool.audio

import de.fabmax.kool.util.createFloat32Buffer

/**
 * @author fabmax
 */

actual class AudioGenerator actual constructor(generatorFun: AudioGenerator.(Float) -> Float) {

    private var spectrum = createFloat32Buffer(1)

    actual val sampleRate = 0f

    actual var isPaused = false

    actual fun stop() { }

    actual fun enableFftComputation(nSamples: Int) {
        spectrum = createFloat32Buffer(nSamples / 2)
    }

    actual fun getPowerSpectrum() = spectrum

}