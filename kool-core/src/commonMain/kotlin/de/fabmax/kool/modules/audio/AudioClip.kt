package de.fabmax.kool.modules.audio

interface AudioClip {

    var masterVolume: Float

    var volume: Float
    var currentTime: Float
    val duration: Float
    val isEnded: Boolean
    var loop: Boolean
    var minIntervalMs: Float

    fun play()
    fun stop()

}