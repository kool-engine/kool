package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.math.spatial.BoundingBox
import org.lwjgl.system.MemoryStack
import physx.extensions.PxRigidActorExt
import physx.physics.PxRigidActor
import physx.physics.PxShapeFlagEnum

actual open class RigidActor : CommonRigidActor() {

    init {
        Physics.checkIsLoaded()
    }

    internal lateinit var pxRigidActor: PxRigidActor

    private val simFilterData = FilterData()
    private val qryFilterData = FilterData()

    private val bufBounds = BoundingBox()
    private val bufPosition = MutableVec3f()
    private val bufRotation = MutableVec4f()

    override var position: Vec3f
        get() = pxRigidActor.globalPose.p.toVec3f(bufPosition)
        set(value) {
            val pose = pxRigidActor.globalPose
            value.toPxVec3(pose.p)
            pxRigidActor.globalPose = pose
            updateTransform()
        }

    override var rotation: Vec4f
        get() = pxRigidActor.globalPose.q.toVec4f(bufRotation)
        set(value) {
            val pose = pxRigidActor.globalPose
            value.toPxQuat(pose.q)
            pxRigidActor.globalPose = pose
            updateTransform()
        }

    override var isTrigger: Boolean = false
        set(value) {
            field = value
            MemoryStack.stackPush().use { mem ->
                val flags = if (isTrigger) TRIGGER_SHAPE_FLAGS else SIM_SHAPE_FLAGS
                val shapeFlags = mem.createPxShapeFlags(flags)
                shapes.forEach { it.pxShape?.flags = shapeFlags }
            }
        }

    override var isActive = true
        internal set

    actual val worldBounds: BoundingBox
        get() = pxRigidActor.worldBounds.toBoundingBox(bufBounds)

    init {
        simFilterData.apply {
            setCollisionGroup(0)
            setCollidesWithEverything()
        }
    }

    actual fun setSimulationFilterData(simulationFilterData: FilterData) {
        MemoryStack.stackPush().use { mem ->
            simFilterData.set(simulationFilterData)
            val fd = simulationFilterData.toPxFilterData(mem.createPxFilterData())
            shapes.forEach { it.pxShape?.simulationFilterData = fd }
        }
    }

    actual fun setQueryFilterData(queryFilterData: FilterData) {
        MemoryStack.stackPush().use { mem ->
            qryFilterData.set(queryFilterData)
            val fd = queryFilterData.toPxFilterData(mem.createPxFilterData())
            shapes.forEach { it.pxShape?.queryFilterData = fd }
        }
    }

    override fun attachShape(shape: Shape) {
        super.attachShape(shape)
        MemoryStack.stackPush().use { mem ->
            val flags = if (isTrigger) TRIGGER_SHAPE_FLAGS else SIM_SHAPE_FLAGS
            val shapeFlags = mem.createPxShapeFlags(flags)

            val pxShape = PxRigidActorExt.createExclusiveShape(pxRigidActor, shape.geometry.pxGeometry, shape.material.pxMaterial, shapeFlags)
            pxShape.localPose = shape.localPose.toPxTransform(mem.createPxTransform())

            val simFd = if (shape.simFilterData !== null) shape.simFilterData else simFilterData
            pxShape.simulationFilterData = simFd.toPxFilterData(mem.createPxFilterData())
            val qryFd = if (shape.queryFilterData !== null) shape.queryFilterData else qryFilterData
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
            pxRigidActor.globalPose.toMat4f(transform)
        }
    }

    companion object {
        val SIM_SHAPE_FLAGS: Int = PxShapeFlagEnum.eSIMULATION_SHAPE or PxShapeFlagEnum.eSCENE_QUERY_SHAPE
        val TRIGGER_SHAPE_FLAGS: Int = PxShapeFlagEnum.eTRIGGER_SHAPE
    }
}