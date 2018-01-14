package de.fabmax.kool.scene.animation

class Animation(val duration: Float) {

    val channels = mutableListOf<NodeAnimation>()
    var weight = 0f

    fun apply(pos: Float, clearTransform: Boolean) {
        for (i in channels.indices) {
            channels[i].apply(pos * duration, weight, clearTransform)
        }
    }

}
