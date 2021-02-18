/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("UnsafeCastFromDynamic", "ClassName", "FunctionName", "UNUSED_VARIABLE", "UNUSED_PARAMETER", "unused")

package physx

external interface PxTopLevelFunctions {
    /**
     * WebIDL type: unsigned long
     */
    var PHYSICS_VERSION: Int

    /**
     * @return WebIDL type: [PxSimulationFilterShader] (Value)
     */
    fun DefaultFilterShader(): PxSimulationFilterShader

    /**
     * @return WebIDL type: [PxBatchQueryPreFilterShader] (Value)
     */
    fun DefaultWheelSceneQueryPreFilterBlocking(): PxBatchQueryPreFilterShader

    /**
     * @return WebIDL type: [PxBatchQueryPostFilterShader] (Value)
     */
    fun DefaultWheelSceneQueryPostFilterBlocking(): PxBatchQueryPostFilterShader

    /**
     * @param version    WebIDL type: unsigned long
     * @param foundation WebIDL type: [PxFoundation] (Ref)
     * @param scale      WebIDL type: [PxCookingParams] (Const, Ref)
     * @return WebIDL type: [PxCooking]
     */
    fun CreateCooking(version: Int, foundation: PxFoundation, scale: PxCookingParams): PxCooking

    /**
     * @param version       WebIDL type: unsigned long
     * @param allocator     WebIDL type: [PxDefaultAllocator] (Ref)
     * @param errorCallback WebIDL type: [PxErrorCallback] (Ref)
     * @return WebIDL type: [PxFoundation]
     */
    fun CreateFoundation(version: Int, allocator: PxDefaultAllocator, errorCallback: PxErrorCallback): PxFoundation

    /**
     * @param version    WebIDL type: unsigned long
     * @param foundation WebIDL type: [PxFoundation] (Ref)
     * @param params     WebIDL type: [PxTolerancesScale] (Const, Ref)
     * @return WebIDL type: [PxPhysics]
     */
    fun CreatePhysics(version: Int, foundation: PxFoundation, params: PxTolerancesScale): PxPhysics

    /**
     * @param numThreads WebIDL type: unsigned long
     * @return WebIDL type: [PxDefaultCpuDispatcher]
     */
    fun DefaultCpuDispatcherCreate(numThreads: Int): PxDefaultCpuDispatcher

    /**
     * @param physics WebIDL type: [PxPhysics] (Ref)
     * @return WebIDL type: boolean
     */
    fun InitExtensions(physics: PxPhysics): Boolean

    /**
     * @param physics     WebIDL type: [PxPhysics] (Ref)
     * @param actor0      WebIDL type: [PxRigidActor]
     * @param localFrame0 WebIDL type: [PxTransform] (Ref)
     * @param actor1      WebIDL type: [PxRigidActor]
     * @param localFrame1 WebIDL type: [PxTransform] (Ref)
     * @return WebIDL type: [PxRevoluteJoint]
     */
    fun RevoluteJointCreate(physics: PxPhysics, actor0: PxRigidActor, localFrame0: PxTransform, actor1: PxRigidActor, localFrame1: PxTransform): PxRevoluteJoint

}

