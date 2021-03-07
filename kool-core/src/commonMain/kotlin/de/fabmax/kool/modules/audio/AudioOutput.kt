package de.fabmax.kool.modules.audio

expect class AudioOutput(bufSize: Int = 1024) {

    val bufSize: Int
    val sampleRate: Float
    var isPaused: Boolean

    val mixer: MixNode

    var onBufferUpdate: (Double) -> Unit

    fun close()

}
