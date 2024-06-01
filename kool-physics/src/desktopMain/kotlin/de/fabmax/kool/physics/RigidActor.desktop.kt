package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableQuatF
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.QuatF
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.spatial.BoundingBoxF
import de.fabmax.kool.physics.character.HitActorBehavior
import de.fabmax.kool.scene.Tags
import de.fabmax.kool.scene.TrsTransformF
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.checkIsNotReleased
import org.lwjgl.system.MemoryStack
import physx.extensions.PxRigidActorExt
import physx.physics.PxRigidActor
import physx.physics.PxShapeFlagEnum

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual typealias RigidActorHolder = PxRigidActor

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
    private val bufPosition = MutableVec3f()
    private val bufRotation = MutableQuatF()

    override var position: Vec3f
        get() = holder.globalPose.p.toVec3f(bufPosition)
        set(value) {
            val pose = holder.globalPose
            value.toPxVec3(pose.p)
            holder.globalPose = pose
            updateTransform(true)
        }

    override var rotation: QuatF
        get() = holder.globalPose.q.toQuatF(bufRotation)
        set(value) {
            val pose = holder.globalPose
            value.toPxQuat(pose.q)
            holder.globalPose = pose
            updateTransform(true)
        }

    override val worldBounds: BoundingBoxF
        get() = holder.worldBounds.toBoundingBox(bufBounds)

    override var isTrigger: Boolean = false
        set(value) {
            field = value
            MemoryStack.stackPush().use { mem ->
                val flags = if (isTrigger) TRIGGER_SHAPE_FLAGS else SIM_SHAPE_FLAGS
                val shapeFlags = mem.createPxShapeFlags(flags)
                shapes.forEach { it.holder?.flags = shapeFlags }
            }
        }

    override var isActive = true

    override val transform = TrsTransformF()

    override val onPhysicsUpdate = mutableListOf<(Float) -> Unit>()

    private val _shapes = mutableListOf<Shape>()
    override val shapes: List<Shape>
        get() = _shapes

    override val tags: Tags = Tags()

    private fun updateFilterData() {
        MemoryStack.stackPush().use { mem ->
            val sfd = simulationFilterData.toPxFilterData(mem.createPxFilterData())
            val qfd = queryFilterData.toPxFilterData(mem.createPxFilterData())
            shapes.forEach { shape ->
                shape.holder?.let {
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

            val pxShape = PxRigidActorExt.createExclusiveShape(holder, shape.geometry.holder, shape.material.pxMaterial, shapeFlags)
            pxShape.localPose = shape.localPose.toPxTransform(mem.createPxTransform())

            val simFd = shape.simFilterData ?: simulationFilterData
            pxShape.simulationFilterData = simFd.toPxFilterData(mem.createPxFilterData())
            val qryFd = shape.queryFilterData ?: queryFilterData
            pxShape.queryFilterData = qryFd.toPxFilterData(mem.createPxFilterData())
            shape.holder = pxShape
        }
    }

    override fun detachShape(shape: Shape) {
        _shapes -= shape
        shape.holder?.release()
        shape.holder = null
    }

    override fun release() {
        super.release()
        holder.release()
        _shapes.clear()
    }

    override fun onPhysicsUpdate(timeStep: Float) {
        checkIsNotReleased()
        updateTransform(false)
        super.onPhysicsUpdate(timeStep)
    }

    private fun updateTransform(force: Boolean) {
        if (isActive || force) {
            holder.globalPose.toTrsTransform(transform)
        }
    }

    companion object {
        val SIM_SHAPE_FLAGS: Int = PxShapeFlagEnum.eSIMULATION_SHAPE.value or PxShapeFlagEnum.eSCENE_QUERY_SHAPE.value
        val TRIGGER_SHAPE_FLAGS: Int = PxShapeFlagEnum.eTRIGGER_SHAPE.value
    }
}