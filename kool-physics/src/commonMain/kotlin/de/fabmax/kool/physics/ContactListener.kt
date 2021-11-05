package de.fabmax.kool.physics

import de.fabmax.kool.math.Vec3f

interface ContactListener {
    fun onTouchFound(actorA: RigidActor, actorB: RigidActor, contactPoints: List<ContactPoint>?) { }
    fun onTouchLost(actorA: RigidActor, actorB: RigidActor) { }
}

class ContactPoint(val position: Vec3f, val normal: Vec3f, val impulse: Vec3f, val separation: Float)