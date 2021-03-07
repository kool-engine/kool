package de.fabmax.kool.modules.audio

abstract class AudioNode {
    var speed = 1f
    var gain = 1f

    abstract fun nextSample(dt: Float): Float
}