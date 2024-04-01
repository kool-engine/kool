package de.fabmax.kool.modules.audio

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configAndroid
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.logE
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.roundToInt

fun AudioClipImpl(inStream: InputStream, srcPath: String, context: Context): AudioClipImpl {
    val tempName = srcPath.replace('/', '_').replace('.', '_')
    val audioCacheDir = File(KoolSystem.configAndroid.appContext.cacheDir, "audioClips")
    if (!audioCacheDir.exists()) {
        audioCacheDir.mkdirs()
    }
    val tempFile = File(audioCacheDir, tempName)
    if (!tempFile.exists()) {
        FileOutputStream(tempFile).use { outStream -> inStream.copyTo(outStream) }
    }
    return AudioClipImpl(Uri.fromFile(tempFile), context)
}

class AudioClipImpl(private val clipUri: Uri, private val context: Context) : AudioClip {

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

    private val clipPool = mutableListOf(PlayerWrapper())
    private var latestClip = clipPool.first()
    private var lastPlay = Double.NEGATIVE_INFINITY

    private fun nextClip(): PlayerWrapper {
        for (i in clipPool.indices) {
            if (clipPool[i].clipState == ClipState.STOPPED) {
                return clipPool[i]
            }
        }
        if (clipPool.size < MAX_CLIP_POOL_SIZE) {
            val clip = PlayerWrapper()
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

    private val errorListener = MediaPlayer.OnErrorListener { mp, what, extra ->
        val cause =  when (extra) {
            MediaPlayer.MEDIA_ERROR_IO -> "MEDIA_ERROR_IO"
            MediaPlayer.MEDIA_ERROR_MALFORMED -> "MEDIA_ERROR_MALFORMED"
            MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> "MEDIA_ERROR_UNSUPPORTED"
            MediaPlayer.MEDIA_ERROR_TIMED_OUT -> "MEDIA_ERROR_TIMED_OUT"
            else -> when (what) {
                MediaPlayer.MEDIA_ERROR_UNKNOWN -> "MEDIA_ERROR_UNKNOWN"
                MediaPlayer.MEDIA_ERROR_SERVER_DIED -> "MEDIA_ERROR_SERVER_DIED"
                else -> "OTHER_ERROR"
            }
        }
        logE { "Error playing AudioClip ${clipUri}: $cause" }
        false
    }

    private inner class PlayerWrapper {
        var mediaPlayer: MediaPlayer = MediaPlayer.create(context, clipUri)

        var volume: Float = 1f
            set(value) {
                field = value
                mediaPlayer.setVolume(value, value)
            }

        var currentTime: Float
            get() = mediaPlayer.currentPosition / 1000f
            set(value) {
                mediaPlayer.seekTo((value * 1000f).roundToInt())
            }

        val duration: Float
            get() = mediaPlayer.duration / 1000f

        var loop: Boolean = false
            set(value) {
                field = value
                mediaPlayer.isLooping = value
            }

        var clipState = ClipState.STOPPED
            private set
        var startTime = 0.0

        init {
            mediaPlayer.setOnErrorListener(errorListener)
            volume = this@AudioClipImpl.volume * this@AudioClipImpl.masterVolume
        }

        fun play() {
            mediaPlayer.stop()
            mediaPlayer.prepare()
            currentTime = 0f
            clipState = ClipState.PLAYING
            mediaPlayer.start()
            startTime = Time.precisionTime
        }

        fun stop() {
            mediaPlayer.stop()
        }
    }
}