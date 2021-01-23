/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("UnsafeCastFromDynamic", "ClassName", "FunctionName", "UNUSED_VARIABLE", "UNUSED_PARAMETER", "unused")

package physx

external interface PxTopLevelFunctions {
    var PHYSICS_VERSION: Int

    fun DefaultFilterShader(): PxSimulationFilterShader
    fun DefaultWheelSceneQueryPreFilterBlocking(): PxBatchQueryPreFilterShader
    fun DefaultWheelSceneQueryPostFilterBlocking(): PxBatchQueryPostFilterShader
    fun CreateCooking(version: Int, foundation: PxFoundation, scale: PxCookingParams): PxCooking
    fun CreateFoundation(version: Int, allocator: PxDefaultAllocator, errorCallback: PxDefaultErrorCallback): PxFoundation
    fun CreatePhysics(version: Int, foundation: PxFoundation, params: PxTolerancesScale): PxPhysics
    fun DefaultCpuDispatcherCreate(numThreads: Int): PxDefaultCpuDispatcher
    fun InitExtensions(physics: PxPhysics): Boolean
    fun RevoluteJointCreate(physics: PxPhysics, actor0: PxRigidActor, localFrame0: PxTransform, actor1: PxRigidActor, localFrame1: PxTransform): PxRevoluteJoint
    fun getU8At(base: PxU8Ptr, index: Int): Int
    fun getVec3At(base: PxVec3, index: Int): PxVec3
}