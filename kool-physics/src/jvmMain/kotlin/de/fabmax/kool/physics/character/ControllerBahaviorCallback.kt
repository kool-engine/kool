package de.fabmax.kool.physics.character

import de.fabmax.kool.physics.PhysicsWorld
import physx.character.JavaControllerBehaviorCallback
import physx.character.PxController
import physx.character.PxControllerBehaviorFlagEnum
import physx.character.PxObstacle
import physx.physics.PxActor
import physx.physics.PxShape

class ControllerBahaviorCallback(private val world: PhysicsWorld) : JavaControllerBehaviorCallback() {

    lateinit var controller: JvmCharacterController

    override fun getShapeBehaviorFlags(shape: PxShape, actor: PxActor): Int {
        controller.hitActorBehaviorCallback?.let { cb ->
            world.getActor(actor)?.let { rigidActor ->
                return when (cb.hitActorBehavior(rigidActor)) {
                    HitActorBehavior.DEFAULT -> 0
                    HitActorBehavior.SLIDE -> PxControllerBehaviorFlagEnum.eCCT_SLIDE
                    HitActorBehavior.RIDE -> PxControllerBehaviorFlagEnum.eCCT_CAN_RIDE_ON_OBJECT
                }
            }
        }
        return 0
    }

    override fun getControllerBehaviorFlags(controller: PxController?): Int {
        return 0
    }

    override fun getObstacleBehaviorFlags(obstacle: PxObstacle?): Int {
        return 0
    }
}