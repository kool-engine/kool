package de.fabmax.kool.physics.util

import de.fabmax.kool.math.*
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.physics.RigidBody
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.InterpolatableSimulation
import kotlin.math.atan2

class ActorTrackingCamRig(
    world: PhysicsWorld,
    var trackedActor: RigidBody? = null,
) : Node("ActorTrackingCamRig"), InterpolatableSimulation {
    var localFrontDir = Vec3f.NEG_Z_AXIS

    var positionStiffness = 20f
    var rotationStiffness = 10f

    private val trackPosDesired = MutableVec3f()
    private val trackDirDesired = MutableVec3f(localFrontDir)

    private val poseMat = MutableMat4f()
    private val trackPosCurrent = MutableVec3f()
    private val trackDirCurrent = MutableVec3f(localFrontDir)
    private val trackDelta = MutableVec3f()

    private val capturePosA = MutableVec3f()
    private val capturePosB = MutableVec3f()
    private val captureDirA = MutableVec3f()
    private val captureDirB = MutableVec3f()
    private val trackPosLerp = MutableVec3f()
    private val trackDirLerp = MutableVec3f()

    private val ppos = MutableVec3f()

    init {
        world.physicsStepListeners += this
        onRelease { world.physicsStepListeners -= this }
    }

    override fun simulateStep(timeStep: Float) {
        val actor = trackedActor ?: return

        poseMat.setIdentity().compose(actor.pose.position, actor.pose.rotation)
        poseMat.transform(Vec3f.ZERO, 1f, trackPosDesired)
        poseMat.transform(localFrontDir, 0f, trackDirDesired)

        trackDelta.set(trackPosDesired).subtract(trackPosCurrent).mul(positionStiffness * timeStep)
        trackPosCurrent.add(trackDelta)
        ppos.set(actor.pose.position)

        trackDelta.set(trackDirDesired).subtract(trackDirCurrent).mul(rotationStiffness * timeStep)
        trackDirCurrent.add(trackDelta)
    }

    override fun captureStepResults(simulationTime: Double) {
        capturePosA.set(capturePosB)
        captureDirA.set(captureDirB)
        capturePosB.set(trackPosCurrent)
        captureDirB.set(trackDirCurrent)
    }

    override fun interpolateSteps(simulationTimePrev: Double, simulationTimeNext: Double, simulationTimeLerp: Double, weightNext: Float) {
        capturePosA.mix(capturePosB, weightNext, trackPosLerp)
        captureDirA.mix(captureDirB, weightNext, trackDirLerp)

        transform.setIdentity()
        transform.translate(trackPosLerp)
        val ang = atan2(trackDirLerp.x, trackDirLerp.z).toDeg()
        transform.rotate(ang.deg, Vec3f.Y_AXIS)
    }
}