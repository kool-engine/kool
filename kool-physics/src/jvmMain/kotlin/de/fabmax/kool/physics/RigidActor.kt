package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.util.BoundingBox
import physx.physics.PxRigidActor
import physx.physics.PxShape
import kotlin.collections.forEach
import kotlin.collections.mutableMapOf
import kotlin.collections.set

actual open class RigidActor : CommonRigidActor() {

    internal lateinit var pxRigidActor: PxRigidActor
    protected val pxPose = PxTransform()
    protected val pxSimFilterData = PxFilterData()
    protected val pxQryFilterData = PxFilterData()
    protected val pxShapes = mutableMapOf<Shape, PxShape>()

    private val bufBounds = BoundingBox()
    private val bufPosition = MutableVec3f()
    private val bufRotation = MutableVec4f()

    override var position: Vec3f
        get() = pxRigidActor.globalPose.p.toVec3f(bufPosition)
        set(value) {
            val pose = pxRigidActor.globalPose
            value.toPxVec3(pose.p)
            pxRigidActor.globalPose = pose
        }

    override var rotation: Vec4f
        get() = pxRigidActor.globalPose.q.toVec4f(bufRotation)
        set(value) {
            val pose = pxRigidActor.globalPose
            value.toPxQuat(pose.q)
            pxRigidActor.globalPose = pose
            updateTransform()
        }

    actual val worldBounds: BoundingBox
        get() = pxRigidActor.worldBounds.toBoundingBox(bufBounds)

    init {
        FilterData().apply {
            setCollisionGroup(0)
            setCollidesWithEverything()
            toPxFilterData(pxSimFilterData)
        }
    }

    actual fun setSimulationFilterData(simulationFilterData: FilterData) {
        simulationFilterData.toPxFilterData(pxSimFilterData)
        pxShapes.values.forEach { it.simulationFilterData = pxSimFilterData }
    }

    actual fun setQueryFilterData(queryFilterData: FilterData) {
        queryFilterData.toPxFilterData(pxQryFilterData)
        pxShapes.values.forEach { it.queryFilterData = pxQryFilterData }
    }

    override fun attachShape(shape: Shape) {
        super.attachShape(shape)

        val pxShape = Physics.physics.createShape(shape.geometry.pxGeometry, shape.material.pxMaterial, true)
        pxShapes[shape] = pxShape
        pxShape.localPose = shape.localPose.toPxTransform(pxPose)
        pxShape.simulationFilterData = pxSimFilterData
        pxShape.queryFilterData = pxQryFilterData
        pxRigidActor.attachShape(pxShape)
    }

    override fun detachShape(shape: Shape) {
        pxShapes.remove(shape)?.release()
        super.detachShape(shape)
    }

    actual open fun release() {
        pxRigidActor.release()
        // attached PxShapes are auto-released when pxRigidDynamic is released, just clear the lists
        pxShapes.clear()
        mutShapes.clear()

        pxPose.destroy()
        pxSimFilterData.destroy()
        pxQryFilterData.destroy()
    }

    override fun fixedUpdate(timeStep: Float) {
        updateTransform()
        super.fixedUpdate(timeStep)
    }

    protected fun updateTransform() {
        pxRigidActor.globalPose.toMat4f(transform)
    }
}