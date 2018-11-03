package de.fabmax.kool.modules.audio

import de.fabmax.kool.KoolContext
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.Float32BufferImpl
import de.fabmax.kool.util.createFloat32Buffer

/**
 * @author fabmax
 */

actual class AudioGenerator actual constructor(ctx: KoolContext, generatorFun: AudioGenerator.(Float) -> Float) {
    private val audioCtx = js("new (window.AudioContext || window.webkitAudioContext)();")

    actual val sampleRate: Float = audioCtx.sampleRate

    actual var isPaused: Boolean = false
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
    private var powerSpectrum: Float32BufferImpl = createFloat32Buffer(1) as Float32BufferImpl
    private val dt = 1f / sampleRate

    init {
        scriptNode = audioCtx.createScriptProcessor(4096, 1, 1)
        val buffer = audioCtx.createBuffer(1, scriptNode.bufferSize, sampleRate)

        scriptNode.onaudioprocess = { ev: dynamic ->
            val outputBuffer = ev.outputBuffer
            val data = outputBuffer.getChannelData(0)
            for (i in 0 until outputBuffer.length) {
                data[i] = generatorFun(dt)
            }
        }

        analyserNode = null

        source = audioCtx.createBufferSource()
        source.buffer = buffer
        source.loop = true
        source.connect(scriptNode)
        scriptNode.connect(audioCtx.destination)
        source.start()
    }

    actual fun stop() {
        scriptNode.disconnect()
        source.loop = false
        source.disconnect()
        source.stop()
    }

    actual fun enableFftComputation(nSamples: Int) {
        if (nSamples <= 0) {
            analyserNode?.disconnect()
            analyserNode = null
        } else {
            if (analyserNode == null) {
                analyserNode = audioCtx.createAnalyser()
                analyserNode.minDecibels = -90
                analyserNode.maxDecibels = 0
                analyserNode.smoothingTimeConstant = 0.5
                scriptNode.connect(analyserNode)
            }
            analyserNode.fftSize = nSamples
            powerSpectrum = createFloat32Buffer(analyserNode.frequencyBinCount) as Float32BufferImpl
        }
    }

    actual fun getPowerSpectrum(): Float32Buffer {
        analyserNode.getFloatFrequencyData(powerSpectrum.buffer)
        return powerSpectrum
    }
}
