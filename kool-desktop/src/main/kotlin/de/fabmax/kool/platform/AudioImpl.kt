package de.fabmax.kool.platform

import de.fabmax.kool.audio.Shaker
import java.nio.ByteBuffer
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem

/**
 * @author fabmax
 */

internal class AudioImpl : Audio() {

    override fun newAudioGenerator(generatorFun: AudioGenerator.(Double) -> Float): AudioGenerator {
        return AudioGeneratorImpl(generatorFun)
    }

    private class AudioGeneratorImpl(generatorFun: AudioGenerator.(Double) -> Float) : AudioGenerator() {
        private val pauseLock = java.lang.Object()
        private val generatorThread: Thread
        private var isStopRequested = false

        override val sampleRate = 48000f
        override var isPaused: Boolean = false
            set(value) {
                if (field != value) {
                    field = value
                    synchronized(pauseLock) {
                        pauseLock.notify()
                    }
                }
            }

        init {
            generatorThread = Thread {
                val SAMPLE_RATE = 48000f
                val format = AudioFormat(SAMPLE_RATE, 16, 1, true, true)
                val line = AudioSystem.getSourceDataLine(format)
                line.open(format)
                line.start()

                val numSamples = 256
                val buf = ByteBuffer.allocate(numSamples * 2)
                val samples = buf.asShortBuffer()
                var sampleIdx = 0

                while (!isStopRequested) {
                    samples.rewind()
                    for (i in 0..numSamples-1) {
                        val t = (sampleIdx++ / SAMPLE_RATE.toDouble())
                        samples.put((generatorFun(t) * 32767).toShort())
                    }
                    val data = buf.array()
                    line.write(data, 0, data.size)

                    if (isPaused) {
                        synchronized (pauseLock) {
                            pauseLock.wait()
                        }
                    }
                }
            }
            generatorThread.isDaemon = true
            generatorThread.start()
        }

        override fun stop() {
            isStopRequested = true
        }

    }
}
