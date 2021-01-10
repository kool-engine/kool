package physx

external interface PxStatics {
    val PHYSICS_VERSION: Int

    fun CreateFoundation(version: Int, allocator: PxDefaultAllocator, errorCallback: PxDefaultErrorCallback): PxFoundation
    fun CreatePhysics(version: Int, foundation: PxFoundation, scale: PxTolerancesScale): PxPhysics
    fun CreateCooking(version: Int, foundation: PxFoundation, cookingParams: PxCookingParams): PxCooking
    fun DefaultCpuDispatcherCreate(numThreads: Int): PxCpuDispatcher
    fun DefaultFilterShader(): PxSimulationFilterShader
    fun InitExtensions(physics: PxPhysics)
    fun RevoluteJointCreate(physics: PxPhysics, actor0: PxRigidActor, localFrame0: PxTransform, actor1: PxRigidActor, localFrame1: PxTransform): PxRevoluteJoint

    fun getU8At(pointer: Int, index: Int): Int
    fun getVec3At(pointer: Int, index: Int): PxVec3
}
