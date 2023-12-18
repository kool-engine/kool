package de.fabmax.kool.physics.character

import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.physics.PhysicsWorldImpl
import de.fabmax.kool.physics.toVec3d
import de.fabmax.kool.physics.toVec3f
import physx.character.PxControllerObstacleHit
import physx.character.PxControllerShapeHit
import physx.character.PxControllersHit
import physx.character.PxUserControllerHitReportImpl

class ControllerHitListener(private val world: PhysicsWorldImpl) : PxUserControllerHitReportImpl() {

    private val hitPosD = MutableVec3d()
    private val hitPos = MutableVec3f()
    private val hitNormal = MutableVec3f()

    lateinit var controller: JvmCharacterController

    override fun onShapeHit(hit: PxControllerShapeHit) {
        hit.worldNormal.toVec3f(hitNormal)
        hit.worldPos.toVec3d(hitPosD)
        hitPos.set(hitPosD.x.toFloat(), hitPosD.y.toFloat(), hitPosD.z.toFloat())
        world.getActor(hit.actor)?.let { controller.onHitActor(it, hitPos, hitNormal) }
    }

    override fun onControllerHit(hit: PxControllersHit) {
        // not used
    }

    override fun onObstacleHit(hit: PxControllerObstacleHit) {
        // not used
    }
}