package de.fabmax.kool.physics.shapes

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.physics.PhysicsFilterData
import de.fabmax.kool.physics.RigidBodyProperties
import de.fabmax.kool.util.BoundingBox
import de.fabmax.kool.util.MeshBuilder
import physx.*

actual interface CollisionShape {

    actual fun generateGeometry(target: MeshBuilder)

    actual fun getAabb(result: BoundingBox): BoundingBox

    actual fun getBoundingSphere(result: MutableVec4f): MutableVec4f

    actual fun estimateInertiaForMass(mass: Float, result: MutableVec3f): MutableVec3f

    fun attachTo(actor: PxRigidActor, flags: PxShapeFlags, material: PxMaterial, bodyProps: RigidBodyProperties?): PxShape?

    fun PhysicsFilterData.toPxFilterData(target: PxFilterData) {
        target.word0 = data[0]
        target.word1 = data[1]
        target.word2 = data[2]
        target.word3 = data[3]
    }

    fun setFilterDatas(shape: PxShape, bodyProps: RigidBodyProperties) {
        val pxFilterData = PhysX.PxFilterData()
        bodyProps.simFilterData.toPxFilterData(pxFilterData)
        shape.setSimulationFilterData(pxFilterData)
        bodyProps.queryFilterData.toPxFilterData(pxFilterData)
        shape.setQueryFilterData(pxFilterData)
        PhysX.destroy(pxFilterData)
    }
}