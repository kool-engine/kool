package de.fabmax.kool.physics.joints

data class LimitBehavior(
    val stiffness: Float,
    val damping: Float,
    val restitution: Float = 0f,
    val bounceThreshold: Float = 0f
) {

    companion object {
        val HARD_LIMIT = LimitBehavior(0f, 0f)
    }
}