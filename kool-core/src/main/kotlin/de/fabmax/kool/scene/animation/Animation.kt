package de.fabmax.kool.scene.animation

class Animation(val duration: Float) {

    val channels = mutableListOf<NodeAnimation>()

    fun apply(time: Double) {
        val pos = (time % duration).toFloat()
        for (i in channels.indices) {
            channels[i].apply(pos)
        }
    }

}
