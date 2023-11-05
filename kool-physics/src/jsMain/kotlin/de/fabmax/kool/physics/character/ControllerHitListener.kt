package de.fabmax.kool.physics.character

import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.physics.PhysicsWorldImpl
import de.fabmax.kool.physics.toVec3d
import de.fabmax.kool.physics.toVec3f
import physx.PxControllerShapeHitFromPointer
import physx.PxUserControllerHitReportImpl

class ControllerHitListener(val world: PhysicsWorldImpl) {

    private val hitPosD = MutableVec3d()
    private val hitPos = MutableVec3f()
    private val hitNormal = MutableVec3f()

    lateinit var controller: JsCharacterController

    val callback = PxUserControllerHitReportImpl().apply {
        onShapeHit = { h: Int ->
            val hit = PxControllerShapeHitFromPointer(h)
            hit.worldNormal.toVec3f(hitNormal)
            hit.worldPos.toVec3d(hitPosD)
            hitPos.set(hitPosD.x.toFloat(), hitPosD.y.toFloat(), hitPosD.z.toFloat())
            world.getActor(hit.actor)?.let { controller.onHitActor(it, hitPos, hitNormal) }
        }

        // not used
        onControllerHit = { _ -> }
        onObstacleHit = { _ -> }
    }
}