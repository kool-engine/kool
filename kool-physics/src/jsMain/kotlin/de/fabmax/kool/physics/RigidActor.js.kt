package de.fabmax.kool.physics

import de.fabmax.kool.math.MutablePoseF
import de.fabmax.kool.math.PoseF
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.spatial.BoundingBoxF
import de.fabmax.kool.physics.character.HitActorBehavior
import de.fabmax.kool.scene.Tags
import de.fabmax.kool.scene.TrsTransformF
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.BufferedList
import de.fabmax.kool.util.checkIsNotReleased
import physx.*

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
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

    private val bufBounds = BoundingBoxF()
    private val poseBuffer = MutablePoseF()

    override var pose: PoseF
        get() = poseBuffer
        set(value) {
            poseBuffer.set(value)
            val pose = holder.px.globalPose
            value.position.toPxVec3(pose.p)
            value.rotation.toPxQuat(pose.q)
            holder.px.globalPose = pose
            transform.setCompositionOf(value.position, value.rotation, Vec3f.ONES)
        }

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

    override val transform = TrsTransformF()

    override val onPhysicsUpdate = BufferedList<PhysicsStepListener>()

    private val _shapes = mutableListOf<Shape>()
    override val shapes: List<Shape>
        get() = _shapes

    override val tags: Tags = Tags()

    private fun updateFilterData() {
        MemoryStack.stackPush().use { mem ->
            val sfd = simulationFilterData.toPxFilterData(mem.createPxFilterData())
            val qfd = queryFilterData.toPxFilterData(mem.createPxFilterData())
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
        MemoryStack.stackPush().use { mem ->
            val flags = if (isTrigger) TRIGGER_SHAPE_FLAGS else SIM_SHAPE_FLAGS
            val shapeFlags = mem.createPxShapeFlags(flags)

            val pxGeom = shape.geometry.holder.px
            val pxShape = PxRigidActorExt.createExclusiveShape(holder.px, pxGeom, shape.material.pxMaterial, shapeFlags)
            pxShape.localPose = shape.localPose.toPxTransform(mem.createPxTransform())

            val simFd = shape.simFilterData ?: simulationFilterData
            pxShape.simulationFilterData = simFd.toPxFilterData(mem.createPxFilterData())
            val qryFd = shape.queryFilterData ?: queryFilterData
            pxShape.queryFilterData = qryFd.toPxFilterData(mem.createPxFilterData())
            shape.holder = ShapeHolder(pxShape)
        }
    }

    override fun detachShape(shape: Shape) {
        _shapes -= shape
        shape.holder?.px?.release()
        shape.holder = null
    }

    override fun release() {
        super.release()
        holder.px.release()
        _shapes.clear()
    }

    override fun onPhysicsUpdate(timeStep: Float) {
        checkIsNotReleased()
        updateTransform()
        super.onPhysicsUpdate(timeStep)
    }

    private fun updateTransform() {
        if (isActive) {
            holder.px.globalPose.toPoseF(poseBuffer)
            holder.px.globalPose.toTrsTransform(transform)
        }
    }

    companion object {
        val SIM_SHAPE_FLAGS: Int = PxShapeFlagEnum.eSIMULATION_SHAPE or PxShapeFlagEnum.eSCENE_QUERY_SHAPE
        val TRIGGER_SHAPE_FLAGS: Int = PxShapeFlagEnum.eTRIGGER_SHAPE
    }
}