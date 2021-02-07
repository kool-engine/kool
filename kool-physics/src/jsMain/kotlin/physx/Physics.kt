/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("UnsafeCastFromDynamic", "ClassName", "FunctionName", "UNUSED_VARIABLE", "UNUSED_PARAMETER", "unused")

package physx

external interface PxActor : PxBase {
    fun getType(): Int
    fun getScene(): PxScene
    fun setName(name: String)
    fun getName(): String
    fun getWorldBounds(): PxBounds3
    fun getWorldBounds(inflation: Float): PxBounds3
    fun setActorFlags(flags: PxActorFlags)
    fun getActorFlags(): PxActorFlags
    fun setDominanceGroup(dominanceGroup: Byte)
    fun getDominanceGroup(): Byte
    fun setOwnerClient(inClient: Byte)
    fun getOwnerClient(): Byte
}

external interface PxActorFlags {
    fun isSet(flag: Int): Boolean
    fun set(flag: Int)
    fun clear(flag: Int)
}
fun PxActorFlags(flags: Byte): PxActorFlags {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxActorFlags(flags)")
}

external interface PxActorShape {
    var actor: PxRigidActor
    var shape: PxShape
}

external interface PxBatchQuery {
    fun execute()
    fun getPreFilterShader(): PxBatchQueryPreFilterShader
    fun getPostFilterShader(): PxBatchQueryPostFilterShader
    fun getFilterShaderData(): Int
    fun getFilterShaderDataSize(): Int
    fun setUserMemory(userMemory: PxBatchQueryMemory)
    fun getUserMemory(): PxBatchQueryMemory
    fun release()
}

external interface PxBatchQueryDesc {
    var filterShaderData: Int
    var filterShaderDataSize: Int
    var preFilterShader: PxBatchQueryPreFilterShader?
    var postFilterShader: PxBatchQueryPostFilterShader?
    var queryMemory: PxBatchQueryMemory

    fun isValid(): Boolean
}
fun PxBatchQueryDesc(maxRaycastsPerExecute: Int, maxSweepsPerExecute: Int, maxOverlapsPerExecute: Int): PxBatchQueryDesc {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxBatchQueryDesc(maxRaycastsPerExecute, maxSweepsPerExecute, maxOverlapsPerExecute)")
}

external interface PxBatchQueryMemory {
    var userRaycastResultBuffer: PxRaycastQueryResult
    var userRaycastTouchBuffer: PxRaycastHit
    var userSweepResultBuffer: PxSweepQueryResult
    var userSweepTouchBuffer: PxSweepHit
    var userOverlapResultBuffer: PxOverlapQueryResult
    var userOverlapTouchBuffer: PxOverlapHit
    var raycastTouchBufferSize: Int
    var sweepTouchBufferSize: Int
    var overlapTouchBufferSize: Int
}

external interface PxBatchQueryPostFilterShader

external interface PxBatchQueryPreFilterShader

external interface PxFilterData {
    var word0: Int
    var word1: Int
    var word2: Int
    var word3: Int
}
fun PxFilterData(): PxFilterData {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxFilterData()")
}
fun PxFilterData(w0: Int, w1: Int, w2: Int, w3: Int): PxFilterData {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxFilterData(w0, w1, w2, w3)")
}

external interface PxHitFlags {
    fun isSet(flag: Int): Boolean
    fun set(flag: Int)
    fun clear(flag: Int)
}
fun PxHitFlags(flags: Short): PxHitFlags {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxHitFlags(flags)")
}

external interface PxLocationHit : PxQueryHit {
    var flags: PxHitFlags
    var position: PxVec3
    var normal: PxVec3
    var distance: Float
}

external interface PxOverlapHit : PxQueryHit

external interface PxOverlapQueryResult {
    var block: PxOverlapHit
    var touches: PxOverlapHit
    var nbTouches: Int
    var userData: Int
    var queryStatus: Byte
    var hasBlock: Boolean

    fun getNbAnyHits(): Int
    fun getAnyHit(index: Int): PxOverlapHit
}

external interface PxMaterial : PxBase

external interface PxPhysics {
    fun release()
    fun getFoundation(): PxFoundation
    fun getTolerancesScale(): PxTolerancesScale
    fun createScene(sceneDesc: PxSceneDesc): PxScene
    fun createRigidStatic(pose: PxTransform): PxRigidStatic
    fun createRigidDynamic(pose: PxTransform): PxRigidDynamic
    fun createShape(geometry: PxGeometry, material: PxMaterial): PxShape
    fun createShape(geometry: PxGeometry, material: PxMaterial, isExclusive: Boolean): PxShape
    fun createShape(geometry: PxGeometry, material: PxMaterial, isExclusive: Boolean, shapeFlags: PxShapeFlags): PxShape
    fun getNbShapes(): Int
    fun createMaterial(staticFriction: Float, dynamicFriction: Float, restitution: Float): PxMaterial
    fun getPhysicsInsertionCallback(): PxPhysicsInsertionCallback
}

external interface PxQueryHit : PxActorShape {
    var faceIndex: Int
}

external interface PxRaycastHit : PxLocationHit {
    var u: Float
    var v: Float
}
fun PxRaycastHit(): PxRaycastHit {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxRaycastHit()")
}

external interface PxRaycastQueryResult {
    var block: PxRaycastHit
    var touches: PxRaycastHit
    var nbTouches: Int
    var userData: Int
    var queryStatus: Byte
    var hasBlock: Boolean

    fun getNbAnyHits(): Int
    fun getAnyHit(index: Int): PxRaycastHit
}

external interface PxRigidActor : PxActor {
    fun getGlobalPose(): PxTransform
    fun setGlobalPose(pose: PxTransform)
    fun setGlobalPose(pose: PxTransform, autowake: Boolean)
    fun attachShape(shape: PxShape): Boolean
    fun detachShape(shape: PxShape)
    fun detachShape(shape: PxShape, wakeOnLostTouch: Boolean)
    fun getNbShapes(): Int
}

external interface PxRigidBody : PxRigidActor {
    fun setCMassLocalPose(pose: PxTransform)
    fun getCMassLocalPose(): PxTransform
    fun setMass(mass: Float)
    fun getMass(): Float
    fun getInvMass(): Float
    fun setMassSpaceInertiaTensor(m: PxVec3)
    fun getMassSpaceInertiaTensor(): PxVec3
    fun getMassSpaceInvInertiaTensor(): PxVec3
    fun setLinearDamping(linDamp: Float)
    fun getLinearDamping(): Float
    fun setAngularDamping(angDamp: Float)
    fun getAngularDamping(): Float
    fun getLinearVelocity(): PxVec3
    fun setLinearVelocity(linVel: PxVec3)
    fun setLinearVelocity(linVel: PxVec3, autowake: Boolean)
    fun getAngularVelocity(): PxVec3
    fun setAngularVelocity(angVel: PxVec3)
    fun setAngularVelocity(angVel: PxVec3, autowake: Boolean)
    fun getMaxLinearVelocity(): Float
    fun setMaxLinearVelocity(maxLinVel: Float)
    fun getMaxAngularVelocity(): Float
    fun setMaxAngularVelocity(maxAngVel: Float)
    fun addForce(force: PxVec3)
    fun addForce(force: PxVec3, mode: Int)
    fun addForce(force: PxVec3, mode: Int, autowake: Boolean)
    fun addTorque(torque: PxVec3)
    fun addTorque(torque: PxVec3, mode: Int)
    fun addTorque(torque: PxVec3, mode: Int, autowake: Boolean)
    fun clearForce(mode: Int)
    fun clearTorque(mode: Int)
    fun setForceAndTorque(force: PxVec3, torque: PxVec3)
    fun setForceAndTorque(force: PxVec3, torque: PxVec3, mode: Int)
    fun setRigidBodyFlag(flag: Int, value: Boolean)
    fun setRigidBodyFlags(inFlags: PxRigidBodyFlags)
    fun getRigidBodyFlags(): PxRigidBodyFlags
    fun setMinCCDAdvanceCoefficient(advanceCoefficient: Float)
    fun getMinCCDAdvanceCoefficient(): Float
    fun setMaxDepenetrationVelocity(biasClamp: Float)
    fun getMaxDepenetrationVelocity(): Float
    fun setMaxContactImpulse(maxImpulse: Float)
    fun getMaxContactImpulse(): Float
    fun getInternalIslandNodeIndex(): Int
}

external interface PxRigidBodyFlags {
    fun isSet(flag: Int): Boolean
    fun set(flag: Int)
    fun clear(flag: Int)
}
fun PxRigidBodyFlags(flags: Byte): PxRigidBodyFlags {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxRigidBodyFlags(flags)")
}

external interface PxRigidDynamic : PxRigidBody {
    fun isSleeping(): Boolean
    fun setSleepThreshold(threshold: Float)
    fun getSleepThreshold(): Float
    fun setStabilizationThreshold(threshold: Float)
    fun getStabilizationThreshold(): Float
    fun getRigidDynamicLockFlags(): PxRigidDynamicLockFlags
    fun setRigidDynamicLockFlag(flag: Int, value: Boolean)
    fun setRigidDynamicLockFlags(flags: PxRigidDynamicLockFlags)
    fun setWakeCounter(wakeCounterValue: Float)
    fun getWakeCounter(): Float
    fun wakeUp()
    fun putToSleep()
    fun setSolverIterationCounts(minPositionIters: Int)
    fun setSolverIterationCounts(minPositionIters: Int, minVelocityIters: Int)
    fun getContactReportThreshold(): Float
    fun setContactReportThreshold(threshold: Float)
}

external interface PxRigidDynamicLockFlags {
    fun isSet(flag: Int): Boolean
    fun set(flag: Int)
    fun clear(flag: Int)
}
fun PxRigidDynamicLockFlags(flags: Byte): PxRigidDynamicLockFlags {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxRigidDynamicLockFlags(flags)")
}

external interface PxRigidStatic : PxRigidActor

external interface PxScene {
    fun addActor(actor: PxActor)
    fun addActor(actor: PxActor, bvhStructure: PxBVHStructure)
    fun removeActor(actor: PxActor)
    fun removeActor(actor: PxActor, wakeOnLostTouch: Boolean)
    fun simulate(elapsedTime: Float)
    fun simulate(elapsedTime: Float, completionTask: PxBaseTask)
    fun simulate(elapsedTime: Float, completionTask: PxBaseTask, scratchMemBlock: Int)
    fun simulate(elapsedTime: Float, completionTask: PxBaseTask, scratchMemBlock: Int, scratchMemBlockSize: Int)
    fun simulate(elapsedTime: Float, completionTask: PxBaseTask, scratchMemBlock: Int, scratchMemBlockSize: Int, controlSimulation: Boolean)
    fun fetchResults(): Boolean
    fun fetchResults(block: Boolean): Boolean
    fun setGravity(vec: PxVec3)
    fun getGravity(): PxVec3
    fun createBatchQuery(desc: PxBatchQueryDesc): PxBatchQuery
    fun release()
    fun setFlag(flag: Int, value: Boolean)
    fun getFlags(): PxSceneFlags
    fun getPhysics(): PxPhysics
    fun getTimestamp(): Int
}

external interface PxSceneDesc {
    var gravity: PxVec3
    var simulationEventCallback: PxSimulationEventCallback
    var filterShader: PxSimulationFilterShader
    var cpuDispatcher: PxCpuDispatcher
    var flags: PxSceneFlags
}
fun PxSceneDesc(scale: PxTolerancesScale): PxSceneDesc {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxSceneDesc(scale)")
}

external interface PxSceneFlags {
    fun isSet(flag: Int): Boolean
    fun set(flag: Int)
    fun clear(flag: Int)
}
fun PxSceneFlags(flags: Int): PxSceneFlags {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxSceneFlags(flags)")
}

external interface PxShape : PxBase {
    fun setLocalPose(pose: PxTransform)
    fun getLocalPose(): PxTransform
    fun setSimulationFilterData(data: PxFilterData)
    fun getSimulationFilterData(): PxFilterData
    fun setQueryFilterData(data: PxFilterData)
    fun getQueryFilterData(): PxFilterData
}

external interface PxShapeFlags {
    fun isSet(flag: Int): Boolean
    fun set(flag: Int)
    fun clear(flag: Int)
}
fun PxShapeFlags(flags: Byte): PxShapeFlags {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxShapeFlags(flags)")
}

external interface PxSimulationEventCallback

external interface SimplePxSimulationEventCallback : PxSimulationEventCallback {
    fun cbFun(count: Int)
}

external interface PxSimulationFilterShader

external interface PxSweepHit : PxLocationHit

external interface PxSweepQueryResult {
    var block: PxSweepHit
    var touches: PxSweepHit
    var nbTouches: Int
    var userData: Int
    var queryStatus: Byte
    var hasBlock: Boolean

    fun getNbAnyHits(): Int
    fun getAnyHit(index: Int): PxSweepHit
}

object PxActorFlagEnum {
    val eVISUALIZATION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxActorFlagEnum_eVISUALIZATION()
    val eDISABLE_GRAVITY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxActorFlagEnum_eDISABLE_GRAVITY()
    val eSEND_SLEEP_NOTIFIES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxActorFlagEnum_eSEND_SLEEP_NOTIFIES()
    val eDISABLE_SIMULATION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxActorFlagEnum_eDISABLE_SIMULATION()
}

object PxActorTypeEnum {
    val eRIGID_STATIC: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxActorTypeEnum_eRIGID_STATIC()
    val eRIGID_DYNAMIC: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxActorTypeEnum_eRIGID_DYNAMIC()
    val eARTICULATION_LINK: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxActorTypeEnum_eARTICULATION_LINK()
    val eACTOR_COUNT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxActorTypeEnum_eACTOR_COUNT()
    val eACTOR_FORCE_DWORD: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxActorTypeEnum_eACTOR_FORCE_DWORD()
}

object PxForceModeEnum {
    val eFORCE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxForceModeEnum_eFORCE()
    val eIMPULSE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxForceModeEnum_eIMPULSE()
    val eVELOCITY_CHANGE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxForceModeEnum_eVELOCITY_CHANGE()
    val eACCELERATION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxForceModeEnum_eACCELERATION()
}

object PxHitFlagEnum {
    val ePOSITION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_ePOSITION()
    val eNORMAL: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eNORMAL()
    val eUV: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eUV()
    val eASSUME_NO_INITIAL_OVERLAP: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eASSUME_NO_INITIAL_OVERLAP()
    val eMESH_MULTIPLE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eMESH_MULTIPLE()
    val eMESH_ANY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eMESH_ANY()
    val eMESH_BOTH_SIDES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eMESH_BOTH_SIDES()
    val ePRECISE_SWEEP: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_ePRECISE_SWEEP()
    val eMTD: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eMTD()
    val eFACE_INDEX: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eFACE_INDEX()
    val eDEFAULT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eDEFAULT()
    val eMODIFIABLE_FLAGS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eMODIFIABLE_FLAGS()
}

object PxRigidBodyFlagEnum {
    val eKINEMATIC: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRigidBodyFlagEnum_eKINEMATIC()
    val eUSE_KINEMATIC_TARGET_FOR_SCENE_QUERIES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRigidBodyFlagEnum_eUSE_KINEMATIC_TARGET_FOR_SCENE_QUERIES()
    val eENABLE_CCD: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRigidBodyFlagEnum_eENABLE_CCD()
    val eENABLE_CCD_FRICTION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRigidBodyFlagEnum_eENABLE_CCD_FRICTION()
    val eENABLE_POSE_INTEGRATION_PREVIEW: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRigidBodyFlagEnum_eENABLE_POSE_INTEGRATION_PREVIEW()
    val eENABLE_SPECULATIVE_CCD: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRigidBodyFlagEnum_eENABLE_SPECULATIVE_CCD()
    val eENABLE_CCD_MAX_CONTACT_IMPULSE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRigidBodyFlagEnum_eENABLE_CCD_MAX_CONTACT_IMPULSE()
    val eRETAIN_ACCELERATIONS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRigidBodyFlagEnum_eRETAIN_ACCELERATIONS()
}

object PxRigidDynamicLockFlagEnum {
    val eLOCK_LINEAR_X: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_LINEAR_X()
    val eLOCK_LINEAR_Y: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_LINEAR_Y()
    val eLOCK_LINEAR_Z: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_LINEAR_Z()
    val eLOCK_ANGULAR_X: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_ANGULAR_X()
    val eLOCK_ANGULAR_Y: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_ANGULAR_Y()
    val eLOCK_ANGULAR_Z: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_ANGULAR_Z()
}

object PxSceneFlagEnum {
    val eENABLE_ACTIVE_ACTORS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eENABLE_ACTIVE_ACTORS()
    val eENABLE_CCD: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eENABLE_CCD()
    val eDISABLE_CCD_RESWEEP: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eDISABLE_CCD_RESWEEP()
    val eADAPTIVE_FORCE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eADAPTIVE_FORCE()
    val eENABLE_PCM: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eENABLE_PCM()
    val eDISABLE_CONTACT_REPORT_BUFFER_RESIZE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eDISABLE_CONTACT_REPORT_BUFFER_RESIZE()
    val eDISABLE_CONTACT_CACHE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eDISABLE_CONTACT_CACHE()
    val eREQUIRE_RW_LOCK: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eREQUIRE_RW_LOCK()
    val eENABLE_STABILIZATION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eENABLE_STABILIZATION()
    val eENABLE_AVERAGE_POINT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eENABLE_AVERAGE_POINT()
    val eEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS()
    val eENABLE_GPU_DYNAMICS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eENABLE_GPU_DYNAMICS()
    val eENABLE_ENHANCED_DETERMINISM: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eENABLE_ENHANCED_DETERMINISM()
    val eENABLE_FRICTION_EVERY_ITERATION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eENABLE_FRICTION_EVERY_ITERATION()
    val eMUTABLE_FLAGS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eMUTABLE_FLAGS()
}

object PxShapeFlagEnum {
    val eSIMULATION_SHAPE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxShapeFlagEnum_eSIMULATION_SHAPE()
    val eSCENE_QUERY_SHAPE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxShapeFlagEnum_eSCENE_QUERY_SHAPE()
    val eTRIGGER_SHAPE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxShapeFlagEnum_eTRIGGER_SHAPE()
    val eVISUALIZATION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxShapeFlagEnum_eVISUALIZATION()
}