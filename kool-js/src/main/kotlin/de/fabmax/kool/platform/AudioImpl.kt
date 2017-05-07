package de.fabmax.kool.platform

import de.fabmax.kool.platform.js.Float32BufferImpl

/**
 * @author fabmax
 */

internal class AudioImpl(private val platform: PlatformImpl) : Audio() {
    override fun newAudioGenerator(generatorFun: AudioGenerator.(Float) -> Float): AudioGenerator {
        return AudioGeneratorImpl(generatorFun)
    }

    private inner class AudioGeneratorImpl(generatorFun: AudioGenerator.(Float) -> Float) : AudioGenerator() {
        override val sampleRate: Float = platform.audioCtx.sampleRate

        override var isPaused: Boolean = false
            set(value) {
                if (field != value) {
                    field = value
                    if (value) {
                        source.stop()
                    } else {
                        source.start()
                    }
                }
            }

        private val source: dynamic
        private val scriptNode: dynamic
        private var analyserNode: dynamic
        private var powerSpectrum: Float32BufferImpl = Platform.createFloat32Buffer(1) as Float32BufferImpl
        private val dt = 1f / sampleRate

        init {
            scriptNode = platform.audioCtx.createScriptProcessor(4096, 1, 1)
            val buffer = platform.audioCtx.createBuffer(1, scriptNode.bufferSize, sampleRate)

            scriptNode.onaudioprocess = { ev: dynamic ->
                val outputBuffer = ev.outputBuffer
                val data = outputBuffer.getChannelData(0)
                for (i in 0..outputBuffer.length - 1) {
                    data[i] = generatorFun(dt)
                }
            }

            analyserNode = null

            source = platform.audioCtx.createBufferSource()
            source.buffer = buffer
            source.loop = true
            source.connect(scriptNode)
            scriptNode.connect(platform.audioCtx.destination)
            source.start()
        }

        override fun stop() {
            scriptNode.disconnect()
            source.loop = false
            source.disconnect()
            source.stop()
        }

        override fun enableFftComputation(nSamples: Int) {
            if (nSamples <= 0) {
                analyserNode?.disconnect()
                analyserNode = null
            } else {
                if (analyserNode == null) {
                    analyserNode = platform.audioCtx.createAnalyser()
                    analyserNode.minDecibels = -90
                    analyserNode.maxDecibels = 0
                    analyserNode.smoothingTimeConstant = 0.5
                    scriptNode.connect(analyserNode)
                }
                analyserNode.fftSize = nSamples
                powerSpectrum = Platform.createFloat32Buffer(analyserNode.frequencyBinCount) as Float32BufferImpl
            }
        }

        override fun getPowerSpectrum(): Float32Buffer {
            analyserNode.getFloatFrequencyData(powerSpectrum.buffer)
            return powerSpectrum
        }
    }
}
