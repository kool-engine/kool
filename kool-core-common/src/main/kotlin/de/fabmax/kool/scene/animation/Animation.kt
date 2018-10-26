package de.fabmax.kool.scene.animation

class Animation(val duration: Float) {

    val channels = mutableListOf<NodeAnimation>()
    var weight = 0f

    fun apply(pos: Float, clearTransform: Boolean) {
        for (i in channels.indices) {
            channels[i].apply(pos * duration, weight, clearTransform)
        }
    }

    fun copy(withNodes: Map<String, AnimatedNode>): Animation {
        val copy = Animation(duration)
        channels.forEach { copy.channels += it.copy(withNodes[it.name]!!) }
        return copy
    }
}
