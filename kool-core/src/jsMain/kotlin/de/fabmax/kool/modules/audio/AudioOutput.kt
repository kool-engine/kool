package de.fabmax.kool.modules.audio

import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.Float32BufferImpl

/**
 * @author fabmax
 */

@Suppress("UnsafeCastFromDynamic")
actual class AudioOutput actual constructor(actual val bufSize: Int) {
    private val audioCtx = js("new AudioContext();")

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

    actual val mixer = MixNode()

    actual var onBufferUpdate: (Double) -> Unit = { }

    private val source: dynamic
    private val scriptNode: dynamic
    private var analyserNode: dynamic
    private var powerSpectrum: Float32BufferImpl = Float32Buffer(1) as Float32BufferImpl
    private val dt = 1f / sampleRate

    private var t = 0.0

    init {
        scriptNode = audioCtx.createScriptProcessor(bufSize, 1, 1)
        val buffer = audioCtx.createBuffer(1, scriptNode.bufferSize, sampleRate)

        scriptNode.onaudioprocess = { ev: dynamic ->
            val outputBuffer = ev.outputBuffer
            val data = outputBuffer.getChannelData(0)
            val bufSamples: Int = outputBuffer.length

            onBufferUpdate(t)
            t += (dt * bufSamples)

            for (i in 0 until bufSamples) {
                data[i] = mixer.nextSample(dt)
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

    actual fun close() {
        scriptNode.disconnect()
        source.loop = false
        source.disconnect()
        source.stop()
    }
}
