package de.fabmax.kool.modules.audio

import de.fabmax.kool.math.clamp
import de.fabmax.kool.util.*
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import org.lwjgl.stb.STBVorbis
import org.lwjgl.stb.STBVorbisInfo
import org.lwjgl.system.MemoryUtil
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import javax.sound.sampled.*
import kotlin.math.log10
import kotlin.math.pow

class AudioClipImpl(private val audioData: ByteArray, private val format: String) : AudioClip {

    override var masterVolume = 1f
        set(value) {
            field = value
            latestClip.volume = volume * value
        }

    override var volume = 1f
        set(value) {
            field = value
            latestClip.volume = value * masterVolume
        }

    override var currentTime: Float
        get() = latestClip.currentTime
        set(value) {
            latestClip.currentTime = value
        }

    override val duration: Float
        get() = latestClip.duration

    override val isEnded: Boolean
        get() = latestClip.clipState == ClipState.STOPPED

    override var loop: Boolean
        get() = latestClip.loop
        set(value) {
            latestClip.loop = value
        }

    override var minIntervalMs: Float = MIN_PLAY_INTERVAL_MS

    private val clipPool = mutableListOf(ClipWrapper())
    private var latestClip = clipPool.first()
    private var lastPlay = Double.NEGATIVE_INFINITY

    private fun nextClip(): ClipWrapper {
        for (i in clipPool.indices) {
            if (clipPool[i].clipState == ClipState.STOPPED) {
                return clipPool[i]
            }
        }
        if (clipPool.size < MAX_CLIP_POOL_SIZE) {
            val clip = ClipWrapper()
            clipPool += clip
            return clip
        }
        return clipPool.minByOrNull { it.startTime }!!
    }

    override fun play() {
        val t = Time.precisionTime
        if (t - lastPlay > minIntervalMs / 1000.0) {
            lastPlay = t
            latestClip = nextClip().apply { play() }
        }
    }

    override fun stop() {
        latestClip.stop()
    }

    companion object {
        const val MIN_PLAY_INTERVAL_MS = 150f
        const val MAX_CLIP_POOL_SIZE = 5

        private val VORBIS_LOCK = SynchronizedObject()
    }

    private enum class ClipState {
        STOPPED,
        PLAYING
    }

    private inner class ClipWrapper {
        var clip: Clip? = null

        var volume: Float
            get() {
                val gainControl = clip?.getControl(FloatControl.Type.MASTER_GAIN)
                return if (gainControl != null) {
                    gainControl as FloatControl
                    10f.pow((gainControl.value / 20f))
                } else {
                    1f
                }
            }
            set(value) {
                val gainControl = clip?.getControl(FloatControl.Type.MASTER_GAIN)
                if (gainControl != null) {
                    gainControl as FloatControl
                    gainControl.value = (20f * log10(value)).clamp(-79.9f, 0f)
                }
            }

        var currentTime: Float
            get() = ((clip?.microsecondPosition ?: 0) / 1e6).toFloat()
            set(value) {
                clip?.microsecondPosition = (value * 1e6).toLong()
            }

        val duration: Float
            get() = ((clip?.microsecondLength ?: 0) / 1e6).toFloat()

        var loop: Boolean = false
            set(value) {
                field = value
                if (value) {
                    clip?.loop(Clip.LOOP_CONTINUOUSLY)
                }
            }

        var clipState = ClipState.STOPPED
            private set
        var startTime = 0.0

        init {
            clip = AudioSystem.getClip()
            clip?.open(loadAudio(audioData, format))
            clip?.addLineListener { lineEvent ->
                if (lineEvent.type == LineEvent.Type.STOP) {
                    clipState = ClipState.STOPPED
                }
            }
            volume = this@AudioClipImpl.volume * this@AudioClipImpl.masterVolume
        }

        fun play() {
            clip?.stop()
            currentTime = 0f
            clipState = ClipState.PLAYING
            clip?.start()
            startTime = Time.precisionTime
        }

        fun stop() {
            clip?.stop()
        }

        private fun loadAudio(data: ByteArray, format: String): AudioInputStream {
            return try {
                AudioSystem.getAudioInputStream(ByteArrayInputStream(audioData))
            } catch (e: Exception) {
                // no jvm builtin codec
                when (format) {
                    "ogg" -> {
                        synchronized(VORBIS_LOCK) {
                            loadVorbis(data)
                        }
                    }
                    else -> error("Failed loading audio clip, format: $format (try .wav or .ogg)")
                }
            }
        }

        private fun loadVorbis(data: ByteArray): AudioInputStream {
            return scopedMem {
                (data.toBuffer() as Uint8BufferImpl).useRaw { raw ->
                    val error = callocInt(1)
                    val handle = STBVorbis.stb_vorbis_open_memory(raw, error, null)
                    check(handle != 0L) { "Failed opening ogg vorbis file" }
                    val info = STBVorbisInfo.calloc(this)
                    STBVorbis.stb_vorbis_get_info(handle, info)
                    val channels = info.channels()
                    val sampleRate = info.sample_rate()
                    val numSamples = STBVorbis.stb_vorbis_stream_length_in_samples(handle)
                    check(channels > 0 && numSamples > 0 && sampleRate > 0) { "Invalid vorbis file info: channels: $channels, samples: $numSamples, sampleRate: $sampleRate" }

                    val samples = MemoryUtil.memAllocShort(numSamples * channels)
                    val decodedSamples = STBVorbis.stb_vorbis_get_samples_short_interleaved(handle, channels, samples)
                    if (decodedSamples != numSamples) {
                        logW { "Unexpected number of samples decoded: expected $numSamples, got $decodedSamples" }
                    }
                    STBVorbis.stb_vorbis_close(handle)

                    val pcmBytes = ByteArrayOutputStream()
                    pcmBytes.use {
                        // write a basic pcm header, which can be read by Java's AudioSystem
                        it.write("RIFF".toByteArray())
                        it.writeInt(samples.capacity() + 36)
                        it.write("WAVEfmt ".toByteArray())
                        // remaining header length
                        it.writeInt(16)
                        // format code: PCM
                        it.writeShort(1)
                        it.writeShort(channels)
                        it.writeInt(sampleRate)
                        // bytes/s -> sample-rate * block align
                        it.writeInt(sampleRate * channels * 2)
                        // block align: channels * bytes/sample
                        it.writeShort(channels * 2)
                        // bits / sample
                        it.writeShort(16)
                        it.write("data".toByteArray())
                        it.writeInt(samples.capacity() * 2)

                        for (i in 0 until samples.capacity()) {
                            it.writeShort(samples.get(i).toInt() and 0xffff)
                        }
                    }
                    MemoryUtil.memFree(samples)
                    AudioSystem.getAudioInputStream(ByteArrayInputStream(pcmBytes.toByteArray()))
                }
            }
        }

        private fun OutputStream.writeInt(i: Int) {
            write(i shr 0)
            write(i shr 8)
            write(i shr 16)
            write(i shr 24)
        }

        private fun OutputStream.writeShort(s: Int) {
            write(s shr 0)
            write(s shr 8)
        }
    }
}