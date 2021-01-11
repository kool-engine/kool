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

external interface PxActorFlag {
    val eVISUALIZATION: Int
    val eDISABLE_GRAVITY: Int
    val eSEND_SLEEP_NOTIFIES: Int
    val eDISABLE_SIMULATION: Int
}

external interface PxActorFlags : PxFlags

external interface PxActorType {
    val eRIGID_STATIC: Int
    val eRIGID_DYNAMIC: Int
    val eARTICULATION_LINK: Int
    val eACTOR_COUNT: Int
    val eACTOR_FORCE_DWORD: Int
}

external interface PxFilterData {
    val word0: Int
    val word1: Int
    val word2: Int
    val word3: Int
}

external interface PxForceMode {
    val eFORCE: Int
    val eIMPULSE: Int
    val eVELOCITY_CHANGE: Int
    val eACCELERATION: Int
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

external interface PxRigidActor: PxActor {
    // Global Pose Manipulation
    fun getGlobalPose(): PxTransform
    fun setGlobalPose(pose: PxTransform, autowake: Boolean)

    // Shapes
    fun attachShape(shape: PxShape): Boolean
    fun detachShape(shape: PxShape, wakeOnLostTouch: Boolean)
    fun getNbShapes(): Int
    fun getShapes(userBuffer: Array<PxShape?>, bufferSize: Int, startIndex: Int)
}

external interface PxRigidBody: PxRigidActor {
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

external interface PxRigidBodyFlag {
    val eKINEMATIC: Int
    val eUSE_KINEMATIC_TARGET_FOR_SCENE_QUERIES: Int
    val eENABLE_CCD: Int
    val eENABLE_CCD_FRICTION: Int
    val eENABLE_POSE_INTEGRATION_PREVIEW: Int
    val eENABLE_SPECULATIVE_CCD: Int
    val eENABLE_CCD_MAX_CONTACT_IMPULSE: Int
    val eRETAIN_ACCELERATIONS: Int
}

external interface PxRigidBodyFlags : PxFlags

external interface PxRigidDynamic: PxRigidBody

external interface PxRigidStatic: PxRigidActor

external interface PxScene {
    fun addActor(actor: PxActor, bvhStructure: PxBVHStructure?)
    fun removeActor(actor: PxActor, wakeOnLostTouch: Boolean)

    // Simulation
    fun simulate(elapsedTime: Float)
    fun fetchResults(block: Boolean): Boolean
    fun getGravity(): PxVec3
    fun setGravity(vec: PxVec3)
}

external interface PxSceneDesc {
    var gravity: PxVec3
    var filterShader: PxSimulationFilterShader
    var cpuDispatcher: PxCpuDispatcher
    var flags: PxSceneFlags
}

external interface PxSceneFlag {
    val eENABLE_ACTIVE_ACTORS: Int
    val eENABLE_CCD: Int
    val eDISABLE_CCD_RESWEEP: Int
    val eADAPTIVE_FORCE: Int
    val eENABLE_PCM: Int
    val eDISABLE_CONTACT_REPORT_BUFFER_RESIZE: Int
    val eDISABLE_CONTACT_CACHE: Int
    val eREQUIRE_RW_LOCK: Int
    val eENABLE_STABILIZATION: Int
    val eENABLE_AVERAGE_POINT: Int
    val eEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS: Int
    val eENABLE_GPU_DYNAMICS: Int
    val eENABLE_ENHANCED_DETERMINISM: Int
    val eENABLE_FRICTION_EVERY_ITERATION: Int
    val eMUTABLE_FLAGS: Int
}

external interface PxSceneFlags : PxFlags

external interface PxShape : PxBase {
    // Pose Manipulation
    fun setLocalPose(pose: PxTransform)
    fun getLocalPose(): PxTransform

    // Collision Filtering
    fun setSimulationFilterData(data: PxFilterData)
    fun getSimulationFilterData(): PxFilterData
}

external interface PxShapeFlag {
    val eSIMULATION_SHAPE: Int
    val eSCENE_QUERY_SHAPE: Int
    val eTRIGGER_SHAPE: Int
    val eVISUALIZATION: Int
}

external interface PxShapeFlags : PxFlags

external interface PxSimulationFilterShader

external interface PxTolerancesScale