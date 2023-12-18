package de.fabmax.kool.modules.audio

import de.fabmax.kool.math.clamp
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.logE
import java.io.ByteArrayInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.FloatControl
import javax.sound.sampled.LineEvent
import kotlin.math.log10
import kotlin.math.pow


class AudioClipImpl(private val audioData: ByteArray) : AudioClip {

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
            try {
                clip = AudioSystem.getClip()
                clip?.open(AudioSystem.getAudioInputStream(ByteArrayInputStream(audioData)))
                clip?.addLineListener { lineEvent ->
                    if (lineEvent.type == LineEvent.Type.STOP) {
                        clipState = ClipState.STOPPED
                    }
                }
                volume = this@AudioClipImpl.volume * this@AudioClipImpl.masterVolume
            } catch (e: Exception) {
                logE { "Failed playing audio clip: $e" }
            }
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
    }
}