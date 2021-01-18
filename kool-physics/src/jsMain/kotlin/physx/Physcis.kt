@file:Suppress("UnsafeCastFromDynamic")

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

object PxActorFlag {
    val eVISUALIZATION: Int get() = PhysX.physx._emscripten_enum_physx_PxActorFlag_eVISUALIZATION()
    val eDISABLE_GRAVITY: Int get() = PhysX.physx._emscripten_enum_physx_PxActorFlag_eDISABLE_GRAVITY()
    val eSEND_SLEEP_NOTIFIES: Int get() = PhysX.physx._emscripten_enum_physx_PxActorFlag_eSEND_SLEEP_NOTIFIES()
    val eDISABLE_SIMULATION: Int get() = PhysX.physx._emscripten_enum_physx_PxActorFlag_eDISABLE_SIMULATION()
}

external interface PxActorFlags : PxFlags

external interface PxActorShape {
    var actor: PxRigidActor
    var shape: PxShape
}

object PxActorType {
    val eRIGID_STATIC: Int get() = PhysX.physx._emscripten_enum_physx_PxActorType_eRIGID_STATIC()
    val eRIGID_DYNAMIC: Int get() = PhysX.physx._emscripten_enum_physx_PxActorType_eRIGID_DYNAMIC()
    val eARTICULATION_LINK: Int get() = PhysX.physx._emscripten_enum_physx_PxActorType_eARTICULATION_LINK()
    val eACTOR_COUNT: Int get() = PhysX.physx._emscripten_enum_physx_PxActorType_eACTOR_COUNT()
    val eACTOR_FORCE_DWORD: Int get() = PhysX.physx._emscripten_enum_physx_PxActorType_eACTOR_FORCE_DWORD()
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

object PxForceMode {
    val eFORCE: Int get() = PhysX.physx._emscripten_enum_physx_PxForceMode_eFORCE()
    val eIMPULSE: Int get() = PhysX.physx._emscripten_enum_physx_PxForceMode_eIMPULSE()
    val eVELOCITY_CHANGE: Int get() = PhysX.physx._emscripten_enum_physx_PxForceMode_eVELOCITY_CHANGE()
    val eACCELERATION: Int get() = PhysX.physx._emscripten_enum_physx_PxForceMode_eACCELERATION()
}

object PxHitFlag {
    val ePOSITION: Int get() = PhysX.physx._emscripten_enum_physx_PxHitFlag_ePOSITION()
    val eNORMAL: Int get() = PhysX.physx._emscripten_enum_physx_PxHitFlag_eNORMAL()
    val eUV: Int get() = PhysX.physx._emscripten_enum_physx_PxHitFlag_eUV()
    val eASSUME_NO_INITIAL_OVERLAP: Int get() = PhysX.physx._emscripten_enum_physx_PxHitFlag_eASSUME_NO_INITIAL_OVERLAP()
    val eMESH_MULTIPLE: Int get() = PhysX.physx._emscripten_enum_physx_PxHitFlag_eMESH_MULTIPLE()
    val eMESH_ANY: Int get() = PhysX.physx._emscripten_enum_physx_PxHitFlag_eMESH_ANY()
    val eMESH_BOTH_SIDES: Int get() = PhysX.physx._emscripten_enum_physx_PxHitFlag_eMESH_BOTH_SIDES()
    val ePRECISE_SWEEP: Int get() = PhysX.physx._emscripten_enum_physx_PxHitFlag_ePRECISE_SWEEP()
    val eMTD: Int get() = PhysX.physx._emscripten_enum_physx_PxHitFlag_eMTD()
    val eFACE_INDEX: Int get() = PhysX.physx._emscripten_enum_physx_PxHitFlag_eFACE_INDEX()
    val eDEFAULT: Int get() = PhysX.physx._emscripten_enum_physx_PxHitFlag_eDEFAULT()
    val eMODIFIABLE_FLAGS: Int get() = PhysX.physx._emscripten_enum_physx_PxHitFlag_eMODIFIABLE_FLAGS()
}

interface PxHitFlags : PxFlags

external interface PxLocationHit : PxQueryHit {
    var flags: PxHitFlags
    var position: PxVec3
    var normal: PxVec3
    var distance: Float
}

external interface PxOverlapHit : PxQueryHit

interface PxOverlapQueryResult {
    val block: PxOverlapHit
    //attribute PxRaycastHit touches;
    //attribute unsigned long nbTouches;
    val userData: Int
    val queryStatus: Int
    val hasBlock: Boolean

    fun getNbAnyHits(): Int
    fun getAnyHit(index: Int): PxOverlapHit
}

external interface PxMaterial : PxBase

external interface PxPhysics {
    // Basics
    fun getFoundation(): PxFoundation
    fun getTolerancesScale(): PxTolerancesScale

    // Meshes

    // Scenes
    fun createScene(sceneDesc: PxSceneDesc): PxScene

    // Actors
    fun createRigidStatic(pose: PxTransform): PxRigidStatic
    fun createRigidDynamic(pose: PxTransform): PxRigidDynamic

    // Shapes
    fun createShape(geometry: PxGeometry, material: PxMaterial, isExclusive: Boolean, shapeFlags: PxShapeFlags): PxShape
    fun getNbShapes(): Int

    // Constraints and Articulations

    // Materials
    fun createMaterial(staticFriction: Float, dynamicFriction: Float, restitution: Float): PxMaterial

    // Deletion Listeners
    fun getPhysicsInsertionCallback(): PxPhysicsInsertionCallback
}

external interface PxQueryHit : PxActorShape {
    val faceIndex: Int
}

external interface PxRaycastHit : PxLocationHit {
    val u: Float
    val v: Float
}

external interface PxRaycastQueryResult {
    val block: PxRaycastHit
    //attribute PxRaycastHit touches;
    //attribute unsigned long nbTouches;
    val userData: Int
    val queryStatus: Int
    val hasBlock: Boolean

    fun getNbAnyHits(): Int
    fun getAnyHit(index: Int): PxRaycastHit
}

external interface PxRigidActor : PxActor {
    // Global Pose Manipulation
    fun getGlobalPose(): PxTransform
    fun setGlobalPose(pose: PxTransform)
    fun setGlobalPose(pose: PxTransform, autowake: Boolean)

    // Shapes
    fun attachShape(shape: PxShape): Boolean
    fun detachShape(shape: PxShape, wakeOnLostTouch: Boolean)
    fun getNbShapes(): Int
    fun getShapes(userBuffer: Array<PxShape?>, bufferSize: Int, startIndex: Int)
}

external interface PxRigidBody : PxRigidActor {
    // Mass Manipulation
    fun setCMassLocalPose(pose: PxTransform)
    fun getCMassLocalPose(): PxTransform
    fun setMass(mass: Float)
    fun getMass(): Float
    fun getInvMass(): Float
    fun setMassSpaceInertiaTensor(inertia: PxVec3)
    fun getMassSpaceInertiaTensor(): PxVec3
    fun getMassSpaceInvInertiaTensor(): PxVec3

    // Damping
    fun setLinearDamping(linDamp: Float)
    fun getLinearDamping(): Float
    fun setAngularDamping(angDamp: Float)
    fun getAngularDamping(): Float

    // Velocity
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

    // Forces
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

object PxRigidBodyFlag {
    val eKINEMATIC: Int get() = PhysX.physx._emscripten_enum_physx_PxRigidBodyFlag_eKINEMATIC()
    val eUSE_KINEMATIC_TARGET_FOR_SCENE_QUERIES: Int get() = PhysX.physx._emscripten_enum_physx_PxRigidBodyFlag_eUSE_KINEMATIC_TARGET_FOR_SCENE_QUERIES()
    val eENABLE_CCD: Int get() = PhysX.physx._emscripten_enum_physx_PxRigidBodyFlag_eENABLE_CCD()
    val eENABLE_CCD_FRICTION: Int get() = PhysX.physx._emscripten_enum_physx_PxRigidBodyFlag_eENABLE_CCD_FRICTION()
    val eENABLE_POSE_INTEGRATION_PREVIEW: Int get() = PhysX.physx._emscripten_enum_physx_PxRigidBodyFlag_eENABLE_POSE_INTEGRATION_PREVIEW()
    val eENABLE_SPECULATIVE_CCD: Int get() = PhysX.physx._emscripten_enum_physx_PxRigidBodyFlag_eENABLE_SPECULATIVE_CCD()
    val eENABLE_CCD_MAX_CONTACT_IMPULSE: Int get() = PhysX.physx._emscripten_enum_physx_PxRigidBodyFlag_eENABLE_CCD_MAX_CONTACT_IMPULSE()
    val eRETAIN_ACCELERATIONS: Int get() = PhysX.physx._emscripten_enum_physx_PxRigidBodyFlag_eRETAIN_ACCELERATIONS()
}

external interface PxRigidBodyFlags : PxFlags

external interface PxRigidDynamic : PxRigidBody

external interface PxRigidStatic : PxRigidActor

external interface PxScene {
    fun addActor(actor: PxActor)
    fun removeActor(actor: PxActor, wakeOnLostTouch: Boolean)

    // Simulation
    fun simulate(elapsedTime: Float)
    fun fetchResults(block: Boolean): Boolean
    fun getGravity(): PxVec3
    fun setGravity(vec: PxVec3)

    fun createBatchQuery(desc: PxBatchQueryDesc): PxBatchQuery
}

external interface PxSceneDesc {
    var gravity: PxVec3
    var simulationEventCallback: PxSimulationEventCallback
    var filterShader: PxSimulationFilterShader
    var cpuDispatcher: PxCpuDispatcher
    var flags: PxSceneFlags
}

object PxSceneFlag {
    val eENABLE_ACTIVE_ACTORS: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eENABLE_ACTIVE_ACTORS()
    val eENABLE_CCD: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eENABLE_CCD()
    val eDISABLE_CCD_RESWEEP: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eDISABLE_CCD_RESWEEP()
    val eADAPTIVE_FORCE: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eADAPTIVE_FORCE()
    val eENABLE_PCM: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eENABLE_PCM()
    val eDISABLE_CONTACT_REPORT_BUFFER_RESIZE: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eDISABLE_CONTACT_REPORT_BUFFER_RESIZE()
    val eDISABLE_CONTACT_CACHE: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eDISABLE_CONTACT_CACHE()
    val eREQUIRE_RW_LOCK: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eREQUIRE_RW_LOCK()
    val eENABLE_STABILIZATION: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eENABLE_STABILIZATION()
    val eENABLE_AVERAGE_POINT: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eENABLE_AVERAGE_POINT()
    val eEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS()
    val eENABLE_GPU_DYNAMICS: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eENABLE_GPU_DYNAMICS()
    val eENABLE_ENHANCED_DETERMINISM: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eENABLE_ENHANCED_DETERMINISM()
    val eENABLE_FRICTION_EVERY_ITERATION: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eENABLE_FRICTION_EVERY_ITERATION()
    val eMUTABLE_FLAGS: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eMUTABLE_FLAGS()
}

external interface PxSceneFlags : PxFlags

external interface PxShape : PxBase {
    // Pose Manipulation
    fun setLocalPose(pose: PxTransform)
    fun getLocalPose(): PxTransform

    // Collision Filtering
    fun setSimulationFilterData(data: PxFilterData)
    fun getSimulationFilterData(): PxFilterData
    fun setQueryFilterData(data: PxFilterData)
    fun getQueryFilterData(): PxFilterData
}

object PxShapeFlag {
    val eSIMULATION_SHAPE: Int get() = PhysX.physx._emscripten_enum_physx_PxShapeFlag_eSIMULATION_SHAPE()
    val eSCENE_QUERY_SHAPE: Int get() = PhysX.physx._emscripten_enum_physx_PxShapeFlag_eSCENE_QUERY_SHAPE()
    val eTRIGGER_SHAPE: Int get() = PhysX.physx._emscripten_enum_physx_PxShapeFlag_eTRIGGER_SHAPE()
    val eVISUALIZATION: Int get() = PhysX.physx._emscripten_enum_physx_PxShapeFlag_eVISUALIZATION()
}

external interface PxShapeFlags : PxFlags

external interface PxSimulationEventCallback {
    var cbFun: (Int) -> Unit
}

external interface PxSimulationFilterShader

external interface PxSweepHit : PxLocationHit

external interface PxSweepQueryResult {
    val block: PxSweepHit
    //attribute PxSweepHit touches;
    //attribute unsigned long nbTouches;
    val userData: Int
    val queryStatus: Int
    val hasBlock: Boolean

    fun getNbAnyHits(): Int
    fun getAnyHit(index: Int): PxSweepHit
}

external interface PxTolerancesScale

@Suppress("ClassName")
external interface Vector_PxRaycastHit : StdVector<PxRaycastHit>

@Suppress("ClassName")
external interface Vector_PxRaycastQueryResult : StdVector<PxRaycastQueryResult>

@Suppress("ClassName")
external interface Vector_PxSweepHit : StdVector<PxSweepHit>

@Suppress("ClassName")
external interface Vector_PxSweepQueryResult : StdVector<PxSweepQueryResult>
