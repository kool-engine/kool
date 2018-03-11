package de.fabmax.kool.audio

import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.createFloat32Buffer

/**
 * @author fabmax
 */

actual class AudioGenerator actual constructor(generatorFun: AudioGenerator.(Float) -> Float) {

    private val impl = factory(this, generatorFun)

    actual val sampleRate
        get() = impl.sampleRate

    actual var isPaused
        get() = impl.isPaused
        set(value) { impl.isPaused = value }

    actual fun stop() = impl.stop()

    actual fun enableFftComputation(nSamples: Int) = impl.enableFftComputation(nSamples)

    actual fun getPowerSpectrum() = impl.getPowerSpectrum()

    interface Api {
        val sampleRate: Float
        var isPaused: Boolean
        fun stop()
        fun enableFftComputation(nSamples: Int)
        fun getPowerSpectrum(): Float32Buffer
    }

    private class NoImpl : Api {
        private var spectrum = createFloat32Buffer(1)

        override val sampleRate = 0f
        override var isPaused = false

        override fun stop() { }

        override fun enableFftComputation(nSamples: Int) {
            spectrum = createFloat32Buffer(nSamples / 2)
        }

        override fun getPowerSpectrum() = spectrum

    }

    companion object {
        var factory: (gen: AudioGenerator, generatorFun: AudioGenerator.(Float) -> Float) -> Api =
                { _, _ -> NoImpl() }
    }
}