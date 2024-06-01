package de.fabmax.kool.physics.character

import de.fabmax.kool.physics.RigidActor

fun interface HitActorBehaviorCallback {
    fun hitActorBehavior(actor: RigidActor): HitActorBehavior
}