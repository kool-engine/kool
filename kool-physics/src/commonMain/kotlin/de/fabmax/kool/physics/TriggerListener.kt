package de.fabmax.kool.physics

interface TriggerListener {
    fun onActorEntered(trigger: RigidActor, actor: RigidActor) { }
    fun onActorExited(trigger: RigidActor, actor: RigidActor) { }

    fun onShapeEntered(trigger: RigidActor, actor: RigidActor, shape: Shape) { }
    fun onShapeExited(trigger: RigidActor, actor: RigidActor, shape: Shape) { }
}