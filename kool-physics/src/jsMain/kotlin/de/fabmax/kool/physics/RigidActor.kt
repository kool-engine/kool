package de.fabmax.kool.physics

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.util.BoundingBox
import physx.PhysXJsLoader
import physx.PxRigidActor
import physx.PxShape
import kotlin.collections.set

actual open class RigidActor : CommonRigidActor() {

    init {
        Physics.checkIsLoaded()
    }

    internal lateinit var pxRigidActor: PxRigidActor
    protected val pxPose = PxTransform()
    protected val pxFilterData = PxFilterData()
    protected val pxShapes = mutableMapOf<Shape, PxShape>()

    private val simFilterData = FilterData()
    private val qryFilterData = FilterData()

    private val bufBounds = BoundingBox()
    private val bufPosition = MutableVec3f()
    private val bufRotation = MutableVec4f()

    override var position: Vec3f
        get() = pxRigidActor.getGlobalPose().p.toVec3f(bufPosition)
        set(value) {
            val pose = pxRigidActor.getGlobalPose()
            value.toPxVec3(pose.p)
            pxRigidActor.setGlobalPose(pose)
            updateTransform()
        }

    override var rotation: Vec4f
        get() = pxRigidActor.getGlobalPose().q.toVec4f(bufRotation)
        set(value) {
            val pose = pxRigidActor.getGlobalPose()
            value.toPxQuat(pose.q)
            pxRigidActor.setGlobalPose(pose)
            updateTransform()
        }

    actual val worldBounds: BoundingBox
        get() = pxRigidActor.getWorldBounds().toBoundingBox(bufBounds)

    init {
        simFilterData.apply {
            setCollisionGroup(0)
            setCollidesWithEverything()
        }
    }

    actual fun setSimulationFilterData(simulationFilterData: FilterData) {
        simFilterData.set(simulationFilterData)
        simulationFilterData.toPxFilterData(pxFilterData)
        pxShapes.values.forEach { it.setSimulationFilterData(pxFilterData) }
    }

    actual fun setQueryFilterData(queryFilterData: FilterData) {
        qryFilterData.set(queryFilterData)
        queryFilterData.toPxFilterData(pxFilterData)
        pxShapes.values.forEach { it.setQueryFilterData(pxFilterData) }
    }

    override fun attachShape(shape: Shape) {
        super.attachShape(shape)

        val pxShape = Physics.physics.createShape(shape.geometry.pxGeometry, shape.material.pxMaterial, true)
        pxShapes[shape] = pxShape
        pxShape.setLocalPose(shape.localPose.toPxTransform(pxPose))

        val simFd = if (shape.simFilterData !== null) shape.simFilterData else simFilterData
        pxShape.setSimulationFilterData(simFd.toPxFilterData(pxFilterData))
        val qryFd = if (shape.queryFilterData !== null) shape.queryFilterData else qryFilterData
        pxShape.setQueryFilterData(qryFd.toPxFilterData(pxFilterData))
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

        PhysXJsLoader.destroy(pxPose)
        PhysXJsLoader.destroy(pxFilterData)
    }

    override fun fixedUpdate(timeStep: Float) {
        updateTransform()
        super.fixedUpdate(timeStep)
    }

    protected fun updateTransform() {
        pxRigidActor.getGlobalPose().toMat4f(transform)
    }
}