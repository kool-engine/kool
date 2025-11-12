package de.fabmax.kool.physics

import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.BoundingBoxF
import de.fabmax.kool.physics.character.HitActorBehavior
import de.fabmax.kool.scene.Tags
import de.fabmax.kool.scene.TrsTransformF
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.checkIsNotReleased
import physxandroid.extensions.PxRigidActorExt
import physxandroid.physics.PxRigidActor
import physxandroid.physics.PxShapeFlagEnum

// GENERATED CODE BELOW:
// Transformed from desktop source

actual class RigidActorHolder(val px: PxRigidActor)

abstract class RigidActorImpl : BaseReleasable(), RigidActor {
    init { PhysicsImpl.checkIsLoaded() }

    override var simulationFilterData = FilterData { setCollisionGroup(0); setCollidesWithEverything() }
        set(value) {
            field = value
            updateFilterData()
        }

    override var queryFilterData = FilterData()
        set(value) {
            field = value
            updateFilterData()
        }

    override var characterControllerHitBehavior: HitActorBehavior = HitActorBehavior.SLIDE

    private val simPose = MutablePoseF()
    private val poseA = CapturedPose()
    private val poseB = CapturedPose()
    private val bufBounds = BoundingBoxF()

    override var pose: PoseF
        get() = simPose
        set(value) {
            simPose.set(value)
            poseA.pose.set(value)
            poseB.pose.set(value)
            val pose = holder.px.globalPose
            simPose.toPxTransform(pose)
            holder.px.globalPose = pose
            transform.setCompositionOf(value.position, value.rotation, Vec3f.ONES)
        }

    private val lerpPos = MutableVec3f()
    private val lerpRot = MutableQuatF()

    override val worldBounds: BoundingBoxF
        get() = holder.px.worldBounds.toBoundingBox(bufBounds)

    override var isTrigger: Boolean = false
        set(value) {
            field = value
            MemoryStack.stackPush().use { mem ->
                val flags = if (isTrigger) TRIGGER_SHAPE_FLAGS else SIM_SHAPE_FLAGS
                val shapeFlags = mem.createPxShapeFlags(flags)
                shapes.forEach { it.holder?.px?.flags = shapeFlags }
            }
        }

    override var isActive = true
    override var isAttachedToSimulation: Boolean = false
        internal set
    override val transform = TrsTransformF()

    private val _shapes = mutableListOf<Shape>()
    override val shapes: List<Shape>
        get() = _shapes

    override val tags: Tags = Tags()

    private fun updateFilterData() {
        memStack {
            val sfd = simulationFilterData.toPxFilterData(createPxFilterData())
            val qfd = queryFilterData.toPxFilterData(createPxFilterData())
            shapes.forEach { shape ->
                shape.holder?.px?.let {
                    it.simulationFilterData = sfd
                    it.queryFilterData = qfd
                }
            }
        }
    }

    override fun attachShape(shape: Shape) {
        _shapes += shape
        memStack {
            val flags = if (isTrigger) TRIGGER_SHAPE_FLAGS else SIM_SHAPE_FLAGS
            val shapeFlags = createPxShapeFlags(flags)

            val pxShape = PxRigidActorExt.createExclusiveShape(holder.px, shape.geometry.holder.px, shape.material.pxMaterial, shapeFlags)
            pxShape.localPose = shape.localPose.toPxTransform(createPxTransform())

            val simFd = shape.simFilterData ?: simulationFilterData
            pxShape.simulationFilterData = simFd.toPxFilterData(createPxFilterData())
            val qryFd = shape.queryFilterData ?: queryFilterData
            pxShape.queryFilterData = qryFd.toPxFilterData(createPxFilterData())
            shape.holder = ShapeHolder(pxShape)
        }
    }

    override fun detachShape(shape: Shape) {
        _shapes -= shape
        shape.holder?.px?.release()
        shape.holder = null
    }

    override fun doRelease() {
        holder.px.release()
        _shapes.clear()
    }

    override fun syncSimulationData() {
        holder.px.globalPose.toPoseF(simPose)
    }

    override fun capture(simulationTime: Double) {
        checkIsNotReleased()
        poseA.set(poseB)
        poseB.pose.set(simPose)
        poseB.time = simulationTime
    }

    override fun interpolateTransform(captureTimeA: Double, captureTimeB: Double, frameTime: Double, weightB: Float) {
        if (!isActive) {
            return
        }
        poseA.pose.position.mix(poseB.pose.position, weightB, lerpPos)
        poseA.pose.rotation.mix(poseB.pose.rotation, weightB, lerpRot)
        transform.setCompositionOf(lerpPos, lerpRot)
    }

    private class CapturedPose {
        var time: Double = 0.0
        val pose = MutablePoseF()

        fun set(other: CapturedPose) {
            time = other.time
            pose.set(other.pose)
        }
    }

    companion object {
        val SIM_SHAPE_FLAGS: Int = PxShapeFlagEnum.eSIMULATION_SHAPE.value or PxShapeFlagEnum.eSCENE_QUERY_SHAPE.value
        val TRIGGER_SHAPE_FLAGS: Int = PxShapeFlagEnum.eTRIGGER_SHAPE.value
    }
}