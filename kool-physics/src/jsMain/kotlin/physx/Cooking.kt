package physx

external interface PxConvexMeshDesc {
    var points: PxBoundedData
    var flags: PxConvexFlags
}

external interface PxConvexFlag {
    val eCOMPUTE_CONVEX: Int
    val eCHECK_ZERO_AREA_TRIANGLES: Int

    fun isSet(flags: PxConvexFlags, flag: Int): Boolean
    fun set(flags: PxConvexFlags, flag: Int)
    fun clear(flags: PxConvexFlags, flag: Int)
}

external interface PxConvexFlags
fun PxConvexFlags.isSet(flag: Int) = PhysX.PxConvexFlag.isSet(this, flag)
fun PxConvexFlags.set(flag: Int) = PhysX.PxConvexFlag.set(this, flag)
fun PxConvexFlags.clear(flag: Int) = PhysX.PxConvexFlag.clear(this, flag)

external interface PxCooking {
    fun createConvexMesh(desc: PxConvexMeshDesc, insertionCallback: PxPhysicsInsertionCallback): PxConvexMesh
}

external interface PxCookingParams
