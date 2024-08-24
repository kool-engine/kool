package de.fabmax.kool.physics

fun interface PhysicsStepListener {
    fun onPhysicsStep(timeStep: Float)
}