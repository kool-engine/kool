package de.fabmax.kool.physics

interface TriggerListener {
    fun onActorEntered(trigger: RigidActor, actor: RigidActor) { }
    fun onActorExited(trigger: RigidActor, actor: RigidActor) { }
}