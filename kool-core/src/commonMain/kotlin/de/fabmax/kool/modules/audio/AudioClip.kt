package de.fabmax.kool.modules.audio

expect class AudioClip {

    var volume: Float
    var currentTime: Float
    val duration: Float
    val isEnded: Boolean
    var loop: Boolean
    var minIntervalMs: Float

    fun play()
    fun stop()

}