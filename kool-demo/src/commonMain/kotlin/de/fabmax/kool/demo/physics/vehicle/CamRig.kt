package de.fabmax.kool.demo.physics.vehicle

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.toDeg
import de.fabmax.kool.scene.Group
import de.fabmax.kool.scene.Node
import kotlin.math.atan2

class CamRig : Group() {
    var trackedNode: Node? = null
    var localFrontDir = Vec3f.NEG_Z_AXIS

    var positionSmoothing = 0.75f
    var rotationSmoothing = 0.75f

    private val trackPosDesired = MutableVec3f()
    private val trackDirDesired = MutableVec3f(localFrontDir)

    private val trackPosCurrent = MutableVec3f()
    private val trackDirCurrent = MutableVec3f(localFrontDir)

    fun updateTracking() {
        trackedNode?.let {
            trackPosDesired.set(Vec3f.ZERO)
            it.toGlobalCoords(trackPosDesired)
            trackDirDesired.set(localFrontDir)
            it.toGlobalCoords(trackDirDesired, 0f)
        }

        trackPosCurrent.scale(positionSmoothing).add(trackPosDesired.scale(1f - positionSmoothing))
        trackDirCurrent.scale(rotationSmoothing).add(trackDirDesired.scale(1f - rotationSmoothing))

        setIdentity()
        translate(trackPosCurrent)
        val ang = atan2(trackDirCurrent.x, trackDirCurrent.z).toDeg()
        rotate(ang, Vec3f.Y_AXIS)
    }
}