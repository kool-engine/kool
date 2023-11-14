package de.fabmax.kool.modules.audio

import de.fabmax.kool.math.clamp
import de.fabmax.kool.util.Time
import org.w3c.dom.Audio

class AudioClipImpl(val assetPath: String) : AudioClip {

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
        val t = Time.precisionTime * 1000.0
        if (t - lastPlay > minIntervalMs) {
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
        val audioElement = Audio(assetPath)

        var volume: Float
            get() = audioElement.volume.toFloat()
            set(value) { audioElement.volume = value.toDouble().clamp(0.0, 1.0) }

        var currentTime: Float
            get() = audioElement.currentTime.toFloat()
            set(value) { audioElement.currentTime = value.toDouble().clamp(0.0, duration.toDouble()) }

        val duration: Float
            get() = audioElement.duration.toFloat()

        val isEnded: Boolean
            get() = audioElement.ended

        var isPaused: Boolean = false
            private set

        var isStarted = false

        var loop: Boolean
            get() = audioElement.loop
            set(value) { audioElement.loop = value}

        var clipState = ClipState.STOPPED
            private set
        var startTime = 0.0

        init {
            volume = this@AudioClipImpl.volume * this@AudioClipImpl.masterVolume
            audioElement.onended = {
                clipState = ClipState.STOPPED
                true.asDynamic()
            }
        }

        fun play() {
            audioElement.pause()
            currentTime = 0f
            clipState = ClipState.PLAYING
            isStarted = true
            audioElement.play()
        }

        fun stop() {
            isPaused = true
            audioElement.pause()
            clipState = ClipState.STOPPED
        }
    }
}