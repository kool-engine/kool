package de.fabmax.kool.physics.character

import de.fabmax.kool.physics.PhysicsWorld
import physx.JavaControllerBehaviorCallback
import physx.PxActor
import physx.PxControllerBehaviorFlagEnum

class ControllerBahaviorCallback(private val world: PhysicsWorld) {

    lateinit var controller: JsCharacterController

    val callback = JavaControllerBehaviorCallback().apply {
        getShapeBehaviorFlags = { _, actor: PxActor ->
            controller.hitActorBehaviorCallback?.let { cb ->
                world.getActor(actor)?.let { rigidActor ->
                    when (cb.hitActorBehavior(rigidActor)) {
                        HitActorBehavior.DEFAULT -> 0
                        HitActorBehavior.SLIDE -> PxControllerBehaviorFlagEnum.eCCT_SLIDE
                        HitActorBehavior.RIDE -> PxControllerBehaviorFlagEnum.eCCT_CAN_RIDE_ON_OBJECT
                    }
                }
            } ?: 0
        }

        // not used
        getControllerBehaviorFlags = { _ -> 0}
        getObstacleBehaviorFlags = { _ -> 0 }
    }
}