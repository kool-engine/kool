package de.fabmax.kool.physics.character

import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.physics.toVec3d
import de.fabmax.kool.physics.toVec3f
import physx.JavaUserControllerHitReport
import physx.PxControllerObstacleHit
import physx.PxControllerShapeHit
import physx.PxControllersHit

class ControllerHitCallback(val world: PhysicsWorld) {

    private val hitPosD = MutableVec3d()
    private val hitPos = MutableVec3f()
    private val hitNormal = MutableVec3f()

    lateinit var controller: JsCharacterController

    val callback = JavaUserControllerHitReport().apply {
        onShapeHit = { h: PxControllerShapeHit ->
            val hit = Physics.TypeHelpers.getControllerShapeHitAt(h, 0)
            hit.worldNormal.toVec3f(hitNormal)
            hit.worldPos.toVec3d(hitPosD)
            hitPos.set(hitPosD.x.toFloat(), hitPosD.y.toFloat(), hitPosD.z.toFloat())
            world.getActor(hit.actor)?.let { controller.hitActor(it, hitPos, hitNormal) }
        }

        onControllerHit = { _: PxControllersHit ->
            // not used
        }

        onObstacleHit = { _: PxControllerObstacleHit ->
            // not used
        }
    }
}