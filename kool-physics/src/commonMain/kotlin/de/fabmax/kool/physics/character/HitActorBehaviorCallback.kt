package de.fabmax.kool.physics.character

import de.fabmax.kool.physics.RigidActor

interface HitActorBehaviorCallback {
    fun hitActorBehavior(actor: RigidActor): HitActorBehavior
}