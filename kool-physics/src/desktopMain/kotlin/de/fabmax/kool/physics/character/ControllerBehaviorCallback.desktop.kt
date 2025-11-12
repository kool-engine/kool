package de.fabmax.kool.physics.character

import de.fabmax.kool.physics.PhysicsWorldImpl
import physx.character.PxController
import physx.character.PxControllerBehaviorCallbackImpl
import physx.character.PxControllerBehaviorFlagEnum
import physx.character.PxObstacle
import physx.physics.PxActor
import physx.physics.PxShape

class ControllerBehaviorCallback(private val world: PhysicsWorldImpl) {
    lateinit var controller: CharacterControllerImpl

    val callback = object : PxControllerBehaviorCallbackImpl() {
        override fun getShapeBehaviorFlags(shape: PxShape, actor: PxActor): Int {
            return controller.hitActorBehaviorCallback?.let { cb ->
                world.getActor(actor)?.let { rigidActor ->
                    when (cb.hitActorBehavior(rigidActor)) {
                        HitActorBehavior.DEFAULT -> 0
                        HitActorBehavior.SLIDE -> PxControllerBehaviorFlagEnum.eCCT_SLIDE.value
                        HitActorBehavior.RIDE -> PxControllerBehaviorFlagEnum.eCCT_CAN_RIDE_ON_OBJECT.value
                    }
                }
            } ?: 0
        }

        override fun getControllerBehaviorFlags(controller: PxController?): Int = 0
        override fun getObstacleBehaviorFlags(obstacle: PxObstacle?): Int = 0
    }
}