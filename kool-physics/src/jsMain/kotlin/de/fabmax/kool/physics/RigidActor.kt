package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableQuatF
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.QuatF
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.spatial.BoundingBox
import physx.*

actual abstract class RigidActor : CommonRigidActor() {

    init {
        Physics.checkIsLoaded()
    }

    abstract val pxRigidActor: PxRigidActor

    actual var simulationFilterData = FilterData { setCollisionGroup(0); setCollidesWithEverything() }
        set(value) {
            field = value
            updateFilterData()
        }

    actual var queryFilterData = FilterData()
        set(value) {
            field = value
            updateFilterData()
        }

    private val bufBounds = BoundingBox()
    private val bufPosition = MutableVec3f()
    private val bufRotation = MutableQuatF()

    actual override var position: Vec3f
        get() = pxRigidActor.globalPose.p.toVec3f(bufPosition)
        set(value) {
            val pose = pxRigidActor.globalPose
            value.toPxVec3(pose.p)
            pxRigidActor.globalPose = pose
            updateTransform()
        }

    actual override var rotation: QuatF
        get() = pxRigidActor.globalPose.q.toQuatF(bufRotation)
        set(value) {
            val pose = pxRigidActor.globalPose
            value.toPxQuat(pose.q)
            pxRigidActor.globalPose = pose
            updateTransform()
        }

    actual override var isTrigger: Boolean = false
        set(value) {
            field = value
            MemoryStack.stackPush().use { mem ->
                val flags = if (isTrigger) TRIGGER_SHAPE_FLAGS else SIM_SHAPE_FLAGS
                val shapeFlags = mem.createPxShapeFlags(flags)
                shapes.forEach { it.pxShape?.flags = shapeFlags }
            }
        }

    actual override var isActive = true
        internal set

    actual val worldBounds: BoundingBox
        get() = pxRigidActor.worldBounds.toBoundingBox(bufBounds)

    private fun updateFilterData() {
        MemoryStack.stackPush().use { mem ->
            val sfd = simulationFilterData.toPxFilterData(mem.createPxFilterData())
            val qfd = queryFilterData.toPxFilterData(mem.createPxFilterData())
            shapes.forEach { shape ->
                shape.pxShape?.let {
                    it.simulationFilterData = sfd
                    it.queryFilterData = qfd
                }
            }
        }
    }

    override fun attachShape(shape: Shape) {
        super.attachShape(shape)
        MemoryStack.stackPush().use { mem ->
            val flags = if (isTrigger) TRIGGER_SHAPE_FLAGS else SIM_SHAPE_FLAGS
            val shapeFlags = mem.createPxShapeFlags(flags)

            val pxShape = PxRigidActorExt.createExclusiveShape(pxRigidActor, shape.geometry.pxGeometry, shape.material.pxMaterial, shapeFlags)
            pxShape.localPose = shape.localPose.toPxTransform(mem.createPxTransform())

            val simFd = if (shape.simFilterData !== null) shape.simFilterData else simulationFilterData
            pxShape.simulationFilterData = simFd.toPxFilterData(mem.createPxFilterData())
            val qryFd = if (shape.queryFilterData !== null) shape.queryFilterData else queryFilterData
            pxShape.queryFilterData = qryFd.toPxFilterData(mem.createPxFilterData())
            shape.pxShape = pxShape
        }
    }

    override fun detachShape(shape: Shape) {
        shape.pxShape?.release()
        super.detachShape(shape)
    }

    override fun release() {
        pxRigidActor.release()
        super.release()
    }

    override fun onPhysicsUpdate(timeStep: Float) {
        updateTransform()
        super.onPhysicsUpdate(timeStep)
    }

    protected fun updateTransform() {
        if (isActive) {
            pxRigidActor.globalPose.toTrsTransform(transform)
        }
    }

    companion object {
        val SIM_SHAPE_FLAGS: Int = PxShapeFlagEnum.eSIMULATION_SHAPE or PxShapeFlagEnum.eSCENE_QUERY_SHAPE
        val TRIGGER_SHAPE_FLAGS: Int = PxShapeFlagEnum.eTRIGGER_SHAPE
    }
}