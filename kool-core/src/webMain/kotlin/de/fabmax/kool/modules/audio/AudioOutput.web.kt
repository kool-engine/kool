package de.fabmax.kool.modules.audio

import de.fabmax.kool.AudioBufferSourceNode
import de.fabmax.kool.AudioContext
import de.fabmax.kool.ScriptProcessorNode
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.Float32BufferImpl
import org.khronos.webgl.set

actual fun AudioOutput(bufSize: Int): AudioOutput = AudioOutputImpl(bufSize)

class AudioOutputImpl(override val bufSize: Int) : AudioOutput {
    private val audioCtx = AudioContext()

    override val sampleRate: Float get() = audioCtx.sampleRate

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

    override val mixer = MixNode()

    override var onBufferUpdate: (Double) -> Unit = { }

    private val source: AudioBufferSourceNode
    private val scriptNode: ScriptProcessorNode
    private var powerSpectrum: Float32BufferImpl = Float32Buffer(1) as Float32BufferImpl
    private val dt = 1f / sampleRate

    private var t = 0.0

    init {
        scriptNode = audioCtx.createScriptProcessor(bufSize, 1, 1)
        val buffer = audioCtx.createBuffer(1, scriptNode.bufferSize, sampleRate)

        scriptNode.onaudioprocess = { ev ->
            val outputBuffer = ev.outputBuffer
            val data = outputBuffer.getChannelData(0)
            val bufSamples: Int = outputBuffer.length

            onBufferUpdate(t)
            t += (dt * bufSamples)

            for (i in 0 until bufSamples) {
                data[i] = mixer.nextSample(dt)
            }
        }

        source = audioCtx.createBufferSource()
        source.buffer = buffer
        source.loop = true
        source.connect(scriptNode)
        scriptNode.connect(audioCtx.destination)
        source.start()
    }

    override fun close() {
        scriptNode.disconnect()
        source.loop = false
        source.disconnect()
        source.stop()
    }
}
