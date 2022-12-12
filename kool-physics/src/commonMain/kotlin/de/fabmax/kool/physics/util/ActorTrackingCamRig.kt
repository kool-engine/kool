package de.fabmax.kool.physics.util

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.toDeg
import de.fabmax.kool.physics.RigidBody
import de.fabmax.kool.scene.Group
import kotlin.math.atan2

class ActorTrackingCamRig : Group() {
    var trackedActor: RigidBody? = null
        set(value) {
            field?.let {
                it.onPhysicsUpdate -= updateTracking
            }
            value?.let {
                it.onPhysicsUpdate += updateTracking
            }
            field = value
        }

    var localFrontDir = Vec3f.NEG_Z_AXIS

    var positionStiffness = 20f
    var rotationStiffness = 10f

    private val trackPosDesired = MutableVec3f()
    private val trackDirDesired = MutableVec3f(localFrontDir)

    private val trackPosCurrent = MutableVec3f()
    private val trackDirCurrent = MutableVec3f(localFrontDir)
    private val trackDelta = MutableVec3f()

    private val updateTracking: (Float) -> Unit = { timeStep ->
        trackedActor?.let {
            trackPosDesired.set(Vec3f.ZERO)
            it.transform.transform(trackPosDesired)
            trackDirDesired.set(localFrontDir)
            it.transform.transform(trackDirDesired, 0f)
        }

        trackDelta.set(trackPosDesired).subtract(trackPosCurrent).scale(positionStiffness * timeStep)
        trackPosCurrent.add(trackDelta)
        trackDelta.set(trackDirDesired).subtract(trackDirCurrent).scale(rotationStiffness * timeStep)
        trackDirCurrent.add(trackDelta)

        setIdentity()
        translate(trackPosCurrent)
        val ang = atan2(trackDirCurrent.x, trackDirCurrent.z).toDeg()
        rotate(ang, Vec3f.Y_AXIS)
    }
}