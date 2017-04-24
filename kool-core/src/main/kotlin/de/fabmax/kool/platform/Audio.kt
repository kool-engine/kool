package de.fabmax.kool.platform

/**
 * @author fabmax
 */

abstract class Audio {

    /**
     * Creates a new [AudioGenerator] which plays audio from the given sample generator function. The generator
     * function is provided with the current playback time in seconds and is expected to compute one audio sample at a
     * time. The returned sample should be in the range -1f .. 1f.
     */
    abstract fun newAudioGenerator(generatorFun: AudioGenerator.(Double) -> Float): AudioGenerator

}

abstract class AudioGenerator {

    abstract val sampleRate: Float
    abstract var isPaused: Boolean

    abstract fun stop()

}
