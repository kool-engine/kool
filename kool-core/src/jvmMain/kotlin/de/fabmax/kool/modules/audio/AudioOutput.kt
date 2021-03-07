package de.fabmax.kool.modules.audio

import de.fabmax.kool.math.clamp
import java.nio.ByteBuffer
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import kotlin.concurrent.thread

/**
 * @author fabmax
 */

actual class AudioOutput actual constructor(actual val bufSize: Int) {

    private val pauseLock = Object()
    private var isStopRequested = false

    actual val sampleRate = 48000f
    actual var isPaused: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                synchronized(pauseLock) {
                    pauseLock.notify()
                }
            }
        }

    actual val mixer = MixNode()

    actual var onBufferUpdate: (Double) -> Unit = { }

    init {
        thread(start = true, isDaemon = true) {
            val sampleRate = 48000f
            val format = AudioFormat(sampleRate, 16, 1, true, true)
            val line = AudioSystem.getSourceDataLine(format)
            line.open(format)
            line.start()

            val buf = ByteBuffer.allocate(bufSize * 2)
            val samples = buf.asShortBuffer()
            var sampleIdx = 0L

            var startTime = System.currentTimeMillis()
            val dt = 1f / sampleRate

            while (!isStopRequested) {
                onBufferUpdate(sampleIdx * dt.toDouble())

                samples.rewind()
                for (i in 0 until bufSize) {
                    samples.put((mixer.nextSample(dt).clamp(-1f, 1f) * 32767).toInt().toShort())
                    sampleIdx++
                }

                val data = buf.array()
                line.write(data, 0, data.size)

                // don't generate too many samples in advance...
                val playedMs = (sampleIdx / sampleRate) * 1000
                val sleepT = startTime + playedMs.toLong() - System.currentTimeMillis() - 100
                if (sleepT > 0) {
                    Thread.sleep(sleepT)
                }

                if (isPaused) {
                    val t = System.currentTimeMillis()
                    synchronized (pauseLock) {
                        pauseLock.wait()
                    }
                    startTime += System.currentTimeMillis() - t
                }
            }
        }
    }

    actual fun close() {
        isStopRequested = true
    }
}