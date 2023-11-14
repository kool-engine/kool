package de.fabmax.kool.modules.audio

expect fun AudioOutput(bufSize: Int = 1024): AudioOutput

interface AudioOutput {

    val bufSize: Int
    val sampleRate: Float
    var isPaused: Boolean

    val mixer: MixNode

    var onBufferUpdate: (Double) -> Unit

    fun close()

}
