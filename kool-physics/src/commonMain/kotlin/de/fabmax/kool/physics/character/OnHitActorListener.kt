package de.fabmax.kool.physics.character

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.RigidActor

fun interface OnHitActorListener {
    fun onHitActor(actor: RigidActor, hitWorldPos: Vec3f, hitWorldNormal: Vec3f)
}