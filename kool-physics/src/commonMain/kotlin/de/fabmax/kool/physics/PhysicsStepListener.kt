package de.fabmax.kool.physics

interface PhysicsStepListener {
    fun onPhysicsUpdate(timeStep: Float) { }
    fun onPhysicsCapture(simulationTime: Double) { }
    fun onPhysicsInterpolate(captureTimeA: Double, captureTimeB: Double, frameTime: Double, weightB: Float) { }
}