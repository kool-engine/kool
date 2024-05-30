package de.fabmax.kool.physics.character

import de.fabmax.kool.physics.PhysicsWorldImpl
import physx.PxActorFromPointer
import physx.PxControllerBehaviorCallbackImpl
import physx.PxControllerBehaviorFlagEnum

class ControllerBahaviorCallback(private val world: PhysicsWorldImpl) {

    lateinit var controller: JsCharacterController

    val callback = PxControllerBehaviorCallbackImpl().apply {
        getShapeBehaviorFlags = { _, actor: Int ->
            controller.hitActorBehaviorCallback?.let { cb ->
                world.getActor(PxActorFromPointer(actor))?.let { rigidActor ->
                    when (cb.hitActorBehavior(rigidActor)) {
                        HitActorBehavior.DEFAULT -> 0
                        HitActorBehavior.SLIDE -> PxControllerBehaviorFlagEnum.eCCT_SLIDE
                        HitActorBehavior.RIDE -> PxControllerBehaviorFlagEnum.eCCT_CAN_RIDE_ON_OBJECT
                    }
                }
            } ?: 0
        }

        // not used
        getControllerBehaviorFlags = { _ -> 0 }
        getObstacleBehaviorFlags = { _ -> 0 }
    }
}