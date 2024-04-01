package de.fabmax.kool.modules.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import de.fabmax.kool.math.clamp
import de.fabmax.kool.util.logE
import kotlin.concurrent.thread

actual fun AudioOutput(bufSize: Int): AudioOutput = AudioOutputImpl(bufSize)

class AudioOutputImpl(override val bufSize: Int) : AudioOutput {

    private val pauseLock = Object()
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

    override val mixer = MixNode()

    override var onBufferUpdate: (Double) -> Unit = { }

    init {
        thread(start = true, isDaemon = true) {
            val sampleRate = 44100
            val samples = ShortArray(bufSize / 2)
            var sampleIdx = 0L
            val dt = 1f / sampleRate

            val track = AudioTrack.Builder()
                .setAudioAttributes(AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                    .build())
                .setAudioFormat(AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build())
                .setBufferSizeInBytes(samples.size * 2)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()
            track.play()

            audioLoop@
            while (!isStopRequested) {
                onBufferUpdate(sampleIdx * dt.toDouble())
                for (i in samples.indices) {
                    samples[i] = (mixer.nextSample(dt).clamp(-1f, 1f) * 32767).toInt().toShort()
                    sampleIdx++
                }

                var writtenTotal = 0
                while (writtenTotal < samples.size) {
                    val remaining = samples.size - writtenTotal
                    when (val result = track.write(samples, writtenTotal, remaining, AudioTrack.WRITE_BLOCKING)) {
                        AudioTrack.ERROR_INVALID_OPERATION -> {
                            logE { "Error writing AudioTrack: ERROR_INVALID_OPERATION" }
                            break@audioLoop
                        }
                        AudioTrack.ERROR_BAD_VALUE -> {
                            logE { "Error writing AudioTrack: ERROR_BAD_VALUE" }
                            break@audioLoop
                        }
                        AudioTrack.ERROR_DEAD_OBJECT -> {
                            logE { "Error writing AudioTrack: ERROR_DEAD_OBJECT" }
                            break@audioLoop
                        }
                        AudioTrack.ERROR -> {
                            logE { "Error writing AudioTrack: unspecified ERROR" }
                            break@audioLoop
                        }
                        else -> writtenTotal += result
                    }
                }

                if (isPaused) {
                    track.pause()
                    synchronized (pauseLock) {
                        pauseLock.wait()
                    }
                    track.play()
                }
            }
            track.stop()
        }
    }

    override fun close() {
        isStopRequested = true
    }
}